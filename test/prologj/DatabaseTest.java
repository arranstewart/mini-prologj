/**
 * 
 */
package prologj;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static prologj.Globals.atom;
import static prologj.Globals.fact;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * @author arran
 *
 */
public class DatabaseTest {

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
  }

  
  @Test
  public final void simpleTest() {
    System.out.println("\nsimple db test");
    Database db = null;
    {
      Rule[] rules = new Rule[] {
          fact( new Compound("mother_child", atom("mamma"), atom("bob jr")) ),
          fact( new Compound("mother_child", atom("mamma"), atom("kristen")) ),
          fact( new Compound("mother_child", atom("mamma"), atom("felicia")) )
      };
      
      db = new Database(rules);
    }
    
    System.out.println("query: who is mother of kristen?");
    Compound goal = new Compound("mother_child", new Var("X"), atom("kristen") );
    Term X = goal.args.get(0);

    List<Term> queryResults = db.queryAll(goal);
    //List<Term> queryResults = new ArrayList<Term>();
    //db.query(goal).forEach( x -> queryResults.add(x) );
    
    assertEquals("should be one answer", 1, queryResults.size() );
    
    for (Term item : queryResults) {
      System.out.println(item);
      System.out.println("value of X = " + goal.match(item).get().get(X));
    }
    assertTrue("x not null", X != null );
  }

  
  @Test
  public final void multipleAnswers() {
    System.out.println("\nmultiple answer db test");
    Database db = null;
    {
      Rule[] rules = new Rule[] {
          fact( new Compound("kermit_color", atom("green") ) ), // irrelevant fact 
                                                                // shouldn't affect results
          fact( new Compound("mother_child", atom("mamma"), atom("bob jr")) ),
          fact( new Compound("mother_child", atom("mamma"), atom("kristen")) ),
          fact( new Compound("mother_child", atom("mamma"), atom("felicia")) )
      };
      
      db = new Database(rules);
    }
    
    System.out.println("query: who is child of mamma?");
    Compound goal = new Compound("mother_child", atom("mamma"), new Var("X") );
    Term X = goal.args.get(1);

    List<Term> queryResults = db.queryAll(goal);
    
    assertEquals("should be 3 answers", 3, queryResults.size() );
    
    for (Term item : queryResults) {
      System.out.println(item);
      System.out.println("value of X = " + goal.match(item).get().get(X));
    }
    assertTrue("x not null", X != null );
  }
  

}
