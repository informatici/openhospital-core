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
package org.isf.telemetry.daemon;

import java.util.Date;

import org.isf.generaldata.ConfigurationProperties;
import org.isf.generaldata.GeneralData;
import org.isf.menu.manager.Context;
import org.isf.telemetry.manager.TelemetryManager;
import org.isf.telemetry.model.Telemetry;
import org.isf.telemetry.util.TelemetryUtils;
import org.isf.utils.ExceptionUtils;
import org.isf.utils.exception.OHException;
import org.isf.utils.time.TimeTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TelemetryDaemon extends ConfigurationProperties implements Runnable {

	private static final Logger LOGGER = LoggerFactory.getLogger(TelemetryDaemon.class);
	private static final String FILE_PROPERTIES = "telemetry.properties";
	private static final int DEFAULT_DELAY = 10;

	private TelemetryManager telemetryManager;
	private TelemetryUtils telemetryUtils;

	private boolean running = true;
	private int customDelay = DEFAULT_DELAY;

	public TelemetryDaemon() {
		super(FILE_PROPERTIES);
		LOGGER.info("Telemetry daemon started...");
		customDelay = myGetProperty("telemetry.daemon.thread.loop.seconds", DEFAULT_DELAY);
		LOGGER.info("Telemetry daemon loop set to {} seconds.", Integer.valueOf(customDelay));
		this.telemetryManager = Context.getApplicationContext().getBean(TelemetryManager.class);
		this.telemetryUtils = Context.getApplicationContext().getBean(TelemetryUtils.class);
	}

	public void run() {
		while (running) {

			LOGGER.info("Telemetry module running...");
			Telemetry settings = telemetryManager.retrieveSettings();

			if (Boolean.TRUE.equals(Boolean.valueOf(settings.getActive().booleanValue()
							&& (settings.getSentTimestamp() == null || !TimeTools.isSameDay(settings.getSentTimestamp(), new Date()))))) {
				try {
					GeneralData.initialize();
					this.telemetryUtils.sendTelemetryData(settings.getConsentMap(), GeneralData.DEBUG);
				} catch (RuntimeException | OHException e) {
					LOGGER.error("Something strange happened");
					LOGGER.error(ExceptionUtils.retrieveExceptionStacktrace(e));
				}
			} else {
				LOGGER.debug("Telemetry module: issue traking message already sent");
			}

			try {
				Thread.sleep((long) customDelay * 1000);
			} catch (InterruptedException e) {
				LOGGER.error(e.getMessage());
				LOGGER.error(ExceptionUtils.retrieveExceptionStacktrace(e));
			}
		}
	}

	/**
	 * @param running
	 *            the running to set
	 */
	public void setRunning(boolean running) {
		this.running = running;
	}
}
