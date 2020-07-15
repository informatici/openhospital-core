package org.isf.utils.exception;

import java.util.List;

import org.isf.utils.exception.model.OHExceptionMessage;

public class OHOperationNotAllowedException extends OHServiceException {

	public OHOperationNotAllowedException(OHExceptionMessage message) {
		super(message);
	}

	public OHOperationNotAllowedException(List<OHExceptionMessage> messages) {
		super(messages);
	}

}
