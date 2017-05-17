package prologj;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static prologj.Globals.atom;

import java.util.ArrayList;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CompoundTest {

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public final void unifyCompounds() {
    System.out.println("\nunify compounds");
    
    Var x = new Var("X");
    Compound knownTerm = new Compound("father_child", atom("eric"), atom("thorne") );
    Compound goal = new Compound("father_child", atom("eric"), x );
    
    Map<Var, Term> bindings = goal.match(knownTerm).get();
    
    // should be one bound var, X
    assertEquals(1, bindings.keySet().size() );
    Var v = new ArrayList<Var>(bindings.keySet()).get(0);
    assertTrue("var x should be only key",  v == x );
    
    for (Var var : bindings.keySet()) {
      System.out.println(var + " = " + bindings.get(var));
    }
    
    Term res = goal.substitute(bindings);
    System.out.println("goal w/ subs bindings: " + res);
  }

  @Test
  public final void unifyAtoms() {
    System.out.println("\nunify atoms");
    
    Compound knownTerm = atom("bob");
    Var x = new Var("X");
    Term goal = x;
    
    Map<Var, Term> bindings = goal.match(knownTerm).get();
    
    // should be one bound var, X
    assertEquals(1, bindings.keySet().size() );
    Var v = new ArrayList<Var>(bindings.keySet()).get(0);
    assertTrue("var x should be only key",  v == x );
    
    for (Var var : bindings.keySet()) {
      System.out.println(var + " = " + bindings.get(var));
    }
    
    Term res = goal.substitute(bindings);
    System.out.println("goal w/ subs bindings: " + res);
  }
  
  @Test
  public final void unifyAtomsInReverse() {
    System.out.println("\nunify atoms in reverse");
    
    Compound knownTerm = atom("bob");
    Var x = new Var("X");
    Term goal = x;
    
    // we ask for knownTerm to unify with goal, rather than vice versa
    Map<Var, Term> bindings = knownTerm.match(goal).get();
    
    // should be one bound var, X
    assertEquals(1, bindings.keySet().size() );
    Var v = new ArrayList<Var>(bindings.keySet()).get(0);
    assertTrue("var x should be only key",  v == x );
    
    for (Var var : bindings.keySet()) {
      System.out.println(var + " = " + bindings.get(var));
    }
    
    Term res = goal.substitute(bindings);
    System.out.println("goal w/ subs bindings: " + res);
  }
  
  @Test
  public final void unifyCompoundWithVar() {
    System.out.println("\nunify partially ground term with var");
    
    Var x = new Var("X");
    Compound knownTerm = new Compound("father_child", atom("eric"), x );
    
    Term goal = new Var("G");
    
    // we ask for knownTerm to unify with goal, rather than vice versa
    Map<Var, Term> bindings = knownTerm.match(goal).get();
    
    // should be one bound var, X
    assertEquals(1, bindings.keySet().size() );
    Var v = new ArrayList<Var>(bindings.keySet()).get(0);
    assertTrue("var G should be only key",  v == goal );
    
    for (Var var : bindings.keySet()) {
      System.out.println(var + " = " + bindings.get(var));
    }
    
    Term res = goal.substitute(bindings);
    System.out.println("goal w/ subs bindings: " + res);
  }

  
//  @Test
//  public final void testSubstitute() {
//    fail("Not yet implemented"); // TODO
//  }
//
//  @Test
//  public final void testMatch() {
//    fail("Not yet implemented"); // TODO
//  }

}
