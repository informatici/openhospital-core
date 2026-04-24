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
package org.isf.cares.model;

import com.drew.lang.annotations.NotNull;
import jakarta.persistence.*;
import org.isf.patient.model.Patient;
import org.isf.utils.converter.JsonListConverter;
import org.isf.utils.db.Auditable;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="OH_CARES")
@AttributeOverride(name = "createdBy", column = @Column(name = "CR_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "CR_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "CR_LAST_MODIFIED_BY"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "CR_LAST_MODIFIED_DATE"))
@AttributeOverride(name = "active", column = @Column(name = "CR_ACTIVE"))
public class Care extends Auditable<String> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CR_ID")
	protected Integer id;

	@Column(name = "CR_TEAM", columnDefinition = "JSON")
	@Convert(converter = JsonListConverter.class)
	private List<String> team;

	@Column(name = "CR_OBSERVATION")
	private String observation;

	@Column(name = "CR_PLANNED_CARE")
	private String plannedCare;

	@Column(name = "CR_NOTE")
	private String note;

	@Column(name = "CR_CARE_DATE")
	private LocalDateTime careDate;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "CR_PAT_ID")
	private Patient patient;

	public Care() {
	}

	public Care(Integer id, List<String> team, String observation, String plannedCare, String notes, LocalDateTime careDate, Patient patient) {
		this.id = id;
		this.team = team;
		this.observation = observation;
		this.plannedCare = plannedCare;
		this.note = notes;
		this.careDate = careDate;
		this.patient = patient;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public List<String> getTeam() {
		return team;
	}

	public void setTeam(List<String> team) {
		this.team = team;
	}

	public String getObservation() {
		return observation;
	}

	public void setObservation(String observation) {
		this.observation = observation;
	}

	public String getPlannedCare() {
		return plannedCare;
	}

	public void setPlannedCare(String plannedCare) {
		this.plannedCare = plannedCare;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public LocalDateTime getCareDate() {
		return careDate;
	}

	public void setCareDate(LocalDateTime careDate) {
		this.careDate = careDate;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}
}
