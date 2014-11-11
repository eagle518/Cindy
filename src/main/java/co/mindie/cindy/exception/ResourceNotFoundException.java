package co.mindie.cindy.exception;

import co.mindie.cindy.automapping.HttpMethod;

public class ResourceNotFoundException extends ParameterException {
	public ResourceNotFoundException(String parameter, HttpMethod httpMethod, String path) {
		super(httpMethod + " " + path + ": resource URL not found [" + parameter + "]");
	}
}
