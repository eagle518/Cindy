package co.mindie.cindy;

import co.mindie.cindy.utils.CindyTestEnvironment;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public abstract class AbstractCindyTest extends CindyTestEnvironment {

	@Before
	public void setUp() {
		this.prepare();
	}

	@After
	public void tearDownEnvironment() {
		this.unprepare();
	}
}
