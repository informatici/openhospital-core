/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2020 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
 *
 * Open Hospital is a free and open source software for healthcare data management.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isf.medtype.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import org.isf.OHCoreTestCase;
import org.isf.medtype.model.MedicalType;
import org.isf.medtype.service.MedicalTypeIoOperation;
import org.isf.medtype.service.MedicalTypeIoOperationRepository;
import org.isf.utils.exception.OHServiceException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.test.util.ReflectionTestUtils;

public class TestsExceptionsAndNull extends OHCoreTestCase {

	@Autowired
	MedicalTypeIoOperation medicalTypeIoOperation;

	@Mock
	MedicalTypeIoOperationRepository medicalTypeIoOperationRepositoryMock;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		ReflectionTestUtils.setField(medicalTypeIoOperation, "repository", medicalTypeIoOperationRepositoryMock);
	}

	@Test
	public void testIoUpdateMedicalType_when_OHServiceException() {
		//see CrudRepository.save() potential exceptions https://stackoverflow.com/a/28650987/833336
		String msg = "testing exception message";
		when(medicalTypeIoOperationRepositoryMock.save(any(MedicalType.class)))
		//.thenThrow(new OHServiceException(new OHExceptionMessage("title", "message", OHSeverityLevel.ERROR)));
		//.thenThrow(new Exception("message"));
		//.thenThrow(new DataAccessException("message"));
		//.thenThrow(new NonTransientDataAccessException("testing message"));
		//.thenThrow(new SQLNonTransientException("testing message"));
		//.thenThrow(new TransientDataAccessException("testing message"));
		//.thenThrow(new SQLTransientException("testing message"));
		.thenThrow(new RecoverableDataAccessException(msg));
				
		MedicalType foundMedicalType = new MedicalType();
		//foundMedicalType.setDescription("Update");
		assertThatThrownBy( () ->
		{
			medicalTypeIoOperation.updateMedicalType(foundMedicalType);
			fail("Missed OHServiceException");
		})
			.isInstanceOf(OHServiceException.class);
		
//		try {
//			medicalTypeIoOperation.updateMedicalType(foundMedicalType);
//			fail("Missed OHServiceException");
//		} catch (OHServiceException e) {
//			assertThat(e.getMessage()).contains(msg);
//			assertThat(e.getMessages().get(0).getLevel()).isEqualTo(OHSeverityLevel.ERROR);
//			//			e.getMessages().forEach( m -> {
//			//				System.out.println(m.getTitle());
//			//				System.out.println(m.getMessage());
//			//				System.out.println(m.getLevel());
//			//			});
//		} catch (Exception e) {
//			fail("Missed OHServiceException");
//		}
	}
	
	@Test
	public void testIoUpdateMedicalType_when_null() throws Exception {
		MockitoAnnotations.initMocks(this);
		when(medicalTypeIoOperationRepositoryMock.save(any(MedicalType.class)))
		.thenReturn(null);
				
		MedicalType foundMedicalType = new MedicalType();
		foundMedicalType.setCode("test");
		foundMedicalType.setDescription("Update");
		boolean result = medicalTypeIoOperation.updateMedicalType(foundMedicalType);
		assertThat(result).isFalse();

	}

}