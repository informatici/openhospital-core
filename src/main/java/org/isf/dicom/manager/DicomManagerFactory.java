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
package org.isf.dicom.manager;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.isf.generaldata.MessageBundle;
import org.isf.menu.manager.Context;
import org.isf.utils.exception.OHDicomException;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.isf.utils.file.FileTools;

/**
 * Factory for instantiate DicomManager
 *
 * @author Pietro Castellucci
 * @version 1.0.0
 */
public class DicomManagerFactory {

	private static DicomManagerInterface instance = null;

	private static Properties props = new Properties();

	public static String getMaxDicomSize() {
		return props.getProperty("dicom.max.size", "4M");
	}

	public static Long getMaxDicomSizeLong() throws OHDicomException {
		String sizeHumanReadable = props.getProperty("dicom.max.size", "4M");
		long sizeLong = 4194304L; // default for 4M
		try {
			sizeLong = FileTools.humanReadableByteCountParse(sizeHumanReadable);
		} catch (OHException e) {
			throw new OHDicomException(e, new OHExceptionMessage("DICOM",
					e.getMessage(), OHSeverityLevel.WARNING));
		}
		return sizeLong;
	}

	/**
	 * Return the manager for DICOM acquired files
	 *
	 * @throws OHDicomException
	 */
	public static synchronized DicomManagerInterface getManager() throws OHDicomException {

		if (instance == null) {

			try {
				init();
                Object appContext = Context.getApplicationContext();
				instance = (DicomManagerInterface) Context.getApplicationContext().getBean(
						Class.forName(props.getProperty("dicom.manager.impl"))); //.getConstructor(Class.forName("java.util.Properties")).newInstance(props);
				if (instance instanceof FileSystemDicomManager) {
					((FileSystemDicomManager) instance).setDir(props);
				}
			} catch (Exception e) {
				//Any exception
				throw new OHDicomException(e, new OHExceptionMessage(MessageBundle.getMessage("angal.hospital"),
						props.getProperty("dicom.manager.impl") + " " + MessageBundle.getMessage("angal.dicom.manager.noimpl"), OHSeverityLevel.ERROR));
			}
		}

		return instance;
	}

	private static void init() throws OHDicomException {
		try {

			File f = new File("rsc/dicom.properties");

			if (!f.exists()) {
				throw new OHDicomException(new OHExceptionMessage(MessageBundle.getMessage("angal.hospital"),
						MessageBundle.getMessage("angal.dicom.nofile") + " rsc/dicom.manager.properties", OHSeverityLevel.ERROR));
			}

			FileInputStream in = new FileInputStream("rsc/dicom.properties");

			props.load(in);

			in.close();
		} catch (Exception exc) {
			throw new OHDicomException(exc, new OHExceptionMessage(MessageBundle.getMessage("angal.hospital"),
					MessageBundle.getMessage("angal.dicom.manager.err") + " " + exc.getMessage(), OHSeverityLevel.ERROR));
		}
	}

}
