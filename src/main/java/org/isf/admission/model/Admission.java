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
package org.isf.admission.model;

import java.time.LocalDateTime;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EntityResult;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

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

/**
 * ------------------------------------------
 * Admission - model for a patient admission
 * -----------------------------------------
 * modification history
 * ? - ? - first version
 * 30/09/2015 - Antonio - ported to JPA
 * ------------------------------------------
 */
@Entity
@Table(name="OH_ADMISSION")
@SqlResultSetMapping(name="AdmittedPatient",
entities={
		@EntityResult(entityClass=org.isf.patient.model.Patient.class),
		@EntityResult(entityClass=org.isf.admission.model.Admission.class)}
)
@EntityListeners(AuditingEntityListener.class) 
@AttributeOverrides({
    @AttributeOverride(name="createdBy", column=@Column(name="ADM_CREATED_BY")),
    @AttributeOverride(name="createdDate", column=@Column(name="ADM_CREATED_DATE")),
    @AttributeOverride(name="lastModifiedBy", column=@Column(name="ADM_LAST_MODIFIED_BY")),
    @AttributeOverride(name="active", column=@Column(name="ADM_ACTIVE")),
    @AttributeOverride(name="lastModifiedDate", column=@Column(name="ADM_LAST_MODIFIED_DATE"))
})
public class Admission extends Auditable<String> implements Comparable<Admission> 
{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
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
	private String note;                    // free notes (null)

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

	@Column(name = "ADM_USR_ID_A")
	private String userID;                    // the user ID

	@Version
	@Column(name = "ADM_LOCK")
	private int lock;                        // default 0

	@NotNull
	@Column(name = "ADM_DELETED", columnDefinition = "char(1) default 'N'")
	private char deleted = 'N';                // flag record deleted ; values are 'Y' OR 'N' default is 'N'

	@Transient
	private volatile int hashCode = 0;

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
	 * @param operation
	 * @param opResult
	 * @param opDate
	 * @param disDate
	 * @param disType
	 * @param note
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
			LocalDateTime disDate, DischargeType disType, String note, Float transUnit, LocalDateTime visitDate,
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
		this.note = note;
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

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
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

	@Override
	public int compareTo(Admission anAdmission) {
		return this.id - anAdmission.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof Admission)) {
			return false;
		}

		Admission admission = (Admission) obj;
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
