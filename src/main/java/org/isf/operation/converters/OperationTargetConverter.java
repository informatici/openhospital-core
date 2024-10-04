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
package org.isf.operation.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import org.isf.operation.enums.OperationTarget;

@Converter
public class OperationTargetConverter implements AttributeConverter<OperationTarget, String> {

	@Override
	public String convertToDatabaseColumn(OperationTarget opFor) {
		return opFor == null ? null : opFor.toString().toLowerCase();
	}

	@Override
	public OperationTarget convertToEntityAttribute(String dbData) {
		return dbData == null ? null : OperationTarget.valueOf(dbData.toUpperCase());
	}
}
