package co.mindie.cindy.exception;

public class ParameterException extends ClientException {

	public ParameterException(String message) {
		super(message);
	}

	public ParameterException(String message, Exception exception) {
		super(message, exception);
	}
}
