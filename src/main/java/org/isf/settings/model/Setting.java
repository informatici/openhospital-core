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
package org.isf.settings.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.isf.settings.enums.SettingValueType;
import org.isf.utils.db.Auditable;

/**
 * Setting Core Entity
 *
 * @author Silevester D.
 * @since v1.15
 */
@Entity
@Table(name = "OH_SETTINGS")
@AttributeOverride(name = "createdBy", column = @Column(name = "SETT_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "SETT_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "SETT_MODIFIED_BY"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "SETT_MODIFIED_DATE"))
@AttributeOverride(name = "active", column = @Column(name = "SETT_ACTIVE"))
public class Setting extends Auditable<String> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "SETT_ID")
	private int id;

	@Column(name="SETT_CODE", length = 50, nullable = false)
	private String code;

	@Column(name = "SETT_VALUE_TYPE", nullable = false)
	@Enumerated(EnumType.STRING)
	private SettingValueType type;

	@Column(name = "SETT_VALUE_OPTIONS", length = 500)
	private String valueOptions;

	@Column(name="SETT_DEFAULT_VALUE", nullable = false)
	private String defaultValue;

	@Column(name="SETT_VALUE", nullable = false)
	private String value;

	@Column(name="SETT_DESCRIPTION", length = 500)
	private String description;

	@Column(name="SETT_DELETED", nullable = false)
	private Boolean deleted = false;

	@Column(name="SETT_EDITABLE", nullable = false)
	private Boolean isEditable = true;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public SettingValueType getType() {
		return type;
	}

	public void setType(SettingValueType type) {
		this.type = type;
	}

	public String getValueOptions() {
		return valueOptions;
	}

	public void setValueOptions(String valueOptions) {
		this.valueOptions = valueOptions;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public Boolean getEditable() { return isEditable; }

	public void setEditable(Boolean editable) {
		isEditable = editable;
	}
}
