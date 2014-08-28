package co.mindie.cindy;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.After;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public abstract class AbstractCindyTest {
	@After
	public void tearDownEnvironment() {
		releaseTime();
	}

	protected static DateTime setCurrentDateNow() {
		return setCurrentDate(DateTime.now());
	}

	protected static DateTime setCurrentDate(DateTime date) {
		DateTimeUtils.setCurrentMillisFixed(date.getMillis());
		return date;
	}

	protected void releaseTime() {
		DateTimeUtils.setCurrentMillisSystem();
	}
}
