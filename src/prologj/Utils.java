package prologj;

import static java.util.Optional.empty;
import static java.util.Optional.of;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public interface Utils {
  
  /** Merge 2 sets of bindings. It's okay if one or the other is null (that just
   * results in an empty result). 
   * If they can be merged without conflict, returns an Optional of the merge,
   * else empty.
   * 
   * @return Merged bindings, or empty.
   */
  public static Optional<Map<Var, Term>> mergeBindings(Map<Var, Term> bindings1, Map<Var, Term> bindings2) {
    if (bindings1 == null || bindings2 == null) {
      return empty();
    }
    boolean conflict = false;
    Map<Var,Term> resultBinding = new HashMap<Var,Term>();

    resultBinding.putAll(bindings1);

    for (Var b2_var : bindings2.keySet()) {
      
      Term b2_val = bindings2.get(b2_var);
      Term b1_val = resultBinding.get(b2_var);
      
      // is a var in both bindings1 and bindings2? 
      // if so, try match
      if (b1_val != null) {

        // try get substitution.
        // if can't, we are in conflict.
        // if we can, put results of substitution in our result.
        Optional<Map<Var, Term>> substitution = b1_val.match(b2_val);
        if (! substitution.isPresent() ) {
          conflict = true;
        } else {
          for (Var subVar : substitution.get().keySet()) {
            Term subVal = substitution.get().get(subVar);
            resultBinding.put(subVar, subVal);
          }
        }
        
      // var not in both, so add b2.  
      } else {
        resultBinding.put(b2_var, b2_val);
      }
    }
    
    if (conflict) {
        return empty();
    }
    return of(resultBinding);
  }
}


