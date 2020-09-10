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
package org.isf.admission.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.isf.admission.model.Admission;
import org.isf.admission.model.AdmittedPatient;
import org.isf.admission.service.AdmissionIoOperations;
import org.isf.admtype.model.AdmissionType;
import org.isf.admtype.test.TestAdmissionType;
import org.isf.admtype.test.TestAdmissionTypeContext;
import org.isf.disctype.model.DischargeType;
import org.isf.disctype.test.TestDischargeType;
import org.isf.disctype.test.TestDischargeTypeContext;
import org.isf.disease.model.Disease;
import org.isf.disease.test.TestDisease;
import org.isf.disease.test.TestDiseaseContext;
import org.isf.distype.model.DiseaseType;
import org.isf.distype.test.TestDiseaseType;
import org.isf.distype.test.TestDiseaseTypeContext;
import org.isf.dlvrrestype.model.DeliveryResultType;
import org.isf.dlvrrestype.test.TestDeliveryResultType;
import org.isf.dlvrrestype.test.TestDeliveryResultTypeContext;
import org.isf.dlvrtype.model.DeliveryType;
import org.isf.dlvrtype.test.TestDeliveryType;
import org.isf.dlvrtype.test.TestDeliveryTypeContext;
import org.isf.operation.model.Operation;
import org.isf.operation.test.TestOperation;
import org.isf.operation.test.TestOperationContext;
import org.isf.opetype.model.OperationType;
import org.isf.opetype.test.TestOperationType;
import org.isf.opetype.test.TestOperationTypeContext;
import org.isf.patient.model.Patient;
import org.isf.patient.test.TestPatient;
import org.isf.patient.test.TestPatientContext;
import org.isf.pregtreattype.model.PregnantTreatmentType;
import org.isf.pregtreattype.test.TestPregnantTreatmentType;
import org.isf.pregtreattype.test.TestPregnantTreatmentTypeContext;
import org.isf.utils.db.DbJpaUtil;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.isf.ward.model.Ward;
import org.isf.ward.test.TestWard;
import org.isf.ward.test.TestWardContext;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class Tests  
{
	private static DbJpaUtil jpa;
	private static TestAdmission testAdmission;
	private static TestAdmissionContext testAdmissionContext;
	private static TestWard testWard;
	private static TestWardContext testWardContext;
	private static TestPatient testPatient;
	private static TestPatientContext testPatientContext;
	private static TestAdmissionType testAdmissionType;
	private static TestAdmissionTypeContext testAdmissionTypeContext;
	private static TestDiseaseType testDiseaseType;
	private static TestDiseaseTypeContext testDiseaseTypeContext;
	private static TestDisease testDisease;
	private static TestDiseaseContext testDiseaseContext;
	private static TestOperationType testOperationType;
	private static TestOperationTypeContext testOperationTypeContext;
	private static TestOperation testOperation;
	private static TestOperationContext testOperationContext;
	private static TestDischargeType testDischargeType;
	private static TestDischargeTypeContext testDischargeTypeContext;
	private static TestPregnantTreatmentType testPregnantTreatmentType;
	private static TestPregnantTreatmentTypeContext testPregnantTreatmentTypeContext;
	private static TestDeliveryType testDeliveryType;
	private static TestDeliveryTypeContext testDeliveryTypeContext;
	private static TestDeliveryResultType testDeliveryResultType;
	private static TestDeliveryResultTypeContext testDeliveryResultTypeContext;

    @Autowired
    AdmissionIoOperations admissionIoOperation;

	@BeforeClass
    public static void setUpClass()  
    {
    	jpa = new DbJpaUtil();
    	testAdmission = new TestAdmission();
    	testAdmissionContext = new TestAdmissionContext();
    	testWard = new TestWard();
    	testWardContext = new TestWardContext();
    	testPatient = new TestPatient();
    	testPatientContext = new TestPatientContext();
    	testAdmissionType = new TestAdmissionType();
    	testAdmissionTypeContext = new TestAdmissionTypeContext();
    	testDiseaseType = new TestDiseaseType();
    	testDiseaseTypeContext = new TestDiseaseTypeContext();
    	testDisease = new TestDisease();
    	testDiseaseContext = new TestDiseaseContext();
    	testOperationType = new TestOperationType();
    	testOperationTypeContext = new TestOperationTypeContext();
    	testOperation = new TestOperation();
    	testOperationContext = new TestOperationContext();
    	testDischargeType = new TestDischargeType();
    	testDischargeTypeContext = new TestDischargeTypeContext();
    	testPregnantTreatmentType = new TestPregnantTreatmentType();
    	testPregnantTreatmentTypeContext = new TestPregnantTreatmentTypeContext();
    	testDeliveryType = new TestDeliveryType();
    	testDeliveryTypeContext = new TestDeliveryTypeContext();
    	testDeliveryResultType = new TestDeliveryResultType();
    	testDeliveryResultTypeContext = new TestDeliveryResultTypeContext();
    }

    @Before
    public void setUp() throws OHException
    {
        jpa.open();
        
        _saveContext();
    }
        
    @After
    public void tearDown() throws Exception 
    {
        _restoreContext();   
        
        jpa.flush();
        jpa.close();
    }
    
    @AfterClass
    public static void tearDownClass() throws OHException 
    {
    	testAdmission = null;
    	testAdmissionContext = null;
    	testWard = null;
    	testWardContext = null;
    	testPatient = null;
    	testPatientContext = null;
    	testAdmissionType = null;
    	testAdmissionTypeContext = null;
    	testDiseaseType = null;
    	testDiseaseTypeContext = null;
    	testDisease = null;
    	testDiseaseContext = null;
    	testOperationType = null;
    	testOperationTypeContext = null;
    	testOperation = null;
    	testOperationContext = null;
    	testDischargeType = null;
    	testDischargeTypeContext = null;
    	testPregnantTreatmentType = null;
    	testPregnantTreatmentTypeContext = null;
    	testDeliveryType = null;
    	testDeliveryTypeContext = null;
    	testDeliveryResultType = null;
    	testDeliveryResultTypeContext = null;
    }
	
		
	@Test
	public void testAdmissionGets()
	{
		int id = 0;
			
		
		try 
		{		
			id = _setupTestAdmission(false);
			_checkAdmissionIntoDb(id);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testAdmissionSets()
	{
		int id = 0;
			

		try 
		{		
			id = _setupTestAdmission(true);
			_checkAdmissionIntoDb(id);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}

	@Test
	public void test_simple_getAdmittedPatients() throws Exception {
		int id = _setupTestAdmission(false);
		Admission foundAdmission = (Admission)jpa.find(Admission.class, id);
		List<AdmittedPatient> patients = admissionIoOperation.getAdmittedPatients();
		List<AdmittedPatient> searchResult = admissionIoOperation.getAdmittedPatients(null);

		assertThat(searchResult).hasSameSizeAs(patients);
		assertThat(patients.get(0).getAdmission().getId()).isEqualTo(foundAdmission.getId());
	}

	@Test
	public void test_getAdmittedPatient_with_dateRanges() throws Exception {
		int id = _setupTestAdmission(false);
		Admission foundAdmission = (Admission)jpa.find(Admission.class, id);
		List<AdmittedPatient> patients = admissionIoOperation.getAdmittedPatients();
		final GregorianCalendar admissionDate = foundAdmission.getAdmDate();
		final GregorianCalendar dischargeDate = foundAdmission.getDisDate();
		{
			List<AdmittedPatient> searchResult = admissionIoOperation.getAdmittedPatients(null, null, null);
			assertThat(searchResult).hasSameSizeAs(patients);
			assertThat(patients.get(0).getAdmission().getId()).isEqualTo(foundAdmission.getId());
		}
		final GregorianCalendar beforeAdmissionDate = copyFrom(admissionDate);
		beforeAdmissionDate.add(Calendar.DATE, -1);

		final GregorianCalendar oneDayAfterAdmissionDate = copyFrom(admissionDate);
		oneDayAfterAdmissionDate.add(Calendar.DATE, 1);

		final GregorianCalendar twoDaysAfterAdmissionDate = copyFrom(admissionDate);
		twoDaysAfterAdmissionDate.add(Calendar.DATE, 2);

		final GregorianCalendar beforeDischargeDate = copyFrom(dischargeDate);
		beforeDischargeDate.add(Calendar.DATE, -1);

		final GregorianCalendar oneDayAfterDischargeDate = copyFrom(dischargeDate);
		oneDayAfterDischargeDate.add(Calendar.DATE, 1);

		final GregorianCalendar twoDaysAfterDischargeDate = copyFrom(dischargeDate);
		twoDaysAfterDischargeDate.add(Calendar.DATE, 2);
		{
			// search by admission date
			final List<AdmittedPatient> searchOneresult = admissionIoOperation.getAdmittedPatients(null,
					new GregorianCalendar[]{beforeAdmissionDate, oneDayAfterAdmissionDate},
					null
			);
			assertThat(searchOneresult).hasSameSizeAs(patients);
			assertThat(patients.get(0).getAdmission().getId()).isEqualTo(foundAdmission.getId());

			final List<AdmittedPatient> searchTwoResult = admissionIoOperation.getAdmittedPatients(null,
					new GregorianCalendar[]{oneDayAfterAdmissionDate, twoDaysAfterAdmissionDate},
					null
			);
			assertThat(searchTwoResult).isEmpty();
		}
		{
			// search by discharge date
			final List<AdmittedPatient> searchOneresult = admissionIoOperation.getAdmittedPatients(null, null,
					new GregorianCalendar[]{beforeDischargeDate, oneDayAfterDischargeDate}
			);
			assertThat(searchOneresult).hasSameSizeAs(patients);
			assertThat(patients.get(0).getAdmission().getId()).isEqualTo(foundAdmission.getId());

			final List<AdmittedPatient> searchTwoResult = admissionIoOperation.getAdmittedPatients(null, null,
					new GregorianCalendar[]{oneDayAfterDischargeDate, twoDaysAfterDischargeDate}
			);
			assertThat(searchTwoResult).isEmpty();
		}
		{
			// complex search by both admission and discharge date
			final List<AdmittedPatient> searchOneresult = admissionIoOperation.getAdmittedPatients(null,
					new GregorianCalendar[]{beforeAdmissionDate, oneDayAfterAdmissionDate},
					new GregorianCalendar[]{beforeDischargeDate, oneDayAfterDischargeDate}
			);
			assertThat(searchOneresult).hasSameSizeAs(patients);
			assertThat(patients.get(0).getAdmission().getId()).isEqualTo(foundAdmission.getId());
		}
	}

	private GregorianCalendar copyFrom(final GregorianCalendar source) {
		return new GregorianCalendar(source.get(Calendar.YEAR), source.get(Calendar.MONTH), source.get(Calendar.DATE));
	}

	@Test
	public void testIoGetAdmittedPatients() throws OHException, InterruptedException, OHServiceException {
		int id = _setupTestAdmission(false);
		Admission foundAdmission = (Admission)jpa.find(Admission.class, id);
		List<AdmittedPatient> patients = admissionIoOperation.getAdmittedPatients();
		List<AdmittedPatient> patientsNull = admissionIoOperation.getAdmittedPatients(null);

		assertThat(patientsNull).hasSameSizeAs(patients);
		assertThat(patients.get(0).getAdmission().getId()).isEqualTo(foundAdmission.getId());
	}

	@Test
	public void testIoGetAdmittedPatientsShouldFindByOneOfFieldsLikeFirstName() throws OHException, InterruptedException, OHServiceException {
		// given:
		int id = _setupTestAdmission(false);
		Admission foundAdmission = (Admission)jpa.find(Admission.class, id);
		Patient foundPatient = foundAdmission.getPatient();

		// when:
		List<AdmittedPatient> patients = admissionIoOperation.getAdmittedPatients(foundPatient.getFirstName());

		// then:
		assertThat(patients.get(0).getAdmission().getId()).isEqualTo(foundAdmission.getId());
	}

	@Test
	public void testIoGetAdmittedPatientsShouldFindByOneOfFieldsLikeLastName() throws OHException, InterruptedException, OHServiceException {
		// given:
		int id = _setupTestAdmission(false);
		Admission foundAdmission = (Admission)jpa.find(Admission.class, id);
		Patient foundPatient = foundAdmission.getPatient();

		// when:
		List<AdmittedPatient> patients = admissionIoOperation.getAdmittedPatients(foundPatient.getName());

		// then:
		assertThat(patients.get(0).getAdmission().getId()).isEqualTo(foundAdmission.getId());
	}

	@Test
	public void testIoGetAdmittedPatientsShouldFindByOneOfFieldsLikeNote() throws OHException, InterruptedException, OHServiceException {
		// given:
		int id = _setupTestAdmission(false);
		Admission foundAdmission = (Admission)jpa.find(Admission.class, id);
		Patient foundPatient = foundAdmission.getPatient();

		// when:
		List<AdmittedPatient> patients = admissionIoOperation.getAdmittedPatients(foundPatient.getNote());

		// then:
		assertThat(patients.get(0).getAdmission().getId()).isEqualTo(foundAdmission.getId());
	}

	@Test
	public void testIoGetAdmittedPatientsShouldFindByOneOfFieldsLikeTaxCode() throws OHException, InterruptedException, OHServiceException {
		// given:
		int id = _setupTestAdmission(false);
		Admission foundAdmission = (Admission)jpa.find(Admission.class, id);
		Patient foundPatient = foundAdmission.getPatient();

		// when:
		List<AdmittedPatient> patients = admissionIoOperation.getAdmittedPatients(foundPatient.getTaxCode());

		// then:
		assertThat(patients.get(0).getAdmission().getId()).isEqualTo(foundAdmission.getId());
	}

	@Test
	public void testIoGetAdmittedPatientsShouldFindByOneOfFieldsLikeId() throws OHException, InterruptedException, OHServiceException {
		// given:
		int id = _setupTestAdmission(false);
		Admission foundAdmission = (Admission)jpa.find(Admission.class, id);
		Patient foundPatient = foundAdmission.getPatient();

		// when:
		List<AdmittedPatient> patients = admissionIoOperation.getAdmittedPatients(foundPatient.getCode().toString());

		// then:
		assertThat(patients.get(0).getAdmission().getId()).isEqualTo(foundAdmission.getId());
	}

	@Test
	public void testIoGetAdmittedPatientsShouldNotFindAnythingWhenNotExistingWordProvided() throws OHException, InterruptedException, OHServiceException {
		// given:
		int id = _setupTestAdmission(false);
		Admission foundAdmission = (Admission)jpa.find(Admission.class, id);

		// when:
		List<AdmittedPatient> patients = admissionIoOperation.getAdmittedPatients("dupsko");

		// then:
		assertThat(patients).isEmpty();
	}

	@Test
	public void testIoGetAdmittedPatientsShouldNotFindWhenAdmissionOutsideOfDateRange() throws OHException, InterruptedException, OHServiceException {
		// given:
		int id = _setupTestAdmission(false);
		Admission foundAdmission = (Admission)jpa.find(Admission.class, id);
		Patient foundPatient = foundAdmission.getPatient();
		GregorianCalendar[] admissionRange = {
			new DateTime(foundAdmission.getAdmDate()).minusDays(2).toGregorianCalendar(),
			new DateTime(foundAdmission.getAdmDate()).minusDays(1).toGregorianCalendar()
		};
		GregorianCalendar[] dischargeRange = {
			new DateTime(foundAdmission.getDisDate()).minusDays(1).toGregorianCalendar(),
			new DateTime(foundAdmission.getDisDate()).plusDays(1).toGregorianCalendar()
		};

		// when:
		List<AdmittedPatient> patients = admissionIoOperation.getAdmittedPatients(foundPatient.getCode().toString(), admissionRange, dischargeRange);

		// then:
		assertThat(patients).isEmpty();
	}

	@Test
	public void testIoGetAdmittedPatientsShouldNotFindWhenDischargeOutsideOfDateRange() throws OHException, InterruptedException, OHServiceException {
		// given:
		int id = _setupTestAdmission(false);
		Admission foundAdmission = (Admission)jpa.find(Admission.class, id);
		Patient foundPatient = foundAdmission.getPatient();
		GregorianCalendar[] admissionRange = {
			new DateTime(foundAdmission.getAdmDate()).minusDays(1).toGregorianCalendar(),
			new DateTime(foundAdmission.getAdmDate()).plusDays(1).toGregorianCalendar()
		};
		GregorianCalendar[] dischargeRange = {
			new DateTime(foundAdmission.getDisDate()).minusDays(2).toGregorianCalendar(),
			new DateTime(foundAdmission.getDisDate()).minusDays(1).toGregorianCalendar()
		};

		// when:
		List<AdmittedPatient> patients = admissionIoOperation.getAdmittedPatients(foundPatient.getCode().toString(), admissionRange, dischargeRange);

		// then:
		assertThat(patients).isEmpty();
	}


	@Test
	public void testIoGetCurrentAdmission() 
	{
		int id = 0;
		
		
		try 
		{		
			id = _setupTestAdmission(false);
			jpa.beginTransaction();	
			Admission foundAdmission = (Admission)jpa.find(Admission.class, id); 
			foundAdmission.setDisDate(null);
			jpa.merge(foundAdmission);
			jpa.commitTransaction();
			Admission ioAdmission = admissionIoOperation.getCurrentAdmission(foundAdmission.getPatient());
			
			assertThat(ioAdmission.getNote()).isEqualTo(foundAdmission.getNote());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoGetAdmission()
	{
		int id = 0;
		
		
		try 
		{		
			id = _setupTestAdmission(false);
			Admission foundAdmission = admissionIoOperation.getAdmission(id);
			
			testAdmission.check(foundAdmission);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoGetAdmissions() 
	{
		int id = 0;
		
		
		try 
		{		
			id = _setupTestAdmission(false);
			Admission foundAdmission = (Admission)jpa.find(Admission.class, id); 
			List<Admission> admissions = admissionIoOperation.getAdmissions(foundAdmission.getPatient());
			
			assertThat(admissions.get(admissions.size() - 1).getId()).isEqualTo(foundAdmission.getId());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoNewAdmission()
	{
		boolean result = false;

		
		try 
		{		
			Ward ward = testWard.setup(false);
			Patient patient = testPatient.setup(true);
			AdmissionType admissionType = testAdmissionType.setup(false);
			DiseaseType diseaseType = testDiseaseType.setup(false);
			Disease diseaseIn = testDisease.setup(diseaseType, false);
			Disease diseaseOut1 = testDisease.setup(diseaseType, false);
			diseaseOut1.setCode("888");
			Disease diseaseOut2 = testDisease.setup(diseaseType, false);
			diseaseOut2.setCode("777");
			Disease diseaseOut3 = testDisease.setup(diseaseType, false); 
			diseaseOut3.setCode("666");
			OperationType operationType = testOperationType.setup(false);
			Operation operation = testOperation.setup(operationType, false);
			DischargeType dischargeType = testDischargeType.setup(false);
			PregnantTreatmentType pregTreatmentType = testPregnantTreatmentType.setup(false);
			DeliveryType deliveryType = testDeliveryType.setup(false);
			DeliveryResultType deliveryResult = testDeliveryResultType.setup(false);

			
			jpa.beginTransaction();	
			jpa.persist(ward);
			jpa.persist(patient);	
			jpa.persist(admissionType);
			jpa.persist(diseaseType);
			jpa.persist(diseaseIn);
			jpa.persist(diseaseOut1);
			jpa.persist(diseaseOut2);
			jpa.persist(diseaseOut3);
			jpa.persist(operationType);
			jpa.persist(operation);
			jpa.persist(dischargeType);
			jpa.persist(pregTreatmentType);
			jpa.persist(deliveryType);
			jpa.persist(deliveryResult);
			jpa.commitTransaction();
			
			Admission admission = testAdmission.setup(ward, patient, admissionType, diseaseIn, diseaseOut1, 
					diseaseOut2, diseaseOut3, operation, dischargeType, pregTreatmentType, 
					deliveryType, deliveryResult, true);
			result = admissionIoOperation.newAdmission(admission);

			assertThat(result).isTrue();
			_checkAdmissionIntoDb(admission.getId());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}

	@Test
	public void testIoNewAdmissionReturnKey() 
	{
		int id = 0;
		
		
		try 
		{		
			Ward ward = testWard.setup(false);
			Patient patient = testPatient.setup(true);
			AdmissionType admissionType = testAdmissionType.setup(false);
			DiseaseType diseaseType = testDiseaseType.setup(false);
			Disease diseaseIn = testDisease.setup(diseaseType, false);
			Disease diseaseOut1 = testDisease.setup(diseaseType, false);
			diseaseOut1.setCode("888");
			Disease diseaseOut2 = testDisease.setup(diseaseType, false);
			diseaseOut2.setCode("777");
			Disease diseaseOut3 = testDisease.setup(diseaseType, false); 
			diseaseOut3.setCode("666");
			OperationType operationType = testOperationType.setup(false);
			Operation operation = testOperation.setup(operationType, false);
			DischargeType dischargeType = testDischargeType.setup(false);
			PregnantTreatmentType pregTreatmentType = testPregnantTreatmentType.setup(false);
			DeliveryType deliveryType = testDeliveryType.setup(false);
			DeliveryResultType deliveryResult = testDeliveryResultType.setup(false);
			
			
			jpa.beginTransaction();	
			jpa.persist(ward);
			jpa.persist(patient);	
			jpa.persist(admissionType);
			jpa.persist(diseaseType);
			jpa.persist(diseaseIn);
			jpa.persist(diseaseOut1);
			jpa.persist(diseaseOut2);
			jpa.persist(diseaseOut3);
			jpa.persist(operationType);
			jpa.persist(operation);
			jpa.persist(dischargeType);
			jpa.persist(pregTreatmentType);
			jpa.persist(deliveryType);
			jpa.persist(deliveryResult);
			jpa.commitTransaction();
						
			Admission admission = testAdmission.setup(ward, patient, admissionType, diseaseIn, diseaseOut1, 
					diseaseOut2, diseaseOut3, operation, dischargeType, pregTreatmentType, 
					deliveryType, deliveryResult, true);
			id = admissionIoOperation.newAdmissionReturnKey(admission);
					
					_checkAdmissionIntoDb(id);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}

	@Test
	public void testIoUpdateAdmission() 
	{
		int id = 0;
		boolean result = false;
		
		
		try 
		{		
			id = _setupTestAdmission(false);
			Admission foundAdmission = (Admission)jpa.find(Admission.class, id);
			jpa.flush();
			foundAdmission.setNote("Update");
			result = admissionIoOperation.updateAdmission(foundAdmission);
			Admission updateAdmission = (Admission)jpa.find(Admission.class, id);

			assertThat(result).isTrue();
			assertThat(updateAdmission.getNote()).isEqualTo("Update");
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}

	@Test
	public void testIoGetAdmissionType() 
	{
		int id = 0;
		
		
		try 
		{		
			id = _setupTestAdmission(false);
			Admission foundAdmission = (Admission)jpa.find(Admission.class, id); 
			List<AdmissionType> admissionTypes = admissionIoOperation.getAdmissionType();
			
			assertThat(admissionTypes.get(admissionTypes.size() - 1).getDescription()).isEqualTo(foundAdmission.getAdmType().getDescription());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}

	@Test
	public void testIoGetDischargeType()
	{
		int id = 0;
		
		
		try 
		{		
			id = _setupTestAdmission(false);
			Admission foundAdmission = (Admission)jpa.find(Admission.class, id);  
			List<DischargeType> dischargeTypes = admissionIoOperation.getDischargeType();
			
			assertThat(dischargeTypes.get(dischargeTypes.size() - 1).getDescription()).isEqualTo(foundAdmission.getDisType().getDescription());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoGetNextYProg()
	{
		int id = 0;
		int next = 1;
		
		
		try 
		{		
			id = _setupTestAdmission(false);
			Admission foundAdmission = (Admission)jpa.find(Admission.class, id);  
			next = admissionIoOperation.getNextYProg(foundAdmission.getWard().getCode());
			
			assertThat(next).isEqualTo(foundAdmission.getYProg() + 1);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoSetDeleted() 
	{
		int id = 0;
		boolean result = true;
		
		
		try 
		{		
			id = _setupTestAdmission(false);
			Admission foundAdmission = (Admission)jpa.find(Admission.class, id);
			jpa.flush();
			result = admissionIoOperation.setDeleted(foundAdmission.getId());

			assertThat(result).isTrue();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoGetUsedWardBed()
	{
		int id = 0;
		int result = 0;
		
		
		try 
		{		
			id = _setupTestAdmission(false);
			Admission foundAdmission = (Admission)jpa.find(Admission.class, id);  
			result = admissionIoOperation.getUsedWardBed(foundAdmission.getWard().getCode());
			
			assertThat(result).isEqualTo(1);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoDeletePatientPhoto()
	{
		int id = 0;
		boolean result = true;
		
		
		try 
		{		
			id = _setupTestAdmission(false);
			Admission foundAdmission = (Admission)jpa.find(Admission.class, id);  
			result = admissionIoOperation.deletePatientPhoto(foundAdmission.getPatient().getCode());

			assertThat(result).isTrue();
			assertThat(foundAdmission.getPatient().getPatientProfilePhoto().getPhoto()).isNull();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	
	private void _saveContext() throws OHException 
    {	
		testAdmissionContext.saveAll(jpa);
    	testWardContext.saveAll(jpa);
    	testPatientContext.saveAll(jpa);
    	testAdmissionTypeContext.saveAll(jpa);
    	testDiseaseTypeContext.saveAll(jpa);
    	testDiseaseContext.saveAll(jpa);
		testDiseaseContext.addMissingKey(jpa);
    	testOperationTypeContext.saveAll(jpa);
    	testOperationContext.saveAll(jpa);
    	testDischargeTypeContext.saveAll(jpa);
    	testPregnantTreatmentTypeContext.saveAll(jpa);
    	testDeliveryTypeContext.saveAll(jpa);
    	testDeliveryResultTypeContext.saveAll(jpa);
    }
	
    private void _restoreContext() throws OHException 
    {
		testAdmissionContext.deleteNews(jpa);
    	testWardContext.deleteNews(jpa);
    	testPatientContext.deleteNews(jpa);
    	testAdmissionTypeContext.deleteNews(jpa);
    	testDiseaseContext.deleteNews(jpa);
    	testDiseaseTypeContext.deleteNews(jpa);
    	testOperationContext.deleteNews(jpa);
    	testOperationTypeContext.deleteNews(jpa);
    	testDischargeTypeContext.deleteNews(jpa);
    	testPregnantTreatmentTypeContext.deleteNews(jpa);
    	testDeliveryTypeContext.deleteNews(jpa);
    	testDeliveryResultTypeContext.deleteNews(jpa);
    }
        
	private int _setupTestAdmission(
			boolean usingSet) throws OHException, InterruptedException 
	{
		Admission admission;
		Ward ward = testWard.setup(false);
		Patient patient = testPatient.setup(true);
		AdmissionType admissionType = testAdmissionType.setup(false);
		DiseaseType diseaseType = testDiseaseType.setup(false);
		Disease diseaseIn = testDisease.setup(diseaseType, false);
		Disease diseaseOut1 = testDisease.setup(diseaseType, false);
		diseaseOut1.setCode("888");
		Disease diseaseOut2 = testDisease.setup(diseaseType, false);
		diseaseOut2.setCode("777");
		Disease diseaseOut3 = testDisease.setup(diseaseType, false); 
		diseaseOut3.setCode("666");
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, false);
		DischargeType dischargeType = testDischargeType.setup(false);
		PregnantTreatmentType pregTreatmentType = testPregnantTreatmentType.setup(false);
		DeliveryType deliveryType = testDeliveryType.setup(false);
		DeliveryResultType deliveryResult = testDeliveryResultType.setup(false);
		

    	jpa.beginTransaction();	
    	admission = testAdmission.setup(ward, patient, admissionType, diseaseIn, diseaseOut1, 
    			diseaseOut2, diseaseOut3, operation, dischargeType, pregTreatmentType, 
    			deliveryType, deliveryResult, usingSet);
    	jpa.persist(ward);
    	jpa.persist(patient);	
    	jpa.persist(admissionType);
    	jpa.persist(diseaseType);
    	jpa.persist(diseaseIn);
    	jpa.persist(diseaseOut1);
    	jpa.persist(diseaseOut2);
    	jpa.persist(diseaseOut3);
    	jpa.persist(operationType);
    	jpa.persist(operation);
    	jpa.persist(dischargeType);
    	jpa.persist(pregTreatmentType);
    	jpa.persist(deliveryType);
    	jpa.persist(deliveryResult);
    	jpa.persist(admission);
    	jpa.commitTransaction();
    	
		return admission.getId();
	}
		
	private void  _checkAdmissionIntoDb(
			int id) throws OHException 
	{
		Admission foundAdmission;
		

		foundAdmission = (Admission)jpa.find(Admission.class, id); 
		testAdmission.check(foundAdmission);
	}	
}
