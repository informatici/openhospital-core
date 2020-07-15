package org.isf.utils.exception;

import org.isf.utils.exception.model.OHExceptionMessage;

public class OHDataIntegrityViolationException extends OHServiceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OHDataIntegrityViolationException(OHExceptionMessage message) {
		super(message);
	}
	
	public OHDataIntegrityViolationException(Throwable cause,
			OHExceptionMessage message) {
		super(cause, message);
	}

}
