/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2025 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import org.isf.utils.time.TimeTools;

/**
 * Base class for entities supporting <i>soft deletion</i> (a {@code XXX_DELETED} flag instead of a physical
 * removal). It extends {@link Auditable} adding who and when performed the deletion, so soft deletions can be
 * audited like creations and modifications. Unlike the {@code Auditable} fields, these are not managed by Spring
 * Data auditing (there is no {@code @DeletedBy}): they have to be set explicitly on the soft-delete code path.
 */
@MappedSuperclass
public abstract class SoftDeletableAuditable<U> extends Auditable<U> {

	@Column(name = "DELETED_BY")
	protected U deletedBy;

	@Column(name = "DELETED_DATE")
	protected LocalDateTime deletedDate;

	public U getDeletedBy() {
		return deletedBy;
	}

	public void setDeletedBy(U deletedBy) {
		this.deletedBy = deletedBy;
	}

	public LocalDateTime getDeletedDate() {
		return deletedDate;
	}

	public void setDeletedDate(LocalDateTime deletedDate) {
		this.deletedDate = deletedDate;
	}

	/**
	 * Records, on this entity, who performed a soft deletion and stamps the deletion timestamp (now). To be called
	 * on the soft-delete path before saving the entity.
	 *
	 * @param deletedBy the auditor performing the deletion.
	 */
	public void recordDeletion(U deletedBy) {
		this.deletedBy = deletedBy;
		this.deletedDate = TimeTools.getNow();
	}

}
