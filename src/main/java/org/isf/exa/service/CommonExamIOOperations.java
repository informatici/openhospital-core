package org.isf.exa.service;

import org.isf.exa.model.ExamRow;
import org.isf.exatype.model.ExamType;
import org.isf.exatype.service.ExamTypeIoOperationRepository;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class CommonExamIOOperations {

	@Autowired
	private ExamRowIoOperationRepository rowRepository;

	@Autowired
	private ExamTypeIoOperationRepository typeRepository;

	/**
	 * Returns the list of {@link ExamType}s
	 * @return the list of {@link ExamType}s
	 * @throws OHServiceException
	 */
	public List<ExamType> getExamType() throws OHServiceException {
		return typeRepository.findAllByOrderByDescriptionAsc();
	}

	/**
	 * Checks if the code is already in use
	 *
	 * @param code - the exam row code
	 * @return <code>true</code> if the code is already in use, <code>false</code> otherwise
	 * @throws OHServiceException
	 */
	public boolean isRowPresent(Integer code) throws OHServiceException {
		return rowRepository.existsById(code);
	}

	/**
	 * Delete an {@link ExamRow}.
	 * @param examRow - the {@link ExamRow} to delete
	 * @return <code>true</code> if the {@link ExamRow} has been deleted, <code>false</code> otherwise
	 * @throws OHServiceException
	 */
	public boolean deleteExamRow(ExamRow examRow) throws OHServiceException {
		rowRepository.deleteById(examRow.getCode());
		return true;
	}
}
