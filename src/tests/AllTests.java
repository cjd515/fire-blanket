package tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ModelViewUtilTests.class, RequirementsTests.class, ComponentTests.class })
public class AllTests {

}
