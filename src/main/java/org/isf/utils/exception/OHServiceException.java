/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2021 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isf.utils.exception;

import java.util.ArrayList;
import java.util.List;

import org.isf.utils.exception.model.OHExceptionMessage;

/**
 * OH service layer exception. Can encapsulate messages to show in UI.
 *
 * @author akashytsa
 */
public class OHServiceException extends Exception {

	private static final long serialVersionUID = 1L;
	
	private List<OHExceptionMessage> messages = new ArrayList<>();
	
	public OHServiceException(Throwable cause, List<OHExceptionMessage> messages) {
		super(cause);
		this.messages = messages;
	}
	
	public OHServiceException(OHExceptionMessage message) {
		this.messages.add(message);
	}
	
	public OHServiceException(List<OHExceptionMessage> messages) {
		this.messages = messages;
	}
	
	public OHServiceException(Throwable cause, OHExceptionMessage message) {
		super(cause);
		this.messages.add(message);
	}

	public List<OHExceptionMessage> getMessages() {
		return messages;
	}
}
