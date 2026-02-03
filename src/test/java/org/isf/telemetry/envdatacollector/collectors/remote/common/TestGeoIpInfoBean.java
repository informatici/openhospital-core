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
package org.isf.telemetry.envdatacollector.collectors.remote.common;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.OHCoreTestCase;
import org.junit.jupiter.api.Test;

class TestGeoIpInfoBean extends OHCoreTestCase {

	@Test
	void testBean() {
		GeoIpInfoBean geoIpInfoBean = new GeoIpInfoBean();

		geoIpInfoBean.setIp("IP-string");
		assertThat(geoIpInfoBean.getIp()).isEqualTo("IP-string");

		geoIpInfoBean.setCountryCode("countryCode");
		assertThat(geoIpInfoBean.getCountryCode()).isEqualTo("countryCode");

		geoIpInfoBean.setCountryName("countryName");
		assertThat(geoIpInfoBean.getCountryName()).isEqualTo("countryName");

		geoIpInfoBean.setRegionName("regionName");
		assertThat(geoIpInfoBean.getRegionName()).isEqualTo("regionName");

		geoIpInfoBean.setCity("cityName");
		assertThat(geoIpInfoBean.getCity()).isEqualTo("cityName");

		geoIpInfoBean.setPostalCode("postalCode");
		assertThat(geoIpInfoBean.getPostalCode()).isEqualTo("postalCode");

		geoIpInfoBean.setTimeZone("timeZone");
		assertThat(geoIpInfoBean.getTimeZone()).isEqualTo("timeZone");

		geoIpInfoBean.setLatitude(123d);
		assertThat(geoIpInfoBean.getLatitude()).isEqualTo(123d);

		geoIpInfoBean.setLongitude(-123d);
		assertThat(geoIpInfoBean.getLongitude()).isEqualTo(-123d);

		geoIpInfoBean.setCurrencyCode("currencyCode");
		assertThat(geoIpInfoBean.getCurrencyCode()).isEqualTo("currencyCode");

		assertThat(geoIpInfoBean.toString()).isNotNull();
	}
}
