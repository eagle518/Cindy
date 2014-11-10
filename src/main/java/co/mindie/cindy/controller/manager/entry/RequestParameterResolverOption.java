package co.mindie.cindy.controller.manager.entry;

import co.mindie.cindy.automapping.ResolverOption;

public class RequestParameterResolverOption {

	////////////////////////
	// VARIABLES
	////////////////

	final private String key;
	final private String value;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public RequestParameterResolverOption(ResolverOption resolverOption) {
		this(resolverOption.key(), resolverOption.value());
	}

	public RequestParameterResolverOption(String key, String value) {
		this.key = key;
		this.value = value;
	}

	////////////////////////
	// METHODS
	////////////////


	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}
}
