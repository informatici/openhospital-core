/**
 * 
 */
package org.isf.supplier.service;

import java.util.ArrayList;
import java.util.List;

import org.isf.generaldata.ExaminationParameters;
import org.isf.supplier.model.Supplier;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Mwithi
 * 
 */
@Service
@Transactional(rollbackFor=OHServiceException.class)
@TranslateOHServiceException
public class SupplierOperations {

	@Autowired
	private SupplierIoOperationRepository repository;
	
	public SupplierOperations() {
		ExaminationParameters.getExaminationParameters();
	}

	/**
	 * Save or Update a {@link Supplier}
	 * @param supplier - the {@link Supplier} to save or update
	 * return <code>true</code> if data has been saved, <code>false</code> otherwise. 
	 * @throws OHServiceException 
	 */
	public boolean saveOrUpdate(Supplier supplier) throws OHServiceException {
		return repository.save(supplier) != null;
	}

	/**
	 * Returns a {@link Supplier} with specified ID
	 * @param ID - supplier ID
	 * @return supplier - the supplier with specified ID
	 * @throws OHServiceException 
	 */
	public Supplier getByID(int ID) throws OHServiceException {
		return repository.findOne(ID);
	}
	
	/**
	 * Returns the list of all {@link Supplier}s, active and inactive
	 * @return supList - the list of {@link Supplier}s
	 * @throws OHServiceException 
	 */
	public List<Supplier> getAll() throws OHServiceException {
		return repository.findAll();
	}

	/**
	 * Returns the list of active {@link Supplier}s
	 * @return supList - the list of {@link Supplier}s
	 * @throws OHServiceException 
	 */
	public List<Supplier> getList() throws OHServiceException {
		return repository.findAllWhereNotDeleted();
	}
}
