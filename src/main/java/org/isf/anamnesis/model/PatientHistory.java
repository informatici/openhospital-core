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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isf.anamnesis.model;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * ------------------------------------------ PatientHistory - model for a anamnesis -----------------------------------------
 */
@Entity
@Table(name = "OH_PATIENTHISTORY")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverrides({ @AttributeOverride(name = "createdBy", column = @Column(name = "PAH_CREATED_BY")),
		@AttributeOverride(name = "createdDate", column = @Column(name = "PAH_CREATED_DATE")),
		@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "PAH_LAST_MODIFIED_BY")),
		@AttributeOverride(name = "active", column = @Column(name = "PAH_ACTIVE")),
		@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "PAH_LAST_MODIFIED_DATE")) })
public class PatientHistory extends Auditable<String> implements Comparable<PatientHistory> {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "PAH_ID")
	private int id;

	@Column(name = "PAH_PAT_ID")
	private int patientId;

	@Column(name = "PAH_FAM_NOTHING")
	@ColumnDefault("false")
	private boolean familyNothing;

	@Column(name = "PAH_FAM_HYPER")
	@ColumnDefault("false")
	private boolean familyHypertension;

	@Column(name = "PAH_FAM_DRUGADD")
	@ColumnDefault("false")
	private boolean familyDrugAddiction;

	@Column(name = "PAH_FAM_CARDIO")
	@ColumnDefault("false")
	private boolean familyCardiovascular;

	@Column(name = "PAH_FAM_INFECT")
	@ColumnDefault("false")
	private boolean familyInfective;

	@Column(name = "PAH_FAM_ENDO")
	@ColumnDefault("false")
	private boolean familyEndocrinometabol;

	@Column(name = "PAH_FAM_RESP")
	@ColumnDefault("false")
	private boolean familyRespiratory;

	@Column(name = "PAH_FAM_CANCER")
	@ColumnDefault("false")
	private boolean familyCancer;

	@Column(name = "PAH_FAM_ORTO")
	@ColumnDefault("false")
	private boolean familyOrto;

	@Column(name = "PAH_FAM_GYNO")
	@ColumnDefault("false")
	private boolean familyGyno;

	@Column(name = "PAH_FAM_OTHER")
	@ColumnDefault("false")
	private boolean familyOther;

	@Column(name = "PAH_FAM_NOTE")
	private String familyNote;

	@Column(name = "PAH_PAT_CLO_NOTHING")
	@ColumnDefault("false")
	private boolean patClosedNothing;

	@Column(name = "PAH_PAT_CLO_HYPER")
	@ColumnDefault("false")
	private boolean patClosedHypertension;

	@Column(name = "PAH_PAT_CLO_DRUGADD")
	@ColumnDefault("false")
	private boolean patClosedDrugaddiction;

	@Column(name = "PAH_PAT_CLO_CARDIO")
	@ColumnDefault("false")
	private boolean patClosedCardiovascular;

	@Column(name = "PAH_PAT_CLO_INFECT")
	@ColumnDefault("false")
	private boolean patClosedInfective;

	@Column(name = "PAH_PAT_CLO_ENDO")
	@ColumnDefault("false")
	private boolean patClosedEndocrinometabol;

	@Column(name = "PAH_PAT_CLO_RESP")
	@ColumnDefault("false")
	private boolean patClosedRespiratory;

	@Column(name = "PAH_PAT_CLO_CANCER")
	@ColumnDefault("false")
	private boolean patClosedCancer;

	@Column(name = "PAH_PAT_CLO_ORTO")
	@ColumnDefault("false")
	private boolean patClosedOrto;

	@Column(name = "PAH_PAT_CLO_GYNO")
	@ColumnDefault("false")
	private boolean patClosedGyno;

	@Column(name = "PAH_PAT_CLO_OTHER")
	@ColumnDefault("false")
	private boolean patClosedOther;

	@Column(name = "PAH_PAT_CLO_NOTE")
	private String patClosedNote;

	@Column(name = "PAH_PAT_OPN_NOTHING")
	@ColumnDefault("false")
	private boolean patOpenNothing;

	@Column(name = "PAH_PAT_OPN_HYPER")
	@ColumnDefault("false")
	private boolean patOpenHypertension;

	@Column(name = "PAH_PAT_OPN_DRUGADD")
	@ColumnDefault("false")
	private boolean patOpenDrugaddiction;

	@Column(name = "PAH_PAT_OPN_CARDIO")
	@ColumnDefault("false")
	private boolean patOpenCardiovascular;

	@Column(name = "PAH_PAT_OPN_INFECT")
	@ColumnDefault("false")
	private boolean patOpenInfective;

	@Column(name = "PAH_PAT_OPN_ENDO")
	@ColumnDefault("false")
	private boolean patOpenEndocrinometabol;

	@Column(name = "PAH_PAT_OPN_RESP")
	@ColumnDefault("false")
	private boolean patOpenRespiratory;

	@Column(name = "PAH_PAT_OPN_CANCER")
	@ColumnDefault("false")
	private boolean patOpenCancer;

	@Column(name = "PAH_PAT_OPN_ORTO")
	@ColumnDefault("false")
	private boolean patOpenOrto;

	@Column(name = "PAH_PAT_OPN_GYNO")
	@ColumnDefault("false")
	private boolean patOpenGyno;

	@Column(name = "PAH_PAT_OPN_OTHER")
	@ColumnDefault("false")
	private boolean patOpenOther;

	@Column(name = "PAH_PAT_OPN_NOTE")
	private String patOpenNote;

	@Column(name = "PAH_PAT_SURGERY")
	private String patSurgery;

	@Column(name = "PAH_PAT_ALLERGY")
	private String patAllergy;

	@Column(name = "PAH_PAT_THERAPY")
	private String patTherapy;

	@Column(name = "PAH_PAT_MEDICINE")
	private String patMedicine;

	@Column(name = "PAH_PAT_NOTE")
	private String patNote;

	@Column(name = "PAH_PHY_NUTR_NOR")
	@ColumnDefault("true")

	private boolean phyNutritionNormal = true;

	@Column(name = "PAH_PHY_NUTR_ABN")
	private String phyNutritionAbnormal;

	@Column(name = "PAH_PHY_ALVO_NOR")
	@ColumnDefault("true")
	private boolean phyAlvoNormal = true;

	@Column(name = "PAH_PHY_ALVO_ABN")
	private String phyAlvoAbnormal;

	@Column(name = "PAH_PHY_DIURE_NOR")
	@ColumnDefault("true")
	private boolean phyDiuresisNormal = true;

	@Column(name = "PAH_PHY_DIURE_ABN")
	private String phyDiuresisAbnormal;

	@Column(name = "PAH_PHY_ALCOOL")
	@ColumnDefault("false")
	private boolean phyAlcool;

	@Column(name = "PAH_PHY_SMOKE")
	@ColumnDefault("false")
	private boolean phySmoke;

	@Column(name = "PAH_PHY_DRUG")
	@ColumnDefault("false")
	private boolean phyDrug;

	@Column(name = "PAH_PHY_PERIOD_NOR")
	@ColumnDefault("true")
	private boolean phyPeriodNormal = true;

	@Column(name = "PAH_PHY_PERIOD_ABN")
	private String phyPeriodAbnormal;

	@Column(name = "PAH_PHY_MENOP")
	@ColumnDefault("false")
	private boolean phyMenopause;

	@Column(name = "PAH_PHY_MENOP_Y")
	private int phyMenopauseYears;

	@Column(name = "PAH_PHY_HRT_NOR")
	@ColumnDefault("true")
	private boolean phyHrtNormal = true;

	@Column(name = "PAH_PHY_HRT_ABN")
	private String phyHrtAbnormal;

	@Column(name = "PAH_PHY_PREG")
	@ColumnDefault("false")
	private boolean phyPregnancy;

	@Column(name = "PAH_PHY_PREG_N")
	private int phyPregnancyNumber;

	@Column(name = "PAH_PHY_PREG_BIRTH")
	private int phyPregnancyBirth;

	@Column(name = "PAH_PHY_PREG_ABORT")
	private int phyPregnancyAbort;

	@Override
	public int compareTo(PatientHistory obj) {
		return this.id - obj.getId();
	}

	public int getPatientId() {
		return patientId;
	}

	public void setPatientId(int patientId) {
		this.patientId = patientId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isFamilyNothing() {
		return familyNothing;
	}

	public void setFamilyNothing(boolean familyNothing) {
		this.familyNothing = familyNothing;
	}

	public boolean isFamilyHypertension() {
		return familyHypertension;
	}

	public void setFamilyHypertension(boolean familyHypertension) {
		this.familyHypertension = familyHypertension;
	}

	public boolean isFamilyDrugAddiction() {
		return familyDrugAddiction;
	}

	public void setFamilyDrugAddiction(boolean familyDrugAddiction) {
		this.familyDrugAddiction = familyDrugAddiction;
	}

	public boolean isFamilyCardiovascular() {
		return familyCardiovascular;
	}

	public void setFamilyCardiovascular(boolean familyCardiovascular) {
		this.familyCardiovascular = familyCardiovascular;
	}

	public boolean isFamilyInfective() {
		return familyInfective;
	}

	public void setFamilyInfective(boolean familyInfective) {
		this.familyInfective = familyInfective;
	}

	public boolean isFamilyEndocrinometabol() {
		return familyEndocrinometabol;
	}

	public void setFamilyEndocrinometabol(boolean familyEndocrinometabol) {
		this.familyEndocrinometabol = familyEndocrinometabol;
	}

	public boolean isFamilyRespiratory() {
		return familyRespiratory;
	}

	public void setFamilyRespiratory(boolean familyRespiratory) {
		this.familyRespiratory = familyRespiratory;
	}

	public boolean isFamilyCancer() {
		return familyCancer;
	}

	public void setFamilyCancer(boolean familyCancer) {
		this.familyCancer = familyCancer;
	}

	public boolean isFamilyOrto() {
		return familyOrto;
	}

	public void setFamilyOrto(boolean familyOrto) {
		this.familyOrto = familyOrto;
	}

	public boolean isFamilyGyno() {
		return familyGyno;
	}

	public void setFamilyGyno(boolean familyGyno) {
		this.familyGyno = familyGyno;
	}

	public boolean isFamilyOther() {
		return familyOther;
	}

	public void setFamilyOther(boolean familyOther) {
		this.familyOther = familyOther;
	}

	public String getFamilyNote() {
		return familyNote;
	}

	public void setFamilyNote(String familyNote) {
		this.familyNote = familyNote;
	}

	public boolean isPatClosedNothing() {
		return patClosedNothing;
	}

	public void setPatClosedNothing(boolean patClosedNothing) {
		this.patClosedNothing = patClosedNothing;
	}

	public boolean isPatClosedHypertension() {
		return patClosedHypertension;
	}

	public void setPatClosedHypertension(boolean patClosedHypertension) {
		this.patClosedHypertension = patClosedHypertension;
	}

	public boolean isPatClosedDrugaddiction() {
		return patClosedDrugaddiction;
	}

	public void setPatClosedDrugaddiction(boolean patClosedDrugaddiction) {
		this.patClosedDrugaddiction = patClosedDrugaddiction;
	}

	public boolean isPatClosedCardiovascular() {
		return patClosedCardiovascular;
	}

	public void setPatClosedCardiovascular(boolean patClosedCardiovascular) {
		this.patClosedCardiovascular = patClosedCardiovascular;
	}

	public boolean isPatClosedInfective() {
		return patClosedInfective;
	}

	public void setPatClosedInfective(boolean patClosedInfective) {
		this.patClosedInfective = patClosedInfective;
	}

	public boolean isPatClosedEndocrinometabol() {
		return patClosedEndocrinometabol;
	}

	public void setPatClosedEndocrinometabol(boolean patClosedEndocrinometabol) {
		this.patClosedEndocrinometabol = patClosedEndocrinometabol;
	}

	public boolean isPatClosedRespiratory() {
		return patClosedRespiratory;
	}

	public void setPatClosedRespiratory(boolean patClosedRespiratory) {
		this.patClosedRespiratory = patClosedRespiratory;
	}

	public boolean isPatClosedCancer() {
		return patClosedCancer;
	}

	public void setPatClosedCancer(boolean patClosedCancer) {
		this.patClosedCancer = patClosedCancer;
	}

	public boolean isPatClosedOrto() {
		return patClosedOrto;
	}

	public void setPatClosedOrto(boolean patClosedOrto) {
		this.patClosedOrto = patClosedOrto;
	}

	public boolean isPatClosedGyno() {
		return patClosedGyno;
	}

	public void setPatClosedGyno(boolean patClosedGyno) {
		this.patClosedGyno = patClosedGyno;
	}

	public boolean isPatClosedOther() {
		return patClosedOther;
	}

	public void setPatClosedOther(boolean patClosedOther) {
		this.patClosedOther = patClosedOther;
	}

	public String getPatClosedNote() {
		return patClosedNote;
	}

	public void setPatClosedNote(String patClosedNote) {
		this.patClosedNote = patClosedNote;
	}

	public String getPatSurgery() {
		return patSurgery;
	}

	public void setPatSurgery(String patSurgery) {
		this.patSurgery = patSurgery;
	}

	public String getPatAllergy() {
		return patAllergy;
	}

	public void setPatAllergy(String patAllergy) {
		this.patAllergy = patAllergy;
	}

	public String getPatTherapy() {
		return patTherapy;
	}

	public void setPatTherapy(String patTherapy) {
		this.patTherapy = patTherapy;
	}

	public String getPatMedicine() {
		return patMedicine;
	}

	public void setPatMedicine(String patMedicine) {
		this.patMedicine = patMedicine;
	}

	public String getPatNote() {
		return patNote;
	}

	public void setPatNote(String patNote) {
		this.patNote = patNote;
	}

	public boolean isPhyNutritionNormal() {
		return phyNutritionNormal;
	}

	public void setPhyNutritionNormal(boolean phyNutritionNormal) {
		this.phyNutritionNormal = phyNutritionNormal;
	}

	public String getPhyNutritionAbnormal() {
		return phyNutritionAbnormal;
	}

	public void setPhyNutritionAbnormal(String phyNutritionAbnormal) {
		this.phyNutritionAbnormal = phyNutritionAbnormal;
	}

	public boolean isPhyAlvoNormal() {
		return phyAlvoNormal;
	}

	public void setPhyAlvoNormal(boolean phyAlvoNormal) {
		this.phyAlvoNormal = phyAlvoNormal;
	}

	public String getPhyAlvoAbnormal() {
		return phyAlvoAbnormal;
	}

	public void setPhyAlvoAbnormal(String phyAlvoAbnormal) {
		this.phyAlvoAbnormal = phyAlvoAbnormal;
	}

	public boolean isPhyDiuresisNormal() {
		return phyDiuresisNormal;
	}

	public void setPhyDiuresisNormal(boolean phyDiuresisNormal) {
		this.phyDiuresisNormal = phyDiuresisNormal;
	}

	public String getPhyDiuresisAbnormal() {
		return phyDiuresisAbnormal;
	}

	public void setPhyDiuresisAbnormal(String phyDiuresisAbnormal) {
		this.phyDiuresisAbnormal = phyDiuresisAbnormal;
	}

	public boolean isPhyAlcool() {
		return phyAlcool;
	}

	public void setPhyAlcool(boolean phyAlcool) {
		this.phyAlcool = phyAlcool;
	}

	public boolean isPhySmoke() {
		return phySmoke;
	}

	public void setPhySmoke(boolean phySmoke) {
		this.phySmoke = phySmoke;
	}

	public boolean isPhyDrug() {
		return phyDrug;
	}

	public void setPhyDrug(boolean phyDrug) {
		this.phyDrug = phyDrug;
	}

	public boolean isPhyPeriodNormal() {
		return phyPeriodNormal;
	}

	public void setPhyPeriodNormal(boolean phyPeriodNormal) {
		this.phyPeriodNormal = phyPeriodNormal;
	}

	public String getPhyPeriodAbnormal() {
		return phyPeriodAbnormal;
	}

	public void setPhyPeriodAbnormal(String phyPeriodAbnormal) {
		this.phyPeriodAbnormal = phyPeriodAbnormal;
	}

	public boolean isPhyMenopause() {
		return phyMenopause;
	}

	public void setPhyMenopause(boolean phyMenopause) {
		this.phyMenopause = phyMenopause;
	}

	public int getPhyMenopauseYears() {
		return phyMenopauseYears;
	}

	public void setPhyMenopauseYears(int phyMenopauseYears) {
		this.phyMenopauseYears = phyMenopauseYears;
	}

	public boolean isPhyHrtNormal() {
		return phyHrtNormal;
	}

	public void setPhyHrtNormal(boolean phyHrtNormal) {
		this.phyHrtNormal = phyHrtNormal;
	}

	public String getPhyHrtAbnormal() {
		return phyHrtAbnormal;
	}

	public void setPhyHrtAbnormal(String phyHrtAbnormal) {
		this.phyHrtAbnormal = phyHrtAbnormal;
	}

	public boolean isPhyPregnancy() {
		return phyPregnancy;
	}

	public void setPhyPregnancy(boolean phyPregnancy) {
		this.phyPregnancy = phyPregnancy;
	}

	public int getPhyPregnancyNumber() {
		return phyPregnancyNumber;
	}

	public void setPhyPregnancyNumber(int phyPregnancyNumber) {
		this.phyPregnancyNumber = phyPregnancyNumber;
	}

	public int getPhyPregnancyBirth() {
		return phyPregnancyBirth;
	}

	public void setPhyPregnancyBirth(int phyPregnancyBirth) {
		this.phyPregnancyBirth = phyPregnancyBirth;
	}

	public int getPhyPregnancyAbort() {
		return phyPregnancyAbort;
	}

	public void setPhyPregnancyAbort(int phyPregnancyAbort) {
		this.phyPregnancyAbort = phyPregnancyAbort;
	}

	public boolean isPatOpenNothing() {
		return patOpenNothing;
	}

	public void setPatOpenNothing(boolean patOpenNothing) {
		this.patOpenNothing = patOpenNothing;
	}

	public boolean isPatOpenHypertension() {
		return patOpenHypertension;
	}

	public void setPatOpenHypertension(boolean patOpenHypertension) {
		this.patOpenHypertension = patOpenHypertension;
	}

	public boolean isPatOpenDrugaddiction() {
		return patOpenDrugaddiction;
	}

	public void setPatOpenDrugaddiction(boolean patOpenDrugaddiction) {
		this.patOpenDrugaddiction = patOpenDrugaddiction;
	}

	public boolean isPatOpenCardiovascular() {
		return patOpenCardiovascular;
	}

	public void setPatOpenCardiovascular(boolean patOpenCardiovascular) {
		this.patOpenCardiovascular = patOpenCardiovascular;
	}

	public boolean isPatOpenInfective() {
		return patOpenInfective;
	}

	public void setPatOpenInfective(boolean patOpenInfective) {
		this.patOpenInfective = patOpenInfective;
	}

	public boolean isPatOpenEndocrinometabol() {
		return patOpenEndocrinometabol;
	}

	public void setPatOpenEndocrinometabol(boolean patOpenEndocrinometabol) {
		this.patOpenEndocrinometabol = patOpenEndocrinometabol;
	}

	public boolean isPatOpenRespiratory() {
		return patOpenRespiratory;
	}

	public void setPatOpenRespiratory(boolean patOpenRespiratory) {
		this.patOpenRespiratory = patOpenRespiratory;
	}

	public boolean isPatOpenCancer() {
		return patOpenCancer;
	}

	public void setPatOpenCancer(boolean patOpenCancer) {
		this.patOpenCancer = patOpenCancer;
	}

	public boolean isPatOpenOrto() {
		return patOpenOrto;
	}

	public void setPatOpenOrto(boolean patOpenOrto) {
		this.patOpenOrto = patOpenOrto;
	}

	public boolean isPatOpenGyno() {
		return patOpenGyno;
	}

	public void setPatOpenGyno(boolean patOpenGyno) {
		this.patOpenGyno = patOpenGyno;
	}

	public boolean isPatOpenOther() {
		return patOpenOther;
	}

	public void setPatOpenOther(boolean patOpenOther) {
		this.patOpenOther = patOpenOther;
	}

	public String getPatOpenNote() {
		return patOpenNote;
	}

	public void setPatOpenNote(String patOpenNote) {
		this.patOpenNote = patOpenNote;
	}


}