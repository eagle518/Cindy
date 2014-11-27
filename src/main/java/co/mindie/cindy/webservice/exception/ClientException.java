package co.mindie.cindy.webservice.exception;

import co.mindie.cindy.core.exception.CindyException;

/**
 * Created by simoncorsin on 18/08/14.
 */
public class ClientException extends CindyException {

	public ClientException(String msg) {
		super(msg);
	}

	public ClientException(String msg, Exception e) {
		super(msg, e);
	}

}
