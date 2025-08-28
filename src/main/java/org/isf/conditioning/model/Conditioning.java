package org.isf.conditioning.model;

import com.drew.lang.annotations.NotNull;
import jakarta.persistence.*;
import org.isf.menu.model.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "OH_CONDITIONING")
public class Conditioning {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "COND_ID")
	private Integer id;

	@Column(name = "COND_ASPIRATION")
	private Boolean aspiration;

	@NotNull
	@Column(name = "COND_DUREE_MCE")
	private Integer mceDuree;

	@NotNull
	@Column(name = "COND_DUREE_VENTILATION")
	private Integer ventilationDuree;

	@NotNull
	@Column(name = "COND_DEBIT_OXYGENE")
	private Double oxygeneDebit;

	@NotNull
	@Column(name = "COND_SG_VOLUME")
	private Double sgVolume;

	@NotNull
	@Column(name = "COND_DIAZEPAM_DOSE")
	private Double diazepamDose;

	@NotNull
	@Column(name = "COND_BOLUS_SS_VOLUME")
	private Double bolusSsVolume;

	@NotNull
	@Column(name = "COND_SNG_NUMERO")
	private String sngNumero;

	@NotNull
	@Column(name = "COND_OTHERS")
	private String others;

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
