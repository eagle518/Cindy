package co.mindie.cindy.exception;

public class CindyException extends RuntimeException {

	public CindyException(String msg) {
		super(msg);
	}

	public CindyException(String msg, Throwable e) {
		super(msg, e);
	}
}
