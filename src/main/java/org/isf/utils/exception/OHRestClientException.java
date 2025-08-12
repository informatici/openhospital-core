/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2024 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
 *
 * Open Hospital is a free and open source software for healthcare data management.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.isf.utils.exception;

import org.isf.utils.exception.model.OHExceptionMessage;

/**
 * Base Exception for exceptions thrown by REST clients
 * <p>
 *     This exception class extends {@link RuntimeException} rather than {@link OHServiceException}
 *     because REST clients like Feign proxies their client interfaces. So if custom exceptions
 *     are not runtime exceptions, they cannot handle them correctly.
 * </p>
 *
 * @author Silevester D.
 * @since v1.16
 */
public class OHRestClientException extends RuntimeException {
	private OHExceptionMessage message;

	public OHRestClientException(OHExceptionMessage message) {
		super(message.getMessage());
		this.message = message;
	}

	public OHRestClientException(Throwable cause, OHExceptionMessage message) {
		super(message.getMessage(), cause);
		this.message = message;
	}

	public void setMessage(OHExceptionMessage message) {
		this.message = message;
	}

	public OHExceptionMessage getExceptionMessage() {
		return message;
	}
}
