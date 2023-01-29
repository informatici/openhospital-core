/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2023 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.utils.exception.model;

import java.io.Serializable;

/**
 * Composed exception information
 *
 * @author akashytsa
 */
public class OHExceptionMessage implements Serializable{

	private static final long serialVersionUID = 1L;
	private String title;
	private ErrorDescription description;
	private String message;
	private OHSeverityLevel level;
	

	public OHExceptionMessage(String title, String message, OHSeverityLevel level) {
		super();
		this.title = title;
		this.message = message;
		this.level = level;
	}
	public OHExceptionMessage(String title, ErrorDescription description, String message, OHSeverityLevel level) {
		super();
		this.title = title;
		this.description = ErrorDescription.PASSWORD_TOO_SHORT;
		this.message = message;
		this.level = level;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public OHSeverityLevel getLevel() {
		return level;
	}
	public void setLevel(OHSeverityLevel level) {
		this.level = level;
	}
	public ErrorDescription getDescription() {
		return description;
	}
	public void setDescription(ErrorDescription description) {
		this.description = description;
	}
	

}
