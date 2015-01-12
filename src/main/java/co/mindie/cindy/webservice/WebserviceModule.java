package co.mindie.cindy.webservice;

import co.mindie.cindy.core.module.Module;
import co.mindie.cindy.core.module.ModuleConfiguration;
import co.mindie.cindy.webservice.configuration.Configuration;
import co.mindie.cindy.webservice.console.Log4jSocketConsole;
import co.mindie.cindy.webservice.controller.builtin.CamelCaseToSnakeCaseParameterNameResolver;
import co.mindie.cindy.webservice.resolver.ResolverManager;
import co.mindie.cindy.webservice.responsewriter.JsonResponseWriter;
import me.corsin.javatools.array.ArrayUtils;

/**
 * Created by simoncorsin on 12/01/15.
 */
public class WebserviceModule implements Module {

	////////////////////////
	// VARIABLES
	////////////////

	private WebserviceModuleConfiguration webserviceModuleConfiguration;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public WebserviceModule() {
		this.webserviceModuleConfiguration = new WebserviceModuleConfiguration();
		this.webserviceModuleConfiguration.set(WebserviceModuleConfiguration.API_PARAMETER_SYNTAX_KEY, WebserviceModuleConfiguration.API_PARAMETER_SYNTAX_SNAKE_CASE);
	}

	////////////////////////
	// METHODS
	////////////////


	////////////////////////
	// GETTERS/SETTERS
	////////////////

	@Override
	public ModuleConfiguration getConfiguration() {
		return this.webserviceModuleConfiguration;
	}

	@Override
	public Class<?>[] getComponentClasses() {
		Class<?>[] classes = new Class<?>[] {
				Configuration.class,
				Log4jSocketConsole.class,
				ResolverManager.class,
				JsonResponseWriter.class
		};

		String apiParameterSyntax = this.webserviceModuleConfiguration.getString(WebserviceModuleConfiguration.API_PARAMETER_SYNTAX_KEY);

		if (apiParameterSyntax != null) {
			if (apiParameterSyntax.equals(WebserviceModuleConfiguration.API_PARAMETER_SYNTAX_SNAKE_CASE)) {
				classes = ArrayUtils.addItem(classes, CamelCaseToSnakeCaseParameterNameResolver.class);
			}
		}

		return classes;
	}

	@Override
	public String[] getComponentsClasspaths() {
		return new String[] { "co.mindie.cindy.webservice.resolver.builtin" };
	}

	@Override
	public Module[] getDependencies() {
		return new Module[0];
	}

}
