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
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.isf.patconsensus.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import org.isf.patient.model.Patient;
import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "OH_PATIENT_CONSENSUS")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "PTC_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "PTC_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "PTC_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "PTC_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "PTC_LAST_MODIFIED_DATE"))
public class PatientConsensus extends Auditable<String> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PTC_ID")
	private int id;

	@Column(name = "PTC_CONSENSUS")
	private boolean consensusFlag = true;

	@Column(name = "PTC_SERVICE")
	private boolean serviceFlag;

	@OneToOne(cascade = CascadeType.DETACH)
	@JoinColumn(name = "PTC_PAT_ID", referencedColumnName = "PAT_ID")
	private Patient patient;

	public PatientConsensus() {
		super();
	}

	public PatientConsensus(boolean consensusFlag, boolean serviceFlag, Patient patient) {
		super();
		this.consensusFlag = consensusFlag;
		this.serviceFlag = serviceFlag;
		this.patient = patient;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isConsensusFlag() {
		return consensusFlag;
	}

	public void setConsensusFlag(boolean consensusFlag) {
		this.consensusFlag = consensusFlag;
	}

	public boolean isServiceFlag() {
		return serviceFlag;
	}

	public void setServiceFlag(boolean serviceFlag) {
		this.serviceFlag = serviceFlag;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	@Override
	public String toString() {
		return "PatientConsensus [id=" + id + ", consensusFlag=" + consensusFlag + ", serviceFlag=" + serviceFlag
						+ ", patient=" + patient + ']';
	}




}
