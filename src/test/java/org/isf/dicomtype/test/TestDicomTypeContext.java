package org.isf.dicomtype.test;


import java.util.List;

import org.isf.dicom.model.FileDicom;
import org.isf.dicomtype.model.DicomType;
import org.isf.utils.db.DbJpaUtil;
import org.isf.utils.exception.OHException;

public class TestDicomTypeContext 
{		
	private static List<DicomType> savedDicomType;
		
		
	@SuppressWarnings("unchecked")
	public void saveAll(
			DbJpaUtil jpa) throws OHException 
    {	
		jpa.beginTransaction();			
		jpa.createQuery("SELECT * FROM DICOM", FileDicom.class, false);
		savedDicomType = (List<DicomType>)jpa.getList();
		jpa.commitTransaction();
        		
        return;
    }
	
	public List<DicomType> getAllSaved() throws OHException 
    {	        		
        return savedDicomType;
    }
	    
    @SuppressWarnings("unchecked")
    public void deleteNews(
    		DbJpaUtil jpa) throws OHException 
    {
		jpa.beginTransaction();			
		jpa.createQuery("SELECT * FROM DICOMTYPE", DicomType.class, false);
		List<DicomType> dicomTypes = (List<DicomType>)jpa.getList();
		for (DicomType dicomType: dicomTypes) 
		{    		
			int index = savedDicomType.indexOf(dicomType);
			
			
			if (index == -1)
			{				
				jpa.remove(dicomType);
			}
	    }        
		jpa.commitTransaction();
		        
        return;
    } 
}
