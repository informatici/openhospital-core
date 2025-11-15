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
package org.isf.admission.model;

import java.time.LocalDateTime;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EntityResult;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;

import org.isf.admtype.model.AdmissionType;
import org.isf.disctype.model.DischargeType;
import org.isf.disease.model.Disease;
import org.isf.dlvrrestype.model.DeliveryResultType;
import org.isf.dlvrtype.model.DeliveryType;
import org.isf.patient.model.Patient;
import org.isf.pregtreattype.model.PregnantTreatmentType;
import org.isf.utils.db.Auditable;
import org.isf.utils.time.TimeTools;
import org.isf.ward.model.Ward;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name="OH_ADMISSION")
@SqlResultSetMapping(name="AdmittedPatient",
entities={
		@EntityResult(entityClass=Patient.class),
		@EntityResult(entityClass=Admission.class)}
)
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name="createdBy", column=@Column(name="ADM_CREATED_BY", updatable = false))
@AttributeOverride(name="createdDate", column=@Column(name="ADM_CREATED_DATE", updatable = false))
@AttributeOverride(name="lastModifiedBy", column=@Column(name="ADM_LAST_MODIFIED_BY"))
@AttributeOverride(name="active", column=@Column(name="ADM_ACTIVE"))
@AttributeOverride(name="lastModifiedDate", column=@Column(name="ADM_LAST_MODIFIED_DATE"))
public class Admission extends Auditable<String> implements Comparable<Admission> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ADM_ID")
	private int id;                            // admission key

	@NotNull
	@Column(name = "ADM_IN")
	private int admitted;                    // values are 0 or 1, default 0 (not admitted)

	@NotNull
	@Column(name = "ADM_TYPE")
	private String type;                    // values are 'N'(normal)  or 'M' (malnutrition)  default 'N'

	@NotNull
	@ManyToOne
	@JoinColumn(name = "ADM_WRD_ID_A")
	private Ward ward;                        // ward key

	@NotNull
	@Column(name = "ADM_YPROG")
	private int yProg;                        // a progr. in year for each ward

	@NotNull
	@ManyToOne
	@JoinColumn(name = "ADM_PAT_ID")
	private Patient patient;                // patient key

	@NotNull
	@Column(name = "ADM_DATE_ADM")        // SQL type: datetime
	private LocalDateTime admDate;        // admission date

	@NotNull
	@ManyToOne
	@JoinColumn(name = "ADM_ADMT_ID_A_ADM")
	private AdmissionType admissionType;    // admissionType key

	@Column(name = "ADM_FHU")
	private String fHU;                        // FromHealthUnit (null)

	@ManyToOne
	@JoinColumn(name = "ADM_IN_DIS_ID_A")
	private Disease diseaseIn;                // disease in key  (null)

	@ManyToOne
	@JoinColumn(name = "ADM_OUT_DIS_ID_A")
	private Disease diseaseOut1;            // disease out key  (null)

	@ManyToOne
	@JoinColumn(name = "ADM_OUT_DIS_ID_A_2")
	private Disease diseaseOut2;            // disease out key (null)

	@ManyToOne
	@JoinColumn(name = "ADM_OUT_DIS_ID_A_3")
	private Disease diseaseOut3;            // disease out key (null)

	@Column(name = "ADM_DATE_DIS")        // SQL type: datetime
	private LocalDateTime disDate;        // discharge date (null)

	@ManyToOne
	@JoinColumn(name = "ADM_DIST_ID_A")
	private DischargeType disType;            // disChargeType key (null)

	@Column(name = "ADM_NOTE")
	private String anamnesis;                    // anamnesis (null)

	@Column(name = "ADM_TRANS")
	private Float transUnit;                // transfusional unit

	@Column(name = "ADM_PRG_DATE_VIS")        // SQL type: datetime
	private LocalDateTime visitDate;    // ADM_PRG_DATE_VIS

	@ManyToOne
	@JoinColumn(name = "ADM_PRG_PTT_ID_A")
	private PregnantTreatmentType pregTreatmentType;        // ADM_PRG_PTT_ID_A treatmentType key

	@Column(name = "ADM_PRG_DATE_DEL")        // SQL type: datetime
	private LocalDateTime deliveryDate;    // ADM_PRG_DATE_DEL delivery date

	@ManyToOne
	@JoinColumn(name = "ADM_PRG_DLT_ID_A")
	private DeliveryType deliveryType;        // ADM_PRG_DLT_ID_A delivery type key

	@ManyToOne
	@JoinColumn(name = "ADM_PRG_DRT_ID_A")
	private DeliveryResultType deliveryResult;        // ADM_PRG_DRT_ID_A	delivery res. key

	@Column(name = "ADM_PRG_WEIGHT")
	private Float weight;                    // ADM_PRG_WEIGHT	weight

	@Column(name = "ADM_PRG_DATE_CTRL1")        // SQL type: datetime
	private LocalDateTime ctrlDate1;    // ADM_PRG_DATE_CTRL1

	@Column(name = "ADM_PRG_DATE_CTRL2")        // SQL type: datetime
	private LocalDateTime ctrlDate2;    // ADM_PRG_DATE_CTRL2

	@Column(name = "ADM_PRG_DATE_ABORT")        // SQL type: datetime
	private LocalDateTime abortDate;    // ADM_PRG_DATE_ABORT

	/**
	 * Treatment received by the patient before admission.
	 * <p>
	 * Example: medication, outpatient care, prior therapy.
	 */
	@Column(
		name = "ADM_PRE_TREATMENT",
		columnDefinition = "TEXT"
	)
	private String preTreatment;

	/**
	 * Assessment performed before the patient's admission.
	 * <p>
	 * Example: preliminary examinations, lab results, medical observations.
	 */
	@Column(
		name = "ADM_PRE_ASSESSMENT",
		columnDefinition = "TEXT"
	)
	private String preAssessment;

	@Column(name = "ADM_ALERT_RECEIVED")
	private Boolean alertReceived;

	@Column(name = "ADM_REFERENCE_SHEET")
	private Boolean referenceSheet;

	@Column(name = "ADM_QUALIFIED_AGENT")
	private Boolean qualifiedAgent;

	@Column(name = "ADM_USR_ID_A")
	private String userID;                    // the user ID

	@Version
	@Column(name = "ADM_LOCK")
	private int lock;                        // default 0

	@NotNull
	@Column(name = "ADM_DELETED", columnDefinition = "char(1) default 'N'")
	private char deleted = 'N';                // flag record deleted ; values are 'Y' OR 'N' default is 'N'

	@Transient
	private volatile int hashCode;

	@Column(name = "ADM_ENTRY_REASON")
	private String entryReason;

	@Column(name="ADM_TRANSPORTATION")
	private String transportation;
	
	@Column(name = "ADM_PHYSICAL_EXAM")
	private String physicalExam;    // ADM_PHYSICAL_EXAM

	public Admission() {
		super();
	}

	/**
	 * @param id
	 * @param admitted
	 * @param type
	 * @param ward
	 * @param prog
	 * @param patient
	 * @param admDate
	 * @param admType
	 * @param fhu
	 * @param diseaseIn
	 * @param diseaseOut1
	 * @param diseaseOut2
	 * @param diseaseOut3
	 * @param disDate
	 * @param disType
	 * @param anamnesis
	 * @param transUnit
	 * @param visitDate
	 * @param pregTreatmentType
	 * @param deliveryDate
	 * @param deliveryType
	 * @param deliveryResult
	 * @param weight
	 * @param ctrlDate1
	 * @param ctrlDate2
	 * @param abortDate
	 * @param userID
	 * @param deleted
	 */
	public Admission(int id, int admitted, String type, Ward ward, int prog, Patient patient, LocalDateTime admDate, AdmissionType admType, String fhu,
					 Disease diseaseIn, Disease diseaseOut1, Disease diseaseOut2, Disease diseaseOut3,
					 LocalDateTime disDate, DischargeType disType, String anamnesis, Float transUnit, LocalDateTime visitDate,
					 PregnantTreatmentType pregTreatmentType, LocalDateTime deliveryDate, DeliveryType deliveryType, DeliveryResultType deliveryResult, Float weight,
					 LocalDateTime ctrlDate1, LocalDateTime ctrlDate2,
					 LocalDateTime abortDate, String userID, char deleted) {
		super();
		this.id = id;
		this.admitted = admitted;
		this.type = type;
		this.ward = ward;
		this.yProg = prog;
		this.patient = patient;
		this.admDate = TimeTools.truncateToSeconds(admDate);
		this.admissionType = admType;
		this.fHU = fhu;
		this.diseaseIn = diseaseIn;
		this.diseaseOut1 = diseaseOut1;
		this.diseaseOut2 = diseaseOut2;
		this.diseaseOut3 = diseaseOut3;
		this.disDate = TimeTools.truncateToSeconds(disDate);
		this.disType = disType;
		this.anamnesis = anamnesis;
		this.transUnit = transUnit;
		this.visitDate = TimeTools.truncateToSeconds(visitDate);
		this.pregTreatmentType = pregTreatmentType;
		this.deliveryDate = TimeTools.truncateToSeconds(deliveryDate);
		this.deliveryType = deliveryType;
		this.deliveryResult = deliveryResult;
		this.weight = weight;
		this.ctrlDate1 = TimeTools.truncateToSeconds(ctrlDate1);
		this.ctrlDate2 = TimeTools.truncateToSeconds(ctrlDate2);
		this.abortDate = TimeTools.truncateToSeconds(abortDate);
		this.userID = userID;
		this.deleted = deleted;
	}

	/**
	 * Creates a new Admission with all attributes including pre-admission treatment and assessment.
	 *
	 * @param id               the unique identifier of the admission
	 * @param admitted         flag indicating if the patient is admitted (1 = admitted, 0 = not admitted)
	 * @param type             the type of admission (e.g., emergency, scheduled)
	 * @param ward             the ward to which the patient is assigned
	 * @param prog             the progressive number of the admission
	 * @param patient          the patient associated with this admission
	 * @param admDate          the admission date and time
	 * @param admType          the admission type
	 * @param fhu              the first health unit (if applicable)
	 * @param diseaseIn        the disease at admission
	 * @param diseaseOut1      the primary disease at discharge
	 * @param diseaseOut2      the secondary disease at discharge
	 * @param diseaseOut3      the tertiary disease at discharge
	 * @param disDate          the discharge date and time
	 * @param disType          the discharge type
	 * @param anamnesis        anamnesis about the admission
	 * @param transUnit        transfusion units administered
	 * @param visitDate        date of the last visit during admission
	 * @param pregTreatmentType pregnancy treatment type if applicable
	 * @param deliveryDate     delivery date if applicable
	 * @param deliveryType     delivery type if applicable
	 * @param deliveryResult   delivery result if applicable
	 * @param weight           newborn weight if applicable
	 * @param ctrlDate1        first control date after admission
	 * @param ctrlDate2        second control date after admission
	 * @param abortDate        abortion date if applicable
	 * @param userID           the identifier of the user who created the admission
	 * @param deleted          deletion flag ('Y' or 'N')
	 * @param preTreatment     treatment received by the patient before admission
	 * @param preAssessment    assessment performed before the patient's admission
	 * @param entryReason	   reason for admission of a patient
	 * @param alertReceived
	 * @param referenceSheet
	 * @param qualifiedAgent
	 */
	public Admission(int id, int admitted, String type, Ward ward, int prog, Patient patient,
					 LocalDateTime admDate, AdmissionType admType, String fhu,
					 Disease diseaseIn, Disease diseaseOut1, Disease diseaseOut2, Disease diseaseOut3,
					 LocalDateTime disDate, DischargeType disType, String anamnesis, Float transUnit,
					 LocalDateTime visitDate, PregnantTreatmentType pregTreatmentType,
					 LocalDateTime deliveryDate, DeliveryType deliveryType, DeliveryResultType deliveryResult,
					 Float weight, LocalDateTime ctrlDate1, LocalDateTime ctrlDate2,
					 LocalDateTime abortDate, String userID, char deleted,
					 String preTreatment, String preAssessment, String entryReason,
					 Boolean alertReceived, Boolean referenceSheet, Boolean qualifiedAgent, String transportation ) {

		this(id, admitted, type, ward, prog, patient, admDate, admType, fhu,
			diseaseIn, diseaseOut1, diseaseOut2, diseaseOut3,
			disDate, disType, anamnesis, transUnit, visitDate,
			pregTreatmentType, deliveryDate, deliveryType, deliveryResult, weight,
			ctrlDate1, ctrlDate2, abortDate, userID, deleted);

		this.preTreatment = preTreatment;
		this.preAssessment = preAssessment;
		this.entryReason = entryReason;
		this.alertReceived = alertReceived;
		this.referenceSheet = referenceSheet;
		this.qualifiedAgent = qualifiedAgent;
		this.transportation = transportation;
	}

	public Float getTransUnit() {
		return transUnit;
	}

	public void setTransUnit(Float transUnit) {
		this.transUnit = transUnit;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String string) {
		this.userID = string;
	}

	public LocalDateTime getAbortDate() {
		return abortDate;
	}

	public void setAbortDate(LocalDateTime abortDate) {
		this.abortDate = TimeTools.truncateToSeconds(abortDate);
	}

	public LocalDateTime getAdmDate() {
		return admDate;
	}

	public void setAdmDate(LocalDateTime admDate) {
		this.admDate = TimeTools.truncateToSeconds(admDate);
	}

	public int getAdmitted() {
		return admitted;
	}

	public void setAdmitted(int admitted) {
		this.admitted = admitted;
	}

	public AdmissionType getAdmType() {
		return admissionType;
	}

	public void setAdmType(AdmissionType admType) {
		this.admissionType = admType;
	}

	public LocalDateTime getCtrlDate1() {
		return ctrlDate1;
	}

	public void setCtrlDate1(LocalDateTime ctrlDate1) {
		this.ctrlDate1 = TimeTools.truncateToSeconds(ctrlDate1);
	}

	public LocalDateTime getCtrlDate2() {
		return ctrlDate2;
	}

	public void setCtrlDate2(LocalDateTime ctrlDate2) {
		this.ctrlDate2 = TimeTools.truncateToSeconds(ctrlDate2);
	}

	public char getDeleted() {
		return deleted;
	}

	public void setDeleted(char deleted) {
		this.deleted = deleted;
	}

	public LocalDateTime getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(LocalDateTime deliveryDate) {
		this.deliveryDate = TimeTools.truncateToSeconds(deliveryDate);
	}

	public DeliveryResultType getDeliveryResult() {
		return deliveryResult;
	}

	public void setDeliveryResult(DeliveryResultType deliveryResult) {
		this.deliveryResult = deliveryResult;
	}

	public DeliveryType getDeliveryType() {
		return deliveryType;
	}

	public void setDeliveryType(DeliveryType deliveryTypeId) {
		this.deliveryType = deliveryTypeId;
	}

	public LocalDateTime getDisDate() {
		return disDate;
	}

	public void setDisDate(LocalDateTime disDate) {
		this.disDate = TimeTools.truncateToSeconds(disDate);
	}

	public Disease getDiseaseIn() {
		return diseaseIn;
	}

	public void setDiseaseIn(Disease diseaseIn) {
		this.diseaseIn = diseaseIn;
	}

	public Disease getDiseaseOut1() {
		return diseaseOut1;
	}

	public void setDiseaseOut1(Disease diseaseOut1) {
		this.diseaseOut1 = diseaseOut1;
	}

	public Disease getDiseaseOut2() {
		return diseaseOut2;
	}

	public void setDiseaseOut2(Disease diseaseOut2) {
		this.diseaseOut2 = diseaseOut2;
	}

	public Disease getDiseaseOut3() {
		return diseaseOut3;
	}

	public void setDiseaseOut3(Disease diseaseOut3) {
		this.diseaseOut3 = diseaseOut3;
	}

	public DischargeType getDisType() {
		return disType;
	}

	public void setDisType(DischargeType disType) {
		this.disType = disType;
	}

	public String getFHU() {
		return fHU;
	}

	public void setFHU(String fhu) {
		this.fHU = fhu;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getLock() {
		return lock;
	}

	public void setLock(int lock) {
		this.lock = lock;
	}

	public String getAnamnesis() {
		return anamnesis;
	}

	public void setAnamnesis(String anamnesis) {
		this.anamnesis = anamnesis;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public PregnantTreatmentType getPregTreatmentType() {
		return pregTreatmentType;
	}

	public void setPregTreatmentType(PregnantTreatmentType pregTreatmentType) {
		this.pregTreatmentType = pregTreatmentType;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public LocalDateTime getVisitDate() {
		return visitDate;
	}

	public void setVisitDate(LocalDateTime visitDate) {
		this.visitDate = TimeTools.truncateToSeconds(visitDate);
	}

	public Ward getWard() {
		return ward;
	}

	public void setWard(Ward ward) {
		this.ward = ward;
	}

	public Float getWeight() {
		return weight;
	}

	public void setWeight(Float weight) {
		this.weight = weight;
	}

	public int getYProg() {
		return yProg;
	}

	public void setYProg(int prog) {
		this.yProg = prog;
	}

	public String getPreTreatment() {
		return preTreatment;
	}

	public void setPreTreatment(String preTreatment) {
		this.preTreatment = preTreatment;
	}

	public String getPreAssessment() {
		return preAssessment;
	}

	public void setPreAssessment(String preAssessment) {
		this.preAssessment = preAssessment;
	}

	public String getEntryReason() {
		return entryReason;
	}

	public void setEntryReason(String entryReason) {
		this.entryReason = entryReason;
	}

	public String getTransportation() {
		return transportation;
	}

	public void setTransportation(String transportation) {
		this.transportation = transportation;
	}

	public Boolean getAlertReceived() {
		return alertReceived;
	}

	public void setAlertReceived(Boolean alertReceived) {
		this.alertReceived = alertReceived;
	}

	public Boolean getReferenceSheet() {
		return referenceSheet;
	}

	public void setReferenceSheet(Boolean referenceSheet) {
		this.referenceSheet = referenceSheet;
	}

	public Boolean getQualifiedAgent() {
		return qualifiedAgent;
	}

	public void setQualifiedAgent(Boolean qualifiedAgent) {
		this.qualifiedAgent = qualifiedAgent;
	}
	
	
	public String getPhysicalExam() {
		return physicalExam;
	}

	
	public void setPhysicalExam(String physicalExam) {
		this.physicalExam = physicalExam;
	}

	@Override
	public int compareTo(Admission anAdmission) {
		return this.id - anAdmission.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof Admission admission)) {
			return false;
		}

		return (this.getId() == admission.getId());
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
}
