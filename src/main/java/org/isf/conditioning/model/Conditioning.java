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
package org.isf.conditioning.model;

import com.drew.lang.annotations.NotNull;
import jakarta.persistence.*;

import org.isf.menu.model.User;
import org.isf.patient.model.Patient;
import org.isf.utils.converter.JsonListConverter;
import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "OH_CONDITIONING")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name="createdBy", column=@Column(name="COND_CREATED_BY", updatable = false))
@AttributeOverride(name="createdDate", column=@Column(name="COND_CREATED_DATE", updatable = false))
@AttributeOverride(name="lastModifiedBy", column=@Column(name="COND_LAST_MODIFIED_BY"))
@AttributeOverride(name="active", column=@Column(name="COND_ACTIVE"))
@AttributeOverride(name="lastModifiedDate", column=@Column(name="COND_LAST_MODIFIED_DATE"))
public class Conditioning extends Auditable<String> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "COND_ID")
	private Integer id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "COND_PERFORMED_BY")
	private User performedBy;

	@Column(name = "COND_ASPIRATION")
	private Boolean aspiration;

	@Column(name = "COND_MCE")
	private Integer mce;

	@Column(name = "COND_VENTILATION")
	private Integer ventilation;

	@Column(name = "COND_OXYGEN_DEBIT")
	private Double oxygenDebit;

	@Column(name = "COND_SG_VOLUME")
	private Double sgVolume;

	@Column(name = "COND_DIAZEPAM_DOSE")
	private Double diazepamDose;

	@Column(name = "COND_BOLUS_SS_VOLUME")
	private Double bolusSsVolume;

	@Column(name = "COND_SNG_NUMBER")
	private String sngNumber;

	@Column(name = "COND_MALARIA")
	private String malaria;

	@Column(name = "COND_HIV_TEST")
	private String hivTest;

	@Column(name = "COND_BLOOD_GLUCOSE_LEVEL")
	private Double bloodGlucoseLevel;

	@Column(name = "COND_OTHERS")
	private String others;

	@Column(name = "COND_CONDITION_AT_ADMISSION", columnDefinition = "JSON")
	@Convert(converter = JsonListConverter.class)
	private List<String> conditionAtAdmission;

	@Column(name = "COND_PERFORMED_AT")
	private LocalDateTime performedAt;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "COND_PAT_ID")
	private Patient patient;
	
	@Column(name = "COND_CPAP")
	private Boolean cpap;
	
	@Version
	@Column(name="COND_LOCK")
	private int lock;

	public Conditioning(Integer id, Boolean aspiration, Integer mce, Integer ventilation, Double oxygenDebit, Double sgVolume, Double diazepamDose, Double bolusSsVolume, String sngNumber, String others, LocalDateTime performedAt, Patient patient) {
		this.id = id;
		this.aspiration = aspiration;
		this.mce = mce;
		this.ventilation = ventilation;
		this.oxygenDebit = oxygenDebit;
		this.sgVolume = sgVolume;
		this.diazepamDose = diazepamDose;
		this.bolusSsVolume = bolusSsVolume;
		this.sngNumber = sngNumber;
		this.others = others;
		this.patient = patient;
	}

	public Conditioning(Integer id, Boolean aspiration, Integer mce, Integer ventilation, Double oxygenDebit, Double sgVolume, Double diazepamDose,
						Double bolusSsVolume, String sngNumber, String others, LocalDateTime performedAt, Patient patient, Boolean cpap, int lock) {
		super();
		this.id = id;
		this.aspiration = aspiration;
		this.mce = mce;
		this.ventilation = ventilation;
		this.oxygenDebit = oxygenDebit;
		this.sgVolume = sgVolume;
		this.diazepamDose = diazepamDose;
		this.bolusSsVolume = bolusSsVolume;
		this.sngNumber = sngNumber;
		this.others = others;
		this.performedAt = performedAt;
		this.patient = patient;
		this.cpap = cpap;
		this.lock = lock;
	}

	public Conditioning(Integer id, User performedBy, Boolean aspiration, Integer mce, Integer ventilation, Double oxygenDebit, Double sgVolume, Double diazepamDose,
		Double bolusSsVolume, String sngNumber, String others, LocalDateTime performedAt, Patient patient, Boolean cpap, int lock,
		String malaria, Double bloodGlucoseLevel, String hivTest
	) {
		super();
		this.id = id;
		this.performedBy = performedBy;
		this.aspiration = aspiration;
		this.mce = mce;
		this.ventilation = ventilation;
		this.oxygenDebit = oxygenDebit;
		this.sgVolume = sgVolume;
		this.diazepamDose = diazepamDose;
		this.bolusSsVolume = bolusSsVolume;
		this.sngNumber = sngNumber;
		this.others = others;
		this.performedAt = performedAt;
		this.patient = patient;
		this.cpap = cpap;
		this.lock = lock;
		this.malaria = malaria;
		this.bloodGlucoseLevel = bloodGlucoseLevel;
		this.hivTest =  hivTest;
	}

	public Conditioning() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public User getPerformedBy() {
		return performedBy;
	}

	public void setPerformedBy(User performedBy) {
		this.performedBy = performedBy;
	}

	public Boolean getAspiration() {
		return aspiration;
	}

	public void setAspiration(Boolean aspiration) {
		this.aspiration = aspiration;
	}

	public Integer getMce() {
		return mce;
	}

	public void setMce(Integer mce) {
		this.mce = mce;
	}

	public Integer getVentilation() {
		return ventilation;
	}

	public void setVentilation(Integer ventilation) {
		this.ventilation = ventilation;
	}

	public Double getOxygenDebit() {
		return oxygenDebit;
	}

	public void setOxygenDebit(Double oxygenDebit) {
		this.oxygenDebit = oxygenDebit;
	}

	public Double getSgVolume() {
		return sgVolume;
	}

	public void setSgVolume(Double sgVolume) {
		this.sgVolume = sgVolume;
	}

	public Double getDiazepamDose() {
		return diazepamDose;
	}

	public void setDiazepamDose(Double diazepamDose) {
		this.diazepamDose = diazepamDose;
	}

	public Double getBolusSsVolume() {
		return bolusSsVolume;
	}

	public void setBolusSsVolume(Double bolusSsVolume) {
		this.bolusSsVolume = bolusSsVolume;
	}

	public String getMalaria() {return malaria;}

	public void setMalaria(String malaria) {this.malaria = malaria;}

	public String getHivTest() {return hivTest;}

	public void setHivTest(String hivTest) {this.hivTest = hivTest;}

	public Double getBloodGlucoseLevel() {return bloodGlucoseLevel;}

	public void setBloodGlucoseLevel(Double bloodGlucoseLevel) {this.bloodGlucoseLevel = bloodGlucoseLevel;}

	public String getSngNumber() {
		return sngNumber;
	}

	public void setSngNumber(String sngNumber) {
		this.sngNumber = sngNumber;
	}

	public String getOthers() {
		return others;
	}

	public void setOthers(String others) {
		this.others = others;
	}
	
	public LocalDateTime getPerformedAt() {
		return performedAt;
	}

	public List<String> getConditionAtAdmission() {
		return conditionAtAdmission;
	}

	public void setConditionAtAdmission(List<String> conditionAtAdmission) {this.conditionAtAdmission = conditionAtAdmission;}

	public void setPerformedAt(LocalDateTime performedAt) {
		this.performedAt = performedAt;
	}
	
	public Boolean getCpap() {
		return cpap;
	}
	
	public void setCpap(Boolean cpap) {
		this.cpap = cpap;
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
}
