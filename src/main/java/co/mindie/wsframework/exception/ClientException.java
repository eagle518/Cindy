package co.mindie.wsframework.exception;

/**
 * Created by simoncorsin on 18/08/14.
 */
public class ClientException extends WSFrameworkException {

	public ClientException(String msg) {
		super(msg);
	}

	public ClientException(String msg, Exception e) {
		super(msg, e);
	}

}
