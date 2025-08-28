package org.isf.conditioning.model;

import com.drew.lang.annotations.NotNull;
import jakarta.persistence.*;
import org.isf.menu.model.User;
import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "OH_CONDITIONING")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name="createdBy", column=@Column(name="COND_CREATED_BY", updatable = false))
@AttributeOverride(name="createdDate", column=@Column(name="COND_CREATED_DATE", updatable = false))
@AttributeOverride(name="lastModifiedBy", column=@Column(name="COND_LAST_MODIFIED_BY"))
@AttributeOverride(name="active", column=@Column(name="COND_ACTIVE"))
@AttributeOverride(name="lastModifiedDate", column=@Column(name="COND_LAST_MODIFIED_DATE"))
public class Conditioning extends Auditable<String> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "COND_ID")
	private Integer id;

	@Column(name = "COND_ASPIRATION")
	private Boolean aspiration;

	@Column(name = "COND_DUREE_MCE")
	private Integer mceDuree;

	@Column(name = "COND_DUREE_VENTILATION")
	private Integer ventilationDuree;

	@Column(name = "COND_DEBIT_OXYGENE")
	private Double oxygeneDebit;

	@Column(name = "COND_SG_VOLUME")
	private Double sgVolume;

	@Column(name = "COND_DIAZEPAM_DOSE")
	private Double diazepamDose;

	@Column(name = "COND_BOLUS_SS_VOLUME")
	private Double bolusSsVolume;

	@Column(name = "COND_SNG_NUMERO")
	private String sngNumero;

	@Column(name = "COND_OTHERS")
	private String others;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COND_US_ID")
	private User performBy;

	@Column(name = "COND_PERFORM_AT")
	private LocalDateTime performAt;

	public Conditioning(Integer id, Boolean aspiration, Integer mceDuree, Integer ventilationDuree, Double oxygeneDebit, Double sgVolume, Double diazepamDose, Double bolusSsVolume, String sngNumero, String others, User performBy, LocalDateTime performAt) {
		this.id = id;
		this.aspiration = aspiration;
		this.mceDuree = mceDuree;
		this.ventilationDuree = ventilationDuree;
		this.oxygeneDebit = oxygeneDebit;
		this.sgVolume = sgVolume;
		this.diazepamDose = diazepamDose;
		this.bolusSsVolume = bolusSsVolume;
		this.sngNumero = sngNumero;
		this.others = others;
		this.performBy = performBy;
		this.performAt = performAt;
	}

	public Conditioning() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Boolean getAspiration() {
		return aspiration;
	}

	public void setAspiration(Boolean aspiration) {
		this.aspiration = aspiration;
	}

	public Integer getMceDuree() {
		return mceDuree;
	}

	public void setMceDuree(Integer mceDuree) {
		this.mceDuree = mceDuree;
	}

	public Integer getVentilationDuree() {
		return ventilationDuree;
	}

	public void setVentilationDuree(Integer ventilationDuree) {
		this.ventilationDuree = ventilationDuree;
	}

	public Double getOxygeneDebit() {
		return oxygeneDebit;
	}

	public void setOxygeneDebit(Double oxygeneDebit) {
		this.oxygeneDebit = oxygeneDebit;
	}

	public Double getSgVolume() {
		return sgVolume;
	}

	public void setSgVolume(Double sgVolume) {
		this.sgVolume = sgVolume;
	}

	public Double getDiazepamDose() {
		return diazepamDose;
	}

	public void setDiazepamDose(Double diazepamDose) {
		this.diazepamDose = diazepamDose;
	}

	public Double getBolusSsVolume() {
		return bolusSsVolume;
	}

	public void setBolusSsVolume(Double bolusSsVolume) {
		this.bolusSsVolume = bolusSsVolume;
	}

	public String getSngNumero() {
		return sngNumero;
	}

	public void setSngNumero(String sngNumero) {
		this.sngNumero = sngNumero;
	}

	public String getOthers() {
		return others;
	}

	public void setOthers(String others) {
		this.others = others;
	}

	public User getPerformBy() {
		return performBy;
	}

	public void setPerformBy(User performBy) {
		this.performBy = performBy;
	}

	public LocalDateTime getPerformAt() {
		return performAt;
	}

	public void setPerformAt(LocalDateTime performAt) {
		this.performAt = performAt;
	}
}
