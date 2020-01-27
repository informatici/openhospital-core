package org.isf.utils.exception;

import java.util.List;

import org.isf.utils.exception.model.OHExceptionMessage;

public class OHServiceOperationNotAllowedException extends OHServiceException {

	public OHServiceOperationNotAllowedException(OHExceptionMessage message) {
		super(message);
	}

	public OHServiceOperationNotAllowedException(List<OHExceptionMessage> messages) {
		super(messages);
	}

}
