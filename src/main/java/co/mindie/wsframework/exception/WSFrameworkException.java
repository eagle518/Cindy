package co.mindie.wsframework.exception;

public class WSFrameworkException extends RuntimeException {
	public WSFrameworkException(String msg) {
		super(msg);
	}

	public WSFrameworkException(String msg, Exception e) {
		super(msg, e);
	}
}
