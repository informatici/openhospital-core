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
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.isf.utils.time;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DelayTimer extends Thread {

	private static final Logger LOGGER = LoggerFactory.getLogger(DelayTimer.class);

	private final DelayTimerCallback callback;
	private final Object mutex = new Object();
	private final Object triggeredMutex = new Object();
	private final long delay;
	private boolean quit;
	private boolean triggered;
	private long waitTime;

	public DelayTimer(DelayTimerCallback callback, long delay) {
		this.callback = callback;
		this.delay = delay;
		setDaemon(true);
		start();
	}

	/**
	 * Calling this method twice will reset the timer.
	 */
	public void startTimer() {
		synchronized (mutex) {
			waitTime = delay;
			mutex.notify();
		}
	}

	public void stopTimer() {
		try {
			synchronized (mutex) {
				synchronized (triggeredMutex) {
					if (triggered) {
						triggeredMutex.wait();
					}
				}
				waitTime = 0;
				mutex.notify();
			}
		} catch (InterruptedException interruptedException) {
			LOGGER.error("trigger failure", interruptedException);
		}
	}

	@Override
	public void run() {
		try {
			while (!quit) {
				synchronized (mutex) {

					if (waitTime < 0) {
						triggered = true;
						waitTime = 0;
					} else {
						long saveWaitTime = waitTime;
						waitTime = -1;
						mutex.wait(saveWaitTime);
					}
				}
				try {
					if (triggered) {
						callback.trigger();
					}
				} catch (Exception exception) {
					LOGGER.error("trigger() threw exception, continuing", exception);
				} finally {
					synchronized (triggeredMutex) {
						triggered = false;
						triggeredMutex.notify();
					}
				}
			}
		} catch (InterruptedException interruptedException) {
			LOGGER.error("interrupted in run", interruptedException);
		}
	}

	public void quit() {
		synchronized (mutex) {
			this.quit = true;
			mutex.notify();
		}
	}

}
