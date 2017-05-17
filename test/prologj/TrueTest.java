package prologj;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static prologj.Globals.TRUE;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class TrueTest {


  @Test
  public final void testTrue() {
    
    List<Term> aList = new ArrayList<Term>();
    
    TRUE.query(null).forEach( x -> aList.add(x) );
    
    assertEquals("should have one result", 1, aList.size() );
    
    assertTrue("which should be the TRUE obj", aList.get(0) == TRUE );
    
  }



  @Test
  public final void testMatch() {
    
    Var X = new Var("X");
    Map<Var, Term> bindingResult = X.match(TRUE).get();
    
    ArrayList<Var> vars = new ArrayList<>(bindingResult.keySet());
    
    assertEquals("should be one bound var", 1, vars.size() );
    
    assertTrue("and it should be X", vars.get(0) == X);
    
    assertTrue("and it should be bound to TRUE", bindingResult.get(X) == TRUE);
  }

}
