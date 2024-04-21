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
package org.isf.dicomtype.manager;

import java.util.ArrayList;
import java.util.List;

import org.isf.dicomtype.model.DicomType;
import org.isf.dicomtype.service.DicomTypeIoOperation;
import org.isf.generaldata.MessageBundle;
import org.isf.utils.exception.OHDataIntegrityViolationException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.stereotype.Component;

@Component
public class DicomTypeBrowserManager {

	private DicomTypeIoOperation ioOperations;

	public DicomTypeBrowserManager(DicomTypeIoOperation dicomTypeIoOperation) {
		this.ioOperations = dicomTypeIoOperation;
	}

	public DicomType newDicomType(DicomType dicomType) throws OHServiceException {
		validateDicomType(dicomType, true);
		return ioOperations.newDicomType(dicomType);
	}

	public DicomType updateDicomType(DicomType dicomType) throws OHServiceException {
		validateDicomType(dicomType, false);
		return ioOperations.updateDicomType(dicomType);
	}

	public void deleteDicomType(DicomType dicomType) throws OHServiceException {
		ioOperations.deleteDicomType(dicomType);
	}

	public List<DicomType> getDicomType() throws OHServiceException {
		return ioOperations.getDicomType();
	}

	/**
	 * Verify if the object is valid for CRUD and return a list of errors, if any
	 *
	 * @param dicomType
	 * @param insert {@code true} or updated {@code false}
	 * @throws OHServiceException
	 */
	protected void validateDicomType(DicomType dicomType, boolean insert) throws OHServiceException {
		String key = dicomType.getDicomTypeID();
		String description = dicomType.getDicomTypeDescription();
		List<OHExceptionMessage> errors = new ArrayList<>();
		if (key.isEmpty()) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.pleaseinsertacode.msg")));
		}
		if (key.length() > 3) {
			errors.add(new OHExceptionMessage(MessageBundle.formatMessage("angal.common.thecodeistoolongmaxchars.fmt.msg", 3)));
		}
		if (description.isEmpty()) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.pleaseinsertavaliddescription.msg")));
		}
		if (insert && isCodePresent(dicomType.getDicomTypeID())) {
			throw new OHDataIntegrityViolationException(new OHExceptionMessage(MessageBundle.getMessage("angal.common.thecodeisalreadyinuse.msg")));
		}
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}

	public boolean isCodePresent(String code) throws OHServiceException {
		return ioOperations.isCodePresent(code);
	}

}
