import com.myapp.games.schnellen.model.CardRulesTest;
import com.myapp.games.schnellen.model.CardTest;
import com.myapp.games.schnellen.model.SchnellenTest;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

    public static Test suite() {
        TestSuite suite = new TestSuite(AllTests.class.getName());
        //$JUnit-BEGIN$

        suite.addTestSuite(CardTest.class);
        suite.addTestSuite(CardRulesTest.class);
        suite.addTestSuite(SchnellenTest.class);
        
        //$JUnit-END$
        return suite;
    }

}
