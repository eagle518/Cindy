package co.mindie.cindy.web;

import co.mindie.cindy.webservice.CindyWebApp;
import co.mindie.cindy.core.annotation.Load;
import co.mindie.cindy.webservice.annotation.Singleton;
import co.mindie.cindy.core.annotation.Wired;
import co.mindie.cindy.core.component.metadata.ComponentMetadataManagerBuilder;
import co.mindie.cindy.core.component.box.ComponentBox;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class CindyWebAppTest {

	////////////////////////
	// VARIABLES
	////////////////

	////////////////////////
	// CONSTRUCTORS
	////////////////


	////////////////////////
	// METHODS
	////////////////

	@Test
	public void singleton_are_automatically_added() {
		ComponentMetadataManagerBuilder builder = new ComponentMetadataManagerBuilder();
		builder.loadComponent(BSingleton.class);
		builder.loadComponent(CindyWebApp.class);

		ComponentBox box = builder.build().createComponent(CindyWebApp.class, ComponentBox.create(true)).getInnerBox();

		ASingleton singleton = (ASingleton)box.findComponent(ASingleton.class);

		assertNotNull(singleton);
	}

	@Test
	public void singleton_dont_inject_twice() {
		ComponentMetadataManagerBuilder builder = new ComponentMetadataManagerBuilder();
		builder.loadComponent(MyWebApp.class);
		builder.loadComponent(BSingleton.class);

		ComponentBox enclosingBox = builder.build().createComponent(CindyWebApp.class, ComponentBox.create(true)).getInnerBox();

		BSingleton singleton = (BSingleton)enclosingBox.findComponent(ASingleton.class);

		assertNotNull(singleton);
	}

	@Load
	public static class MyWebApp extends CindyWebApp {

		@Wired private ASingleton aSingleton;

		public MyWebApp() {

		}

	}

	public static class ASingleton {
		public ASingleton() {

		}

	}

	@Load
	@Singleton
	public static class BSingleton extends ASingleton {
		public BSingleton() {

		}

	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////


}
