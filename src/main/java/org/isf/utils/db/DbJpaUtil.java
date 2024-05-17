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

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.LockTimeoutException;
import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.PessimisticLockException;
import jakarta.persistence.Query;
import jakarta.persistence.QueryTimeoutException;
import jakarta.persistence.TransactionRequiredException;

import org.isf.generaldata.MessageBundle;
import org.isf.menu.manager.Context;
import org.isf.utils.exception.OHException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that executes a query using JPA
 */
public class DbJpaUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(DbJpaUtil.class);

	private static EntityManagerFactory entityManagerFactory = Context.getApplicationContext().getBean("entityManagerFactory", EntityManagerFactory.class);
	private static EntityManager entityManager;
	private static Query query;

	/**
     * Constructor that initialize the entity Manager
     */
	public DbJpaUtil() {}
	
	/**
     * Constructor that initialize the entity Manager
	 * @throws OHException 
     */
	public void open() throws OHException {
		try {
			entityManager = entityManagerFactory.createEntityManager();
		} catch (IllegalStateException illegalStateException) {
			LOGGER.error("IllegalStateException", illegalStateException);
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlinstruction.msg"), illegalStateException);
		}
	}
	
	/**
	 * @return the entityManager
	 */
	public EntityManagerFactory getEntityManagerFactory() {
		return entityManagerFactory;
	}
    
    /**
	 * @return the entityManager
	 */
	public EntityManager getEntityManager() {
		return entityManager;
	}

    /**
     * Method to remove an object
     * @throws OHException
     */
    public void remove(Object entity) throws OHException {
	    try {
		    LOGGER.debug("Remove: {}", entity);
		    entityManager.remove(entityManager.contains(entity) ? entity : entityManager.merge(entity));
	    } catch (IllegalArgumentException illegalArgumentException) {
		    LOGGER.error("IllegalArgumentException", illegalArgumentException);
		    throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlinstruction.msg"), illegalArgumentException);
	    } catch (TransactionRequiredException transactionRequiredException) {
		    LOGGER.error("TransactionRequiredException", transactionRequiredException);
		    throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlinstruction.msg"), transactionRequiredException);
	    }
    }

	/**
     * @param parameters
     * @param jpql
     * @throws OHException
     */
	public void setParameters(List<?> parameters, boolean jpql) throws OHException {
		try {
			for (int i = 0; i < parameters.size(); i++) {
				query.setParameter((i + 1), parameters.get(i));
			}
		} catch (IllegalArgumentException illegalArgumentException) {
			LOGGER.error("IllegalArgumentException", illegalArgumentException);
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlinstruction.msg"), illegalArgumentException);
		}
	}
    
	/**
     * Method that executes a query and returns a list
     * @return List of objects
     * @throws OHException
     */
	public List<?> getList() throws OHException {
		List<?> list;

		try {
			list = query.getResultList();
		} catch (IllegalStateException illegalStateException) {
			LOGGER.error("IllegalStateException", illegalStateException);
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlinstruction.msg"), illegalStateException);
		} catch (QueryTimeoutException queryTimeoutException) {
			LOGGER.error("QueryTimeoutException", queryTimeoutException);
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlinstruction.msg"), queryTimeoutException);
		} catch (TransactionRequiredException transactionRequiredException) {
			LOGGER.error("TransactionRequiredException", transactionRequiredException);
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlinstruction.msg"), transactionRequiredException);
		} catch (PessimisticLockException pessimisticLockException) {
			LOGGER.error("PessimisticLockException", pessimisticLockException);
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlinstruction.msg"), pessimisticLockException);
		} catch (LockTimeoutException lockTimeoutException) {
			LOGGER.error("LockTimeoutException", lockTimeoutException);
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlinstruction.msg"), lockTimeoutException);
		} catch (PersistenceException persistenceException) {
			LOGGER.error("PersistenceException", persistenceException);
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlinstruction.msg"), persistenceException);
		} catch (StringIndexOutOfBoundsException stringIndexOutOfBoundsException) {
			LOGGER.error("StringIndexOutOfBoundsException", stringIndexOutOfBoundsException);
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlinstruction.msg"), stringIndexOutOfBoundsException);
		}
		return list;
	}
    
    /**
     * Method that executes a query and return an object
     * @return Object
     * @throws OHException
     */
    public Object getResult() throws OHException {
	    Object result = null;

	    try {
		    result = query.getSingleResult();
	    } catch (NoResultException noResultException) {
		    LOGGER.error("NoResultException", noResultException);
		    throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlinstruction.msg"), noResultException);
	    } catch (NonUniqueResultException nonUniqueResultException) {
		    LOGGER.error("NonUniqueResultException", nonUniqueResultException);
		    throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlinstruction.msg"), nonUniqueResultException);
	    } catch (IllegalStateException illegalStateException) {
		    LOGGER.error("IllegalStateException", illegalStateException);
		    throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlinstruction.msg"), illegalStateException);
	    } catch (QueryTimeoutException queryTimeoutException) {
		    LOGGER.error("QueryTimeoutException", queryTimeoutException);
		    throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlinstruction.msg"), queryTimeoutException);
	    } catch (TransactionRequiredException transactionRequiredException) {
		    LOGGER.error("TransactionRequiredException", transactionRequiredException);
		    throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlinstruction.msg"), transactionRequiredException);
	    } catch (PessimisticLockException pessimisticLockException) {
		    LOGGER.error("PessimisticLockException", pessimisticLockException);
		    throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlinstruction.msg"), pessimisticLockException);
	    } catch (LockTimeoutException lockTimeoutException) {
		    LOGGER.error("LockTimeoutException", lockTimeoutException);
		    throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlinstruction.msg"), lockTimeoutException);
	    } catch (PersistenceException persistenceException) {
		    LOGGER.error("PersistenceException", persistenceException);
		    throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlinstruction.msg"), persistenceException);
	    } catch (Exception exception) {
		    LOGGER.error("UnknownException", exception);
	    }
	    return result;
    }    

	/**
     * Method to close the JPA entity manager
     * @throws OHException 
     */
    public void close() throws OHException {
	    try {
		    entityManager.close();
	    } catch (IllegalStateException illegalStateException) {
		    LOGGER.error("IllegalStateException", illegalStateException);
		    throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlinstruction.msg"), illegalStateException);
	    }
    }

}
