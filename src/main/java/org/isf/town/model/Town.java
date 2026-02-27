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
package org.isf.town.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.isf.base.model.BaseEntity;

@Entity
@Table(name="OH_TOWN")
@AttributeOverride(name = "createdBy", column = @Column(name = "TW_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "TW_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "TW_LAST_MODIFIED_BY"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "TW_LAST_MODIFIED_DATE"))
@AttributeOverride(name = "active", column = @Column(name = "TW_ACTIVE"))
@AttributeOverride(name = "id",   column = @Column(name="TW_ID"))
@AttributeOverride(name = "name", column = @Column(name="TW_NAME"))
public class Town extends BaseEntity {
}
