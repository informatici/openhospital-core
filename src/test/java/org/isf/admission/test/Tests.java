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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.isf.OHCoreIntegrationTest;
import org.isf.admission.model.Admission;
import org.isf.admission.model.AdmittedPatient;
import org.isf.admission.service.AdmissionIoOperationRepository;
import org.isf.admission.service.AdmissionIoOperations;
import org.isf.admtype.model.AdmissionType;
import org.isf.admtype.service.AdmissionTypeIoOperationRepository;
import org.isf.admtype.test.TestAdmissionType;
import org.isf.disctype.model.DischargeType;
import org.isf.disctype.service.DischargeTypeIoOperationRepository;
import org.isf.disctype.test.TestDischargeType;
import org.isf.disease.model.Disease;
import org.isf.disease.service.DiseaseIoOperationRepository;
import org.isf.disease.test.TestDisease;
import org.isf.distype.model.DiseaseType;
import org.isf.distype.service.DiseaseTypeIoOperationRepository;
import org.isf.distype.test.TestDiseaseType;
import org.isf.dlvrrestype.model.DeliveryResultType;
import org.isf.dlvrrestype.service.DeliveryResultIoOperationRepository;
import org.isf.dlvrrestype.test.TestDeliveryResultType;
import org.isf.dlvrtype.model.DeliveryType;
import org.isf.dlvrtype.service.DeliveryTypeIoOperationRepository;
import org.isf.dlvrtype.test.TestDeliveryType;
import org.isf.operation.model.Operation;
import org.isf.operation.service.OperationIoOperationRepository;
import org.isf.operation.test.TestOperation;
import org.isf.opetype.model.OperationType;
import org.isf.opetype.service.OperationTypeIoOperationRepository;
import org.isf.opetype.test.TestOperationType;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.patient.test.TestPatient;
import org.isf.pregtreattype.model.PregnantTreatmentType;
import org.isf.pregtreattype.service.PregnantTreatmentTypeIoOperationRepository;
import org.isf.pregtreattype.test.TestPregnantTreatmentType;
import org.isf.utils.db.DbJpaUtil;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.isf.ward.model.Ward;
import org.isf.ward.service.WardIoOperationRepository;
import org.isf.ward.test.TestWard;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;


public class Tests extends OHCoreIntegrationTest
{
	private static TestAdmission testAdmission;
	private static TestWard testWard;
	private static TestPatient testPatient;
	private static TestAdmissionType testAdmissionType;
	private static TestDiseaseType testDiseaseType;
	private static TestDisease testDisease;
	private static TestOperationType testOperationType;
	private static TestOperation testOperation;
	private static TestDischargeType testDischargeType;
	private static TestPregnantTreatmentType testPregnantTreatmentType;
	private static TestDeliveryType testDeliveryType;
	private static TestDeliveryResultType testDeliveryResultType;

    @Autowired
    AdmissionIoOperations admissionIoOperation;
    @Autowired
	AdmissionIoOperationRepository admissionIoOperationRepository;
    @Autowired
    WardIoOperationRepository wardIoOperationRepository;
    @Autowired
    PatientIoOperationRepository patientIoOperationRepository;
    @Autowired
	AdmissionTypeIoOperationRepository admissionTypeIoOperationRepository;
    @Autowired
    DiseaseTypeIoOperationRepository diseaseTypeIoOperationRepository;
    @Autowired
    DiseaseIoOperationRepository diseaseIoOperationRepository;
    @Autowired
    OperationTypeIoOperationRepository operationTypeIoOperationRepository;
    @Autowired
    OperationIoOperationRepository operationIoOperationRepository;
    @Autowired
    DischargeTypeIoOperationRepository dischargeTypeIoOperationRepository;
    @Autowired
	PregnantTreatmentTypeIoOperationRepository pregnantTreatmentTypeIoOperationRepository;
    @Autowired
    DeliveryTypeIoOperationRepository deliveryTypeIoOperationRepository;
    @Autowired
    DeliveryResultIoOperationRepository deliveryResultIoOperationRepository;

    static DbJpaUtil jpa;

	@BeforeClass
    public static void setUpClass()  
    {

		jpa = new DbJpaUtil();
    	testAdmission = new TestAdmission();
    	testWard = new TestWard();
    	testPatient = new TestPatient();
    	testAdmissionType = new TestAdmissionType();
    	testDiseaseType = new TestDiseaseType();
    	testDisease = new TestDisease();
    	testOperationType = new TestOperationType();
    	testOperation = new TestOperation();
    	testDischargeType = new TestDischargeType();
    	testPregnantTreatmentType = new TestPregnantTreatmentType();
    	testDeliveryType = new TestDeliveryType();
    	testDeliveryResultType = new TestDeliveryResultType();
    }

    @Before
    public void setUp() throws OHException {
		cleanH2InMemoryDb();
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
				
		return;
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
		
		return;
	}

	@Test
	public void test_simple_getAdmittedPatients() throws Exception {
		int id = _setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperationRepository.getOne(id);
		List<AdmittedPatient> patients = admissionIoOperation.getAdmittedPatients();
		List<AdmittedPatient> searchResult = admissionIoOperation.getAdmittedPatients(null);

		assertEquals(patients.size(), searchResult.size());
		assertEquals(foundAdmission.getId(), patients.get(0).getAdmission().getId());
	}

	@Test
	public void test_getAdmittedPatient_with_dateRanges() throws Exception {
		int id = _setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperationRepository.getOne(id);
		List<AdmittedPatient> patients = admissionIoOperation.getAdmittedPatients();
		final GregorianCalendar admissionDate = foundAdmission.getAdmDate();
		final GregorianCalendar dischargeDate = foundAdmission.getDisDate();
		{
			List<AdmittedPatient> searchResult = admissionIoOperation.getAdmittedPatients(null, null, null);
			assertEquals(patients.size(), searchResult.size());
			assertEquals(foundAdmission.getId(), patients.get(0).getAdmission().getId());
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
			assertEquals(patients.size(), searchOneresult.size());
			assertEquals(foundAdmission.getId(), patients.get(0).getAdmission().getId());

			final List<AdmittedPatient> searchTwoResult = admissionIoOperation.getAdmittedPatients(null,
					new GregorianCalendar[]{oneDayAfterAdmissionDate, twoDaysAfterAdmissionDate},
					null
			);
			assertEquals(0, searchTwoResult.size());
		}
		{
			// search by discharge date
			final List<AdmittedPatient> searchOneresult = admissionIoOperation.getAdmittedPatients(null, null,
					new GregorianCalendar[]{beforeDischargeDate, oneDayAfterDischargeDate}
			);
			assertEquals(patients.size(), searchOneresult.size());
			assertEquals(foundAdmission.getId(), patients.get(0).getAdmission().getId());

			final List<AdmittedPatient> searchTwoResult = admissionIoOperation.getAdmittedPatients(null, null,
					new GregorianCalendar[]{oneDayAfterDischargeDate, twoDaysAfterDischargeDate}
			);
			assertEquals(0, searchTwoResult.size());
		}
		{
			// complex search by both admission and discharge date
			final List<AdmittedPatient> searchOneresult = admissionIoOperation.getAdmittedPatients(null,
					new GregorianCalendar[]{beforeAdmissionDate, oneDayAfterAdmissionDate},
					new GregorianCalendar[]{beforeDischargeDate, oneDayAfterDischargeDate}
			);
			assertEquals(patients.size(), searchOneresult.size());
			assertEquals(foundAdmission.getId(), patients.get(0).getAdmission().getId());
		}
	}

	private GregorianCalendar copyFrom(final GregorianCalendar source) {
		return new GregorianCalendar(source.get(Calendar.YEAR), source.get(Calendar.MONTH), source.get(Calendar.DATE));
	}

	@Test
	public void testIoGetAdmittedPatients() throws OHException, InterruptedException, OHServiceException {
		int id = _setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperationRepository.getOne(id);
		List<AdmittedPatient> patients = admissionIoOperation.getAdmittedPatients();
		List<AdmittedPatient> patientsNull = admissionIoOperation.getAdmittedPatients(null);

		assertEquals(patients.size(), patientsNull.size());
		assertEquals(foundAdmission.getId(), patients.get(0).getAdmission().getId());
	}

	@Test
	public void testIoGetAdmittedPatientsShouldFindByOneOfFieldsLikeFirstName() throws OHException, InterruptedException, OHServiceException {
		// given:
		int id = _setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperationRepository.getOne(id);
		Patient foundPatient = foundAdmission.getPatient();

		// when:
		List<AdmittedPatient> patients = admissionIoOperation.getAdmittedPatients(foundPatient.getFirstName());

		// then:
		assertEquals(foundAdmission.getId(), patients.get(0).getAdmission().getId());
	}

	@Test
	public void testIoGetAdmittedPatientsShouldFindByOneOfFieldsLikeLastName() throws OHException, InterruptedException, OHServiceException {
		// given:
		int id = _setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperationRepository.getOne(id);
		Patient foundPatient = foundAdmission.getPatient();

		// when:
		List<AdmittedPatient> patients = admissionIoOperation.getAdmittedPatients(foundPatient.getName());

		// then:
		assertEquals(foundAdmission.getId(), patients.get(0).getAdmission().getId());
	}

	@Test
	public void testIoGetAdmittedPatientsShouldFindByOneOfFieldsLikeNote() throws OHException, InterruptedException, OHServiceException {
		// given:
		int id = _setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperationRepository.getOne(id);
		Patient foundPatient = foundAdmission.getPatient();

		// when:
		List<AdmittedPatient> patients = admissionIoOperation.getAdmittedPatients(foundPatient.getNote());

		// then:
		assertEquals(foundAdmission.getId(), patients.get(0).getAdmission().getId());
	}

	@Test
	@Transactional
	public void testIoGetAdmittedPatientsShouldFindByOneOfFieldsLikeTaxCode() throws OHException, InterruptedException, OHServiceException {
		// given:
		int id = _setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperationRepository.getOne(id);
		Patient foundPatient = foundAdmission.getPatient();

		// when:
		List<AdmittedPatient> patients = admissionIoOperation.getAdmittedPatients(foundPatient.getTaxCode());

		// then:
		assertEquals(foundAdmission.getId(), patients.get(0).getAdmission().getId());
	}

	@Test
	public void testIoGetAdmittedPatientsShouldFindByOneOfFieldsLikeId() throws OHException, InterruptedException, OHServiceException {
		// given:
		int id = _setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperationRepository.getOne(id);
		Patient foundPatient = foundAdmission.getPatient();

		// when:
		List<AdmittedPatient> patients = admissionIoOperation.getAdmittedPatients(foundPatient.getCode().toString());

		// then:
		assertEquals(foundAdmission.getId(), patients.get(0).getAdmission().getId());
	}

	@Test
	public void testIoGetAdmittedPatientsShouldNotFindAnythingWhenNotExistingWordProvided() throws OHException, InterruptedException, OHServiceException {
		// given:
		int id = _setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperationRepository.getOne(id);

		// when:
		List<AdmittedPatient> patients = admissionIoOperation.getAdmittedPatients("dupsko");

		// then:
		assertTrue(patients.isEmpty());
	}

	@Test
	public void testIoGetAdmittedPatientsShouldNotFindWhenAdmissionOutsideOfDateRange() throws OHException, InterruptedException, OHServiceException {
		// given:
		int id = _setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperationRepository.getOne(id);
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
		assertTrue(patients.isEmpty());
	}

	@Test
	public void testIoGetAdmittedPatientsShouldNotFindWhenDischargeOutsideOfDateRange() throws OHException, InterruptedException, OHServiceException {
		// given:
		int id = _setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperationRepository.getOne(id);
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
		assertTrue(patients.isEmpty());
	}


	@Test
	public void testIoGetCurrentAdmission() 
	{
		int id = 0;
		
		
		try 
		{		
			id = _setupTestAdmission(false);
			Admission foundAdmission = admissionIoOperationRepository.getOne(id);
			foundAdmission.setDisDate(null);
			admissionIoOperationRepository.save(foundAdmission);
			Admission ioAdmission = admissionIoOperation.getCurrentAdmission(foundAdmission.getPatient());
			
			assertEquals(foundAdmission.getNote(), ioAdmission.getNote());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	
		return;
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
	
		return;
	}
	
	@Test
	public void testIoGetAdmissions() 
	{
		int id = 0;
		
		
		try 
		{		
			id = _setupTestAdmission(false);
			Admission foundAdmission = admissionIoOperationRepository.getOne(id); 
			List<Admission> admissions = admissionIoOperation.getAdmissions(foundAdmission.getPatient());
			
			assertEquals(foundAdmission.getId(), admissions.get(admissions.size()-1).getId());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	
		return;
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

			wardIoOperationRepository.save(ward);
			patientIoOperationRepository.save(patient);
			admissionTypeIoOperationRepository.save(admissionType);
			diseaseTypeIoOperationRepository.save(diseaseType);
			diseaseIoOperationRepository.save(diseaseIn);
			diseaseIoOperationRepository.save(diseaseOut1);
			diseaseIoOperationRepository.save(diseaseOut2);
			diseaseIoOperationRepository.save(diseaseOut3);
			operationTypeIoOperationRepository.save(operationType);
			operationIoOperationRepository.save(operation);
			dischargeTypeIoOperationRepository.save(dischargeType);
			pregnantTreatmentTypeIoOperationRepository.save(pregTreatmentType);
			deliveryTypeIoOperationRepository.save(deliveryType);
			deliveryResultIoOperationRepository.save(deliveryResult);

			Admission admission = testAdmission.setup(ward, patient, admissionType, diseaseIn, diseaseOut1, 
					diseaseOut2, diseaseOut3, operation, dischargeType, pregTreatmentType, 
					deliveryType, deliveryResult, true);
			result = admissionIoOperation.newAdmission(admission);

			assertTrue(result);
			_checkAdmissionIntoDb(admission.getId());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
		
		return;
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

			wardIoOperationRepository.save(ward);
			patientIoOperationRepository.save(patient);
			admissionTypeIoOperationRepository.save(admissionType);
			diseaseTypeIoOperationRepository.save(diseaseType);
			diseaseIoOperationRepository.save(diseaseIn);
			diseaseIoOperationRepository.save(diseaseOut1);
			diseaseIoOperationRepository.save(diseaseOut2);
			diseaseIoOperationRepository.save(diseaseOut3);
			operationTypeIoOperationRepository.save(operationType);
			operationIoOperationRepository.save(operation);
			dischargeTypeIoOperationRepository.save(dischargeType);
			pregnantTreatmentTypeIoOperationRepository.save(pregTreatmentType);
			deliveryTypeIoOperationRepository.save(deliveryType);
			deliveryResultIoOperationRepository.save(deliveryResult);

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
		
		return;
	}

	@Test
	public void testIoUpdateAdmission() 
	{
		int id = 0;
		boolean result = false;
		
		
		try 
		{		
			id = _setupTestAdmission(false);
			Admission foundAdmission = admissionIoOperationRepository.getOne(id);
			foundAdmission.setNote("Update");
			result = admissionIoOperation.updateAdmission(foundAdmission);
			Admission updateAdmission = admissionIoOperationRepository.getOne(id);

			assertTrue(result);
			assertEquals("Update", updateAdmission.getNote());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
		
		return;
	}

	@Test
	public void testIoGetAdmissionType() 
	{
		int id = 0;
		
		
		try 
		{		
			id = _setupTestAdmission(false);
			Admission foundAdmission = admissionIoOperationRepository.getOne(id); 
			List<AdmissionType> admissionTypes = admissionIoOperation.getAdmissionType();
			
			assertEquals(foundAdmission.getAdmType().getDescription(), admissionTypes.get(admissionTypes.size()-1).getDescription());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
		
		return;
	}

	@Test
	public void testIoGetDischargeType()
	{
		int id = 0;
		
		
		try 
		{		
			id = _setupTestAdmission(false);
			Admission foundAdmission = admissionIoOperationRepository.getOne(id);  
			List<DischargeType> dischargeTypes = admissionIoOperation.getDischargeType();
			
			assertEquals(foundAdmission.getDisType().getDescription(), dischargeTypes.get(dischargeTypes.size()-1).getDescription());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
		
		return;
	}
	
	@Test
	public void testIoGetNextYProg()
	{
		int id = 0;
		int next = 1;
		
		
		try 
		{		
			id = _setupTestAdmission(false);
			Admission foundAdmission = admissionIoOperationRepository.getOne(id);  
			next = admissionIoOperation.getNextYProg(foundAdmission.getWard().getCode());
			
			assertEquals(foundAdmission.getYProg() + 1, next);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	
		return;
	}
	
	@Test
	public void testIoSetDeleted() 
	{
		int id = 0;
		boolean result = true;
		
		
		try 
		{		
			id = _setupTestAdmission(false);
			Admission foundAdmission = admissionIoOperationRepository.getOne(id);
			result = admissionIoOperation.setDeleted(foundAdmission.getId());

			assertTrue(result);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}

		return;
	}
	
	@Test
	public void testIoGetUsedWardBed()
	{
		int id = 0;
		int result = 0;
		
		
		try 
		{		
			id = _setupTestAdmission(false);
			Admission foundAdmission = admissionIoOperationRepository.getOne(id);  
			result = admissionIoOperation.getUsedWardBed(foundAdmission.getWard().getCode());
			
			assertEquals(1, result);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}

		return;
	}
	
	@Test
	public void testIoDeletePatientPhoto()
	{
		int id = 0;
		boolean result = true;
		
		
		try 
		{		
			id = _setupTestAdmission(false);
			Admission foundAdmission = admissionIoOperationRepository.getOne(id);  
			result = admissionIoOperation.deletePatientPhoto(foundAdmission.getPatient().getCode());

			assertTrue(result);
			assertNull(foundAdmission.getPatient().getPatientProfilePhoto().getPhoto());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}

		return;
	}

	private int _setupTestAdmission(
			boolean usingSet) throws OHException, InterruptedException 
	{
		Admission admission;
		Ward ward = testWard.setup(false);
		Patient patient = testPatient.setup(true);
		AdmissionType admissionType = testAdmissionType.setup(false);
		DiseaseType diseaseType = diseaseTypeIoOperationRepository.save(testDiseaseType.setup(true));
		Disease diseaseIn = testDisease.setup(diseaseType, true);
		Disease diseaseOut1 = testDisease.setup(diseaseType, true);
		diseaseOut1.setCode("888");
		Disease diseaseOut2 = testDisease.setup(diseaseType, true);
		diseaseOut2.setCode("777");
		Disease diseaseOut3 = testDisease.setup(diseaseType, true);
		diseaseOut3.setCode("666");
		OperationType operationType = operationTypeIoOperationRepository.save(testOperationType.setup(false));
		Operation operation = testOperation.setup(operationType, false);
		DischargeType dischargeType = testDischargeType.setup(false);
		PregnantTreatmentType pregTreatmentType = testPregnantTreatmentType.setup(false);
		DeliveryType deliveryType = testDeliveryType.setup(false);
		DeliveryResultType deliveryResult = testDeliveryResultType.setup(false);
		

    	admission = testAdmission.setup(ward, patient, admissionType, diseaseIn, diseaseOut1,
    			diseaseOut2, diseaseOut3, operation, dischargeType, pregTreatmentType, 
    			deliveryType, deliveryResult, usingSet);
    	wardIoOperationRepository.save(ward);
    	patientIoOperationRepository.save(patient);
    	admissionTypeIoOperationRepository.save(admissionType);
    	diseaseTypeIoOperationRepository.save(diseaseType);
    	diseaseIoOperationRepository.save(diseaseIn);
		diseaseIoOperationRepository.save(diseaseOut1);
		diseaseIoOperationRepository.save(diseaseOut2);
		diseaseIoOperationRepository.save(diseaseOut3);
    	operationIoOperationRepository.save(operation);
    	dischargeTypeIoOperationRepository.save(dischargeType);
    	pregnantTreatmentTypeIoOperationRepository.save(pregTreatmentType);
    	deliveryTypeIoOperationRepository.save(deliveryType);
    	deliveryResultIoOperationRepository.save(deliveryResult);
    	admissionIoOperationRepository.save(admission);

		return admission.getId();
	}
		
	private void  _checkAdmissionIntoDb(
			int id) throws OHException 
	{
		Admission foundAdmission;
		

		foundAdmission = admissionIoOperationRepository.getOne(id);
		testAdmission.check(foundAdmission);
		
		return;
	}	
}
