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
import jakarta.persistence.Version;

@Entity
@Table(name = "OH_MEDICALHISTORY")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "MH_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "MH_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "MH_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "MH_ACTIVE"))
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

	@Column(name = "MH_SIBLI_RANK", length = 100)
	private String siblingRank;

	@Column(name = "MH_TERM_PREG")
	private String termPregnancy;

	@Column(name = "MH_PREGNANCY")
	private String pregnancy;

	@Column(name = "MH_DLV_MODE")
	private String deliveryMode;

	@Column(name = "MH_REASON_MODE")
	private String reasonMode;

	@Column(name = "MH_APGAR_SCORE")
	private  String apgarScore;

	@Column(name = "MH_BIRTH_WGHT")
	private Double birthWeight;

	@Column(name = "MH_VACC_STATE_PREV")
	private String vaccinationStatePev;

	@Column(name = "MH_VACC_STATE_NO_PREV")
	private String vaccinationStateNoPev;

	@Column(name = "MH_ANTI_MLRIAL_PROPHY_MILDA")
	private String antiMalarialProphylaxisMilda;

	@Column(name = "MH_ANTI_MLRIAL_PROPHY_VAP")
	private String antiMalarialProphylaxisVap;

	@Column(name = "MH_ANTI_MLRIAL_PROPHY_OTHERS")
	private String antiMalarialProphylaxisOthers;

	@Column(name = "MH_SURGICAL_PROCEDURE")
	private Boolean surgicalProcedure;

	@Column(name = "MH_SURGICAL_PROCEDURE_CONDITION")
	private String surgicalProcedureCondition;

	@Column(name = "MH_SURGICAL_PROCEDURE_TYPE")
	private String surgicalProcedureType ;

	@Column(name = "MH_SURGICAL_PROCEDURE_DATE")
	private LocalDateTime surgicalProcedureDate;

	@Column(name = "MH_DIVERSIFICATION")
	private String diversification;

	@Column(name = "MH_NEONATAL_PERIOD")
	private String neonatalPeriod;

	@Column(name = "MH_PREVIOUS_HOSPITALIZATION")
	private String previousHospitalization;

	@Column(name = "MH_FATHER")
	private String father;

	@Column(name = "MH_MOTHER")
	private String mother;

	@Column(name = "MH_SIBLINGS")
	private String siblings;

	@Column(name = "MH_OTHER_USEFUL_INFORMATION")
	private String otherUsefulInformation;

	@Column(name = "MH_DIET")
	private String diet;

	@Column(name = "MH_DEPARASITI")
	private Boolean deParasitization;

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

	@Column(name = "MH_PERFORMED_AT")
	private LocalDateTime performedAt;

	@Version
	@Column(name = "MH_LOCK")
	private int lock;
	
	@Transient
	private volatile int hashCode;

	public MedicalHistory() {

	}

	public MedicalHistory(Integer id, Patient patient, String siblingRank, String termPregnancy, String deliveryMode, String reasonMode, String apgarScore, Double birthWeight, String vaccinationStatePev, String vaccinationStateNoPev, String antiMalarialProphylaxisMilda, String antiMalarialProphylaxisVap, String antiMalarialProphylaxisOthers, Boolean surgicalProcedure, String surgicalProcedureCondition, String surgicalProcedureType, LocalDateTime surgicalProcedureDate, String diversification, String neonatalPeriod, String previousHospitalization, String father, String mother, String siblings, String otherUsefulInformation, String diet, Boolean deParasitization, String psychomotorDev, String somaticGrowth, Boolean ironSupplement, Boolean folicAcidSupplement, Boolean vitASupplement, String otherSupplements, Boolean transfusion, LocalDateTime lastTransfusionDate, Boolean sickleCell, Boolean drugAllergy, String allergyPrecision, String hemylosis, String otherPersonalPathologies, String otherFamilyPathologies, LocalDateTime performedAt, int lock, int hashCode) {
		this.id = id;
		this.patient = patient;
		this.siblingRank = siblingRank;
		this.termPregnancy = termPregnancy;
		this.pregnancy = pregnancy;
		this.deliveryMode = deliveryMode;
		this.reasonMode = reasonMode;
		this.apgarScore = apgarScore;
		this.birthWeight = birthWeight;
		this.vaccinationStatePev = vaccinationStatePev;
		this.vaccinationStateNoPev = vaccinationStateNoPev;
		this.antiMalarialProphylaxisMilda = antiMalarialProphylaxisMilda;
		this.antiMalarialProphylaxisVap = antiMalarialProphylaxisVap;
		this.antiMalarialProphylaxisOthers = antiMalarialProphylaxisOthers;
		this.surgicalProcedure = surgicalProcedure;
		this.surgicalProcedureCondition = surgicalProcedureCondition;
		this.surgicalProcedureType = surgicalProcedureType;
		this.surgicalProcedureDate = surgicalProcedureDate;
		this.diversification = diversification;
		this.neonatalPeriod = neonatalPeriod;
		this.previousHospitalization = previousHospitalization;
		this.father = father;
		this.mother = mother;
		this.siblings = siblings;
		this.otherUsefulInformation = otherUsefulInformation;
		this.diet = diet;
		this.deParasitization = deParasitization;
		this.psychomotorDev = psychomotorDev;
		this.somaticGrowth = somaticGrowth;
		this.ironSupplement = ironSupplement;
		this.folicAcidSupplement = folicAcidSupplement;
		this.vitASupplement = vitASupplement;
		this.otherSupplements = otherSupplements;
		this.transfusion = transfusion;
		this.lastTransfusionDate = lastTransfusionDate;
		this.sickleCell = sickleCell;
		this.drugAllergy = drugAllergy;
		this.allergyPrecision = allergyPrecision;
		this.hemylosis = hemylosis;
		this.otherPersonalPathologies = otherPersonalPathologies;
		this.otherFamilyPathologies = otherFamilyPathologies;
		this.performedAt = performedAt;
		this.lock = lock;
		this.hashCode = hashCode;
	}

	public MedicalHistory(
		Integer id,
		Patient patient,
		String  siblingRank,
		String termPregnancy,
		String  pregnancy,
		String deliveryMode,
		String apgarScore,
		Double birthWeight,
		String diet,
		Boolean deParasitization,
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
		String otherFamilyPathology,
		LocalDateTime performedAt
	) {
		this.id = id;
		this.patient = patient;
		this.siblingRank = siblingRank;
		this.termPregnancy = termPregnancy;
		this.pregnancy = pregnancy;
		this.deliveryMode = deliveryMode;
		this.apgarScore = apgarScore;
		this.birthWeight = birthWeight;
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
		this.performedAt = performedAt;
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

	public String getSiblingRank() {
		return siblingRank;
	}

	public void setSiblingRank(String siblingRank) {
		this.siblingRank = siblingRank;
	}

	public String getTermPregnancy() {
		return termPregnancy;
	}

	public void setTermPregnancy(String termPregnancy) {
		this.termPregnancy = termPregnancy;
	}

	public String getPregnancy() {return pregnancy;}

	public void setPregnancy(String pregnancy) {this.pregnancy = pregnancy;}

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

	public String getDiet() {
		return diet;
	}

	public void setDiet(String diet) {
		this.diet = diet;
	}

	public Boolean getDeParasitization() {
		return deParasitization;
	}

	public void setDeParasitization(Boolean deParasitization) {
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

	public LocalDateTime getPerformedAt() {
		return performedAt;
	}

	public void setPerformedAt(LocalDateTime performedAt) {
		this.performedAt = performedAt;
	}

	public int getLock() {
		return lock;
	}

	public void setLock(int lock) {
		this.lock = lock;
	}

	public String getReasonMode() {
		return reasonMode;
	}

	public void setReasonMode(String reasonMode) {
		this.reasonMode = reasonMode;
	}

	public String getVaccinationStatePev() {
		return vaccinationStatePev;
	}

	public void setVaccinationStatePev(String vaccinationStatePev) {
		this.vaccinationStatePev = vaccinationStatePev;
	}

	public String getVaccinationStateNoPev() {
		return vaccinationStateNoPev;
	}

	public void setVaccinationStateNoPev(String vaccinationStateNoPev) {
		this.vaccinationStateNoPev = vaccinationStateNoPev;
	}

	public String getAntiMalarialProphylaxisMilda() {
		return antiMalarialProphylaxisMilda;
	}

	public void setAntiMalarialProphylaxisMilda(String antiMalarialProphylaxisMilda) {
		this.antiMalarialProphylaxisMilda = antiMalarialProphylaxisMilda;
	}

	public String getAntiMalarialProphylaxisVap() {
		return antiMalarialProphylaxisVap;
	}

	public void setAntiMalarialProphylaxisVap(String antiMalarialProphylaxisVap) {
		this.antiMalarialProphylaxisVap = antiMalarialProphylaxisVap;
	}

	public String getAntiMalarialProphylaxisOthers() {
		return antiMalarialProphylaxisOthers;
	}

	public void setAntiMalarialProphylaxisOthers(String antiMalarialProphylaxisOthers) {
		this.antiMalarialProphylaxisOthers = antiMalarialProphylaxisOthers;
	}

	public Boolean getSurgicalProcedure() {
		return surgicalProcedure;
	}

	public void setSurgicalProcedure(Boolean surgicalProcedure) {
		this.surgicalProcedure = surgicalProcedure;
	}

	public String getSurgicalProcedureCondition() {
		return surgicalProcedureCondition;
	}

	public void setSurgicalProcedureCondition(String surgicalProcedureCondition) {
		this.surgicalProcedureCondition = surgicalProcedureCondition;
	}

	public String getSurgicalProcedureType() {
		return surgicalProcedureType;
	}

	public void setSurgicalProcedureType(String surgicalProcedureType) {
		this.surgicalProcedureType = surgicalProcedureType;
	}

	public LocalDateTime getSurgicalProcedureDate() {
		return surgicalProcedureDate;
	}

	public void setSurgicalProcedureDate(LocalDateTime surgicalProcedureDate) {
		this.surgicalProcedureDate = surgicalProcedureDate;
	}

	public String getDiversification() {
		return diversification;
	}

	public void setDiversification(String diversification) {
		this.diversification = diversification;
	}

	public String getNeonatalPeriod() {
		return neonatalPeriod;
	}

	public void setNeonatalPeriod(String neonatalPeriod) {
		this.neonatalPeriod = neonatalPeriod;
	}

	public String getPreviousHospitalization() {
		return previousHospitalization;
	}

	public void setPreviousHospitalization(String previousHospitalization) {
		this.previousHospitalization = previousHospitalization;
	}

	public String getFather() {
		return father;
	}

	public void setFather(String father) {
		this.father = father;
	}

	public String getMother() {
		return mother;
	}

	public void setMother(String mother) {
		this.mother = mother;
	}

	public String getSiblings() {
		return siblings;
	}

	public void setSiblings(String siblings) {
		this.siblings = siblings;
	}

	public String getOtherUsefulInformation() {
		return otherUsefulInformation;
	}

	public void setOtherUsefulInformation(String otherUsefulInformation) {
		this.otherUsefulInformation = otherUsefulInformation;
	}

	public int getHashCode() {
		return hashCode;
	}

	public void setHashCode(int hashCode) {
		this.hashCode = hashCode;
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
