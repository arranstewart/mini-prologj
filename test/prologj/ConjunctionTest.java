/**
 * 
 */
package prologj;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static prologj.Globals.atom;
import static prologj.Globals.fact;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.junit.Test;

/**
 * @author arran
 *
 */
public class ConjunctionTest {


  /**
   * Test method for {@link prologj.Term#match(prologj.Term)}.
   * 
   * No real need for this tho, since unify is just inherited from Compound.
   */
  @Test
  public final void testMatch() {
    System.out.println("\nunify conjunctions"); // TODO
    
    Compound term1 = new Compound("father_child", atom("eric"), atom("eric jr") );
    Compound term2 = new Compound("father_child", atom("stan"), atom("stan jr") );
    
    Conjunction con1 = new Conjunction(term1, term2);
    
    Compound term2wVar = new Compound("father_child", atom("stan"), new Var("X") );
    Term X = term2wVar.args.get(1);
    
    Conjunction con2 = new Conjunction(term1, term2wVar);
    
    // do unify
    ArrayList<Entry<Var, Term>> binding = new ArrayList<Entry<Var, Term>>(con1.match(con2).get().entrySet());
    
    assertEquals("should have one var", 1, binding.size());
    assertTrue("var should be X", binding.get(0).getKey() == X );
    assertTrue("val should be stan jr", binding.get(0).getValue() == term2.args.get(1) );
    
    System.out.println(binding);
  }
  
 
  
  @Test 
  public final void testRule() {
    System.out.println("\ntest rule body (conjunction)");
    // daughter_of(X,Y) :- father_of(Y,X), female(X).
    
    Rule[] rules = {
        fact(  new Compound("male",   atom("noah"))  ),
        fact(  new Compound("male",   atom("shem"))  ),
        fact(  new Compound("male",   atom("ham"))  ),
        fact(  new Compound("male",   atom("japheth"))  ),
        fact(  new Compound("female", atom("unsung daughter 1"))  ),
        fact(  new Compound("female", atom("unsung daughter 2"))  ),
        
        fact(  new Compound("father_of", atom("noah"), atom("shem")) ),
        fact(  new Compound("father_of", atom("noah"), atom("ham")) ),
        fact(  new Compound("father_of", atom("noah"), atom("japheth")) ),
        fact(  new Compound("father_of", atom("noah"), atom("unsung daughter 1")) ),
        fact(  new Compound("father_of", atom("noah"), atom("unsung daughter 2")) ),
        
        null
    };
    
    Compound daughter1 = (Compound) rules[4].head.args.get(0);
    Compound daughter2 = (Compound) rules[5].head.args.get(0);
    
    // rule
    // daughter_of(X,Y) :- father_of(Y,X), female(X)
    Var X = new Var("X");
    Var Y = new Var("Y");
    Rule dtr_of = new Rule( new Compound("daughter_of", X, Y), 
                new Conjunction( 
                      new Compound("female", X), 
                      new Compound("father_of", Y, X) ));
    
    rules[ rules.length - 1 ] = dtr_of;
    Database db = new Database(rules);
 
    Compound goal = new Compound("daughter_of", new Var("A"), new Var("B") );
    //Term Y = goal.args.get(0);
    //assertEquals( "is same", ((Var) Y).name, "Y");
    
    List<Term> queryResults = db.queryAll(goal);
    
    int i = queryResults.size();
    System.out.println("num results: " + i);
    
    assertEquals("should be 2 results", 2, queryResults.size() );
    
    for (Term result : queryResults) {
      Compound resultAsC = (Compound) result; // Cmpd(daughter_of)(unsung daughter 1, noah)
      
      assertEquals( "correct functor", "daughter_of", resultAsC.functor );
      
      Term arg1 = resultAsC.args.get(0);
      boolean arg1_is_a_daughter = daughter1.groundAndEqual(arg1) || daughter2.groundAndEqual(arg1);
      
      assertTrue ("first arg is a daughter", arg1_is_a_daughter);
      
      System.out.println(resultAsC);
    }

    
  }
  
  
  public static void main(String[] args) {
    ConjunctionTest t = new ConjunctionTest();
    t.testRule();
  }

}
