package org.isf.utils.exception;

import org.isf.utils.exception.model.OHExceptionMessage;

public class OHDBConnectionException extends OHServiceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OHDBConnectionException(OHExceptionMessage message) {
		super(message);
	}

	public OHDBConnectionException(Throwable cause,
			OHExceptionMessage message) {
		super(cause, message);
	}

}
