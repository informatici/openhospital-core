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
package org.isf.utils.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.hibernate.Session;
import org.isf.generaldata.MessageBundle;
import org.isf.utils.exception.OHException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Singleton, provide db connection used on persistence unit
 */
public class DbSingleJpaConn {

	private static final Logger LOGGER = LoggerFactory.getLogger(DbSingleJpaConn.class);
	private static Connection connection;

	private DbSingleJpaConn() { }

	public static Connection getConnection() throws OHException {
		if (connection == null) {
			try {
				connection = createConnection();
			} catch (Exception e) {
				String message = MessageBundle.getMessage("angal.sql.databaseserverstoppedornetworkfailure.msg");
				LOGGER.error(">> {}", message);
				throw new OHException(message, e);
			}
		}
		return connection;
	}

	private static Connection createConnection() throws SQLException, IOException, OHException {
		DbJpaUtil jpa = new DbJpaUtil();
		if (jpa.getEntityManager() == null) {
			jpa.open();
		}
		final Connection[] jpaConnection = { null };
		Session session = jpa.getEntityManager().unwrap(Session.class);
		session.doWork(connection -> jpaConnection[0] = connection);
		return jpaConnection[0];
	}

}
