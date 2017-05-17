package prologj;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static prologj.Utils.mergeBindings;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/** A /term/ is either a variable or a compound. 
 * (atoms are just compounds with no arguments.
 */
public interface Term {
  /** applying substitutions to any var you hold will
   * give back a result of the same type ...
   *   So we could make substitute a separate, static function,
   * of type T -> T, to avoid casting. 
   */
  public Term substitute(Map<Var,Term> bindings);
  
  /** trying unifying with another term */
  public Optional<Map<Var, Term>> match(Term other);
}

/** things that can be queried to see if a 
 * goal is satisfiable.
 */
interface Queryable {
  public Stream<Term> query( Queryable q);
}

interface NonVarTerm extends Term, Queryable {
}

/** Intended for use this with types native to the host language.
 * unification and substitution will have to be implemented
 * for whatever type is used.
 */
abstract class Foreign<T> implements Term {
  final public T value;
  
  public Foreign(T value) {
    this.value = value;
  }
  
  public String toString(){
    return this.value.toString();
  }

  @Override
  abstract public Term substitute(Map<Var, Term> bindings);

  @Override
  abstract public Optional<Map<Var, Term>> match(Term other);
}


class Var implements Term {
  final public String name;
  
  public Var(String name) {
    this.name = name;
  }
  public String toString() {
    return this.name; 
  }

  /**
   * Since we are a var, we unify by just mapping from ourselves
   * to the other.
   */
  @Override
  public Optional<Map<Var, Term>> match(Term other) {
    Map<Var,Term> bindings = new HashMap<Var,Term>();
    if (this != other) {
      bindings.put(this, other);
    }
    return of(bindings);
  }
  
  @Override
  /** does this var have a value in bindings?
   *  if so, it may be compound, so tell _it_ to do substitute.
   *  otherwise just return us.
   */
  public Term substitute(Map<Var, Term> bindings) {
    Term value = bindings.get(this);
    if (value != null) {
      // if compound, substitute internals
      return value.substitute(bindings);
    }
    return this;
  }
}


class Compound implements NonVarTerm {
  final public String functor;
  final public List<Term> args;
  
  public Compound(String functor, Term...args ) {
    this.functor = functor;
    this.args = Arrays.asList(args);
  }
  
  /** for testing. if we are ground and other is ground,
   * and all atoms equal, will return true. Otherwise false. */
  protected boolean groundAndEqual(Term other) {
  	if (other == null) 
  		{ return false; }
  	if (! (other instanceof Compound)) 
  		{ return false; }
  	
  	Compound otherC = (Compound) other;
  	
  	if (! functor.equals(otherC.functor) ) 
  		{ return false; }
  	if (args.size() != otherC.args.size() )
  		{ return false ; }
  	
  	for (int i = 0; i < args.size(); i++) {
  		if (! ( args.get(i) instanceof prologj.Compound ) ) 
  			{ return false; }
  		Compound currArg = (Compound) args.get(i);
  		if (!  currArg.groundAndEqual(other) ) 
  			{return false;}
  	}
  	
    return true;
  }

  public String toString() {
    if (args.size() == 0) {
      return functor;
    }
    
    StringBuffer sb = new StringBuffer();
    sb.append(this.functor + "(");
    for (int i = 0; i < this.args.size(); i++) {
      sb.append(this.args.get(i).toString());
      if (i < this.args.size() - 1) sb.append(", ");
    }
    sb.append(")");
    return sb.toString();
  }
  
  /** match compounds against each other.
   * empty() if can't match.
   */
  @Override
  public Optional<Map<Var, Term>> match(Term other) {
    // could we assign other to this? if so, is compound
    boolean isCompound = this.getClass().isAssignableFrom(other.getClass());
    
    if (isCompound) {
      Compound otherCmp = (Compound) other;
      // functors must match
      if (! this.functor.equals(otherCmp.functor) ) {
        return empty();
      }
      // arg length must match
      if (this.args.size() != otherCmp.args.size()) {
        return empty();
      }
      
      // if args are same length, unify each pair of args, and
      // merge all the bindings.
      Map<Var,Term> finalBinding = new HashMap<Var, Term>();
      for (int i = 0; i < this.args.size(); i ++) {
        Term thisArg = this.args.get(i);
        Term otherArg = otherCmp.args.get(i);
        
        Optional<Map<Var, Term>> argBinding_ = thisArg.match(otherArg);
        if (! argBinding_.isPresent() ) {
          return empty();
        }
        
        Map<Var,Term> argBinding = argBinding_.get();
        Optional<Map<Var, Term>> merged_ = mergeBindings(finalBinding, argBinding);
        if( ! merged_.isPresent()) {
          return empty();
        }
       
        finalBinding = merged_.get();
      }
      return of(finalBinding);
    }
    
    // is not a compound
    return other.match(this);
  }
  
  @Override
  public Term substitute(Map<Var, Term> bindings) {
    Term[] newArgs = new Term[this.args.size()];
    // do substitution, by getting all your args to do substitution
    // on themselves.
    for (int i = 0; i < args.size(); i++) {
      newArgs[i] = args.get(i).substitute(bindings);
    }
    return new Compound(this.functor, newArgs );
  }

  /**
   * Attempt to satisfy against a database.
   * (normally because this is the body of a rule)
   */
  @Override
  public Stream<Term> query(Queryable database) {
    return database.query(this);
  }
}

class Conjunction  extends Compound {
  public Conjunction(Term... args) {
    super(",", args); // the functor is ","
  }
  
  /** Conjunctions have special behaviour - their solutions()
   * method - so we have to override substitute, so a new
   * Conjunction is produced (not a mere Compound). 
   */
  @Override
  public Term substitute(Map<Var, Term> bindings) {
    Term[] newArgs = new Term[this.args.size()]; 
    for (int i = 0; i < args.size(); i++) {
        newArgs[i] = args.get(i).substitute(bindings);
    }
    return new Conjunction(newArgs);
  }
  
  /**
   * recursive func. Get query solutions for the "current arg", and call
   * recursively to get solutions for remaining args.
   * if we've reached the end, and we're still here, we're on a line
   * of inquiry where all args were matched okay, so we return a result.
   * 
   * but if an arg match fails, throw null into the stream and abort this
   * line of inquiry.
   */
  private Stream<Term> solutionsForArg(Queryable database, int argIdx, Map<Var, Term> bindings) {
    if (argIdx >= args.size() ) { // success, we satisfied all args & have bindings.
      // so, substitute bindings into this. (one-el array so we can return as stream).
      Term result[] = { this.substitute(bindings) };
      return Arrays.asList(result).stream(); 
    }
    
    // still trying to satisfy all args.
    Term currentArg = this.args.get(argIdx);
    // get results of querying whether satisfiable
    Stream<Term> queryResults = database.query( (Queryable) currentArg.substitute(bindings) );

    // for each of those results, we should try and merge in our bindings,
    // and call results for the next arg.
    // So each result becomes a stream of results, that we merge. 
    Stream<Term> flattenedResults = queryResults.flatMap(  queryResult -> {
      
      // see if result can be merged with the current arg, then our bindings
      // so far.
      Optional<Map<Var, Term>> unified = mergeBindings(
          currentArg.match(queryResult).orElse(null), 
          bindings);
      
      if (!unified.isPresent()) {
        return null; // if can't unify, throw null into the stream
      } 
            
      // if _can_ unify, get results of next iter.
      return solutionsForArg(database, argIdx + 1, unified.get());
    });
    
    return flattenedResults; 
  }

  /**
   * see if can be satisfied in database.
   */
  @Override
  public Stream<Term> query(Queryable database) {
    // we build up answers to queries which can
    // be got once all args have been matched.
    // if an arg _isn't_ matched, we abandon that line of reasoning.
    
    // "branch" - try and get all ways of satisfying all args, 
    // by calling solutionsForArg, starting w/ arg 0.
    Map<Var,Term> emptyBindings = new HashMap<Var,Term>();
    Stream<Term> results = solutionsForArg(database, 0, emptyBindings);
    return results;
  }
  
}






