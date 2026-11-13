/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2026 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.patadminissue.model;

import java.time.LocalDateTime;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import org.isf.patient.model.Patient;
import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * An administrative issue the hospital raised on a patient, such as a missing document or an identity still to be verified.
 * While at least one issue is open the staff is warned before providing further services to the patient. Resolving an issue
 * only sets its end date, so who raised it, when, and when it was cleared stay on record.
 */
@Entity
@Table(name = "OH_PATIENT_ADMIN_ISSUE")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "PAI_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "PAI_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "PAI_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "PAI_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "PAI_LAST_MODIFIED_DATE"))
public class PatientAdminIssue extends Auditable<String> {

	public static final int REASON_LENGTH = 255;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PAI_ID")
	private int id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "PAI_PAT_ID", referencedColumnName = "PAT_ID", nullable = false)
	private Patient patient;

	@Column(name = "PAI_REASON", length = REASON_LENGTH, nullable = false)
	private String reason;

	@Column(name = "PAI_FROM", nullable = false)
	private LocalDateTime fromDate;

	@Column(name = "PAI_TO")
	private LocalDateTime toDate;

	public PatientAdminIssue() {
		super();
	}

	public PatientAdminIssue(Patient patient, String reason) {
		super();
		this.patient = patient;
		this.reason = reason;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public LocalDateTime getFromDate() {
		return fromDate;
	}

	public void setFromDate(LocalDateTime fromDate) {
		this.fromDate = fromDate;
	}

	public LocalDateTime getToDate() {
		return toDate;
	}

	public void setToDate(LocalDateTime toDate) {
		this.toDate = toDate;
	}

	/**
	 * @return {@code true} while the issue has not been resolved.
	 */
	public boolean isOpen() {
		return toDate == null;
	}

	@Override
	public String toString() {
		return "PatientAdminIssue [id=" + id + ", patient=" + (patient == null ? null : patient.getCode()) + ", reason=" + reason + ", fromDate=" + fromDate
						+ ", toDate=" + toDate + ']';
	}

}
