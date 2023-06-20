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
package org.isf.patient.model;

import java.awt.Image;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.Serializable;

import javax.imageio.ImageIO;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Photo related to a single {@link Patient}
 */
@Entity
@Table(name="OH_PATIENT_PROFILE_PHOTO")
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
