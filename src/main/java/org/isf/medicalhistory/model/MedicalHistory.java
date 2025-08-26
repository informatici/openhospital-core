package org.isf.medicalhistory.model;

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
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "OH_MEDICALHISTORY")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "MH_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "MH_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "MH_LAST_MODIFIED_BY"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "MH_LAST_MODIFIED_DATE"))
public class MedicalHistory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MH_PAT_ID", nullable = false)
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

	@Column(name = "MH_SICLE_CELL")
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
}
