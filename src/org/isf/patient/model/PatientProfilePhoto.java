package org.isf.patient.model;

import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import javax.persistence.*;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;

/**
 * Photo related to a single {@link Patient}
 */
@Entity
@Table(name = "PATIENT_PROFILE_PHOTO")
public class PatientProfilePhoto {

	@Id
	private Integer code;

	/**
	 * Patient of the photo.
	 * Here we are using {@link MapsId} so that we dont have to maintain both
	 * primary key (of the photo table) as well as foreign key (back to the patient table).
	 * This makes sense here, as a photo only relates to a single patient (a patient can have
	 * one profile photo). So we can use the same PK for both.
	 */
	@OneToOne
	@MapsId
	@JoinColumn(name = "PAT_ID")
	private Patient patient;

	@Column(name="PAT_PHOTO")
	@Lob
	private Blob photo;

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public Blob getPhoto() {
		return photo;
	}

	@Nullable
	public Image getPhotoAsImage() {
		try {
			if (photo != null && photo.length() > 0) {
				BufferedInputStream is = new BufferedInputStream(photo.getBinaryStream());
				return ImageIO.read(is);
			}
			return null;
		} catch (final Exception exception) {
			throw new RuntimeException("Failed to get image");
		}
	}

	public void setPhoto(Blob photo) {
		this.photo = photo;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(final Patient patient) {
		this.patient = patient;
	}
}
