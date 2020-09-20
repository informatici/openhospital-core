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
package org.isf.sms.service;

/**
 * SmsSenderInterface.java - 03/feb/2014
 */

import org.isf.sms.model.Sms;

/**
 * @author Mwithi
 */
public interface SmsSenderInterface {
	
	/**
	 * Public method to send one {@link Sms}
	 * @param sms - the {@link Sms} to send
	 * @param debug - if <code>true</code> the method should not really send the sms (for debug)
	 * @return <code>true</code> if the SMS has been sent, <code>false</code> otherwise
	 */
    boolean sendSMS(Sms sms, boolean debug);
	
	/**
	 * Public method to initialize the SmsSender.
	 * For GSM Sender it could check if the device is ready.
	 * For HTTP Sender it could check if the URL is reachable
	 * @return
	 */
    boolean initialize();
	
}
