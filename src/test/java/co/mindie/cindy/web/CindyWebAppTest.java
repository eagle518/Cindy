package co.mindie.cindy.web;

import co.mindie.cindy.AbstractCindyTest;
import co.mindie.cindy.CindyWebApp;
import co.mindie.cindy.automapping.Load;
import co.mindie.cindy.automapping.Singleton;
import co.mindie.cindy.automapping.Wired;
import co.mindie.cindy.component.ComponentMetadataManager;
import co.mindie.cindy.component.box.ComponentBox;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


/**
 * Created by simoncorsin on 29/10/14.
 */
public class CindyWebAppTest extends AbstractCindyTest {

	////////////////////////
	// VARIABLES
	////////////////

	////////////////////////
	// CONSTRUCTORS
	////////////////


	////////////////////////
	// METHODS
	////////////////

	@Override
	protected void onLoad(ComponentMetadataManager metadataManager) {
		super.onLoad(metadataManager);


	}

	@Test
	public void singleton_are_automatically_added() {
		this.metadataManager.loadComponent(BSingleton.class);

		ComponentBox enclosingBox = this.metadataManager.createComponent(CindyWebApp.class, ComponentBox.create(true)).getInnerBox();

		ASingleton singleton = (ASingleton)enclosingBox.findComponent(ASingleton.class);

		assertNotNull(singleton);
	}

	@Test
	public void singleton_dont_inject_twice() {
		this.metadataManager.loadComponent(MyWebApp.class).setCreationPriority(0);
		this.metadataManager.loadComponent(BSingleton.class);

		ComponentBox enclosingBox = this.metadataManager.createComponent(CindyWebApp.class, ComponentBox.create(true)).getInnerBox();

		BSingleton singleton = (BSingleton)enclosingBox.findComponent(ASingleton.class);

		assertNotNull(singleton);
	}

	public static class MyWebApp extends CindyWebApp {

		@Wired private ASingleton aSingleton;

		public MyWebApp() {

		}

	}

	public static class ASingleton {
		public ASingleton() {

		}

	}

	@Singleton
	public static class BSingleton extends ASingleton {
		public BSingleton() {

		}

	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////


}
