package org.isf.utils.exception;

import java.util.List;

import org.isf.utils.exception.model.OHExceptionMessage;

public class OHDataValidationException extends OHServiceException {

	public OHDataValidationException(List<OHExceptionMessage> messages) {
		super(messages);
	}
	
	public OHDataValidationException(OHExceptionMessage message) {
		super(message);
	}

}
