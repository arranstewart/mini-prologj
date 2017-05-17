package prologj;

import java.util.Map;
import java.util.stream.Stream;

/** holds factory methods.
 */
class Globals {

  /** factory method - make a fact (just has TRUE as body) */
  public static Rule fact(Compound head) {
    return new Rule(head, TRUE);
  }
  
  /** factory method - make an atom (has an empty body) */
  public static Compound atom(String at) {
    return new Compound(at);
  }
  
  /** represents true */
  protected static final Compound TRUE = 
      new Compound("true") {
        @Override
        public Term substitute(Map<Var, Term> bindings) {
          return this;
        }
        
        @Override
        public Stream<Term> query(Queryable ignored) {
          // just return stream of self
          return Stream.of(this);
        }
      };
}

