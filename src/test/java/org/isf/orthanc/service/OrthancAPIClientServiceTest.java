/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2024 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.isf.orthanc.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.orthanc.model.InstanceResponse;
import org.isf.orthanc.model.SeriesResponse;
import org.isf.orthanc.model.StudyResponse;
import org.isf.utils.exception.OHNotFoundException;
import org.isf.utils.exception.OHServiceException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Orthanc APIs call testing
 * <p>
 *     These tests are disabled by default because a valid running instance of
 *     ORTHANC may be missing. So you need to configure one using the SQL script
 *     loaded by this test before tests can run smoothly. The script is located at
 *     <code>src/test/resources/org/isf/orthanc/service/LoadORTHANCSettings.sql</code>
 * </p>
 *
 * @author Silevester D.
 * @since 1.15
 */
@Disabled("Disabled because ORTHANC may not be properly configured")
public class OrthancAPIClientServiceTest extends OHCoreTestCase {

	@Autowired
	private OrthancAPIClientService service;

	@Test
	@DisplayName("Should successfully establish connection to ORTHANC server")
	void testTestConnection() throws OHServiceException {
		assertThat(service.testConnection()).isTrue();
	}

	@Test
	@DisplayName("Should successfully get all studies")
	void testGetAllStudies() throws OHServiceException {
		List<StudyResponse> studies = service.getAllStudies();

		assertThat(studies).isNotNull();
		assertThat(studies).isNotEmpty();
		assertThat(studies.get(0).getStudy()).isNotNull();
	}

	@Test
	@DisplayName("Should successfully get patient's studies using ID")
	void testGetPatientStudiesById() throws OHServiceException {
		List<StudyResponse> studies = service.getPatientStudiesById("TCGA-CS-5396");

		assertThat(studies).isNotNull();
		assertThat(studies).isNotEmpty();
		assertThat(studies.get(0).getStudy()).isNotNull();
	}

	@Test
	@DisplayName("Should successfully get patient's studies using UUID")
	void testGetPatientStudiesByUuid() throws OHServiceException {
		List<StudyResponse> studies = service.getPatientStudiesByUuid("2eb243f6-71d716cd-f195ef0b-09e7c680-9937a931");

		assertThat(studies).isNotNull();
		assertThat(studies).isNotEmpty();
		assertThat(studies.get(0).getStudy()).isNotNull();
	}

	@Test
	@DisplayName("Should successfully get study using study's ID")
	void testGetStudyById() throws OHServiceException {
		StudyResponse studies = service.getStudyById("8fb3d973-4449cad4-c21bb79d-81c41b56-b9412373");

		assertThat(studies).isNotNull();
		assertThat(studies.getStudy()).isNotNull();
		assertThat(studies.lastUpdateToLocalDateTime()).isInstanceOf(LocalDateTime.class);
	}

	@Test
	@DisplayName("Should throw OHServiceException when trying to get study using a wrong ID")
	void testGetStudyWithWrongIdThrowsException() {
		assertThatThrownBy(() -> service.getStudyById("8fb3d973-4449ad4-c21bb79d-81c41b56-b9412373"))
			.isInstanceOf(OHNotFoundException.class);
	}

	@Test
	@DisplayName("Should successfully get study's series using study's ID")
	void testGetStudySeries() throws OHServiceException {
		List<SeriesResponse> series = service.getStudySeries("8fb3d973-4449cad4-c21bb79d-81c41b56-b9412373");

		assertThat(series).isNotNull();
		assertThat(series).isNotEmpty();
		assertThat(series.get(0).getSeries()).isNotNull();
	}

	@Test
	@DisplayName("Should successfully get series using series' ID")
	void testGetSeriesById() throws OHServiceException {
		SeriesResponse series = service.getSeriesById("61af62b9-54162615-e473cdc1-e3676d55-0b5d7a4b");

		assertThat(series).isNotNull();
		assertThat(series.getInstancesIds()).isNotEmpty();
	}

	@Test
	@DisplayName("Should successfully get series' instances using series' ID")
	void testGetSeriesInstances() throws OHServiceException {
		List<InstanceResponse> instances = service.getSeriesInstances("61af62b9-54162615-e473cdc1-e3676d55-0b5d7a4b");

		assertThat(instances).isNotNull();
		assertThat(instances.get(0).getInstance()).isNotNull();
	}

	@Test
	@DisplayName("Should successfully get instance using instance's ID")
	void testGetInstanceById() throws OHServiceException {
		InstanceResponse instance = service.getInstanceById("d4ea97a3-dad85671-26c5ffaf-6b9ed334-6a2fad91");

		assertThat(instance).isNotNull();
		assertThat(instance.getInstance()).isNotNull();
	}

	@Test
	@DisplayName("Should successfully get instance preview")
	void testGetInstancePreview() throws OHServiceException {
		byte[] pngImage = service.getInstancePreview("d4ea97a3-dad85671-26c5ffaf-6b9ed334-6a2fad91");

		assertThat(pngImage).isNotNull();
	}
}
