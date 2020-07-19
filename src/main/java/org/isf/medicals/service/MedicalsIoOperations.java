/**
 * 11-dec-2005
 * 14-jan-2006
 * author bob
 */
package org.isf.medicals.service;

import org.isf.medicals.model.Medical;
import org.isf.medicalstock.model.Movement;
import org.isf.medicalstock.service.MovementIoOperationRepository;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;


/**
 * This class offers the io operations for recovering and managing
 * medical records from the database
 * 
 * @author bob 
 * 		   modified by alex:
 * 			- column product code
 * 			- column pieces per packet
 */
@Service
@Transactional(rollbackFor=OHServiceException.class)
@TranslateOHServiceException
public class MedicalsIoOperations 
{
	@Autowired
	private MedicalsIoOperationRepository repository;
	@Autowired	
	private MovementIoOperationRepository moveRepository;
	
	/**
	 * Retrieves the specified {@link Medical}.
	 * @param code the medical code
	 * @return the stored medical.
	 * @throws OHServiceException if an error occurs retrieving the stored medical.
	 */
	public Medical getMedical(
			int code) throws OHServiceException 
	{
		return repository.findOne(code);
	}

	/**
	 * Gets all stored {@link Medical}s.
	 * @return all the stored medicals.
	 * @throws OHServiceException if an error occurs retrieving the stored medicals.
	 */
	public ArrayList<Medical> getMedicals() throws OHServiceException 
	{
		return getMedicals(null, false);
	}

	/**
	 * Retrieves all stored {@link Medical}s.
	 * If a description value is provides the medicals are filtered.
	 * @param description the medical description.
	 * @return the stored medicals.
	 * @throws OHServiceException if an error occurs retrieving the stored medicals.
	 */
	public ArrayList<Medical> getMedicals(
			String description) throws OHServiceException 
	{
    	ArrayList<Medical> medicals;
    	
    	
    	if (description!=null) 
    	{
    		medicals = (ArrayList<Medical>)repository.findAllWhereDescriptionOrderByDescription(description);
		}
		else
		{
			medicals = (ArrayList<Medical>)repository.findAllByOrderByDescription();
		}
		
		return medicals;
	}
	
	/**
	 * Retrieves all stored {@link Medical}s.
	 * If a description value is provided the medicals are filtered.
	 * @param type the medical type description.
	 * @nameSorted if <code>true</code> return the list in alphabetical order, by code otherwise
	 * @return the stored medicals.
	 * @throws OHServiceException if an error occurs retrieving the stored medicals.
	 */
	public ArrayList<Medical> getMedicals(String type, boolean nameSorted) throws OHServiceException {
		
		if (type != null) {
			return getMedicalsByType(type, nameSorted);
		} else {
			return getMedicals(nameSorted);
		}

	}


	/**
	 * Retrieves the stored {@link Medical}s based on the specified filter criteria.
	 * @param description the medical description or <code>null</code>
	 * @param type the medical type or <code>null</code>
	 * @param critical <code>true</code> if include only medicals under critical level.
	 * @return the retrieved medicals.
	 * @throws OHServiceException if an error occurs retrieving the medicals.
	 */
	public ArrayList<Medical> getMedicals(
			String description, 
			String type, 
			boolean critical) throws OHServiceException 
	{
		ArrayList<Medical> medicals = null;
		
	
		if (description != null) 
		{
			if(type!=null)
			{
				if(critical)
				{
					medicals = (ArrayList<Medical>)repository.findAllWhereDescriptionAndTypeAndCriticalOrderByTypeAndDescription(description, type);
				}
				else
				{
					medicals = (ArrayList<Medical>)repository.findAllWhereDescriptionAndTypeOrderByTypeAndDescription(description, type);
				}
			}
			else
			{
				if(critical)
				{
					medicals = (ArrayList<Medical>)repository.findAllWhereDescriptionAndCriticalOrderByTypeAndDescription(description);
				}
				else
				{
					medicals = (ArrayList<Medical>)repository.findAllWhereDescriptionOrderByTypeAndDescription(description);
				}				
			}
		}
		else
		{
			if(type!=null)
			{
				if(critical)
				{
					medicals = (ArrayList<Medical>)repository.findAllWhereTypeAndCriticalOrderByTypeAndDescription(type);
				}
				else
				{
					medicals = (ArrayList<Medical>)repository.findAllWhereTypeOrderByTypeAndDescription(type);
				}
			}
			else
			{
				if(critical)
				{
					medicals = (ArrayList<Medical>)repository.findAllWhereCriticalOrderByTypeAndDescription();
				}
				else
				{
					medicals = (ArrayList<Medical>)repository.findAllByOrderByTypeAndDescription();
				}				
			}			
		}  

		return medicals;
	}
	
	/**
	 * Checks if the specified {@link Medical} exists or not.
	 * @param medical - the medical to check.
	 * @param update - if <code>true</code> excludes the actual {@link Medical}
	 * @return all {@link Medical} with similar description
	 * @throws OHServiceException if an SQL error occurs during the check.
	 */
	public ArrayList<Medical> medicalCheck(Medical medical, boolean update) throws OHServiceException
	{
		ArrayList<Medical> medicals = null;
		
		if (update) {
			medicals = (ArrayList<Medical>)repository.findAllWhereDescriptionSoundsLike(medical.getDescription(), medical.getCode());
		} else {
			medicals = (ArrayList<Medical>)repository.findAllWhereDescriptionSoundsLike(medical.getDescription()); 
		}

		return medicals;
	}
	
	/**
	 * Checks if the specified {@link Medical} ProductCode exists or not.
	 * @param medical - the medical to check.
	 * @param update - if <code>true</code> excludes the actual {@link Medical}
	 * @return <code>true</code> if exists, <code>false</code> otherwise.
	 * @throws OHServiceException if an SQL error occurs during the check.
	 */
	public boolean productCodeExists(Medical medical, boolean update) throws OHServiceException
	{
		boolean result = false;

		
		Medical foundMedical = null;
		
		if (update) {
			foundMedical = repository.findOneWhereProductCode(medical.getProd_code(), medical.getCode());
		} else {
			foundMedical = repository.findOneWhereProductCode(medical.getProd_code()); 
		}
		if (foundMedical != null) 
		{
			result = true;
		}
		
		return result;
	}
    

	/**
	 * Checks if the specified {@link Medical} exists or not.
	 * @param medical the medical to check.
	 * @param update - if <code>true</code> exclude the current medical itself from search
	 * @return <code>true</code> if exists <code>false</code> otherwise.
	 * @throws OHServiceException if an error occurs during the check.
	 */
	public boolean medicalExists(Medical medical, boolean update) throws OHServiceException 
	{
		boolean result = false;

		
		Medical foundMedical = null;
		
		if (update) {
			foundMedical = repository.findOneWhereDescriptionAndType(medical.getDescription(), medical.getType().getCode(), medical.getCode());
		} else {
			foundMedical = repository.findOneWhereDescriptionAndType(medical.getDescription(), medical.getType().getCode()); 
		}
		if (foundMedical != null) 
		{
			result = true;
		}
		
		return result;
	}
	
	/**
	 * Stores the specified {@link Medical}.
	 * @param medical the medical to store.
	 * @return <code>true</code> if the medical has been stored, <code>false</code> otherwise.
	 * @throws OHServiceException if an error occurs storing the medical.
	 */
	public boolean newMedical(Medical medical) throws OHServiceException 
	{
		boolean result = true;
		

		Medical savedMedical = repository.save(medical);
		result = (savedMedical != null);

		return result;
	}

	/**
	 * Updates the specified {@link Medical}.
	 * @param medical the medical to update.
	 * @return <code>true</code> if the medical has been updated <code>false</code> otherwise.
	 * @throws OHServiceException if an error occurs during the update.
	 */
	public boolean updateMedical(Medical medical) throws OHServiceException 
	{
		boolean result = true;
		

		Medical savedMedical = repository.save(medical);
		result = (savedMedical != null);

		return result;
	}

	/**
	 * Checks if the specified {@link Medical} is referenced in stock movement.
	 * @param code the medical code.
	 * @return <code>true</code> if the medical is referenced, <code>false</code> otherwise.
	 * @throws OHServiceException if an error occurs during the check.
	 */
	public boolean isMedicalReferencedInStockMovement(
			int code) throws OHServiceException 
	{
		boolean result = false;

		
		Movement foundMovement = moveRepository.findAllByMedicalCode(code);
		if (foundMovement != null) 
		{
			result = true;
		}
		
		return result;
	}

	/**
	 * Deletes the specified {@link Medical}.
	 * @param medical the medical to delete.
	 * @return <code>true</code> if the medical has been deleted, <code>false</code> otherwise.
	 * @throws OHServiceException if an error occurs during the medical deletion.
	 */
	public boolean deleteMedical(
			Medical medical) throws OHServiceException
	{
		boolean result = true;
		
		
		repository.delete(medical);

		return result;
	}

	/**
	 * Retrieves all stored medicals, sorted by description or smart code.
	 * @param nameSorted if true the found medicals are sorted by description, otherwise sorted by
	 *                      prod_code and description.
	 * @return sorted List of medicals or empty list if none found.
	 * @throws OHServiceException
	 */
	private ArrayList<Medical> getMedicals(boolean nameSorted) throws OHServiceException {
		if (nameSorted) {
			return getMedicals(null);
		} else {
			return repository.findAllOrderBySmartCodeAndDescription();
		}
	}

	/**
	 * Retrieves all stored medicals by a given type, sorted by description or smart code.
	 * @param type the type the found medicals should have.
	 * @param nameSorted if true the found medicals are sorted by description, otherwise sorted by
	 *                      prod_code and description.
	 * @return sorted List of medicals or empty list if none found.
	 * @throws OHServiceException
	 */
	private ArrayList<Medical> getMedicalsByType(String type, boolean nameSorted) {
		if (nameSorted) {
			return repository.findAllWhereTypeOrderByDescription(type);
		} else {
			return repository.findAllWhereTypeOrderBySmartCodeAndDescription(type);
		}
	}

}
