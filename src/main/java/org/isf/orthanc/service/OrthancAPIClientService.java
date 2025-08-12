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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.isf.orthanc.client.OrthancAPIClient;
import org.isf.orthanc.client.OrthancFeignClientFactory;
import org.isf.orthanc.model.FindRequest;
import org.isf.orthanc.model.FindRequestLevel;
import org.isf.orthanc.model.InstanceResponse;
import org.isf.orthanc.model.Query;
import org.isf.orthanc.model.SeriesResponse;
import org.isf.orthanc.model.StudyResponse;
import org.isf.utils.exception.OHInternalServerException;
import org.isf.utils.exception.OHRestClientException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Service;

import feign.Response;

/**
 * @author Silevester D.
 * @since 1.15
 */
@Service
public class OrthancAPIClientService {

	private final OrthancFeignClientFactory feignClientFactory;

	public OrthancAPIClientService(OrthancFeignClientFactory feignClientFactory) {
		this.feignClientFactory = feignClientFactory;
	}

	/**
	 * Test if the connection with ORTHANC server can be established
	 *
	 * @return <code>true</code> if connection successfully established, <code>false</code> otherwise
	 * @throws OHServiceException see when {@link #getClient()} throws this exception
	 * @throws OHRestClientException when 4xx or 5xx errors is thrown by Feign
	 */
	public boolean testConnection() throws OHServiceException, OHRestClientException {
		String time = feignClientFactory.createClient(true).testConnection();

		return time != null && !time.isEmpty() ;
	}

	/**
	 * Get Feign instance
	 *
	 * @return instance of {@link OrthancAPIClient}
	 * @throws OHServiceException see when {@link OrthancFeignClientFactory#createClient(boolean)} throws this exception
	 * @throws OHRestClientException when 4xx or 5xx errors is thrown by Feign
	 */
	public OrthancAPIClient getClient() throws OHServiceException, OHRestClientException {
		return feignClientFactory.createClient(false);
	}

	/**
	 * Get all studies
	 *
	 * @return The list of studies
	 * @throws OHServiceException see when {@link #getClient()} throws this exception
	 * @throws OHRestClientException when 4xx or 5xx errors is thrown by Feign
	 */
	public List<StudyResponse> getAllStudies() throws OHServiceException, OHRestClientException {
		return getClient().getAllStudies();
	}

	/**
	 * Get patient's studies
	 * <p>This method uses the find tool of ORTHANC to matches patient and retrieve his studies.
	 * We assume that the value of the tag <code>PatientId</code> in the study's prop refers
	 * OH patient's ID.</p>
	 *
	 * @return The list of studies related to the given patient ID
	 * @throws OHServiceException see when {@link #getClient()} throws this exception
	 * @throws OHRestClientException when 4xx or 5xx errors is thrown by Feign
	 */
	public List<StudyResponse> getPatientStudiesById(String patientId) throws OHServiceException, OHRestClientException {
		FindRequest request = new FindRequest(
			FindRequestLevel.Study.name(),
			true,
			new Query(patientId)
		);

		return getClient().getPatientStudiesById(request);
	}

	/**
	 * Get patient's studies
	 * <p>This method retrieves studies related to a given patient using patient's UUID
	 * stored in ORTHANC server.</p>
	 *
	 * @return The list of studies related to the given patient ID
	 * @throws OHServiceException see when {@link #getClient()} throws this exception
	 * @throws OHRestClientException when 4xx or 5xx errors is thrown by Feign
	 */
	public List<StudyResponse> getPatientStudiesByUuid(String patientUUID) throws OHServiceException, OHRestClientException {
		return getClient().getPatientStudiesByUuid(patientUUID);
	}

	/**
	 * Get a study using study's UUID
	 *
	 * @param studyId Study UUID
	 * @return the study that matched the provided UUID
	 * @throws OHServiceException see when {@link #getClient()} throws this exception
	 * @throws OHRestClientException when 4xx or 5xx errors is thrown by Feign
	 */
	public StudyResponse getStudyById(String studyId) throws OHServiceException, OHRestClientException {
		return getClient().getStudyById(studyId);
	}

	/**
	 * Get study's series using study's UUID
	 *
	 * @param studyId Study UUID
	 * @return the list of study's series
	 * @throws OHServiceException see when {@link #getClient()} throws this exception
	 * @throws OHRestClientException when 4xx or 5xx errors is thrown by Feign
	 */
	public List<SeriesResponse> getStudySeries(String studyId) throws OHServiceException, OHRestClientException {
		return getClient().getStudySeries(studyId);
	}

	/**
	 * Get a series using series' UUID
	 *
	 * @param seriesId Series UUID
	 * @return the series that matched the provided UUID
	 * @throws OHServiceException see when {@link #getClient()} throws this exception
	 * @throws OHRestClientException when 4xx or 5xx errors is thrown by Feign
	 */
	public SeriesResponse getSeriesById(String seriesId) throws OHServiceException,OHRestClientException {
		return getClient().getSeriesById(seriesId);
	}

	/**
	 * Get series' instances using series' UUID
	 *
	 * @param seriesId Series UUID
	 * @return the list of series' instances
	 * @throws OHServiceException see when {@link #getClient()} throws this exception
	 * @throws OHRestClientException when 4xx or 5xx errors is thrown by Feign
	 */
	public List<InstanceResponse> getSeriesInstances(String seriesId) throws OHServiceException,OHRestClientException {
		return getClient().getSeriesInstances(seriesId);
	}

	/**
	 * Get an instance using instance's UUID
	 *
	 * @param instanceId Instance UUID
	 * @return the instance that matched the provided UUID
	 * @throws OHServiceException see when {@link #getClient()} throws this exception
	 * @throws OHRestClientException when 4xx or 5xx errors is thrown by Feign
	 */
	public InstanceResponse getInstanceById(String instanceId) throws OHServiceException,OHRestClientException {
		return getClient().getInstanceById(instanceId);
	}

	/**
	 * Get the preview of an instance using instance's UUID
	 *
	 * @param instanceId Instance UUID
	 * @return the instance preview image in PNG format
	 * @throws OHServiceException see when {@link #getClient()} throws this exception
	 * @throws OHRestClientException when 4xx or 5xx errors is thrown by Feign
	 */
	public byte[] getInstancePreview(String instanceId) throws OHServiceException, OHRestClientException {
		// TODO: Better handle the file download to avoid {@link OutOfMemoryError} error

		try (Response response = getClient().getInstancePreview(instanceId)) {
			InputStream inputStream = response.body().asInputStream();

			return inputStream.readAllBytes();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
