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
package org.isf.orthanc.utils;

import org.isf.utils.exception.OHInternalServerException;
import org.isf.utils.exception.OHNotFoundException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.http.HttpStatus;

import feign.Response;
import feign.codec.ErrorDecoder;

/**
 * Custom Error Decoder for Feign Exceptions
 *
 * @author Silevester D.
 * @since v1.16
 */
public class CustomErrorDecoder implements ErrorDecoder {

	private final ErrorDecoder defaultErrorDecoder = new Default();

	@Override
	public Exception decode(String methodKey, Response response) {
		HttpStatus status = HttpStatus.valueOf(response.status());

		if (status.is4xxClientError()) {
			return new OHNotFoundException(new OHExceptionMessage(response.reason()));
		}

		if (status.is5xxServerError()) {
			return new OHInternalServerException(new OHExceptionMessage(response.reason()));
		}

		return defaultErrorDecoder.decode(methodKey, response);
	}
}

