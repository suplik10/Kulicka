package cz.kulicka.test;

import cz.kulicka.CoreEngine;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;


//@RunWith(SpringRunner.class)
//@ContextConfiguration("/test-spring-context.xml")
public class CoreEngineTest {

    @Autowired
    CoreEngine coreEngine;

    @Before
    public void setUp() {

    }


    //@Test
    public void testCoreEngine() {
        //TODO complet test
        //coreEngine.runIt();
    }
}
