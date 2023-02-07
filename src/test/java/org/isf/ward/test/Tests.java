/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2023 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.ward.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import org.assertj.core.api.Condition;
import org.isf.OHCoreTestCase;
import org.isf.admission.model.Admission;
import org.isf.admission.service.AdmissionIoOperationRepository;
import org.isf.admtype.model.AdmissionType;
import org.isf.admtype.service.AdmissionTypeIoOperationRepository;
import org.isf.utils.exception.OHDataIntegrityViolationException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.time.TimeTools;
import org.isf.ward.manager.WardBrowserManager;
import org.isf.ward.model.Ward;
import org.isf.ward.service.WardIoOperationRepository;
import org.isf.ward.service.WardIoOperations;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class Tests extends OHCoreTestCase {

	private static TestWard testWard;

	@Autowired
	WardIoOperations wardIoOperation;
	@Autowired
	WardIoOperationRepository wardIoOperationRepository;
	@Autowired
	WardBrowserManager wardBrowserManager;
	@Autowired
	AdmissionIoOperationRepository admissionIoOperationRepository;
	@Autowired
	AdmissionTypeIoOperationRepository admissionTypeIoOperationRepository;

	@BeforeClass
	public static void setUpClass() {
		testWard = new TestWard();
	}

	@Before
	public void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	public void testWardGets() throws Exception {
		String code = setupTestWard(false);
		checkWardIntoDb(code);
	}

	@Test
	public void testWardSets() throws Exception {
		String code = setupTestWard(true);
		checkWardIntoDb(code);
	}

	@Test
	public void testIoGetCurrentOccupationNoAdmissions() throws Exception {
		String code = setupTestWard(false);
		Ward ward = wardIoOperationRepository.findById(code).get();
		assertThat(wardIoOperation.getCurrentOccupation(ward)).isZero();
	}

	@Test
	public void testIoGetCurrentOccupation() throws Exception {
		String code = setupTestWard(false);
		Ward ward = wardIoOperationRepository.findById(code).get();
		LocalDateTime admDate = TimeTools.getNow();
		AdmissionType admissionType = new AdmissionType("ZZ", "TestDescription");
		Admission admission1 = new Admission(0, 1, "N", ward, 0, null, admDate, admissionType,
				"TestFHU", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
				"TestUserId", 'N');
		Admission admission2 = new Admission(0, 1, "N", ward, 0, null, admDate, admissionType,
				"TestFHU", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
				"TestUserId", 'N');
		admissionTypeIoOperationRepository.saveAndFlush(admissionType);
		admissionIoOperationRepository.saveAndFlush(admission1);
		admissionIoOperationRepository.saveAndFlush(admission2);
		assertThat(wardIoOperation.getCurrentOccupation(ward)).isEqualTo(2);
	}

	@Test
	public void testIoGetWardsNoMaternity() throws Exception {
		// given:
		String code = setupTestWard(false);
		Ward foundWard = wardIoOperationRepository.findById(code).get();

		// when:
		List<Ward> wards = wardIoOperation.getWardsNoMaternity();

		// then:
		assertThat(wards.get(wards.size() - 1).getDescription()).isEqualTo(foundWard.getDescription());
	}

	@Test
	public void testIoGetWards() throws Exception {
		// given:
		String code = setupTestWard(false);
		Ward foundWard = wardIoOperationRepository.findById(code).get();

		// when:
		List<Ward> wards = wardIoOperation.getWards(code);

		// then:
		assertThat(wards.get(0).getDescription()).isEqualTo(foundWard.getDescription());
	}
	
	@Test
	public void testIoGetIpdWards() throws Exception {
		// given:
		String code = setupTestWard(false);
		Ward foundWard = wardIoOperationRepository.findById(code).get();
		
		// when:
		List<Ward> wards = wardIoOperation.getIpdWards();
		
		// then:
		assertThat(wards.size() == 1);
		
		// given:
		foundWard.setBeds(0);
		wardIoOperationRepository.saveAndFlush(foundWard);
		
		// when:
		wards = wardIoOperation.getIpdWards();
		
		// then:
		assertThat(wards.size() == 0);
	}

	@Test
	public void testIoGetOpdWards() throws Exception {
		// given:
		String code = setupTestWard(false);
		Ward foundWard = wardIoOperationRepository.findById(code).get();
		
		// when:
		List<Ward> wards = wardIoOperation.getOpdWards();
		
		// then:
		assertThat(wards.size() == 0);
		
		// given:
		foundWard.setBeds(0);
		wardIoOperationRepository.saveAndFlush(foundWard);
		
		// when:
		wards = wardIoOperation.getOpdWards();
		
		// then:
		assertThat(wards.size() == 1);
	}
	
	@Test
	public void testIoGetWardsGetAll() throws Exception {
		String code = setupTestWard(false);
		Ward ward = wardIoOperationRepository.findById(code).get();
		List<Ward> wards = wardIoOperation.getWards(null);
		assertThat(wards).hasSize(1);
		assertThat(wards.get(0).getDescription()).isEqualTo(ward.getDescription());
	}

	@Test
	public void testIoNewWard() throws Exception {
		Ward ward = testWard.setup(true);
		Ward newWard = wardIoOperation.newWard(ward);

		assertThat(newWard.getDescription()).isEqualTo("TestDescription");
		checkWardIntoDb(ward.getCode());
	}

	@Test
	public void testIoUpdateWard() throws Exception {
		// given:
		String code = setupTestWard(false);
		Ward foundWard = wardIoOperationRepository.findById(code).get();
		foundWard.setDescription("Update");

		// when:
		wardIoOperation.updateWard(foundWard);
		Ward updateWard = wardIoOperationRepository.findById(code).get();

		// then:
		assertThat(updateWard.getDescription()).isEqualTo("Update");
	}

	@Test
	public void testIoUpdateWardNoCodePresent() throws Exception {
		Ward ward = testWard.setup(true);
		ward.setCode("X");
		Ward updateWard = wardIoOperation.updateWard(ward);
		assertThat(updateWard.getCode()).isEqualTo("X");
	}

	@Test
	public void testIoDeleteWard() throws Exception {
		// given:
		String code = setupTestWard(false);
		Ward foundWard = wardIoOperationRepository.findById(code).get();

		// when:
		boolean result = wardIoOperation.deleteWard(foundWard);

		// then:
		assertThat(result).isTrue();
		result = wardIoOperation.isCodePresent(code);
		assertThat(result).isFalse();
	}

	@Test
	public void testIoIsCodePresent() throws Exception {
		String code = setupTestWard(false);
		boolean result = wardIoOperation.isCodePresent(code);
		assertThat(result).isTrue();
	}

	@Test
	public void testIoIsCodePresentFalse() throws Exception {
		boolean result = wardIoOperation.isCodePresent("X");
		assertThat(result).isFalse();
	}

	@Test
	public void testIoIsMaternityPresent() throws Exception {
		boolean result = wardIoOperation.isMaternityPresent();

		if (!result) {
			Ward ward = testWard.setup(false);
			ward.setCode("M");
			wardIoOperationRepository.saveAndFlush(ward);
			result = wardIoOperation.isMaternityPresent();
		}

		assertThat(result).isTrue();
	}

	@Test
	public void testIoIsMaternityOpdPresent() throws Exception {
		boolean result = wardIoOperation.isOpdPresent();

		if (!result) {
			Ward ward = testWard.setup(false);
			ward.setCode("OPD");
			wardIoOperationRepository.saveAndFlush(ward);
			result = wardIoOperation.isOpdPresent();
		}

		assertThat(result).isTrue();
	}
	
	@Test
	public void testIoFindWard() throws Exception {
		String code = setupTestWard(false);
		Ward result = wardIoOperation.findWard(code);
		assertThat(result).isNotNull();
		assertThat(result.getCode()).isEqualTo(code);
	}

	@Test
	public void testIoFindWardNull() throws Exception {
		assertThatThrownBy(() -> wardIoOperation.findWard(null))
				.isInstanceOf(OHServiceException.class);
	}

	@Test
	public void testMgrGetCurrentOccupationNoAdmissions() throws Exception {
		String code = setupTestWard(false);
		Ward ward = wardIoOperationRepository.findById(code).get();
		assertThat(wardBrowserManager.getCurrentOccupation(ward)).isZero();
	}

	@Test
	public void testMgrGetCurrentOccupation() throws Exception {
		String code = setupTestWard(false);
		Ward ward = wardIoOperationRepository.findById(code).get();
		Admission admission1 = new Admission(1, 1, null, ward, 1, null, null, null, null, null,
				null, null, null, null, null, null, null, null, null, null,
				null, null, null, null, null, null, null, 'N');
		Admission admission2 = new Admission(2, 1, null, ward, 1, null, null, null, null, null,
				null, null, null, null, null, null, null, null, null, null,
				null, null, null, null, null, null, null, 'N');
		admissionIoOperationRepository.saveAndFlush(admission1);
		admissionIoOperationRepository.saveAndFlush(admission2);
		assertThat(wardBrowserManager.getCurrentOccupation(ward)).isEqualTo(2);
	}

	@Test
	public void testMgrGetWardsNoMaternity() throws Exception {
		String code = setupTestWard(false);
		Ward ward = wardIoOperationRepository.findById(code).get();
		List<Ward> wards = wardBrowserManager.getWardsNoMaternity();
		assertThat(wards.get(wards.size() - 1).getDescription()).isEqualTo(ward.getDescription());
	}

	@Test
	public void testMgrGetWards() throws Exception {
		String code = setupTestWard(false);
		Ward ward = wardIoOperationRepository.findById(code).get();
		List<Ward> wards = wardBrowserManager.getWards(ward);
		assertThat(wards.get(0).getDescription()).isEqualTo(ward.getDescription());
	}
	
	@Test
	public void testMgrGetIpdWards() throws Exception {
		String code = setupTestWard(false);
		Ward opdWard = wardBrowserManager.findWard(code);
		opdWard.setBeds(0);
		wardIoOperationRepository.saveAndFlush(opdWard);
		
		List<Ward> wards = wardBrowserManager.getIpdWards();
		assertThat(wards.size() == 0);
	}
	
	@Test
	public void testMgrGetOpdWards() throws Exception {
		String code = setupTestWard(false);
		Ward opdWard = wardBrowserManager.findWard(code);
		opdWard.setBeds(0);
		wardIoOperationRepository.saveAndFlush(opdWard);
		
		List<Ward> wards = wardBrowserManager.getOpdWards();
		assertThat(wards.size() == 1);
	}

	@Test
	public void testMgrGetWardsGetAll() throws Exception {
		String code = setupTestWard(false);
		Ward ward = wardIoOperationRepository.findById(code).get();
		List<Ward> wards = wardBrowserManager.getWards();
		assertThat(wards).hasSize(1);
		assertThat(wards.get(0).getDescription()).isEqualTo(ward.getDescription());
	}

	@Test
	public void testMgrNewWard() throws Exception {
		Ward ward = testWard.setup(true);
		Ward newWard = wardBrowserManager.newWard(ward);

		assertThat(newWard.getDescription()).isEqualTo("TestDescription");
		checkWardIntoDb(ward.getCode());
	}

	@Test
	public void testMgrUpdateWard() throws Exception {
		String code = setupTestWard(false);
		Ward ward = wardIoOperationRepository.findById(code).get();
		ward.setDescription("Update");
		wardBrowserManager.updateWard(ward);
		Ward updateWard = wardIoOperationRepository.findById(code).get();
		assertThat(updateWard.getDescription()).isEqualTo("Update");
	}

	@Test
	public void testMgrUpdateWardNoCodePresent() throws Exception {
		Ward ward = testWard.setup(true);
		ward.setCode("X");
		Ward updateWard = wardBrowserManager.updateWard(ward);
		assertThat(updateWard.getCode()).isEqualTo("X");
	}

	@Test
	public void testMgrDeleteWardNoPatients() throws Exception {
		String code = setupTestWard(false);
		Ward ward = wardIoOperationRepository.findById(code).get();
		assertThat(wardBrowserManager.deleteWard(ward)).isTrue();
		assertThat(wardBrowserManager.isCodePresent(code)).isFalse();
	}

	@Test
	public void testMgrDeleteWardMaternity() throws Exception {
		wardBrowserManager.maternityControl(true);
		List<Ward> wards = wardBrowserManager.getWards();
		assertThatThrownBy(() -> wardBrowserManager.deleteWard(wards.get(0)))
				.isInstanceOf(OHServiceException.class);
	}

	@Test
	public void testMgrDeleteWardWithPatients() throws Exception {
		String code = setupTestWard(false);
		Ward ward = wardIoOperationRepository.findById(code).get();
		Admission admission1 = new Admission(1, 1, null, ward, 1, null, null, null, null, null,
				null, null, null, null, null, null, null, null, null, null,
				null, null, null, null, null, null, null, 'N');
		Admission admission2 = new Admission(2, 1, null, ward, 1, null, null, null, null, null,
				null, null, null, null, null, null, null, null, null, null,
				null, null, null, null, null, null, null, 'N');
		admissionIoOperationRepository.saveAndFlush(admission1);
		admissionIoOperationRepository.saveAndFlush(admission2);
		assertThatThrownBy(() -> wardBrowserManager.deleteWard(ward))
				.isInstanceOf(OHServiceException.class);
	}

	@Test
	public void testMgrIsCodePresent() throws Exception {
		String code = setupTestWard(false);
		assertThat(wardBrowserManager.isCodePresent(code)).isTrue();
	}

	@Test
	public void testMgrIsCodePresentFalse() throws Exception {
		assertThat(wardBrowserManager.isCodePresent("X")).isFalse();
	}

	@Test
	public void testMgrMaternityControlDoNotCreate() throws Exception {
		boolean result = wardBrowserManager.maternityControl(false);

		if (!result) {
			Ward ward = testWard.setup(false);
			ward.setCode("M");
			wardIoOperationRepository.saveAndFlush(ward);
			result = wardBrowserManager.maternityControl(false);
		}

		assertThat(result).isTrue();
	}

	@Test
	public void testMgrMaternityControlCreate() throws Exception {
		assertThat(wardBrowserManager.maternityControl(true)).isTrue();
	}

	@Test
	public void testMgrOpdControlDoNotCreate() throws Exception {
		boolean result = wardBrowserManager.opdControl(false);

		if (!result) {
			Ward ward = testWard.setup(false);
			ward.setCode("OPD");
			wardIoOperationRepository.saveAndFlush(ward);
			result = wardBrowserManager.opdControl(false);
		}

		assertThat(result).isTrue();
	}

	@Test
	public void testMgrOpdControlCreate() throws Exception {
		assertThat(wardBrowserManager.opdControl(true)).isTrue();
	}

	@Test
	public void testMgrFindWard() throws Exception {
		String code = setupTestWard(false);
		Ward result = wardBrowserManager.findWard(code);
		assertThat(result).isNotNull();
		assertThat(result.getCode()).isEqualTo(code);
	}

	@Test
	public void testMgrFindWardNull() throws Exception {
		assertThatThrownBy(() -> wardBrowserManager.findWard(null))
				.isInstanceOf(OHServiceException.class);
	}

	@Test
	public void testMgrValidationCodeIsEmpty() throws Exception {
		Ward ward = testWard.setup(true);
		ward.setCode("");
		assertThatThrownBy(() -> wardBrowserManager.newWard(ward))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
	}

	@Test
	public void testMgrValidationCodeTooLong() throws Exception {
		Ward ward = testWard.setup(true);
		ward.setCode("thisIsACodeThatShouldBeTooLong");
		assertThatThrownBy(() -> wardBrowserManager.newWard(ward))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
	}

	@Test
	public void testMgrValidationDescriptionEmpty() throws Exception {
		Ward ward = testWard.setup(true);
		ward.setDescription("");
		assertThatThrownBy(() -> wardBrowserManager.newWard(ward))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
	}

	@Test
	public void testMgrValidationNegativeBeds() throws Exception {
		Ward ward = testWard.setup(true);
		ward.setBeds(-99);
		assertThatThrownBy(() -> wardBrowserManager.newWard(ward))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
	}

	@Test
	public void testMgrValidationNegativeNurses() throws Exception {
		Ward ward = testWard.setup(true);
		ward.setNurs(-99);
		assertThatThrownBy(() -> wardBrowserManager.newWard(ward))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
	}

	@Test
	public void testMgrValidationNegativeDoctors() throws Exception {
		Ward ward = testWard.setup(true);
		ward.setDocs(-99);
		assertThatThrownBy(() -> wardBrowserManager.newWard(ward))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
	}

	@Test
	public void testMgrValidationBadEmailFormat() throws Exception {
		Ward ward = testWard.setup(true);
		ward.setEmail("badFormatForEmailAddress");
		assertThatThrownBy(() -> wardBrowserManager.newWard(ward))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
	}

	@Test
	public void testMgrValidationCodeAlreadyExists() throws Exception {
		Ward ward = testWard.setup(true);
		wardBrowserManager.newWard(ward);
		assertThatThrownBy(() -> wardBrowserManager.newWard(ward))
				.isInstanceOf(OHDataIntegrityViolationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
	}

	@Test
	public void testWardConstructor() throws Exception {
		Ward ward = new Ward("Z", null, null, null, "TestEmail@gmail.com", 1, 1, 1, false, true);
		assertThat(ward.getLock()).isNull();
		ward.setLock(-1);
		assertThat(ward.getLock()).isEqualTo(-1);
	}

	@Test
	public void testWardEquals() throws Exception {
		Ward ward = testWard.setup(false);
		assertThat(ward.equals(ward)).isTrue();
		assertThat(ward)
				.isNotNull()
				.isNotEqualTo("someString");

		Ward ward2 = testWard.setup(false);
		ward2.setCode("-1");
		assertThat(ward).isNotEqualTo(ward2);

		ward2.setCode(ward.getCode());
		ward2.setDescription("someNewDescription");
		assertThat(ward).isNotEqualTo(ward2);

		ward2.setDescription(ward.getDescription().toLowerCase(Locale.ROOT));
		ward2.setTelephone("someNewTelephone");
		assertThat(ward).isNotEqualTo(ward2);

		ward2.setTelephone(ward.getTelephone().toUpperCase(Locale.ROOT));
		ward2.setFax("someNewFax");
		assertThat(ward).isNotEqualTo(ward2);

		ward2.setFax(ward.getFax());
		ward2.setEmail("someNewEmailAddress");
		assertThat(ward).isNotEqualTo(ward2);

		ward2.setEmail(ward.getEmail());
		ward2.setBeds(-99);
		assertThat(ward).isNotEqualTo(ward2);

		ward2.setBeds(ward.getBeds());
		ward2.setNurs(-99);
		assertThat(ward).isNotEqualTo(ward2);

		ward2.setNurs(ward.getNurs());
		ward2.setDocs(-99);
		assertThat(ward).isNotEqualTo(ward2);

		ward2.setDocs(ward.getDocs());
		assertThat(ward).isEqualTo(ward2);
	}

	@Test
	public void testWardToString() throws Exception {
		Ward ward = new Ward("Z", "someDescription", null, null, "TestEmail@gmail.com", 1, 1, 1, false, true);
		assertThat(ward).hasToString("someDescription");
	}

	@Test
	public void testWardDebug() throws Exception {
		Ward ward = testWard.setup(true);
		assertThat(ward.debug()).isEqualTo(
				"Ward [code=Z, description=TestDescription, telephone=TestTelephone, fax=TestFac, email=TestEmail@gmail.com, beds=100, nurs=101, docs=102, isPharmacy=true, isMale=false, isFemale=true, lock=null, hashCode=0]");
	}

	@Test
	public void testWardHashCode() throws Exception {
		Ward ward = testWard.setup(true);
		int hashCode = ward.hashCode();
		assertThat(hashCode).isEqualTo(23 * 133 + ward.getCode().hashCode());
		// return stored value
		assertThat(ward.hashCode()).isEqualTo(hashCode);
	}

	private String setupTestWard(boolean usingSet) throws OHException {
		Ward ward = testWard.setup(usingSet);
		wardIoOperationRepository.saveAndFlush(ward);
		return ward.getCode();
	}

	private void checkWardIntoDb(String code) throws OHException {
		Ward foundWard = wardIoOperationRepository.findById(code).get();
		testWard.check(foundWard);
	}
}
