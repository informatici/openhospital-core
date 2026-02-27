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
package org.isf.occupation.model;

import jakarta.persistence.*;
import org.isf.base.model.BaseEntity;
import org.isf.utils.db.Auditable;

@Entity
@Table(name="OH_OCCUPATION")
@AttributeOverride(name = "createdBy", column = @Column(name = "OCC_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "OCC_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "OCC_LAST_MODIFIED_BY"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "OCC_LAST_MODIFIED_DATE"))
@AttributeOverride(name = "active", column = @Column(name = "OCC_ACTIVE"))
@AttributeOverride(name = "id",   column = @Column(name="OCC_ID"))
@AttributeOverride(name = "name", column = @Column(name="OCC_NAME"))
public class Occupation extends BaseEntity {
}
