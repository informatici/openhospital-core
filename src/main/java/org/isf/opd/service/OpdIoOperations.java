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
package org.isf.opd.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.isf.generaldata.MessageBundle;
import org.isf.opd.model.Opd;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.pagination.PageInfo;
import org.isf.utils.pagination.PagedResponse;
import org.isf.ward.model.Ward;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class OpdIoOperations {

	private OpdIoOperationRepository repository;

	public OpdIoOperations(OpdIoOperationRepository opdIoOperationRepository) {
		this.repository = opdIoOperationRepository;
	}

	/**
	 * Return all {@link Opd}s for today or one week ago.
	 * 
	 * @param oneWeek - if {@code true} return the last week, only today otherwise.
	 * @return the list of Opds. It could be {@code empty}.
	 * @throws OHServiceException 
	 */
	public List<Opd> getOpdList(boolean oneWeek) throws OHServiceException {
		LocalDate dateTo = LocalDate.now();
		LocalDate dateFrom = LocalDate.now();
		if (oneWeek) {
			dateFrom = LocalDate.now().minusWeeks(1);
		}
		return getOpdList(null, MessageBundle.getMessage("angal.common.alltypes.txt"), MessageBundle.getMessage("angal.opd.alldiseases.txt"), dateFrom, dateTo,
						0, 0, 'A', 'A', null);
	}

	/**
	 * Retrieves creation date of the last Opd
	 * @return creation date of the last Opd
	 */
	public LocalDateTime lastOpdCreationDate() {
		return this.repository.lastOpdCreationDate();
	}

	/**
	 * Count not deleted {@link Opd}s
	 * 
	 * @return the number of recorded {@link Opd}s
	 * @throws OHServiceException
	 */
	public long countAllActiveOpds() {
		return this.repository.countAllActiveOpds();
	}

	/**
	 * Return all {@link Opd}s within specified dates and parameters.
	 * 
	 * @param ward 
	 * @param diseaseTypeCode
	 * @param diseaseCode
	 * @param dateFrom
	 * @param dateTo
	 * @param ageFrom
	 * @param ageTo
	 * @param sex
	 * @param newPatient
	 * @param user
	 * @return the list of Opds. It could be {@code empty}.
	 * @throws OHServiceException 
	 */
	public List<Opd> getOpdList(
					Ward ward,
					String diseaseTypeCode,
					String diseaseCode,
					LocalDate dateFrom,
					LocalDate dateTo,
					int ageFrom,
					int ageTo,
					char sex,
					char newPatient,
					String user) throws OHServiceException {
		return repository.findAllOpdWhereParams(ward, diseaseTypeCode, diseaseCode, dateFrom, dateTo, ageFrom, ageTo, sex, newPatient, user);
	}

	/**
	 * Return all {@link Opd}s associated to specified patient ID.
	 * 
	 * @param patID - the patient ID
	 * @return the list of {@link Opd}s associated to specified patient ID.
	 * 		   the whole list of {@link Opd}s if {@code 0} is passed.
	 * @throws OHServiceException 
	 */
	public List<Opd> getOpdList(int patID) throws OHServiceException {
		return patID == 0 ? repository.findAllOrderByProgYearDesc() : repository.findAllByPatient_CodeOrderByProgYearDesc(patID);
	}

	/**
	 * Insert a new {@link Opd} into the db.
	 * 
	 * @param opd - an {@link Opd}
	 * @return the newly inserted {@link Opd} object.
	 * @throws OHServiceException 
	 */
	public Opd newOpd(Opd opd) throws OHServiceException {
		return repository.save(opd);
	}

	/**
	 * Modify an {@link Opd} in the db.
	 * 
	 * @param opd - an {@link Opd}
	 * @return the updated {@link Opd} object.
	 * @throws OHServiceException 
	 */
	public Opd updateOpd(Opd opd) throws OHServiceException {
		return repository.save(opd);
	}

	/**
	 * Delete an {@link Opd} from the db.
	 * 
	 * @param opd - the {@link Opd} to delete
	 * @throws OHServiceException
	 */
	public void deleteOpd(Opd opd) throws OHServiceException {
		repository.delete(opd);
	}

	/**
	 * Returns the max progressive number within specified year or within current year if {@code 0}.
	 * 
	 * @param year
	 * @return {@code int} - the progressive number in the year
	 * @throws OHServiceException 
	 */
	public int getProgYear(int year) throws OHServiceException {
		Integer progYear = year == 0 ? repository.findMaxProgYear()
						: repository.findMaxProgYearWhereDateBetween(LocalDateTime.of(year, 1, 1, 0, 0), LocalDateTime.of(year + 1, 1, 1, 0, 0));
		return progYear == null ? 0 : progYear;
	}

	/**
	 * Return the last {@link Opd} in time associated with specified patient ID.
	 * 
	 * @param patID - the patient ID
	 * @return last Opd associated with specified patient ID or {@code null}
	 * @throws OHServiceException 
	 */
	public Opd getLastOpd(int patID) throws OHServiceException {
		List<Opd> opdList = repository.findTop1ByPatient_CodeOrderByDateDesc(patID);
		return opdList.isEmpty() ? null : opdList.get(0);
	}

	/**
	 * Checks if the code is already in use.
	 *
	 * @param code - the opd code
	 * @return {@code true} if the code is already in use, {@code false} otherwise
	 * @throws OHServiceException 
	 */
	public boolean isCodePresent(Integer code) throws OHServiceException {
		return repository.existsById(code);
	}

	/**
	 * Check if the given {@code opdNum} does already exist for the given {@code year}.
	 * 
	 * @param opdNum - the OPD progressive in year
	 * @param year - the year
	 * @return {@code true} if the given number exists in year, {@code false} otherwise
	 * @throws OHServiceException
	 */
	public boolean isExistOpdNum(int opdNum, int year) throws OHServiceException {
		List<Opd> opds = year == 0 ? repository.findByProgYear(opdNum)
						: repository.findByProgYearAndDateBetween(opdNum, LocalDateTime.of(year, 1, 1, 0, 0), LocalDateTime.of(year + 1, 1, 1, 0, 0));
		return !opds.isEmpty();
	}

	/**
	 * Get an {@link Opd} by its code.
	 * @param code - the OPD code
	 * @return an OPD or {@code null}
	 */
	public Optional<Opd> getOpdById(Integer code) {
		return repository.findById(code);
	}

	/**
	 * Get a list of {@link Opd}s with the specified Progressive in Year number.
	 * @param code - the OPD code
	 * @return a list of OPD or an empty list
	 */
	public List<Opd> getOpdByProgYear(Integer code) {
		return repository.findByProgYear(code);
	}

	/**
	 * Retrieves a page of {@link Opd}s within specified dates and parameters.
	 * 
	 * @param ward
	 * @param diseaseTypeCode
	 * @param diseaseCode
	 * @param dateFrom
	 * @param dateTo
	 * @param ageFrom
	 * @param ageTo
	 * @param sex
	 * @param newPatient
	 * @param user
	 * @param page
	 * @param size
	 * @return a {@link PagedResponse} object that contains the {@link Opd}s.
	 * @throws OHServiceException 
	 */
	public PagedResponse<Opd> getOpdListPageable(
					Ward ward,
					String diseaseTypeCode,
					String diseaseCode,
					LocalDate dateFrom,
					LocalDate dateTo,
					int ageFrom,
					int ageTo,
					char sex,
					char newPatient,
					String user,
					int page,
					int size) throws OHServiceException {
		Pageable pageRequest = PageRequest.of(page, size);
		List<Opd> ops = this.getOpdList(ward, diseaseTypeCode, diseaseCode, dateFrom, dateTo, ageFrom, ageTo, sex, newPatient, null);
		int start = (int) pageRequest.getOffset();
		int end = Math.min(start + pageRequest.getPageSize(), ops.size());
		List<Opd> pageContent = ops.subList(start, end);
		return setPaginationData(new PageImpl<>(pageContent, pageRequest, ops.size()));
	}

	PagedResponse<Opd> setPaginationData(Page<Opd> pages) {
		PagedResponse<Opd> data = new PagedResponse<>();
		data.setData(pages.getContent());
		data.setPageInfo(PageInfo.from(pages));
		return data;
	}
}
