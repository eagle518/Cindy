package co.mindie.cindy.utils;

import co.mindie.cindy.automapping.Box;
import co.mindie.cindy.automapping.CreationBox;
import co.mindie.cindy.automapping.SearchScope;
import co.mindie.cindy.automapping.WiredCore;
import co.mindie.cindy.component.ComponentInitializer;
import co.mindie.cindy.component.ComponentMetadata;
import co.mindie.cindy.component.ComponentMetadataManager;
import co.mindie.cindy.component.ComponentMetadataManagerBuilder;
import co.mindie.cindy.component.box.ComponentBox;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.Duration;

@Box(rejectAspects = {}, needAspects = {})
public abstract class CindyTestEnvironment {

	@WiredCore private ComponentBox innerBox;

	protected ComponentMetadataManagerBuilder metadataManagerBuilder;
	protected ComponentMetadataManager metadataManager;

	protected void onLoad(ComponentMetadataManagerBuilder metadataManager) {
		metadataManager.loadComponents("co.mindie.cindy");
	}

	protected void onCreate(ComponentInitializer initializer) {

	}

	protected void injectComponent(Object component, ComponentMetadataManagerBuilder metadataManagerBuilder) {
		Class<?> cls = component.getClass();

		ComponentMetadata metadata = metadataManagerBuilder.loadComponent(cls);
		metadata.setCreationPriority(Integer.MAX_VALUE);
		metadata.setFactory(() -> component);
		metadataManagerBuilder.loadComponent(this.getClass()).addDependency(cls, true, false, SearchScope.NO_SEARCH, CreationBox.CURRENT_BOX);
	}

	public void prepare() {
		this.metadataManagerBuilder = new ComponentMetadataManagerBuilder();
		this.metadataManagerBuilder.loadComponent(this.getClass());

		this.onLoad(metadataManagerBuilder);

		this.metadataManager = metadataManagerBuilder.build();

		ComponentInitializer initializer = this.metadataManager.createInitializer();

		initializer.addCreatedComponent(this, ComponentBox.create(true));

		this.onCreate(initializer);

		initializer.init();
	}

	public void unprepare() {
		if (this.innerBox != null) {
			this.innerBox.close();
		}

		releaseTime();
	}

	protected static DateTime setCurrentDateNow() {
		return setCurrentDate(DateTime.now());
	}

	protected static DateTime setCurrentDate(DateTime date) {
		DateTimeUtils.setCurrentMillisFixed(date.getMillis());
		return date;
	}

	protected static DateTime incrementCurrentDate(Duration duration) {
		DateTime newDate = DateTime.now().plus(duration);
		return setCurrentDate(newDate);
	}

	protected void releaseTime() {
		DateTimeUtils.setCurrentMillisSystem();
	}
}
