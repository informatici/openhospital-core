package org.isf.medicalsinventory.test;

import org.isf.medicalinventory.model.MedicalInventory;
import org.isf.medicalinventory.model.MedicalInventoryRow;
import org.isf.medicals.model.Medical;
import org.isf.medicalstock.model.Lot;
import org.isf.utils.exception.OHException;

public class TestMedicalInventoryRow {

	private Integer id = 1;
	private double theoreticQty = 50.0;
	private double realqty = 50.0;
	private double unitPrice = 150;
	
	public MedicalInventoryRow setup(MedicalInventory inventory, Medical medical, Lot lot, boolean usingSet) throws OHException {
		MedicalInventoryRow medInventoryRow;
		if (usingSet) {
			medInventoryRow = new MedicalInventoryRow();
			setParameters(medInventoryRow);
		} else {
			// Create MedicalInventoryRow with all parameters 
			medInventoryRow = new MedicalInventoryRow(id, theoreticQty, realqty, inventory, medical, lot, unitPrice);
		}
		return medInventoryRow;
	}
	
	public void setParameters(MedicalInventoryRow medInventoryRow) {
		medInventoryRow.setId(id);
		medInventoryRow.setTheoreticQty(theoreticQty);
		medInventoryRow.setRealqty(realqty);
		medInventoryRow.setUnitPrice(unitPrice);
	}
	
	
}
