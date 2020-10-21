/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2020 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
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
import org.isf.operation.model.Operation;
import org.isf.patient.model.Patient;
import org.isf.pregtreattype.model.PregnantTreatmentType;
import org.isf.ward.model.Ward;

/*
 * pure model
 */
/*------------------------------------------
 * Bill - model for the bill entity
 * -----------------------------------------
 * modification history
 * ? - ? - first version 
 * 30/09/2015 - Antonio - ported to JPA
 * 
 *------------------------------------------*/
@Entity
@Table(name="ADMISSION")
@SqlResultSetMapping(name="AdmittedPatient",
entities={
    @EntityResult(entityClass=org.isf.patient.model.Patient.class, fields={
            @FieldResult(name="code", column="PAT_ID"),
            @FieldResult(name="firstName", column="PAT_FNAME"), 
            @FieldResult(name="secondName", column="PAT_SNAME"),
            @FieldResult(name="name", column="PAT_NAME"),
            @FieldResult(name="birthDate", column="PAT_BDATE"),
            @FieldResult(name="age", column="PAT_AGE"),
            @FieldResult(name="agetype", column="PAT_AGETYPE"),
            @FieldResult(name="sex", column="PAT_SEX"),
            @FieldResult(name="address", column="PAT_ADDR"),
            @FieldResult(name="city", column="PAT_CITY"),
            @FieldResult(name="nextKin", column="PAT_NEXT_KIN"),
            @FieldResult(name="telephone", column="PAT_TELE"),
            @FieldResult(name="note", column="PAT_NOTE"),
            @FieldResult(name="mother_name", column="PAT_MOTH_NAME"),
            @FieldResult(name="mother", column="PAT_MOTH"),
            @FieldResult(name="father_name", column="PAT_FATH_NAME"),
            @FieldResult(name="father", column="PAT_FATH"),
            @FieldResult(name="bloodType", column="PAT_BTYPE"),
            @FieldResult(name="hasInsurance", column="PAT_ESTA"),
            @FieldResult(name="parentTogether", column="PAT_PTOGE"),
            @FieldResult(name="taxCode", column="PAT_TAXCODE"),
            @FieldResult(name="lock", column="PAT_LOCK"),
            @FieldResult(name="photo", column="PAT_PHOTO")
    }),
     @EntityResult(entityClass=org.isf.admission.model.Admission.class, fields={
	        @FieldResult(name="id", column="ADM_ID"),
	        @FieldResult(name="admitted", column="ADM_IN"), 
	        @FieldResult(name="type", column="ADM_TYPE"),
	        @FieldResult(name="ward", column="ADM_WRD_ID_A"),
	        @FieldResult(name="yProg", column="ADM_YPROG"),
	        @FieldResult(name="patient", column="ADM_PAT_ID"),
	        @FieldResult(name="admDate", column="ADM_DATE_ADM"),
	        @FieldResult(name="admissionType", column="ADM_ADMT_ID_A_ADM"),
	        @FieldResult(name="FHU", column="ADM_FHU"),
	        @FieldResult(name="diseaseIn", column="ADM_IN_DIS_ID_A"),
	        @FieldResult(name="diseaseOut1", column="ADM_OUT_DIS_ID_A"),
	        @FieldResult(name="diseaseOut2", column="ADM_OUT_DIS_ID_A_2"),
	        @FieldResult(name="diseaseOut3", column="ADM_OUT_DIS_ID_A_3"),
	        @FieldResult(name="operation", column="ADM_OPE_ID_A"),
	        @FieldResult(name="opDate", column="ADM_DATE_OP"),
	        @FieldResult(name="opResult", column="ADM_RESOP"),
	        @FieldResult(name="disDate", column="ADM_DATE_DIS"),
	        @FieldResult(name="disType", column="ADM_DIST_ID_A"),
	        @FieldResult(name="note", column="ADM_NOTE"),
	        @FieldResult(name="transUnit", column="ADM_TRANS"),
	        @FieldResult(name="visitDate", column="ADM_PRG_DATE_VIS"),
	        @FieldResult(name="pregTreatmentType", column="ADM_PRG_PTT_ID_A"),
	        @FieldResult(name="deliveryDate", column="ADM_PRG_DATE_DEL"),
	        @FieldResult(name="deliveryType", column="ADM_PRG_DLT_ID_A"),
	        @FieldResult(name="deliveryResult", column="ADM_PRG_DRT_ID_A"),
	        @FieldResult(name="weight", column="ADM_PRG_WEIGHT"),
	        @FieldResult(name="ctrlDate1", column="ADM_PRG_DATE_CTRL1"),
	        @FieldResult(name="ctrlDate2", column="ADM_PRG_DATE_CTRL2"),
	        @FieldResult(name="abortDate", column="ADM_PRG_DATE_ABORT"),
	        @FieldResult(name="userID", column="ADM_USR_ID_A"),
	        @FieldResult(name="lock", column="ADM_LOCK"),
	        @FieldResult(name="deleted", column="ADM_DELETED")
	        
     })}
)
//@EntityListeners(AuditingEntityListener.class) 
public class Admission implements Comparable<Admission>  //extends Auditable<String> implements Comparable<Admission> 
{
	@Id 
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ADM_ID")	
	private int id;							// admission key

	@NotNull
	@Column(name="ADM_IN")
	private int admitted;					// values are 0 or 1, default 0 (not admitted)

	@NotNull
	@Column(name="ADM_TYPE")
	private String type;	   				// values are 'N'(normal)  or 'M' (malnutrition)  default 'N' 

	@NotNull
	@ManyToOne
	@JoinColumn(name="ADM_WRD_ID_A")
	private Ward ward; 						// ward key

	@NotNull
	@Column(name="ADM_YPROG")
	private int yProg;						// a progr. in year for each ward

	@NotNull
	@ManyToOne
	@JoinColumn(name="ADM_PAT_ID")
	private Patient patient;				// patient key

	@NotNull
	@Column(name="ADM_DATE_ADM") 		// SQL type: datetime
	private LocalDateTime admDate;		// admission date

	@NotNull
	@ManyToOne
	@JoinColumn(name="ADM_ADMT_ID_A_ADM")
	private AdmissionType admissionType;	// admissionType key

	@Column(name="ADM_FHU")
	private String FHU;						// FromHealthUnit (null)
	
	@ManyToOne
	@JoinColumn(name="ADM_IN_DIS_ID_A")
	private Disease diseaseIn;				// disease in key  (null)
	
	@ManyToOne
	@JoinColumn(name="ADM_OUT_DIS_ID_A")
	private Disease diseaseOut1;			// disease out key  (null)
	
	@ManyToOne
	@JoinColumn(name="ADM_OUT_DIS_ID_A_2")
	private Disease diseaseOut2; 			// disease out key (null)
	
	@ManyToOne
	@JoinColumn(name="ADM_OUT_DIS_ID_A_3")
	private Disease diseaseOut3; 			// disease out key (null)
	
	@ManyToOne
	@JoinColumn(name="ADM_OPE_ID_A")
	private Operation operation;				// operation key (null)

	@Column(name="ADM_DATE_OP")					// SQL type: datetime
	private LocalDateTime opDate; 				// operation date (null)

	@Column(name="ADM_RESOP")
	private String opResult;				// value is 'P' or 'N' (null)

	@Column(name="ADM_DATE_DIS")		// SQL type: datetime
	private LocalDateTime disDate; 		// discharge date (null)
	
	@ManyToOne
	@JoinColumn(name="ADM_DIST_ID_A")
	private DischargeType disType;			// disChargeType key (null)

	@Column(name="ADM_NOTE")
	private String note;					// free notes (null)

	@Column(name="ADM_TRANS")
	private Float transUnit;				// transfusional unit

	@Column(name="ADM_PRG_DATE_VIS")		// SQL type: datetime
	private LocalDateTime visitDate;	// ADM_PRG_DATE_VIS	
		
	@ManyToOne
	@JoinColumn(name="ADM_PRG_PTT_ID_A")
	private PregnantTreatmentType pregTreatmentType;		// ADM_PRG_PTT_ID_A treatmentType key
	
	@Column(name="ADM_PRG_DATE_DEL")		// SQL type: datetime
	private LocalDateTime deliveryDate;	// ADM_PRG_DATE_DEL delivery date	
		
	@ManyToOne
	@JoinColumn(name="ADM_PRG_DLT_ID_A")	
	private DeliveryType deliveryType;		// ADM_PRG_DLT_ID_A delivery type key
		
	@ManyToOne
	@JoinColumn(name="ADM_PRG_DRT_ID_A")
	private DeliveryResultType deliveryResult;		// ADM_PRG_DRT_ID_A	delivery res. key
	
	@Column(name="ADM_PRG_WEIGHT")
	private Float weight;					// ADM_PRG_WEIGHT	weight
	
	@Column(name="ADM_PRG_DATE_CTRL1")	// SQL type: datetime
	private LocalDateTime ctrlDate1;	// ADM_PRG_DATE_CTRL1
	
	@Column(name="ADM_PRG_DATE_CTRL2")	// SQL type: datetime
	private LocalDateTime ctrlDate2;	// ADM_PRG_DATE_CTRL2
	
	@Column(name="ADM_PRG_DATE_ABORT")	// SQL type: datetime
	private LocalDateTime abortDate;	// ADM_PRG_DATE_ABORT
	
	@Column(name="ADM_USR_ID_A")
	private String userID;					// the user ID

	@Version
	@Column(name="ADM_LOCK")
	private int lock;						// default 0

	@NotNull
	@Column(name="ADM_DELETED")
	private String deleted;					// flag record deleted ; values are 'Y' OR 'N' default is 'N'
	
	@Transient
	private volatile int hashCode = 0;
	
	public Admission() {
		super();
	}

	/**
	 * 
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
	public Admission(int id, int admitted, String type, Ward ward, int prog, Patient patient, LocalDateTime admDate, AdmissionType admType, String fhu, Disease diseaseIn, Disease diseaseOut1, Disease diseaseOut2, Disease diseaseOut3,
			Operation operation, String opResult, LocalDateTime opDate, LocalDateTime disDate, DischargeType disType, String note, Float transUnit, LocalDateTime visitDate,
			PregnantTreatmentType pregTreatmentType, LocalDateTime deliveryDate, DeliveryType deliveryType, DeliveryResultType deliveryResult, Float weight, LocalDateTime ctrlDate1, LocalDateTime ctrlDate2,
			LocalDateTime abortDate, String userID, String deleted) 
	{
		super();
		this.id = id;
		this.admitted = admitted;
		this.type = type;
		this.ward = ward;
		this.yProg = prog;
		this.patient = patient;
		this.admDate = admDate;
		this.admissionType = admType;
		this.FHU = fhu;
		this.diseaseIn = diseaseIn;
		this.diseaseOut1 = diseaseOut1;
		this.diseaseOut2 = diseaseOut2;
		this.diseaseOut3 = diseaseOut3;
		this.operation = operation;
		this.opResult = opResult;
		this.opDate = opDate;
		this.disDate = disDate;
		this.disType = disType;
		this.note = note;
		this.transUnit = transUnit;
		this.visitDate = visitDate;
		this.pregTreatmentType = pregTreatmentType;
		this.deliveryDate = deliveryDate;
		this.deliveryType = deliveryType;
		this.deliveryResult = deliveryResult;
		this.weight = weight;
		this.ctrlDate1 = ctrlDate1;
		this.ctrlDate2 = ctrlDate2;
		this.abortDate = abortDate;
		this.userID = userID;
		this.deleted = deleted;
	}
	
	public LocalDateTime getOpDate() {
		return opDate;
	}

	public void setOpDate(LocalDateTime opDate) {
		this.opDate = opDate;
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
		this.abortDate = abortDate;
	}

	public LocalDateTime getAdmDate() {
		return admDate;
	}

	public void setAdmDate(LocalDateTime admDate) {
		this.admDate = admDate;
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
		this.ctrlDate1 = ctrlDate1;
	}

	public LocalDateTime getCtrlDate2() {
		return ctrlDate2;
	}

	public void setCtrlDate2(LocalDateTime ctrlDate2) {
		this.ctrlDate2 = ctrlDate2;
	}

	public String getDeleted() {
		return deleted;
	}

	public void setDeleted(String deleted) {
		this.deleted = deleted;
	}

	public LocalDateTime getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(LocalDateTime deliveryDate) {
		this.deliveryDate = deliveryDate;
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
		this.disDate = disDate;
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
		return FHU;
	}

	public void setFHU(String fhu) {
		this.FHU = fhu;
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

	public Operation getOperation() {
		return operation;
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}

	public String getOpResult() {
		return opResult;
	}

	public void setOpResult(String opResult) {
		this.opResult = opResult;
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
		this.visitDate = visitDate;
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
		
		Admission admission = (Admission)obj;
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
