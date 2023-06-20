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

import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.LockTimeoutException;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceException;
import javax.persistence.PessimisticLockException;
import javax.persistence.Query;
import javax.persistence.QueryTimeoutException;
import javax.persistence.RollbackException;
import javax.persistence.TransactionRequiredException;

import org.isf.generaldata.MessageBundle;
import org.isf.utils.exception.OHException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Class that executes a query using JPA
 */
public class DbJpaUtil 
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DbJpaUtil.class);

	private static ApplicationContext context =	new ClassPathXmlApplicationContext("applicationContext.xml");
	private static EntityManagerFactory entityManagerFactory = context.getBean("entityManagerFactory", EntityManagerFactory.class);
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
     * Method to persist an object
     * @throws OHException 
     */
	public void persist(Object entity) throws OHException {
		try {
			LOGGER.debug("Persist: {}", entity);
			entityManager.persist(entity);
		} catch (EntityExistsException entityExistsException) {
			LOGGER.error("EntityExistsException", entityExistsException);
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlinstruction.msg"), entityExistsException);
		} catch (IllegalArgumentException illegalArgumentException) {
			LOGGER.error("IllegalArgumentException", illegalArgumentException);
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlinstruction.msg"), illegalArgumentException);
		} catch (TransactionRequiredException transactionRequiredException) {
			LOGGER.error("TransactionRequiredException", transactionRequiredException);
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlinstruction.msg"), transactionRequiredException);
		}
	}

    /**
     * Method to merge an object
     * @throws OHException 
     */
    public Object merge(Object entity) throws OHException {
	    Object mergedEntity;

	    try {
		    mergedEntity = entityManager.merge(entity);
		    LOGGER.debug("Merge: {}", mergedEntity);
	    } catch (IllegalArgumentException illegalArgumentException) {
		    LOGGER.error("IllegalArgumentException", illegalArgumentException);
		    throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlinstruction.msg"), illegalArgumentException);
	    } catch (TransactionRequiredException transactionRequiredException) {
		    LOGGER.error("TransactionRequiredException", transactionRequiredException);
		    throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlinstruction.msg"), transactionRequiredException);
	    }

	    return mergedEntity;
    }  
    
    /**
     * Method to find an object
     * @return merged entity
     * @throws OHException 
     */
    public Object find(Class<?> entityClass, Object primaryKey) throws OHException {
	    Object entity;

	    try {
		    entity = entityManager.find(entityClass, primaryKey);
		    LOGGER.debug("Find: {}", entity);
	    } catch (IllegalArgumentException illegalArgumentException) {
		    LOGGER.error("IllegalArgumentException", illegalArgumentException);
		    throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlinstruction.msg"), illegalArgumentException);
	    }
	    return entity;
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
     * Method to start a JPA transaction
	 * @throws OHException 
     */
	public void beginTransaction() throws OHException {
		try {
			if (getEntityManager() == null) {
				open();
			}
			if (!entityManager.getTransaction().isActive()) {
				entityManager.getTransaction().begin();
			}
		} catch (IllegalStateException illegalStateException) {
			LOGGER.error("IllegalStateException", illegalStateException);
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlinstruction.msg"), illegalStateException);
		}
	}
		         
	/**
	  * Method to create a query
	  * @param aQuery
	  * @param aClass
	  * @param jpql
	  * @throws OHException
	  */
	public void createQuery(String aQuery, Class<?> aClass, boolean jpql) throws OHException {
		if (jpql) {
			createJPQLQuery(aQuery, aClass);
		} else {
			createNativeQuery(aQuery, aClass);
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
     * Method that executes a query and returns a list
     * @throws OHException
     */
	public void executeUpdate() throws OHException {
		try {
			query.executeUpdate();
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
		} catch (PersistenceException persistenceException) {
			LOGGER.error("PersistenceException", persistenceException);
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlinstruction.msg"), persistenceException);
		}
	}
    
  	/**
     * Method to commit a JPA transactions
  	 * @throws OHException 
     */
    public void commitTransaction() throws OHException {
	    try {
		    entityManager.getTransaction().commit();
		    entityManager.clear();
	    } catch (IllegalStateException illegalStateException) {
		    LOGGER.error("IllegalStateException", illegalStateException);
		    throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlinstruction.msg"), illegalStateException);
	    } catch (RollbackException rollbackException) {
		    LOGGER.error("RollbackException", rollbackException);
		    throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlinstruction.msg"), rollbackException);
	    }
    }
	
	/**
     * Method to flush the JPA transactions
	 * @throws OHException 
     */
	public void flush() throws OHException {
		try {
			entityManager.getTransaction().begin();
			entityManager.flush();
			entityManager.getTransaction().commit();
			entityManager.clear();
		} catch (IllegalStateException illegalStateException) {
			LOGGER.error("IllegalStateException", illegalStateException);
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlinstruction.msg"), illegalStateException);
		} catch (TransactionRequiredException transactionRequiredException) {
			LOGGER.error("TransactionRequiredException", transactionRequiredException);
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlinstruction.msg"), transactionRequiredException);
		} catch (RollbackException rollbackException) {
			LOGGER.error("RollbackException", rollbackException);
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlinstruction.msg"), rollbackException);
		}
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
    
    /**
     * Method to destroy the factory
     * @throws OHException 
     */
    public void destroy() throws OHException {
	    try {
		    entityManagerFactory.close();
	    } catch (IllegalStateException illegalStateException) {
		    LOGGER.error("IllegalStateException", illegalStateException);
		    throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlinstruction.msg"), illegalStateException);
	    }
    }

	private void createJPQLQuery(String aQuery, Class<?> aClass) throws OHException {
		try {
			if (aClass == null) {
				query = entityManager.createQuery(aQuery);
			} else {
				query = entityManager.createQuery(aQuery, aClass);
			}
		} catch (IllegalArgumentException illegalArgumentException) {
			LOGGER.error("IllegalArgumentException", illegalArgumentException);
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlinstruction.msg"), illegalArgumentException);
		}
	}

	private void createNativeQuery(String aQuery, Class<?> aClass) {
		// Native SQL query
		if (aClass == null) {
			query = entityManager.createNativeQuery(aQuery);
		} else {
			query = entityManager.createNativeQuery(aQuery, aClass);
		}
	}

	/**
	 * Method to rollback a JPA transactions
	 * 
	 * @throws OHException
	 */
	public void rollbackTransaction() throws OHException {
		try {
			EntityTransaction tx = entityManager.getTransaction();
			if (tx != null && tx.isActive()) {
				entityManager.getTransaction().rollback();
			}
			entityManager.clear();
		} catch (IllegalStateException illegalStateException) {
			LOGGER.error("IllegalStateException", illegalStateException);
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlinstruction.msg"), illegalStateException);
		} catch (RollbackException rollbackException) {
			LOGGER.error("RollbackException", rollbackException);
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlinstruction.msg"), rollbackException);
		}
	}

}
