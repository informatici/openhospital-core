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
package org.isf.sms.manager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.isf.generaldata.MessageBundle;
import org.isf.menu.manager.UserBrowsingManager;
import org.isf.sms.model.Sms;
import org.isf.sms.service.SmsOperations;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SmsManager {

	public static final int MAX_LENGHT = 160;
	private final String NUMBER_REGEX = "^\\+?\\d+$"; //$NON-NLS-1$

	@Autowired
	private SmsOperations smsOperations;

	public SmsManager() {
	}

	/**
	 * Verify if the object is valid for CRUD and return a list of errors, if any
	 *
	 * @param sms
	 * @throws OHDataValidationException
	 */
	protected void validateSms(Sms sms) throws OHDataValidationException {
		List<OHExceptionMessage> errors = new ArrayList<OHExceptionMessage>();
		String number = sms.getSmsNumber();
		String text = sms.getSmsText();

		if (!number.matches(NUMBER_REGEX)) {
			errors.add(new OHExceptionMessage("numberError",
					MessageBundle.getMessage("angal.sms.pleaseinsertavalidtelephonenumber"),
					OHSeverityLevel.ERROR));
		}
		if (text.isEmpty()) {
			errors.add(new OHExceptionMessage("emptyTextError",
					MessageBundle.getMessage("angal.sms.pleaseinsertatext"),
					OHSeverityLevel.ERROR));
		}
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}

	public List<Sms> getAll(LocalDateTime from, LocalDateTime to) throws OHServiceException {
		return smsOperations.getAll(from, to);
	}

	/**
	 * Save or Update a {@link Sms}. If the sms's text lenght is greater than
	 * {@code MAX_LENGHT} it will throw a {@code testMaxLenghtError} error if
	 * {@code split} parameter is set to {@code false}
	 *
	 * @param smsToSend - the {@link Sms} to save or update
	 * @param split - specify if to split sms's text longer than {@code MAX_LENGHT}
	 * @throws OHServiceException
	 */
	public void saveOrUpdate(Sms smsToSend, boolean split) throws OHServiceException {
		validateSms(smsToSend);

		List<Sms> smsList = new ArrayList<Sms>();
		String text = smsToSend.getSmsText();
		int textLenght = text.length();
		if (textLenght > MAX_LENGHT && !split) {

			StringBuilder message = new StringBuilder();
			message.append(MessageBundle.getMessage("angal.sms.themessageislongerthen"))
					.append(" ")
					.append(MAX_LENGHT)
					.append(" ")
					.append(MessageBundle.getMessage("angal.sms.chars"));
			throw new OHDataValidationException(new OHExceptionMessage("testMaxLenghtError",
					message.toString(),
					OHSeverityLevel.ERROR));

		} else if (textLenght > MAX_LENGHT && split) {

			String[] parts = split(text);
			String number = smsToSend.getSmsNumber();
			LocalDateTime schedDate = smsToSend.getSmsDateSched();

			for (String part : parts) {
				Sms sms = new Sms();
				sms.setSmsNumber(number);
				sms.setSmsDateSched(schedDate);
				sms.setSmsUser(UserBrowsingManager.getCurrentUser());
				sms.setSmsText(part);
				sms.setModule("smsmanager");
				sms.setModuleID(null);

				smsList.add(sms);
			}

		} else {
			smsList.add(smsToSend);
		}
		smsOperations.saveOrUpdate(smsList);
	}

	public void delete(List<Sms> smsToDelete) throws OHServiceException {
		smsOperations.delete(smsToDelete);
	}

	public int getMAX_LENGHT() {
		return MAX_LENGHT;
	}

	public String getNUMBER_REGEX() {
		return NUMBER_REGEX;
	}

	private String[] split(String text) {
		int len = text.length();
		if (len <= MAX_LENGHT) {
			String[] messages = { text };
			return messages;
		}

		// Number of parts
		int nParts = (len + MAX_LENGHT - 1) / MAX_LENGHT;
		String[] parts = new String[nParts];

		// Break into parts
		int offset = 0;
		int i = 0;
		while (i < nParts) {
			parts[i] = text.substring(offset, Math.min(offset + MAX_LENGHT, len));
			offset += MAX_LENGHT;
			i++;
		}
		return parts;
	}
}
