package co.mindie.wsframework.exception;

/**
 * Created by simoncorsin on 18/08/14.
 */
public class BadParameterException extends ClientException {

	public BadParameterException(String parameter) {
		super("Invalid/Missing parameter [" + parameter + "]");
	}

}
