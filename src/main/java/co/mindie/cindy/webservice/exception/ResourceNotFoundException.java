package co.mindie.cindy.webservice.exception;

import co.mindie.cindy.exception.ParameterException;
import co.mindie.cindy.webservice.controller.HttpMethod;

public class ResourceNotFoundException extends ClientException {
	public ResourceNotFoundException(String parameter, HttpMethod httpMethod, String path) {
		super(httpMethod + " " + path + ": resource URL not found [" + parameter + "]");
	}
}
