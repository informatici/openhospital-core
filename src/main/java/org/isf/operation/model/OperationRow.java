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
package org.isf.operation.model;

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

import org.isf.accounting.model.Bill;
import org.isf.admission.model.Admission;
import org.isf.opd.model.Opd;
import org.isf.utils.db.Auditable;
import org.isf.utils.time.TimeTools;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * @author xavier
 */
@Entity
@Table(name="OH_OPERATIONROW")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "OPER_CREATED_BY"))
@AttributeOverride(name = "createdDate", column = @Column(name = "OPER_CREATED_DATE"))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "OPER_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "OPER_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "OPER_LAST_MODIFIED_DATE"))
public class OperationRow extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "OPER_ID_A")
    private int id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "OPER_OPE_ID_A")
    private Operation operation;

    @NotNull
    @Column(name = "OPER_PRESCRIBER")
    private String prescriber;

    @NotNull
    @Column(name = "OPER_RESULT")
    private String opResult;

    @NotNull
    @Column(name = "OPER_OPDATE")       // SQL type: datetime
    private LocalDateTime opDate;

    @NotNull
    @Column(name = "OPER_REMARKS")
    private String remarks;

    @ManyToOne
    @JoinColumn(name = "OPER_ADMISSION_ID")
    private Admission admission;

    @ManyToOne
    @JoinColumn(name = "OPER_OPD_ID")
    private Opd opd;

    @ManyToOne
    @JoinColumn(name = "OPER_BILL_ID")
    private Bill bill;

    @Column(name = "OPER_TRANS_UNIT", columnDefinition = "float default 0")
    private Float transUnit = 0f;
    
    @Transient
    private volatile int hashCode = 0;

	public OperationRow() {
		super();
	}
    
    public OperationRow(Operation operation, 
            String prescriber, 
            String opResult, 
            LocalDateTime opDate,
            String remarks, 
            Admission admission, 
            Opd opd, 
            Bill bill, 
            Float transUnit) {
        super();
        this.operation = operation;
        this.prescriber = prescriber;
        this.opResult = opResult;
        this.opDate = TimeTools.truncateToSeconds(opDate);
        this.remarks = remarks;
        this.admission = admission;
        this.opd = opd;
        this.bill = bill;
        this.transUnit = transUnit;
    }

    public OperationRow(int id, 
            Operation operation, 
            String prescriber, 
            String opResult, 
            LocalDateTime opDate,
            String remarks, 
            Admission admission, 
            Opd opd, 
            Bill bill, 
            Float transUnit) {
        this(operation, prescriber, opResult, opDate, remarks, admission, opd, bill, transUnit);
        this.id = id;
    }

    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public String getPrescriber() {
        return prescriber;
    }

    public void setPrescriber(String prescriber) {
        this.prescriber = prescriber;
    }

    public String getOpResult() {
        return opResult;
    }

    public void setOpResult(String opResult) {
        this.opResult = opResult;
    }

    public LocalDateTime getOpDate() {
        return opDate;
    }

    public void setOpDate(LocalDateTime opDate) {
        this.opDate = TimeTools.truncateToSeconds(opDate);
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Admission getAdmission() {
        return admission;
    }

    public void setAdmission(Admission admission) {
        this.admission = admission;
    }

    public Opd getOpd() {
        return opd;
    }

    public void setOpd(Opd opd) {
        this.opd = opd;
    }

    public Bill getBill() {
        return bill;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
    }

    public Float getTransUnit() {
        return transUnit;
    }

    public void setTransUnit(Float transUnit) {
        this.transUnit = transUnit;
    }

    @Override
    public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }

        if (!(anObject instanceof OperationRow)) {
            return false;
        }

        OperationRow operationRow = (OperationRow) anObject;
        return (this.getOperation().equals(operationRow.getOperation())
                && this.getPrescriber().equals(operationRow.getPrescriber()))
                && operationRow.getTransUnit().equals(this.getTransUnit())
                && this.getAdmission().equals(operationRow.getAdmission())
                && this.getOpd().equals(operationRow.getOpd());
    }

    @Override
    public int hashCode() {
        if (this.hashCode == 0) {
            final int m = 23;
            int c = 133;

            c = m * c + ((operation == null) ? 0 : operation.hashCode());
            c = m * c + ((prescriber == null) ? 0 : prescriber.hashCode());
            c = m * c + ((admission == null) ? 0 : admission.hashCode());
            c = m * c + ((opd == null) ? 0 : opd.hashCode());
            c = m * c + ((transUnit == null) ? 0 : transUnit.intValue());

            this.hashCode = c;
        }

        return this.hashCode;
    }

    public String toString() {
        return this.operation.getDescription() + " " + this.admission.getUserID();
    }
}
