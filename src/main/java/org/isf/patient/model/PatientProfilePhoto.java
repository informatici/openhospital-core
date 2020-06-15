package org.isf.patient.model;

import javax.imageio.ImageIO;
import javax.persistence.*;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.Serializable;

/**
 * Photo related to a single {@link Patient}
 */
@Entity
@Table(name = "PATIENT_PROFILE_PHOTO")
public class PatientProfilePhoto implements Serializable {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="PAT_PROFILE_PHOTO_ID")
	private Integer code;

	@OneToOne(mappedBy = "patientProfilePhoto")
	private Patient patient;

	@Column(name="PAT_PHOTO")
	@Lob
	private byte[] photo;


	public byte[] getPhoto() {
		return photo;
	}

	public Image getPhotoAsImage() {
		try {
			if (photo != null && photo.length > 0) {
				BufferedInputStream is = new BufferedInputStream(new ByteArrayInputStream(photo));
				return ImageIO.read(is);
			}
			return null;
		} catch (final Exception exception) {
			throw new RuntimeException("Failed to get image");
		}
	}

	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(final Patient patient) {
		this.patient = patient;
	}
}
