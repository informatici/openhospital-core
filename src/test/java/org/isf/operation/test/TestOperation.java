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
package org.isf.operation.test;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.operation.model.Operation;
import org.isf.opetype.model.OperationType;
import org.isf.utils.exception.OHException;

public class TestOperation {

	private String code = "ZZ";
	private String description = "TestDescription";
	private Integer major = 99;

	public Operation setup(OperationType operationType,
			boolean usingSet) throws OHException {
		Operation operation;

		if (usingSet) {
			operation = new Operation();
			setParameters(operation, operationType);
		} else {
			// Create Operation with all parameters 
			operation = new Operation(code, description, operationType, major);
		}

		return operation;
	}

	public void setParameters(Operation operation,
			OperationType operationType) {
		operation.setCode(code);
		operation.setDescription(description);
		operation.setType(operationType);
		operation.setMajor(major);
	}

	public void check(Operation operation) {
		assertThat(operation.getCode()).isEqualTo(code);
		assertThat(operation.getDescription()).isEqualTo(description);
	}
}
