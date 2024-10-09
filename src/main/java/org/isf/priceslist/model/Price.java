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
package org.isf.priceslist.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;

import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name="OH_PRICES")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "PRC_CREATED_BY"))
@AttributeOverride(name = "createdDate", column = @Column(name = "PRC_CREATED_DATE"))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "PRC_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "PRC_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "PRC_LAST_MODIFIED_DATE"))
public class Price extends Auditable<String> {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="PRC_ID")
	private int id;

	@NotNull
	@ManyToOne
	@JoinColumn(name="PRC_LST_ID")
	private PriceList list;

	@NotNull
	@Column(name="PRC_GRP", length=3)
	private String group;

	@NotNull
	@Column(name="PRC_ITEM")
	private String item;

	@NotNull
	@Column(name="PRC_DESC")
	private String description;

	@NotNull
	@Column(name="PRC_PRICE")
	private Double price;

	@Version
	@Column(name = "PRC_LOCK")
	private int lock;

	@Transient
	private boolean editable;

	@Transient
	private volatile int hashCode;

	public Price() {
		super();
	}

	/**
	 * @param list Parent list
	 * @param group Item group name
	 * @param item Item name
	 * @param desc Item description
	 * @param price Price
	 * @param editable Can be edited or not
	 */
	public Price(PriceList list, String group, String item, String desc, Double price, boolean editable) {
		super();
		this.list = list;
		this.group = group;
		this.item = item;
		this.description = desc;
		this.price = price;
		this.editable = editable;
	}

	/**
	 *
	 * @param id Price ID
	 * @param list Parent list
	 * @param group Item group name
	 * @param item Item name
	 * @param desc Item description
	 * @param price Price
	 */
	public Price(int id, PriceList list, String group, String item, String desc, Double price) {
		super();
		this.id = id;
		this.list = list;
		this.group = group;
		this.item = item;
		this.description = desc;
		this.price = price;
		this.editable = true;
	}

	/**
	 * @param list Parent list
	 * @param group Item group name
	 * @param item Item name
	 * @param desc Item description
	 * @param price Price
	 */
	public Price(PriceList list, String group, String item, String desc, Double price) {
		this.list = list;
		this.group = group;
		this.item = item;
		this.description = desc;
		this.price = price;
		this.editable = true;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public PriceList getList() {
		return list;
	}

	public void setList(PriceList list) {
		this.list = list;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public String getDesc() {
		return description;
	}

	public void setDesc(String desc) {
		this.description = desc;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public boolean isPrice() {
		return item.compareTo("") != 0;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public int getLock() { return lock; }

	public void setLock(int lock) { this.lock = lock; }

	@Override
	public String toString() {
		return description;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof Price price1)) {
			return false;
		}

		return (id == price1.getId());
	}

	@Override
	public int hashCode() {
		if (this.hashCode == 0) {
			final int m = 23;
			int c = 133;

			c = m * c + id;

			this.hashCode = c;
		}

		return this.hashCode;
	}
}
