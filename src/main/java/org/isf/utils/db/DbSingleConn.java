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
package org.isf.utils.db;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import javax.swing.JOptionPane;

import org.isf.generaldata.MessageBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Singleton class to manage database connections.
 * Connection parameters are read from Properties file.
 */
public class DbSingleConn {

	protected static Logger logger = LoggerFactory.getLogger(DbSingleConn.class);

	private static final int MYSQL_DEFAULT_PORT = 3306;

	private static Connection pConn;

	private DbSingleConn() {
	}

	public static Connection getConnection() throws SQLException, IOException {
		if (pConn == null) {
			try {
				pConn = createConnection();
			} catch (Exception ex){
				String message = MessageBundle.getMessage("angal.utils.dbserverconnectionfailure");
				logger.error(">> {}", message);
				JOptionPane.showMessageDialog(null, message);
				System.exit(1);
			}

		}
		return pConn;
	}

	public static void closeConnection() throws SQLException, IOException {
		pConn.close();
	}

	public static void releaseConnection() throws SQLException, IOException {
		pConn.close();
		pConn = null;
	}

	public static void commitConnection() throws SQLException, IOException {
		pConn.commit();
	}

	public static void rollbackConnection() throws SQLException, IOException {
		pConn.rollback();
	}

	private static Connection createConnection() throws SQLException, IOException {

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

		return DriverManager.getConnection(sbURL.toString());
	}

}
