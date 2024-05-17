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
package org.isf.utils.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.isf.generaldata.MessageBundle;
import org.isf.utils.exception.OHException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that executes a query using the connection defined in DbSingleJpaConn; thus a single connection is reused for all queries.
 * The various methods that open a connection with the autocommit flag set to false have the responsibility
 * of doing the commit/rollback operation
 */
public class DbQueryLogger {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DbQueryLogger.class);
    
	/**
     * Method that executes a query and returns a resultset
     * @param aQuery
     * @param autocommit
     * @return ResultSet
     * @throws OHException
     */
	public ResultSet getData(String aQuery, boolean autocommit) throws OHException {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Query {}", sanitize(aQuery));
		}
		try {
			Connection conn = DbSingleJpaConn.getConnection();
			conn.setAutoCommit(autocommit);
			Statement stat = conn.createStatement();
			return stat.executeQuery(aQuery);
		} catch (OHException e) {
			throw e;
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlinstruction.msg"), e);
		} catch (Exception e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwithserverconnection.msg"), e);
		}
	}

	/**
     * Method for sanitize a String object for logging purpose
     * @param aString - the String object
     * @return the string sanitized
     */
    private String sanitize(String aString) {
    	return aString.replace("'", "\\'");
	}
}
