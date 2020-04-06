package org.isf.lab.service;


import org.isf.admission.service.*;
import java.util.List;


public interface LabIoOperationRepositoryCustom {

	List<Object[]> findAllBySearch(String searchTerms);
	
}
