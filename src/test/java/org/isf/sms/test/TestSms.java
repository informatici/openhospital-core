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
package org.isf.sms.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import org.isf.sms.model.Sms;
import org.isf.utils.exception.OHException;

public class TestSms {

	private int smsId = 0;
	private LocalDateTime smsDate = LocalDateTime.of(2010, 9, 8, 0, 0, 0);
	private LocalDateTime smsDateSched = LocalDateTime.of(2011, 9, 8, 0, 0, 0);
	private String smsNumber = "TestNumber";
	private String smsText = "TestText";
	private LocalDateTime smsDateSent = null;
	private String smsUser = "TestUser";
	private String module = "TestModule";
	private String moduleID = "TestModId";

	public Sms setup(boolean usingSet) throws OHException {
		Sms sms;

		if (usingSet) {
			sms = new Sms();
			setParameters(sms);
		} else {
			// Create Sms with all parameters 
			sms = new Sms(smsId, smsDate, smsDateSched, smsNumber, smsText, smsDateSent, smsUser, module, moduleID);
		}

		return sms;
	}

	public void setParameters(Sms sms) {
		sms.setModule(module);
		sms.setModuleID(moduleID);
		sms.setSmsDate(smsDate);
		sms.setSmsDateSched(smsDateSched);
		sms.setSmsDateSent(smsDateSent);
		sms.setSmsNumber(smsNumber);
		sms.setSmsText(smsText);
		sms.setSmsUser(smsUser);
	}

	public void check(Sms sms) {
		assertThat(sms.getModule()).isEqualTo(module);
		assertThat(sms.getModuleID()).isEqualTo(moduleID);
		assertThat(Date.from(sms.getSmsDate().toInstant(ZoneOffset.UTC))).isInSameDayAs(Date.from(smsDate.toInstant(ZoneOffset.UTC)));
		assertThat(Date.from(sms.getSmsDateSched().toInstant(ZoneOffset.UTC))).isInSameDayAs(Date.from(smsDateSched.toInstant(ZoneOffset.UTC)));
		if (sms.getSmsDateSent() == null) {
			assertThat(smsDateSent).isNull();
		} else {
			assertThat(Date.from(sms.getSmsDateSent().toInstant(ZoneOffset.UTC))).isInSameDayAs(Date.from(smsDateSent.toInstant(ZoneOffset.UTC)));
		}
		assertThat(sms.getSmsNumber()).isEqualTo(smsNumber);
		assertThat(sms.getSmsText()).isEqualTo(smsText);
		assertThat(sms.getSmsUser()).isEqualTo(smsUser);
	}
}
