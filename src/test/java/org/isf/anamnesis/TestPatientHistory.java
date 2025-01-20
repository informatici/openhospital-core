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
package org.isf.anamnesis;

import org.isf.anamnesis.model.PatientHistory;

public class TestPatientHistory {

	public PatientHistory setup() throws Exception {
		PatientHistory patientHistory = new PatientHistory();

		patientHistory.setPatientId(1);
		patientHistory.setFamilyNothing(true);
		patientHistory.setFamilyHypertension(false);
		patientHistory.setFamilyDrugAddiction(true);
		patientHistory.setFamilyCardiovascular(false);
		patientHistory.setFamilyInfective(true);
		patientHistory.setFamilyEndocrinometabol(false);
		patientHistory.setFamilyRespiratory(true);
		patientHistory.setFamilyCancer(false);
		patientHistory.setFamilyOrto(true);
		patientHistory.setFamilyGyno(false);
		patientHistory.setFamilyOrto(true);
		patientHistory.setFamilyOther(false);
		patientHistory.setFamilyNote("Family Note");
		patientHistory.setPatClosedNothing(false);
		patientHistory.setPatClosedHypertension(true);
		patientHistory.setPatClosedDrugaddiction(false);
		patientHistory.setPatClosedCardiovascular(false);
		patientHistory.setPatClosedInfective(true);
		patientHistory.setPatClosedEndocrinometabol(false);
		patientHistory.setPatClosedRespiratory(true);
		patientHistory.setPatClosedCancer(false);
		patientHistory.setPatClosedGyno(true);
		patientHistory.setPatClosedOrto(true);
		patientHistory.setPatClosedOther(false);
		patientHistory.setPatClosedNote("Closed Note");
		patientHistory.setPatOpenNothing(true);
		patientHistory.setPatOpenHypertension(false);
		patientHistory.setPatOpenDrugaddiction(true);
		patientHistory.setPatOpenCardiovascular(false);
		patientHistory.setPatOpenInfective(true);
		patientHistory.setPatOpenEndocrinometabol(false);
		patientHistory.setPatOpenRespiratory(true);
		patientHistory.setPatOpenGyno(false);
		patientHistory.setPatOpenOther(true);
		patientHistory.setPatOpenCancer(false);
		patientHistory.setPatOpenOrto(true);
		patientHistory.setPatOpenNote("Open Note");
		patientHistory.setPatNote("Open Note");
		patientHistory.setPatSurgery("Surgery");
		patientHistory.setPatAllergy("Allergy");
		patientHistory.setPatTherapy("Therapy");
		patientHistory.setPatMedicine("Medicine");
		patientHistory.setPatNote("Patient Note");
		patientHistory.setPhyNutritionNormal(true);
		patientHistory.setPhyNutritionAbnormal("Nutrition Abnormal");
		patientHistory.setPhyBowelNormal(false);
		patientHistory.setPhyBowelAbnormal("Bowel Abnormal");
		patientHistory.setPhyDiuresisNormal(true);
		patientHistory.setPhyDiuresisAbnormal("Diuresis Abnormal");
		patientHistory.setPhyAlcohol(false);
		patientHistory.setPhySmoke(true);
		patientHistory.setPhyDrug(false);
		patientHistory.setPhyPeriodNormal(true);
		patientHistory.setPhyPeriodAbnormal("Period Abnormal");
		patientHistory.setPhyMenopause(false);
		patientHistory.setPhyMenopauseYears(0);
		patientHistory.setPhyHrtNormal(true);
		patientHistory.setPhyHrtAbnormal("Hrt Abnormal");
		patientHistory.setPhyPregnancy(false);
		patientHistory.setPhyPregnancyNumber(0);
		patientHistory.setPhyPregnancyBirth(0);
		patientHistory.setPhyPregnancyAbort(0);

		return patientHistory;
	}
}
