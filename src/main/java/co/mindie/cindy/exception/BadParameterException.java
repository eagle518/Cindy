package co.mindie.cindy.exception;

import co.mindie.cindy.automapping.HttpMethod;

public class BadParameterException extends ClientException {

	public BadParameterException(String parameter) {
		super("Invalid/Missing parameter [" + parameter + "]");
	}

	public BadParameterException(String parameter, HttpMethod httpMethod, String path) {
		super(httpMethod + " " + path + ": Invalid/Missing parameter [" + parameter + "]");
	}

	public BadParameterException(String parameter, Exception e) {
		super("Invalid/Missing parameter [" + parameter + "]", e);
	}
}
