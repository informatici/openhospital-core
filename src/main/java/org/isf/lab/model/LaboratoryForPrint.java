/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2020 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.lab.model;

import java.time.LocalDateTime;

import org.isf.exa.model.Exam;

public class LaboratoryForPrint {
	
	private String exam;
	private String date;
	private String result;
	private Integer code;

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public LaboratoryForPrint(Integer aCode, Exam aExam, LocalDateTime aDate, String aResult){
		code = aCode;
		exam=aExam.getDescription();
		date=getConvertedString(aDate);
		result=aResult;
	}
	
	private String getConvertedString(LocalDateTime time){
		String string= "" + time.getDayOfMonth();
		string+= "/"+ time.getMonthValue();
		string+= "/"+ time.getYear();
		string+= "  "+ time.getHour();
		string+= ":"+ time.getMinute();
		string+= ":"+ time.getSecond();
		return string;
	}

    public String getDate() {
        return this.date;
    }

    public void setDate(String aDate) {
        this.date = aDate;
    }

    public String getExam() {
        return this.exam;
    }

    public void setExam(String aExam) {
        this.exam = aExam;
    }

    public String getResult() {
        return this.result;
    }

    public void setResult(String aResult) {
        this.result = aResult;
    }


}
