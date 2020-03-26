package org.isf.utils.exception;

import java.util.List;

import org.isf.utils.exception.model.OHExceptionMessage;

public class OHDicomException extends OHServiceException {

	public OHDicomException(List<OHExceptionMessage> messages) {
		super(messages);
	}
	
	public OHDicomException(Throwable cause, OHExceptionMessage message) {
		super(cause, message);
	}
	
	public OHDicomException(OHExceptionMessage message) {
		super(message);
	}
}
