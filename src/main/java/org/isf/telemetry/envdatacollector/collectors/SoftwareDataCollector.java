/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2024 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.telemetry.envdatacollector.collectors;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.isf.generaldata.GeneralData;
import org.isf.generaldata.Version;
import org.isf.telemetry.envdatacollector.AbstractDataCollector;
import org.isf.telemetry.envdatacollector.constants.CollectorsConstants;
import org.isf.utils.exception.OHException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import oshi.SystemInfo;
import oshi.software.os.OperatingSystem;

@Order(value = 20)
@Component
public class SoftwareDataCollector extends AbstractDataCollector {

	private static final String ID = "TEL_SW";
	private static final Logger LOGGER = LoggerFactory.getLogger(SoftwareDataCollector.class);
	private String version;

	@PersistenceContext
	private EntityManager em;

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public String getDescription() {
		version = Version.getVersion().toString();
		return "Software information versions and usage (ex. Ubuntu 22.04, MariaDB 10.6, Open Hospital " + version + ')';
	}

	@Override
	public Map<String, String> retrieveData() throws OHException {
		LOGGER.debug("Collecting Software data...");
		Map<String, String> result = new LinkedHashMap<>();
		try {
			SystemInfo si = new SystemInfo();
			OperatingSystem os = si.getOperatingSystem();
			result.put(CollectorsConstants.OS_FAMILY, os.getFamily());
			result.put(CollectorsConstants.OS_VERSION, String.valueOf(os.getVersionInfo().getVersion()));
			result.put(CollectorsConstants.OS_MANUFACTURER, os.getManufacturer());
			result.put(CollectorsConstants.OS_BITNESS, String.valueOf(os.getBitness()));
			result.put(CollectorsConstants.OS_CODENAME, os.getVersionInfo().getCodeName());

			Connection connection = em.unwrap(Connection.class);
			DatabaseMetaData dbmd = connection.getMetaData();
			result.put(CollectorsConstants.DBMS_DRIVER_NAME, dbmd.getDriverName());
			result.put(CollectorsConstants.DBMS_DRIVER_VERSION, dbmd.getDriverVersion());
			result.put(CollectorsConstants.DBMS_PRODUCT_NAME, dbmd.getDatabaseProductName());
			result.put(CollectorsConstants.DBMS_PRODUCT_VERSION, dbmd.getDatabaseProductVersion());

			Version.initialize();
			result.put(CollectorsConstants.APP_VERSION, version);
			result.put(CollectorsConstants.APP_MODE, GeneralData.MODE);
			result.put(CollectorsConstants.APP_DEMODATA, String.valueOf(GeneralData.DEMODATA));
			result.put(CollectorsConstants.APP_APISERVER, String.valueOf(GeneralData.APISERVER));
			result.put(CollectorsConstants.APP_LANGUAGE, GeneralData.LANGUAGE);
			// result.put(CollectorsConstants.APP_SINGLEUSER, GeneralData.getSINGLEUSER());
			result.put(CollectorsConstants.APP_DEBUG, String.valueOf(GeneralData.DEBUG));
			result.put(CollectorsConstants.APP_INTERNALVIEWER, String.valueOf(GeneralData.INTERNALVIEWER));
			result.put(CollectorsConstants.APP_SMSENABLED, String.valueOf(GeneralData.SMSENABLED));
			result.put(CollectorsConstants.APP_VIDEOMODULEENABLED, String.valueOf(GeneralData.VIDEOMODULEENABLED));
			result.put(CollectorsConstants.APP_XMPPMODULEENABLED, String.valueOf(GeneralData.XMPPMODULEENABLED));
			result.put(CollectorsConstants.APP_ENHANCEDSEARCH, String.valueOf(GeneralData.ENHANCEDSEARCH));
			result.put(CollectorsConstants.APP_INTERNALPHARMACIES, String.valueOf(GeneralData.INTERNALPHARMACIES));
			result.put(CollectorsConstants.APP_LABEXTENDED, String.valueOf(GeneralData.LABEXTENDED));
			result.put(CollectorsConstants.APP_LABMULTIPLEINSERT, String.valueOf(GeneralData.LABMULTIPLEINSERT));
			result.put(CollectorsConstants.APP_MATERNITYRESTARTINJUNE, String.valueOf(GeneralData.MATERNITYRESTARTINJUNE));
			result.put(CollectorsConstants.APP_MERGEFUNCTION, String.valueOf(GeneralData.MERGEFUNCTION));
			result.put(CollectorsConstants.APP_OPDEXTENDED, String.valueOf(GeneralData.OPDEXTENDED));
			result.put(CollectorsConstants.APP_PATIENTEXTENDED, String.valueOf(GeneralData.PATIENTEXTENDED));
			result.put(CollectorsConstants.APP_PATIENTVACCINEEXTENDED, String.valueOf(GeneralData.PATIENTVACCINEEXTENDED));
			result.put(CollectorsConstants.APP_MAINMENUALWAYSONTOP, String.valueOf(GeneralData.MAINMENUALWAYSONTOP));
			result.put(CollectorsConstants.APP_ALLOWMULTIPLEOPENEDBILL, String.valueOf(GeneralData.ALLOWMULTIPLEOPENEDBILL));
			result.put(CollectorsConstants.APP_ALLOWPRINTOPENEDBILL, String.valueOf(GeneralData.ALLOWPRINTOPENEDBILL));
			result.put(CollectorsConstants.APP_RECEIPTPRINTER, String.valueOf(GeneralData.RECEIPTPRINTER));
			result.put(CollectorsConstants.APP_AUTOMATICLOT_IN, String.valueOf(GeneralData.AUTOMATICLOT_IN));
			result.put(CollectorsConstants.APP_AUTOMATICLOT_OUT, String.valueOf(GeneralData.AUTOMATICLOT_OUT));
			result.put(CollectorsConstants.APP_AUTOMATICLOTWARD_TOWARD, String.valueOf(GeneralData.AUTOMATICLOTWARD_TOWARD));
			result.put(CollectorsConstants.APP_LOTWITHCOST, String.valueOf(GeneralData.LOTWITHCOST));
			result.put(CollectorsConstants.APP_DICOMMODULEENABLED, String.valueOf(GeneralData.DICOMMODULEENABLED));
			result.put(CollectorsConstants.APP_DICOMTHUMBNAILS, String.valueOf(GeneralData.DICOMTHUMBNAILS));

		} catch (RuntimeException | SQLException e) {
			LOGGER.error("Something went wrong with " + ID);
			LOGGER.error(e.toString());
			throw new OHException("Data collector [" + ID + ']', e);
		}
		return result;
	}

}
