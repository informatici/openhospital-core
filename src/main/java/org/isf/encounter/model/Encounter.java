/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2025 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.encounter.model;

import jakarta.persistence.*;

import org.isf.patient.model.Patient;
import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name="OH_ENCOUNTER")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "ENC_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "ENC_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "ENC_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "ENC_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "ENC_LAST_MODIFIED_DATE"))
public class Encounter extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ENC_ID", nullable = false, unique = true)
    private Integer id;

    @Column(name="ENC_CODE", nullable = false, unique = true)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name="ENC_STATUS", nullable = false)
    private EncounterStatus status = EncounterStatus.OPEN;

	@ManyToOne
    @JoinColumn(name = "ENC_PAT_ID", nullable = false)
    private Patient patient;
    
    @Version
	@Column(name="ENC_LOCK")
	private int lock;

	@Column(name = "ENC_DATE")
	private LocalDateTime date;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	public EncounterStatus getStatus() {
		return status;
	}
	
	public void setStatus(EncounterStatus status) {
		this.status = status;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public int getLock() {
		return lock;
	}

	public void setLock(int lock) {
		this.lock = lock;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public Encounter(String code, EncounterStatus status, Patient patient) {
		this.code = code;
		this.status = status;
		this.patient = patient;
	}

	public Encounter(String code, EncounterStatus status) {
		this.code = code;
		this.status = status;
	}

	public Encounter() {}

}
