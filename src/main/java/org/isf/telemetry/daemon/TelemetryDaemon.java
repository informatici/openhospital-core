/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2022 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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

import java.time.LocalDateTime;

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
	// XXX load it from properties? Perhaps it could be better...
	private static final int RELOAD_SETTINGS_TIME = 30;

	private TelemetryManager telemetryManager;
	private TelemetryUtils telemetryUtils;

	private Telemetry settings;

	private boolean running = true;
	private int customDelay = DEFAULT_DELAY;
	private int updateSettingsCounter;

	public TelemetryDaemon() {
		super(FILE_PROPERTIES);
		LOGGER.info("Telemetry daemon started...");
		customDelay = myGetProperty("telemetry.daemon.thread.loop.seconds", DEFAULT_DELAY);
		LOGGER.info("Telemetry daemon loop set to {} seconds.", Integer.valueOf(customDelay));
		this.telemetryManager = Context.getApplicationContext().getBean(TelemetryManager.class);
		this.telemetryUtils = Context.getApplicationContext().getBean(TelemetryUtils.class);
		this.settings = telemetryManager.retrieveSettings();
	}

	public void run() {
		while (running) {
			LOGGER.info("Telemetry module running ({})...", updateSettingsCounter);
			boolean isSendingMessageServiceActive = settings != null && settings.getActive() != null ? settings.getActive().booleanValue() : false;
			if (!isSendingMessageServiceActive) {
				LOGGER.debug("Telemetry module DISABLED (reloading settings in {} seconds)",
						calculateReloadSettingsTime());
			} else if (isSendingMessageServiceActive && isTimeToSendMessage()) {
				try {
					GeneralData.initialize();
					this.telemetryUtils.sendTelemetryData(telemetryUtils.retrieveDataToSend(settings.getConsentMap()),
							GeneralData.DEBUG);
				} catch (RuntimeException | OHException e) {
					LOGGER.error("Something strange happened");
					LOGGER.error(ExceptionUtils.retrieveExceptionStacktrace(e));
					LOGGER.error("Stopping Telemetry Daemon...");
					setRunning(false);
				}
			} else {
				LOGGER.debug("Telemetry module: issue traking message already sent (reloading settings in {} seconds)",
						calculateReloadSettingsTime());
			}

			try {
				Thread.sleep((long) customDelay * 1000);
				updateSettingsCounter++;
				if (updateSettingsCounter % RELOAD_SETTINGS_TIME == 0) {
					LOGGER.debug("Reloading telemetry settings (after {} seconds)...", RELOAD_SETTINGS_TIME);
					this.settings = telemetryManager.retrieveSettings();
					LOGGER.debug("settings {}", this.settings);
				}
			} catch (InterruptedException e) {
				LOGGER.error(e.getMessage());
				LOGGER.error(ExceptionUtils.retrieveExceptionStacktrace(e));
				LOGGER.error("Stopping Telemetry Daemon...");
				setRunning(false);
			}
		}
	}

	private int calculateReloadSettingsTime() {
		return RELOAD_SETTINGS_TIME - (updateSettingsCounter % RELOAD_SETTINGS_TIME);
	}

	private boolean isTimeToSendMessage() {
		return settings.getSentTimestamp() == null || !TimeTools.isSameDay(settings.getSentTimestamp(), LocalDateTime.now());
	}

	/**
	 * @param running the running to set
	 */
	public void setRunning(boolean running) {
		this.running = running;
	}
}
