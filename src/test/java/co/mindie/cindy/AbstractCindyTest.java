package co.mindie.cindy;

import co.mindie.cindy.automapping.Box;
import co.mindie.cindy.automapping.WiredCore;
import co.mindie.cindy.component.box.ComponentBox;
import co.mindie.cindy.component.ComponentInitializer;
import co.mindie.cindy.component.ComponentMetadataManager;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
@Box(rejectAspects = {}, needAspects = {})
public abstract class AbstractCindyTest {

	@WiredCore private ComponentBox innerBox;

	protected ComponentMetadataManager metadataManager;

	protected void onLoad(ComponentMetadataManager metadataManager) {
		metadataManager.loadComponents("co.mindie.cindy");
	}

	@Before
	public void setUp() {
		this.metadataManager = new ComponentMetadataManager();
		this.metadataManager.loadComponent(this.getClass());

		this.onLoad(metadataManager);

		ComponentInitializer initializer =  this.metadataManager.createInitializer();

		initializer.addCreatedComponent(this, ComponentBox.create(true));

		initializer.init();
	}

	@After
	public void tearDownEnvironment() {
		this.innerBox.close();

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
