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
package org.isf.examination.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.isf.patient.model.Patient;
import org.isf.utils.time.TimeTools;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * The model for Patient Examination
 *
 * @author Mwithi
 */
@Entity
@Table(name="OH_PATIENTEXAMINATION")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "PEX_CREATED_BY"))
@AttributeOverride(name = "createdDate", column = @Column(name = "PEX_CREATED_DATE"))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "PEX_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "PEX_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "PEX_LAST_MODIFIED_DATE"))
public class PatientExamination implements Serializable, Comparable<PatientExamination> {

	private static final long serialVersionUID = 1L;
	public static final int PEX_NOTE_LENGTH = 2000;
	
	@Id 
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="PEX_ID")
	private int pex_ID;

	@NotNull
	@Column(name="PEX_DATE")		// SQL type: datetime
	private LocalDateTime pex_date;

	@NotNull
	@ManyToOne
	@JoinColumn(name="PEX_PAT_ID")
	private Patient patient;
	
	@Column(name="PEX_HEIGHT")
	private Integer pex_height;
	
	@Column(name="PEX_WEIGHT")
	private Double pex_weight;
	
	@Column(name="PEX_AP_MIN")
	private Integer pex_ap_min;
	
	@Column(name="PEX_AP_MAX")
	private Integer pex_ap_max;
	
	@Column(name="PEX_HR")
	private Integer pex_hr;
	
	@Column(name="PEX_TEMP")
	private Double pex_temp;
	
	@Column(name="PEX_SAT")
	private Double pex_sat;

	@Column(name="PEX_RR")
	private Integer pex_rr;

	@Column(name="PEX_AUSC")
	private String pex_auscultation;
	
	@Column(name="PEX_HGT")
	private Integer pex_hgt;
	
	@Column(name="PEX_DIURESIS")
	private Integer pex_diuresis;
	
	@Column(name="PEX_DIURESIS_DESC")
	private String pex_diuresis_desc;
	
	@Column(name="PEX_BOWEL_DESC")
	private String pex_bowel_desc;
	
	@Column(name="PEX_NOTE", length=PEX_NOTE_LENGTH)
	private String pex_note;
	
	@Transient
	private volatile int hashCode = 0;

	public PatientExamination() {
		super();
	}
	
	/**
	 * @param pex_date
	 * @param patient
	 * @param pex_height
	 * @param pex_weight
	 * @param pex_ap_min
	 * @param pex_ap_max
	 * @param pex_hr
	 * @param pex_temp
	 * @param pex_sat
	 * @param pex_hgt
	 * @param pex_diuresis
	 * @param pex_diuresis_desc
	 * @param pex_bowel_desc
	 * @param pex_rr
	 * @param pex_ausc
	 * @param pex_note
	 */
	public PatientExamination(
			LocalDateTime pex_date, 
			Patient patient, 
			Integer pex_height, 
			Double pex_weight, 
			Integer pex_ap_min, 
			Integer pex_ap_max, 
			Integer pex_hr, 
			Double pex_temp, 
			Double pex_sat,
			Integer pex_hgt,
			Integer pex_diuresis,
			String pex_diuresis_desc,
			String pex_bowel_desc,
			Integer pex_rr,
			String pex_ausc,
			String pex_note) {
		super();
		this.pex_date = TimeTools.truncateToSeconds(pex_date);
		this.patient = patient;
		this.pex_height = pex_height;
		this.pex_weight = pex_weight;
		this.pex_ap_min = pex_ap_min;
		this.pex_ap_max = pex_ap_max;
		this.pex_hr = pex_hr;
		this.pex_temp = pex_temp;
		this.pex_sat = pex_sat;
		this.pex_hgt = pex_hgt;
		this.pex_diuresis = pex_diuresis;
		this.pex_diuresis_desc = pex_diuresis_desc;
		this.pex_bowel_desc = pex_bowel_desc;
		this.pex_rr= pex_rr;
		this.pex_auscultation = pex_ausc;
		this.pex_note = pex_note;
	}

	/**
	 * @return the pex_ID
	 */
	public int getPex_ID() {
		return pex_ID;
	}

	/**
	 * @return the patient
	 */
	public Patient getPatient() {
		return patient;
	}

	/**
	 * @param patient the patient to set
	 */
	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	/**
	 * @param pex_ID the pex_ID to set
	 */
	public void setPex_ID(int pex_ID) {
		this.pex_ID = pex_ID;
	}

	/**
	 * @return the pex_date
	 */
	public LocalDateTime getPex_date() {
		return pex_date;
	}

	/**
	 * @param pex_date the pex_date to set
	 */
	public void setPex_date(LocalDateTime pex_date) {
		this.pex_date = TimeTools.truncateToSeconds(pex_date);
	}

	/**
	 * @return the pex_height
	 */
	public Integer getPex_height() {
		return pex_height;
	}

	/**
	 * @param pex_height the pex_height to set
	 */
	public void setPex_height(Integer pex_height) {
		this.pex_height = pex_height;
	}

	/**
	 * @return the pex_weight
	 */
	public Double getPex_weight() {
		return pex_weight;
	}

	/**
	 * @param weight the pex_weight to set
	 */
	public void setPex_weight(Double weight) {
		this.pex_weight = weight;
	}

	/**
	 * @return the pex_ap_min
	 */
	public Integer getPex_ap_min() {
		return pex_ap_min;
	}

	/**
	 * @param pex_ap_min the pex_ap_min to set
	 */
	public void setPex_ap_min(Integer pex_ap_min) {
		this.pex_ap_min = pex_ap_min;
	}

	/**
	 * @return the pex_ap_max
	 */
	public Integer getPex_ap_max() {
		return pex_ap_max;
	}

	/**
	 * @param pex_ap_max the pex_ap_max to set
	 */
	public void setPex_ap_max(Integer pex_ap_max) {
		this.pex_ap_max = pex_ap_max;
	}

	/**
	 * @return the pex_hr
	 */
	public Integer getPex_hr() {
		return pex_hr;
	}

	/**
	 * @param pex_hr the pex_hr to set
	 */
	public void setPex_hr(Integer pex_hr) {
		this.pex_hr = pex_hr;
	}

	/**
	 * @return the pex_temp
	 */
	public Double getPex_temp() {
		return pex_temp;
	}

	/**
	 * @param pex_temp the pex_temp to set
	 */
	public void setPex_temp(Double pex_temp) {
		this.pex_temp = pex_temp;
	}

	/**
	 * @return the pex_sat
	 */
	public Double getPex_sat() {
		return pex_sat;
	}

	/**
	 * @param pex_sat the pex_sat to set
	 */
	public void setPex_sat(Double pex_sat) {
		this.pex_sat = pex_sat;
	}

	/**
	 * @return the pex_rr
	 */
	public Integer getPex_rr() {
		return pex_rr;
	}

	/**
	 * @param pex_rr the pex_rr to set
	 */
	public void setPex_rr(Integer pex_rr) {
		this.pex_rr = pex_rr;
	}

	/**
	 * @return the pex_auscultation
	 */
	public String getPex_auscultation() {
		return pex_auscultation;
	}

	/**
	 * @param pex_auscultation the pex_auscultation to set
	 */
	public void setPex_auscultation(String pex_auscultation) {
		this.pex_auscultation = pex_auscultation;
	}

	/**
	 * @return the pex_hgt
	 */
	public Integer getPex_hgt() {
		return pex_hgt;
	}

	/**
	 * @param pex_hgt the pex_hgt to set
	 */
	public void setPex_hgt(Integer pex_hgt) {
		this.pex_hgt = pex_hgt;
	}

	/**
	 * @return the pex_diuresis
	 */
	public Integer getPex_diuresis() {
		return pex_diuresis;
	}

	/**
	 * @param pex_diuresis the pex_diuresis to set
	 */
	public void setPex_diuresis(Integer pex_diuresis) {
		this.pex_diuresis = pex_diuresis;
	}

	/**
	 * @return the pex_diuresis_desc
	 */
	public String getPex_diuresis_desc() {
		return pex_diuresis_desc;
	}

	/**
	 * @param pex_diuresis_desc the pex_diuresis_desc to set
	 */
	public void setPex_diuresis_desc(String pex_diuresis_desc) {
		this.pex_diuresis_desc = pex_diuresis_desc;
	}

	/**
	 * @return the pex_bowel_desc
	 */
	public String getPex_bowel_desc() {
		return pex_bowel_desc;
	}

	/**
	 * @param pex_bowel_desc the pex_bowel_desc to set
	 */
	public void setPex_bowel_desc(String pex_bowel_desc) {
		this.pex_bowel_desc = pex_bowel_desc;
	}

	/**
	 * @return the pex_note
	 */
	public String getPex_note() {
		return pex_note;
	}

	/**
	 * @param pex_note the pex_note to set
	 */
	public void setPex_note(String pex_note) {
		this.pex_note = pex_note;
	}

	@Override
	public int compareTo(PatientExamination o) {
		return this.pex_date.compareTo(o.getPex_date());
	}
	
	public double getBMI() {
		if (pex_height != null) {
			double temp = Math.pow(10, 2); // 2 <-- decimal digits;
			double height = pex_height * (1. / 100); // convert to m
			double weight = pex_weight; // Kg
			return Math.round(weight / Math.pow(height, 2) * temp) / temp ; //getting Kg/m2
		} else return 0;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (!(obj instanceof PatientExamination)) {
			return false;
		}
		
		PatientExamination patex = (PatientExamination)obj;
		return (pex_ID == patex.getPex_ID());
	}
	
	@Override
	public int hashCode() {
	    if (this.hashCode == 0) {
	        final int m = 23;
	        int c = 133;
	        
	        c = m * c + pex_ID;
	        
	        this.hashCode = c;
	    }
	  
	    return this.hashCode;
	}
}
