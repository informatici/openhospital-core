/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2021 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.exa.model;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.isf.exatype.model.ExamType;
import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * -----------------------------------------
 * Exam - model for the exam (laboratory exams) entity
 * -----------------------------------------
 * modification history
 * 20-jan-2006 - bob - first version
 * 05/01/2016 - Antonio - ported to JPA
 * ------------------------------------------
 */
@Entity
@Table(name="OH_EXAM")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "EXA_CREATED_BY"))
@AttributeOverride(name = "createdDate", column = @Column(name = "EXA_CREATED_DATE"))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "EXA_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "EXA_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "EXA_LAST_MODIFIED_DATE"))
public class Exam extends Auditable<String> {

	@Id
	@Column(name="EXA_ID_A")	
	private String code;

	@NotNull
	@Column(name="EXA_DESC")
	private String description;

	@NotNull
	@Column(name="EXA_PROC")
	private Integer procedure;

	@Column(name="EXA_DEFAULT")
	private String defaultResult;

	@NotNull
	@ManyToOne
	@JoinColumn(name="EXA_EXC_ID_A")
	private ExamType examtype;

	@Version
	@Column(name="EXA_LOCK")
	private Integer lock;

	@Transient
	private volatile int hashCode = 0;
	
	public Exam() 
    {
		super();
    }
	
	public Exam(String code, String description, ExamType examtype,
			Integer procedure, String defaultResult) {
		super();
		this.code = code;
		this.description = description;
		this.examtype = examtype;
		this.defaultResult = defaultResult;
		this.procedure = procedure;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ExamType getExamtype() {
		return examtype;
	}

	public void setExamtype(ExamType examtype) {
		this.examtype = examtype;
	}

	public Integer getLock() {
		return lock;
	}

	public void setLock(Integer lock) {
		this.lock = lock;
	}

	public String getDefaultResult() {
		return defaultResult;
	}

	public void setDefaultResult(String defaultResult) {
		this.defaultResult = defaultResult;
	}

	public Integer getProcedure() {
		return procedure;
	}

	public void setProcedure(Integer procedure) {
		this.procedure = procedure;
	}

	@Override
	public boolean equals(Object anObject) {
		return anObject instanceof Exam && (getCode().equals(((Exam) anObject).getCode())
				&& getDescription().equalsIgnoreCase(((Exam) anObject).getDescription()) && getExamtype().equals(((Exam) anObject).getExamtype()));
	}

	public String toString() {
		return getDescription();
	}	

	@Override
	public int hashCode() {
	    if (this.hashCode == 0) {
	        final int m = 23;
	        int c = 133;
	        c = m * c + code.hashCode();   
	        this.hashCode = c;
	    }	  
	    return this.hashCode;
	}
	
	public String getSearchString() {
		StringBuilder sbNameCode = new StringBuilder();
		sbNameCode.append(getCode().toLowerCase());
		sbNameCode.append(getDescription().toLowerCase());
		return sbNameCode.toString();
	}
}
