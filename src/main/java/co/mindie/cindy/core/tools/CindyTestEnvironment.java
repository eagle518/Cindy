package co.mindie.cindy.core.tools;

import co.mindie.cindy.core.annotation.Box;
import co.mindie.cindy.core.component.CreationBox;
import co.mindie.cindy.core.component.SearchScope;
import co.mindie.cindy.core.annotation.Core;
import co.mindie.cindy.core.component.initializer.ComponentInitializer;
import co.mindie.cindy.core.component.metadata.ComponentMetadata;
import co.mindie.cindy.core.component.metadata.ComponentMetadataManager;
import co.mindie.cindy.core.component.metadata.ComponentMetadataManagerBuilder;
import co.mindie.cindy.core.component.box.ComponentBox;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;

@Box(rejectAspects = {}, needAspects = {})
public abstract class CindyTestEnvironment {

	@Core
	private ComponentBox innerBox;

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

	protected void releaseTime() {
		DateTimeUtils.setCurrentMillisSystem();
	}
}
