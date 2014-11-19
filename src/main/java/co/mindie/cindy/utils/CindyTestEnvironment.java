package co.mindie.cindy.utils;

import co.mindie.cindy.automapping.Box;
import co.mindie.cindy.automapping.CreationBox;
import co.mindie.cindy.automapping.SearchScope;
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

	protected void onCreate(ComponentInitializer initializer) {

	}

	protected void injectComponent(Object component, ComponentMetadataManager metadataManager) {
		Class<?> cls = component.getClass();

		ComponentMetadata metadata = metadataManager.loadComponent(cls);
		metadata.setCreationPriority(Integer.MAX_VALUE);
		metadata.setFactory(() -> component);
		metadataManager.loadComponent(this.getClass()).addDependency(cls, true, false, SearchScope.NO_SEARCH, CreationBox.CURRENT_BOX);
	}

	public void prepare() {
		this.metadataManager = new ComponentMetadataManager();
		this.metadataManager.loadComponent(this.getClass());

		this.onLoad(metadataManager);

		metadataManager.ensureIntegrity();

		ComponentInitializer initializer =  this.metadataManager.createInitializer();

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
