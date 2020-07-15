package org.isf.utils.exception;

import org.isf.utils.exception.model.OHExceptionMessage;

public class OHReportException extends OHServiceException {

	public OHReportException(Throwable cause,
			OHExceptionMessage message) {
		super(cause, message);
	}

}
