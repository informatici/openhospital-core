/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2026 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.dicom.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Field;
import java.util.Properties;

import org.isf.OHCoreTestCase;
import org.isf.menu.manager.Context;
import org.isf.utils.exception.OHDicomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

class DicomManagerFactoryTest extends OHCoreTestCase {

	public static final int PATIENT_ID = 0;
	public static final Long _4M = 4194304L;

	@Autowired
	private ApplicationContext applicationContext;

	@BeforeEach
	void setUp() throws Exception {
		cleanH2InMemoryDb();
		Context.setApplicationContext(applicationContext);
	}

	@Test
	void getManager_shouldReturnFileSystemDicomManager() throws Exception {
		DicomManagerInterface manager = DicomManagerFactory.getManager();
		assertThat(manager).isInstanceOf(FileSystemDicomManager.class);
	}

	@Test
	void getMaxDicomSize_shouldReturnDefault_whenMaxSizeIsNotSet() throws Exception {
		String maxDicomSize = DicomManagerFactory.getMaxDicomSize();
		assertThat(maxDicomSize).isEqualTo("4M");
	}

	@Test
	void getMaxDicomSize_shouldReturnConfiguredValue() throws Exception {
		DicomManagerFactory.getManager();
		Class clazz = Class.forName("org.isf.dicom.manager.DicomManagerFactory");
		Field props = clazz.getDeclaredField("props");
		props.setAccessible(true);
		Properties properties = new Properties();
		properties.setProperty("dicom.max.size", "2M");
		props.set(clazz, properties);
		String maxDicomSize = DicomManagerFactory.getMaxDicomSize();
		assertThat(maxDicomSize).isEqualTo("2M");
		// reset/clear properties
		props.set(clazz, new Properties());
	}

	@Test
	void getMaxDicomSizeLong_shouldReturnDefault_whenMaxSizeIsNotSet() throws Exception {
		Long maxDicomSize = DicomManagerFactory.getMaxDicomSizeLong();
		assertThat(maxDicomSize).isEqualTo(_4M);
	}

	@Test
	void getMaxDicomSizeLong_shouldReturnConfiguredValueInBytes() throws Exception {
		DicomManagerFactory.getManager();
		Class clazz = Class.forName("org.isf.dicom.manager.DicomManagerFactory");
		Field props = clazz.getDeclaredField("props");
		props.setAccessible(true);
		Properties properties = new Properties();
		properties.setProperty("dicom.max.size", "2M");
		props.set(clazz, properties);
		long maxDicomSize = DicomManagerFactory.getMaxDicomSizeLong();
		assertThat(maxDicomSize).isEqualTo(2097152L);
		// reset/clear properties
		props.set(clazz, new Properties());
	}

	@Test
	void getMaxDicomSizeLong_shouldThrow_whenConfiguredValueIsNotASize() throws Exception {
		assertThatThrownBy(() ->
		{
			DicomManagerFactory.getManager();
			Class clazz = Class.forName("org.isf.dicom.manager.DicomManagerFactory");
			Field props = clazz.getDeclaredField("props");
			props.setAccessible(true);
			Properties properties = new Properties();
			properties.setProperty("dicom.max.size", "aString");
			props.set(clazz, properties);
			DicomManagerFactory.getMaxDicomSizeLong();
		})
				.isInstanceOf(OHDicomException.class);
		Class clazz = Class.forName("org.isf.dicom.manager.DicomManagerFactory");
		// reset/clear properties
		Field props = clazz.getDeclaredField("props");
		props.setAccessible(true);
		props.set(clazz, new Properties());
	}

}
