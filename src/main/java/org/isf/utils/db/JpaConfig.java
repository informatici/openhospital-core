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

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * @author uni2grow
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class JpaConfig {

	private final AuditorAwareInterface auditorAwareImpl;

	public JpaConfig(@Lazy AuditorAwareInterface auditorAwareImpl) {
		this.auditorAwareImpl = auditorAwareImpl;
	}

	@Bean
	public AuditorAware<String> auditorAware() {
		if (auditorAwareImpl != null) {
			return () -> auditorAwareImpl.getCurrentAuditor();
		}
		return () -> Optional.of("defaultAuditor");
	}

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
