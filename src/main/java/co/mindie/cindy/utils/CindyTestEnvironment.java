package co.mindie.cindy.utils;

import co.mindie.cindy.automapping.Box;
import co.mindie.cindy.automapping.WiredCore;
import co.mindie.cindy.component.ComponentInitializer;
import co.mindie.cindy.component.ComponentMetadata;
import co.mindie.cindy.component.ComponentMetadataManager;
import co.mindie.cindy.component.box.ComponentBox;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;

@Box(rejectAspects = {}, needAspects = {})
public abstract class CindyTestEnvironment {

	@WiredCore private ComponentBox innerBox;

	protected ComponentMetadataManager metadataManager;

	protected void onLoad(ComponentMetadataManager metadataManager) {
		metadataManager.loadComponents("co.mindie.cindy");
	}

	protected void injectComponent(Object component, ComponentMetadataManager metadataManager) {
		Class<?> cls = component.getClass();

		ComponentMetadata metadata = metadataManager.loadComponent(cls);
		metadata.setCreationPriority(Integer.MAX_VALUE);
		metadata.setFactory(() -> component);
	}

	public void prepare() {
		this.metadataManager = new ComponentMetadataManager();
		this.metadataManager.loadComponent(this.getClass());

		this.onLoad(metadataManager);

		ComponentInitializer initializer =  this.metadataManager.createInitializer();

		initializer.addCreatedComponent(this, ComponentBox.create(true));

		initializer.init();
	}

	public void unprepare() {
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
