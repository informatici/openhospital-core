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
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
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
	private static final int DEFAULT_DELAY = 30; // seconds

	// Singleton instance
	private static volatile TelemetryDaemon instance;
	private Thread telemetryThread;

	private TelemetryManager telemetryManager;
	private TelemetryUtils telemetryUtils;
	private Telemetry settings;

	private boolean running = true;
	private int customDelay = DEFAULT_DELAY;
	private int updateSettingsCounter;
	private boolean reloadSettings = true;

	public void start() {
		telemetryThread = new Thread(this);
		telemetryThread.start();
	}

	private TelemetryDaemon() {
		super(FILE_PROPERTIES);
		LOGGER.info("Telemetry daemon started...");
		customDelay = myGetProperty("telemetry.daemon.thread.loop.seconds", DEFAULT_DELAY);
		LOGGER.info("Telemetry daemon loop set to {} seconds.", customDelay);
		this.telemetryManager = Context.getApplicationContext().getBean(TelemetryManager.class);
		this.telemetryUtils = Context.getApplicationContext().getBean(TelemetryUtils.class);
		this.settings = telemetryManager.retrieveSettings();
	}

	// Singleton instance getter
	public static TelemetryDaemon getTelemetryDaemon() {
		if (instance == null) {
			synchronized (TelemetryDaemon.class) {
				if (instance == null) {
					instance = new TelemetryDaemon();
				}
			}
		}
		return instance;
	}

	public void run() {
		while (running) {
			if (reloadSettings) {
				this.settings = telemetryManager.retrieveSettings();
				this.reloadSettings = false;
			}
			LOGGER.info("Telemetry module running ({})...", updateSettingsCounter);
			boolean isSendingMessageServiceActive = settings != null && settings.getActive() != null ? settings.getActive().booleanValue() : false;
			if (!isSendingMessageServiceActive) {
				LOGGER.info("No data selected.");
			} else if (isSendingMessageServiceActive && isTimeToSendMessage()) {
				try {
					// GeneralData.getGeneralData();
					this.telemetryUtils.sendTelemetryData(telemetryUtils.retrieveDataToSend(settings.getConsentMap()),
									GeneralData.DEBUG);
				} catch (RuntimeException | OHException e) {
					LOGGER.error("Something strange happened");
					LOGGER.error(ExceptionUtils.retrieveExceptionStacktrace(e));
					stop();
				}
			}
			try {
				Thread.sleep((long) customDelay * 1000);
				updateSettingsCounter++;
			} catch (InterruptedException e) {
				LOGGER.error(e.getMessage());
				LOGGER.error(ExceptionUtils.retrieveExceptionStacktrace(e));
				stop();
			}
		}
	}

	private boolean isTimeToSendMessage() {
		boolean isTime = settings.getSentTimestamp() == null || !TimeTools.isSameDay(settings.getSentTimestamp(), LocalDateTime.now());
		LOGGER.info("Telemetry module : already sent today");
		return isTime;
	}

	/**
	 * @param running the running to set
	 */
	private void setRunning(boolean running) {
		this.running = running;
	}

	/**
	 * Stops the Telemetry Daemon
	 */
	public void stop() {
		LOGGER.info("Stopping Telemetry Daemon...");
		setRunning(false);
	}

	/**
	 * Stops the Telemetry Daemon
	 */
	public void restart() {
		LOGGER.info("Restarting Telemetry Daemon...");
		setRunning(true);
	}

	/**
	 * Reload settings at the next run, restart daemon if stopped.
	 */
	public void reloadSettings() {
		this.reloadSettings = true;
		if (!this.running) {
			restart();
		}
	}

}
