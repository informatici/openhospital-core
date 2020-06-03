package org.isf.utils.exception;

import org.isf.utils.exception.model.OHExceptionMessage;

public class OHInvalidSQLException extends OHServiceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OHInvalidSQLException(OHExceptionMessage message) {
		super(message);
	}

	public OHInvalidSQLException(Throwable cause,
			OHExceptionMessage message) {
		super(cause, message);
	}

}
