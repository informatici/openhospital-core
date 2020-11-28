package org.isf.generaldata;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Ignore;
import org.junit.Test;

public class GeneralDataTest {

	@Test
	public void testGetGeneralData() {
		GeneralData generalData =  GeneralData.getGeneralData();
		
		assertThat(generalData).isNotNull();
		
		assertThat(GeneralData.LANGUAGE).isEqualTo("es");

	}

}
