package org.isf.visits.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.persistence.Transient;

import org.isf.patient.model.Patient;
import org.isf.utils.db.Auditable;
import org.isf.ward.model.Ward;

 /*------------------------------------------
 * Visits : ?
 * -----------------------------------------
 * modification history
 * ? - ? - first version 
 * 1/08/2016 - Antonio - ported to JPA
 * 
 *------------------------------------------*/

public class Visit  extends Auditable<String>
{
	

	@Transient
	private volatile int hashCode = 0;
	private int visitID;
	private GregorianCalendar date;
	private Patient patient;
	private String note;
	private boolean sms;
	private Ward ward;
	private String duration;
	private String service;
	

	public Visit() {
		super();
	}

	public Visit(int visitID, GregorianCalendar date, Patient patient, String note, boolean sms, Ward ward, String duration, String service) {
		super();
		this.visitID = visitID;
		this.date = date;
		this.patient = patient;
		this.note = note;
		this.sms = sms;		
		this.ward=ward;
		this.duration=duration;
		this.service=service;
	}
	
	public GregorianCalendar getDate() {
		return date;
	}

	public void setDate(GregorianCalendar date) {
		this.date = date;
	}
	
	public void setDate(Date date) {
		GregorianCalendar gregorian = new GregorianCalendar();
		gregorian.setTime(date);
		setDate(gregorian);
	}

	public int getVisitID() {
		return visitID;
	}

	public void setVisitID(int visitID) {
		this.visitID = visitID;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	
	public Ward getWard() {
		return ward;
	}

	public void setWard(Ward ward) {
		this.ward = ward;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}
	
	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}
	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	
	public boolean isSms() {
		return sms;
	}

	public void setSms(boolean sms) {
		this.sms = sms;
	}
	

	
	public String toStringSMS() {
		
		return formatDateTimeSMS(this.date);
	}

	public String formatDateTime(GregorianCalendar time) {
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy - HH:mm:ss"); //$NON-NLS-1$
		return format.format(time.getTime());
	}
	
	public String formatDateTimeSMS(GregorianCalendar time) {
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy HH:mm"); //$NON-NLS-1$
		return format.format(time.getTime());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (!(obj instanceof Visit)) {
			return false;
		}
		
		Visit visit = (Visit)obj;
		return (visitID == visit.getVisitID());
	}
	public String toString() {
		String desc = ""+ ward.getDescription()+ " - "+ this.service + " - " + formatDateTime(this.date);
		
		return desc;
	}
	@Override
	public int hashCode() {
	    if (this.hashCode == 0) {
	        final int m = 23;
	        int c = 133;
	        
	        c = m * c + visitID;
	        
	        this.hashCode = c;
	    }
	  
	    return this.hashCode;
	}
}
