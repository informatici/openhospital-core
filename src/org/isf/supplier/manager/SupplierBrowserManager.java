package org.isf.supplier.manager;

import java.util.HashMap;
import java.util.List;

import org.isf.supplier.model.Supplier;
import org.isf.supplier.service.SupplierOperations;
import org.isf.utils.exception.OHServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SupplierBrowserManager {

    private final Logger logger = LoggerFactory.getLogger(SupplierBrowserManager.class);
    @Autowired
    private SupplierOperations ioOperations;

    public boolean saveOrUpdate(Supplier supplier) throws OHServiceException {
        return ioOperations.saveOrUpdate(supplier);
    }

    public Supplier getByID(int ID) throws OHServiceException {
        return ioOperations.getByID(ID);
    }

    public List<Supplier> getAll() throws OHServiceException {
        return ioOperations.getAll();
    }

    public List<Supplier> getList() throws OHServiceException {
        return ioOperations.getList();
    }
    
    /**
	 * returns the {@link HashMap} of all {@link Supplier}s 
	 * @param all - if <code>true</code> it will returns deleted ones also
	 * @return the {@link HashMap} of all {@link Supplier}s  
     * @throws OHServiceException 
	 */
	public HashMap<Integer, String> getHashMap(boolean all) throws OHServiceException {
		List<Supplier> supList = getAll();
		HashMap<Integer, String> supMap = new HashMap<Integer, String>();
		for (Supplier sup : supList) {
			supMap.put(sup.getSupId(), sup.getSupName());
		}
		return supMap;
	}
}

