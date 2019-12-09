/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.isf.utils.db;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import static javax.persistence.TemporalType.TIMESTAMP;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
/**
 *
 * @author uni2grow
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)

public abstract class Auditable<U> {
    @CreatedBy
    @Column(name="CREATED_BY")
    protected U createdBy;
     
    @CreatedDate
    @Temporal(TIMESTAMP)
    @Column(name="CREATED_DATE")
    protected Date createdDate;
    
    @LastModifiedBy
    @Column(name="LAST_MODIFIED_BY")
    protected U lastModifiedBy;
    
    @LastModifiedDate
    @Temporal(TIMESTAMP)
    @Column(name="LAST_MODIFIED_DATE")
    protected Date lastModifiedDate;
   
    @Column(name="ACTIVE")
    protected int active = 1;

    public U getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(U createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public int getActive() {
		return active;
	}

	public void setActive(int active) {
		this.active = active;
	}

	public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public U getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(U lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
    
}
