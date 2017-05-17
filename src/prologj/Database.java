package prologj;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/** Database contains a list of rules, and can be queried.
 */
class Database implements Queryable {
  final List<Rule> rules;
  
  public Database(Rule...rules) {
    this.rules = Arrays.asList(rules);
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("DB(\n");
    for (int i = 0; i < rules.size(); i++) {
      sb.append( "   " + rules.get(i).toString() );
      if (i < rules.size() - 1) { sb.append(","); }
      sb.append("\n");
    }
    sb.append(")");
    return sb.toString();
  }

  /** given a goal, query the database to see if it can be satisfied. */
  @Override
  public Stream<Term> query(Queryable goal) {
    final Database db = this;
    
    // if we know there are no rules, we return an empty stream.
    if (db.rules.size() == 0) {
      List<Term> bogusList = new ArrayList<Term>();
      return bogusList.stream();
    }
    
    // else we make a stream by mapping over rules, and expanding
    // each rule into a potential stream of query results.
    Stream<Rule> ruleStrm  = db.rules.stream();
    
    // given the stream of _rules_, do a flatMap which "flattens" each
    // rule into a stream of _terms_ (being results from matching the head
    // of that query).
    Stream<Term> termStrm= ruleStrm.flatMap(  rule -> {
      Term goalAsTerm = (Term) goal;
      Optional<Map<Var, Term>> headMatch_ = rule.head.match(goalAsTerm);
      
      if (! headMatch_.isPresent() ) { // rule not applicable, so return null
        return null; 
      }
      
      Map<Var,Term> matchBinding = headMatch_.get();
      
      // else there were matches. So make the substitutions in head and body.
      Term head = rule.head.substitute(matchBinding);
      Term body = rule.body.substitute(matchBinding);
      Queryable bodyAsQ = (Queryable) body; // body _shouldn't_ legally ever be a var,
                                            // but should probably enforce this.
      
      Stream<Term> queryResults = bodyAsQ.query(db);
      // do the substitutions there as well, using /map/.
      
      if (queryResults == null) {
        System.err.println("query results were null!");
        queryResults = new ArrayList<Term>().stream();
      }
      
      return (Stream<? extends Term>) queryResults.map( item -> { 
        Optional<Map<Var, Term>> unifiedBits = body.match(item);
        if (unifiedBits.isPresent()) {
          Term x = head.substitute( unifiedBits.get()  );
          return x;
        } else {
          return null;
        }
      });
      
    });
 
    return termStrm;
  }
  
  // get all query answers, and dump in a list
  public List<Term> queryAll(Queryable goal) {
    List<Term> queryResults = new ArrayList<Term>();
    this.query(goal).forEach( x -> queryResults.add(x) );
    return queryResults;
  }
  
}

