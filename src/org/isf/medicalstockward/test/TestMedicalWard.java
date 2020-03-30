package org.isf.medicalstockward.test;

import static org.junit.Assert.assertEquals;

import org.isf.medicals.model.Medical;
import org.isf.medicalstock.model.Lot;
import org.isf.medicalstockward.model.MedicalWard;
import org.isf.utils.exception.OHException;
import org.isf.ward.model.Ward;

public class TestMedicalWard 
{	 
	private float in_quantity = (float)100.100;
	private float out_quantity = (float)30.30;
    
			
	public MedicalWard setup(
			Medical medical,
			Ward ward,
			Lot lot,
			boolean usingSet) throws OHException 
	{
		MedicalWard medicalward;
	
				
		if (usingSet)
		{
			medicalward = new MedicalWard();
			_setParameters(medicalward, medical, ward, lot);
		}
		else
		{
			// Create MedicalWard with all parameters 
			medicalward = new MedicalWard(ward, medical, in_quantity, out_quantity, lot);
		}
				    	
		return medicalward;
	}
	
	public void _setParameters(
			MedicalWard medicalward,
			Medical medical,
			Ward ward, Lot lot) 
	{	
		medicalward.setMedical(medical);
		medicalward.setWard(ward);
		medicalward.setInQuantity(in_quantity);
		medicalward.setOutQuantity(out_quantity);
		medicalward.setLot(lot);
		
		return;
	}
	
	public void check(
			MedicalWard medicalward) 
	{		
    	assertEquals(in_quantity, medicalward.getInQuantity(), 0.1);
    	assertEquals(out_quantity, medicalward.getOutQuantity(), 0.1);
		
		return;
	}
}
