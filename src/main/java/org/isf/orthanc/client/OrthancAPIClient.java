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
package org.isf.orthanc.client;

import java.util.List;

import org.isf.orthanc.model.FindRequest;
import org.isf.orthanc.model.InstanceResponse;
import org.isf.orthanc.model.SeriesResponse;
import org.isf.orthanc.model.StudyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import feign.Response;

/**
 * @author Silevester D.
 * @since 1.15
 */
@FeignClient(name = "orthanc-rest-api-client")
public interface OrthancAPIClient {
	@GetMapping(value = "/tools/now", produces = MediaType.TEXT_PLAIN_VALUE)
	String testConnection();

	@GetMapping(value = "/studies?expand=true", produces = MediaType.APPLICATION_JSON_VALUE)
	List<StudyResponse> getAllStudies();

	@PostMapping(value = "/tools/find", produces = MediaType.APPLICATION_JSON_VALUE)
	List<StudyResponse> getPatientStudiesById(@RequestBody FindRequest request);

	@GetMapping(value = "/patients/{id}/studies", produces = MediaType.APPLICATION_JSON_VALUE)
	List<StudyResponse> getPatientStudiesByUuid(@PathVariable("id") String patientUuid);

	@GetMapping(value = "/studies/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	StudyResponse getStudyById(@PathVariable("id") String id);

	@GetMapping(value = "/studies/{id}/series", produces = MediaType.APPLICATION_JSON_VALUE)
	List<SeriesResponse> getStudySeries(@PathVariable("id") String id);

	@GetMapping(value = "/series/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	SeriesResponse getSeriesById(@PathVariable("id") String id);

	@GetMapping(value = "/series/{id}/instances", produces = MediaType.APPLICATION_JSON_VALUE)
	List<InstanceResponse> getSeriesInstances(@PathVariable("id") String id);

	@GetMapping(value = "/instances/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	InstanceResponse getInstanceById(@PathVariable("id") String id);

	@GetMapping(value = "/instances/{id}/preview", produces = MediaType.IMAGE_PNG_VALUE)
	Response getInstancePreview(@PathVariable("id") String id);
}