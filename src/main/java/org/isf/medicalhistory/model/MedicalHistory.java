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
package org.isf.medicalhistory.model;

import java.time.LocalDateTime;

import org.isf.examination.model.PatientExamination;
import org.isf.patient.model.Patient;
import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.drew.lang.annotations.NotNull;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "OH_MEDICAL_HISTORY")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "MH_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "MH_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "MH_LAST_MODIFIED_BY"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "MH_LAST_MODIFIED_DATE"))
public class MedicalHistory extends Auditable<String> implements Comparable<PatientExamination> {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MH_ID")
	private Integer id;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "MH_PAT_ID")
	private Patient patient;

	@Column(name = "MH_SIBLI_RANK")
	private Integer siblingRank;

	@Column(name = "MH_TERM_PREG")
	private String termPregnancy;

	@Column(name = "MH_DLV_MODE")
	private String deliveryMode;

	@Column(name = "MH_APGAR_SCORE")
	private  String apgarScore;

	@Column(name = "MH_BIRTH_WGHT")
	private Double birthWeight;

	@Column(name = "MH_VACC_STATE")
	private String vaccinationState;

	@Column(name = "MH_ANTI_MLRIAL_PROPHY")
	private String antiMalarialProphylaxis;

	@Column(name = "MH_DIET")
	private String diet;

	@Column(name = "MH_DEPARASITI")
	private String deParasitization;

	@Column(name = "MH_PSYCHOMOTOR_DEV")
	private String psychomotorDev;

	@Column(name = "MH_SOMATIC_GRWTH")
	private String somaticGrowth;

	@Column(name = "MH_IRON_SUPP")
	private Boolean ironSupplement;

	@Column(name = "MH_FOLIC_ACID_SUPP")
	private Boolean folicAcidSupplement;

	@Column(name = "MH_VIT_A_SUPP")
	private Boolean vitASupplement;

	@Column(name = "MH_OTHR_SUPP")
	private String otherSupplements;

	@Column(name = "MH_TRANSFU")
	private Boolean transfusion;

	@Column(name = "MH_LAST_TRANSFU_DATE")
	private LocalDateTime lastTransfusionDate;

	@Column(name = "MH_SIKCLE_CELL")
	private Boolean sickleCell;

	@Column(name = "MH_DRG_ALRGY")
	private Boolean drugAllergy;

	@Column(name = "MH_ALRGY_PREC")
	private String allergyPrecision;

	@Column(name = "MH_HEMYLOSIS")
	private String hemylosis;

	@Column(name = "MH_OTHR_PERSONAL_PATHO")
	private String otherPersonalPathologies;

	@Column(name = "MH_OTHR_FAM_PATHO")
	private String otherFamilyPathologies;
	
	@Transient
	private volatile int hashCode;

	public MedicalHistory() {

	}

	public MedicalHistory(
		Integer id,
		Patient patient,
		Integer siblingRank,
		String termPregnancy,
		String deliveryMode,
		String apgarScore,
		Double birthWeight,
		String vaccinationState,
		String antiMalarialProphylaxis,
		String diet,
		String deParasitization,
		String psychomotorDevelopment,
		String somaticGrowth,
		Boolean ironSupplement,
		Boolean folicAcidSupplement,
		Boolean vitaminASupplement,
		String otherSupplements,
		Boolean transfusion,
		LocalDateTime lastTransfusionDate,
		Boolean sickleCell,
		Boolean drugAllergy,
		String allergyPrecisions,
		String hemylosis,
		String otherPersonalPathology,
		String otherFamilyPathology
	) {
		this.id = id;
		this.patient = patient;
		this.siblingRank = siblingRank;
		this.termPregnancy = termPregnancy;
		this.deliveryMode = deliveryMode;
		this.apgarScore = apgarScore;
		this.birthWeight = birthWeight;
		this.vaccinationState = vaccinationState;
		this.antiMalarialProphylaxis = antiMalarialProphylaxis;
		this.diet = diet;
		this.deParasitization = deParasitization;
		this.psychomotorDev = psychomotorDevelopment;
		this.somaticGrowth = somaticGrowth;
		this.ironSupplement = ironSupplement;
		this.folicAcidSupplement = folicAcidSupplement;
		this.vitASupplement = vitaminASupplement;
		this.otherSupplements = otherSupplements;
		this.transfusion = transfusion;
		this.lastTransfusionDate = lastTransfusionDate;
		this.sickleCell = sickleCell;
		this.drugAllergy = drugAllergy;
		this.allergyPrecision = allergyPrecisions;
		this.hemylosis = hemylosis;
		this.otherPersonalPathologies = otherPersonalPathology;
		this.otherFamilyPathologies = otherFamilyPathology;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public Integer getSiblingRank() {
		return siblingRank;
	}

	public void setSiblingRank(Integer siblingRank) {
		this.siblingRank = siblingRank;
	}

	public String getTermPregnancy() {
		return termPregnancy;
	}

	public void setTermPregnancy(String termPregnancy) {
		this.termPregnancy = termPregnancy;
	}

	public String getDeliveryMode() {
		return deliveryMode;
	}

	public void setDeliveryMode(String deliveryMode) {
		this.deliveryMode = deliveryMode;
	}

	public String getApgarScore() {
		return apgarScore;
	}

	public void setApgarScore(String apgarScore) {
		this.apgarScore = apgarScore;
	}

	public Double getBirthWeight() {
		return birthWeight;
	}

	public void setBirthWeight(Double birthWeight) {
		this.birthWeight = birthWeight;
	}

	public String getVaccinationState() {
		return vaccinationState;
	}

	public void setVaccinationState(String vaccinationState) {
		this.vaccinationState = vaccinationState;
	}

	public String getAntiMalarialProphylaxis() {
		return antiMalarialProphylaxis;
	}

	public void setAntiMalarialProphylaxis(String antiMalarialProphylaxis) {
		this.antiMalarialProphylaxis = antiMalarialProphylaxis;
	}

	public String getDiet() {
		return diet;
	}

	public void setDiet(String diet) {
		this.diet = diet;
	}

	public String getDeParasitization() {
		return deParasitization;
	}

	public void setDeParasitization(String deParasitization) {
		this.deParasitization = deParasitization;
	}

	public String getPsychomotorDev() {
		return psychomotorDev;
	}

	public void setPsychomotorDev(String psychomotorDevelopment) {
		this.psychomotorDev = psychomotorDevelopment;
	}

	public String getSomaticGrowth() {
		return somaticGrowth;
	}

	public void setSomaticGrowth(String somaticGrowth) {
		this.somaticGrowth = somaticGrowth;
	}

	public Boolean getIronSupplement() {
		return ironSupplement;
	}

	public void setIronSupplement(Boolean ironSupplement) {
		this.ironSupplement = ironSupplement;
	}

	public Boolean getFolicAcidSupplement() {
		return folicAcidSupplement;
	}

	public void setFolicAcidSupplement(Boolean folicAcidSupplement) {
		this.folicAcidSupplement = folicAcidSupplement;
	}

	public Boolean getVitASupplement() {
		return vitASupplement;
	}

	public void setVitASupplement(Boolean vitaminASupplement) {
		this.vitASupplement = vitaminASupplement;
	}

	public String getOtherSupplements() {
		return otherSupplements;
	}

	public void setOtherSupplements(String otherSupplements) {
		this.otherSupplements = otherSupplements;
	}

	public Boolean getTransfusion() {
		return transfusion;
	}

	public void setTransfusion(Boolean transfusion) {
		this.transfusion = transfusion;
	}

	public LocalDateTime getLastTransfusionDate() {
		return lastTransfusionDate;
	}

	public void setLastTransfusionDate(LocalDateTime lastTransfusionDate) {
		this.lastTransfusionDate = lastTransfusionDate;
	}

	public Boolean getSickleCell() {
		return sickleCell;
	}

	public void setSickleCell(Boolean sickleCell) {
		this.sickleCell = sickleCell;
	}

	public Boolean getDrugAllergy() {
		return drugAllergy;
	}

	public void setDrugAllergy(Boolean drugAllergy) {
		this.drugAllergy = drugAllergy;
	}

	public String getAllergyPrecision() {
		return allergyPrecision;
	}

	public void setAllergyPrecision(String allergyPrecision) {
		this.allergyPrecision = allergyPrecision;
	}

	public String getHemylosis() {
		return hemylosis;
	}

	public void setHemylosis(String hemylosis) {
		this.hemylosis = hemylosis;
	}

	public String getOtherPersonalPathologies() {
		return otherPersonalPathologies;
	}

	public void setOtherPersonalPathologies(String otherPersonalPathologies) {
		this.otherPersonalPathologies = otherPersonalPathologies;
	}

	public String getOtherFamilyPathologies() {
		return otherFamilyPathologies;
	}

	public void setOtherFamilyPathologies(String otherFamilyPathologies) {
		this.otherFamilyPathologies = otherFamilyPathologies;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (!(obj instanceof MedicalHistory mh)) {
			return false;
		}

		return (id == mh.getId());
	}
	
	@Override
	public int hashCode() {
	    if (this.hashCode == 0) {
	        final int m = 23;
	        int c = 133;
	        
	        c = m * c + id;
	        
	        this.hashCode = c;
	    }
	  
	    return this.hashCode;
	}

	@Override
	public int compareTo(PatientExamination o) {
		// TODO Auto-generated method stub
		return 0;
	}
}
