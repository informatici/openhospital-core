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
package org.isf.utils.db;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import org.isf.generaldata.MessageBundle;
import org.isf.menu.manager.Context;
import org.isf.utils.exception.OHException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that is used to create a DB connection.
 */
public class DbJpaUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(DbJpaUtil.class);

	private static EntityManagerFactory entityManagerFactory = Context.getApplicationContext().getBean("entityManagerFactory", EntityManagerFactory.class);
	private static EntityManager entityManager;

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
	public EntityManager getEntityManager() {
		return entityManager;
	}

}
