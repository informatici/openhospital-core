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
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.isf.utils.tuple;

import java.util.List;

public final class JPAImmutableTriple {

	private boolean result;
	private Object object;
	private List<String> errors;

	/**
	 * Create a new JPA immutable triple instance.
	 *
	 * @param result the Boolean result of the action.
	 * @param object the object of the action.
	 * @param errors the {@code List<String>} of formatted error messages.
	 */
	public JPAImmutableTriple(boolean result, Object object, List<String> errors) {
		this.result = result;
		this.object = object;
		this.errors = errors;
	}

	/**
	 * @return {@code true} if successful, otherwise {@code false}.
	 */
	public boolean getResult() {
		return result;
	}

	/**
	 * @return the object associated with the operation.
	 */
	public Object getObject() {
		return object;
	}

	/**
	 * @return {@code List<String>} of formatted message text
	 */
	public List<String> getErrors() {
		return errors;
	}

}
