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
package org.isf.priceslist.test;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.priceslist.model.Price;
import org.isf.priceslist.model.PriceList;
import org.isf.utils.exception.OHException;

public class TestPrice {

	private static String group = "TG";
	private static String item = "TestItem";
	private static String desc = "TestDescription";
	private static Double priceValue = 10.10;
	private static boolean editable = true;

	public Price setup(PriceList list, boolean usingSet) throws OHException {
		Price price;

		if (usingSet) {
			price = new Price();
			setParameters(list, price);
		} else {
			// Create PriceList with all parameters 
			price = new Price(0, list, group, item, desc, priceValue);
		}

		return price;
	}

	public void setParameters(PriceList list, Price price) {
		price.setDesc(desc);
		price.setEditable(editable);
		price.setGroup(group);
		price.setItem(item);
		price.setList(list);
		price.setPrice(priceValue);
	}

	public void check(Price price) {
		assertThat(price.getDesc()).isEqualTo(desc);
		assertThat(price.getGroup()).isEqualTo(group);
		assertThat(price.getItem()).isEqualTo(item);
		assertThat(price.getPrice()).isEqualTo(priceValue);
	}
}
