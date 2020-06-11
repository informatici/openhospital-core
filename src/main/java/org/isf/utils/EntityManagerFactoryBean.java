package org.isf.utils;

import javax.persistence.EntityManagerFactory;

import org.isf.utils.db.DbJpaUtil;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

public class EntityManagerFactoryBean implements FactoryBean<EntityManagerFactory> {
	@Autowired
	private DbJpaUtil ioOperations;
	
	@Override
	public EntityManagerFactory getObject() throws Exception {
		return ioOperations.getEntityManagerFactory();
	}

	@Override
	public Class<?> getObjectType() {
		return null;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

}
