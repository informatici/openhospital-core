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
package org.isf.envdatacollector.collectors;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.isf.envdatacollector.AbstractDataCollector;
import org.isf.utils.db.DbSingleConn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(value = 20)
@Component
public class DBMSDataCollector extends AbstractDataCollector {

	private static final String ID = "FUN_DBMS";
	private static final int MYSQL_DEFAULT_PORT = 3306;
	private static final Logger LOGGER = LoggerFactory.getLogger(DBMSDataCollector.class);

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public String getDescription() {
		return "DBMS information (ex. MySQL 5.0)";
	}

	@Override
	public Map<String, String> retrieveData() {
		LOGGER.debug("Collecting DBMS data...");
		Map<String, String> result = new HashMap<>();
		Connection con = null;
		try {
			Properties props = loadProperties();
			String drivers = props.getProperty("jdbc.drivers");
			if (drivers != null)
				System.setProperty("jdbc.drivers", drivers);
			String url = props.getProperty("jdbc.url");
			String server = props.getProperty("jdbc.server");
			String db = props.getProperty("jdbc.db");
			String username = props.getProperty("jdbc.username");
			String password = props.getProperty("jdbc.password");
			String port = props.getProperty("jdbc.port");
			if (port == null) {
				port = String.valueOf(MYSQL_DEFAULT_PORT);
			}
			StringBuilder sbURL = new StringBuilder();
			sbURL.append(url);
			sbURL.append("//");
			sbURL.append(server);
			sbURL.append(":");
			sbURL.append(port);
			sbURL.append("/");
			sbURL.append(db);
			sbURL.append("?useUnicode=true&characterEncoding=UTF-8");
			sbURL.append("&user=");
			sbURL.append(username);
			sbURL.append("&password=");
			sbURL.append(password);
			con = DriverManager.getConnection(sbURL.toString());

			DatabaseMetaData dbmd = con.getMetaData();
			result.put(CollectorsConst.DBMS_DRIVER_NAME, dbmd.getDriverName());
			result.put(CollectorsConst.DBMS_DRIVER_VERSION, dbmd.getDriverVersion());
			result.put(CollectorsConst.DBMS_USERNAME, dbmd.getUserName());
			result.put(CollectorsConst.DBMS_PRODUCT_NAME, dbmd.getDatabaseProductName());
			result.put(CollectorsConst.DBMS_PRODUCT_VERSION, dbmd.getDatabaseProductVersion());
			con.close();
		} catch (Exception e) {
			LOGGER.error(e.toString());
		}
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				LOGGER.error(e.toString());
			}
		}
		return result;
	}

	private Properties loadProperties() throws FileNotFoundException, IOException {
		Properties props = new Properties();
		InputStream is = DbSingleConn.class.getClassLoader().getResourceAsStream("database.properties");
		if (is == null) {
			FileInputStream in = new FileInputStream("rsc/database.properties");
			props.load(in);
			in.close();
		} else {
			props.load(is);
			is.close();
		}
		return props;
	}

}
