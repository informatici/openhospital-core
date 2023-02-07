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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isf.utils.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
     * Method that executes a PreparedStatement with params and returns a resultset
     * @param aQuery
     * @param params
     * @param autocommit
     * @return
     * @throws OHException
     */
    public ResultSet getDataWithParams(String aQuery, List<?> params, boolean autocommit) throws OHException {
	    if (LOGGER.isDebugEnabled()) {
		    LOGGER.debug("Query {}", sanitize(aQuery));
		    if (!params.isEmpty())
			    LOGGER.trace("  parameters : {}", sanitize(params));
	    }
	    ResultSet results;
	    Connection conn = null;
	    try {
		    conn = DbSingleJpaConn.getConnection();
		    conn.setAutoCommit(autocommit);
		    PreparedStatement pstmt = conn.prepareStatement(aQuery);
		    for (int i = 0; i < params.size(); i++) {
			    pstmt.setObject(i + 1, params.get(i));
		    }
		    results = pstmt.executeQuery();
	    } catch (OHException e) {
		    throw e;
	    } catch (SQLException e) {
		    throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlinstruction.msg"), e);
	    } catch (Exception e) {
		    throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwithserverconnection.msg"), e);
	    }
	    return results;
    }

    /**
     * Method that executes an insert-update-delete query and returns true or false
     * depending on the success of the operation
     * @param aQuery
     * @param autocommit
     * @return Boolean True/False
     * @throws SQLException
     * @throws IOException
     */
    public boolean setData(String aQuery, boolean autocommit) throws OHException {
	    if (LOGGER.isDebugEnabled()) {
		    LOGGER.debug("Query {}", sanitize(aQuery));
	    }
	    try {
		    Connection conn = DbSingleJpaConn.getConnection();
		    conn.setAutoCommit(autocommit);
		    Statement stat = conn.createStatement();
		    return stat.executeUpdate(aQuery) > 0;
	    } catch (OHException e) {
		    throw e;
	    } catch (SQLIntegrityConstraintViolationException e) {
		    throw new OHException(MessageBundle.getMessage("angal.sql.theselecteditemisstillusedsomewhere.msg"), e);
	    } catch (SQLException e) {
		    throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlinstruction.msg"), e);
	    } catch (Exception e) {
		    throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwithserverconnection.msg"), e);
	    }
    }
    
    /**
     * Method that executes an insert-update-delete PreparedStatement with params and returns true or false
     * depending on the success of the operation
     * @param aQuery
     * @param params
     * @param autocommit
     * @return
     * @throws OHException
     */
    public boolean setDataWithParams(String aQuery, List<?> params, boolean autocommit) throws OHException {
	    if (LOGGER.isDebugEnabled()) {
		    LOGGER.debug("Query {}", sanitize(aQuery));
		    if (!params.isEmpty())
			    LOGGER.trace("  parameters : {}", sanitize(params));
	    }
	    Connection conn = null;
	    try {
		    conn = DbSingleJpaConn.getConnection();
		    conn.setAutoCommit(autocommit);
		    PreparedStatement pstmt = conn.prepareStatement(aQuery);
		    for (int i = 0; i < params.size(); i++) {
			    pstmt.setObject(i + 1, params.get(i));
		    }
		    return pstmt.executeUpdate() > 0;
	    } catch (OHException e) {
		    throw e;
	    } catch (SQLIntegrityConstraintViolationException e) {
		    throw new OHException(MessageBundle.getMessage("angal.sql.theselecteditemisstillusedsomewhere.msg"), e);
	    } catch (SQLException e) {
		    throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlinstruction.msg"), e);
	    } catch (Exception e) {
		    throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwithserverconnection.msg"), e);
	    }
    }
    
	/**
     * Method that executes an insert-update-delete query and returns A ResultSet
     * containing the autogenerated key (integer counter)
     * if no key has been generated the ResultSet will be empty
     * @param aQuery
     * @param autocommit
     * @return ResultSet
     * @throws SQLException
     * @throws IOException
     */
	public ResultSet setDataReturnGeneratedKey(String aQuery, boolean autocommit) throws OHException {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Query {}", sanitize(aQuery));
		}
		try {
			Connection conn = DbSingleJpaConn.getConnection();
			conn.setAutoCommit(autocommit);
			Statement stat = conn.createStatement();
			stat.execute(aQuery,Statement.RETURN_GENERATED_KEYS);
			return stat.getGeneratedKeys();
		} catch (OHException e) {
			throw e;
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlinstruction.msg"), e);
		} catch (Exception e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwithserverconnection.msg"), e);
		}
	}
    
    /**
     * Method that executes an insert-update-delete PreparedStatement with params and returns A ResultSet
     * containing the autogenerated key (integer counter)
     * if no key has been generated the ResultSet will be empty
     * @param aQuery
     * @param params
     * @param autocommit
     * @return
     * @throws OHException
     */
    public ResultSet setDataReturnGeneratedKeyWithParams(String aQuery, List<?> params, boolean autocommit) throws OHException {
	    if (LOGGER.isDebugEnabled()) {
		    LOGGER.debug("Query {}", sanitize(aQuery));
		    if (!params.isEmpty()) {
			    LOGGER.trace("  parameters : {}", sanitize(params));
		    }
	    }
	    try {
		    Connection conn = DbSingleJpaConn.getConnection();
		    conn.setAutoCommit(autocommit);
		    PreparedStatement pstmt = conn.prepareStatement(aQuery, Statement.RETURN_GENERATED_KEYS);
		    for (int i = 0; i < params.size(); i++) {
			    pstmt.setObject(i + 1, params.get(i));
		    }
		    pstmt.execute();
		    return pstmt.getGeneratedKeys();
	    } catch (OHException e) {
		    throw e;
	    } catch (SQLException e) {
		    throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlinstruction.msg"), e);
	    } catch (Exception e) {
		    throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwithserverconnection.msg"), e);
	    }
    }
    
    /**
     * Method that executes a query and returns true or false
     * depending on the existence of records or not in
     * the Recordset
     * @param aQuery
     * @return Boolean True/False
     * @throws OHException
     */
    public boolean isData(String aQuery) throws OHException {
	    if (LOGGER.isDebugEnabled()) {
		    LOGGER.debug("Query {}", sanitize(aQuery));
	    }
	    try {
		    Connection conn = DbSingleJpaConn.getConnection();
		    Statement stat = conn.createStatement();
		    ResultSet set = stat.executeQuery(aQuery);
		    return set.first();
	    } catch (OHException e) {
		    throw e;
	    } catch (SQLException e) {
		    throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlinstruction.msg"), e);
	    } catch (Exception e) {
		    throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwithserverconnection.msg"), e);
	    }
    }
    
    /**
     * Method for sanitize every String object in the list for logging purpose
     * @param params - the list of objects
     * @return the list sanitized
     */
    private List<?> sanitize(List<?> params) {
    	List<Object> saneParams = new ArrayList<>();
    	for (Object param : params) {
    		if (param instanceof String) {
    			saneParams.add(sanitize((String) param));
			} else {
				saneParams.add(param);
			}
    	} 
		return saneParams;
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
