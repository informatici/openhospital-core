package org.isf.dicomtype.manager;

import java.util.ArrayList;

import org.isf.dicomtype.model.DicomType;
import org.isf.dicomtype.service.DicomTypeIoOperation;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DicomTypeBrowserManager {
	
	@Autowired
	private DicomTypeIoOperation ioOperations;

	public boolean newDicomType(DicomType dicomType) throws OHServiceException {
		return ioOperations.newDicomType(dicomType);
	}

	public boolean updateDicomType(DicomType dicomType) throws OHServiceException {
		return ioOperations.updateDicomType(dicomType);
	}

	public boolean deleteDicomType(DicomType dicomType) throws OHServiceException {
		return ioOperations.deleteDicomType(dicomType);
	}

	public ArrayList<DicomType> getDicomType() throws OHServiceException {
		return ioOperations.getDicomType();
	}

}
