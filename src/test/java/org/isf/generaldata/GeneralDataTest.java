package org.isf.generaldata;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

public class GeneralDataTest {

	@Ignore
	@Test
	public void testGetGeneralData() {
		GeneralData generalData =  GeneralData.getGeneralData();
		
		assertNotNull(generalData);
		
		assertEquals("es",GeneralData.LANGUAGE);
		
		generalData.printAllProperties();
	}

}
