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
package org.isf.serviceprinting.print;

import org.isf.medicals.model.Medical;
import org.isf.medicalstockward.model.MedicalWard;
import org.isf.ward.model.Ward;

/**
 * @author mwithi
 */
public class MedicalWardForPrint implements Comparable<MedicalWardForPrint> {
	
	private Ward ward;
	private String code;
	private Medical medical;
	private Double qty;
	private Integer packets;
	
	public MedicalWardForPrint(MedicalWard med, Ward ward) {
		super();
		this.ward = ward;
		this.medical = null;
		this.medical = med.getMedical();
		this.qty = med.getQty();
		this.code = medical.getProdCode();
		this.packets = 0;
		int pcsPerPck = medical.getPcsperpck();
		if (pcsPerPck > 1) { 
			this.packets = (int) (qty / pcsPerPck);
		}
	}
	
	public Ward getWard() {
		return ward;
	}
	
	public String getCode() {
		return code;
	}

	public Medical getMedical() {
		return medical;
	}
	
	public Double getQty() {
		return qty;
	}
	
	public Integer getPackets() {
		return packets;
	}

	@Override
	public int compareTo(MedicalWardForPrint o) {
		return medical.getDescription().toUpperCase().compareTo(o.getMedical().getDescription().toUpperCase());
	}
}
