package org.isf.utils.exception;

import org.isf.utils.exception.model.OHExceptionMessage;

public class OHDataLockFailureException extends OHServiceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OHDataLockFailureException(OHExceptionMessage message) {
		super(message);
	}

	public OHDataLockFailureException(Throwable cause,
			OHExceptionMessage message) {
		super(cause, message);
	}

}
