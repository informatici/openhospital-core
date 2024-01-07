package org.isf.utils.db;

import java.lang.reflect.InvocationTargetException;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuditorAwareConfig {

	@Bean
	@ConditionalOnClass(name = "org.isf.security.ApiAuditorAwareImpl")
	public AuditorAwareInterface auditorAwareCustomizer() {
		try {
			Class< ? > customAuditorAwareImplClass = Class.forName("org.isf.security.ApiAuditorAwareImpl");
			return (AuditorAwareInterface) customAuditorAwareImplClass.getDeclaredConstructor().newInstance();
		} catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
			return new DefaultAuditorAwareImpl();
		}
	}

	@Bean
	@ConditionalOnMissingBean(AuditorAwareInterface.class)
	public AuditorAwareInterface defaultAuditorAwareCustomizer() {
		return new DefaultAuditorAwareImpl();
	}
}
