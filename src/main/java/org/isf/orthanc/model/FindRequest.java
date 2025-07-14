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
package org.isf.orthanc.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author Silevester D.
 * @since 1.15
 */
public class FindRequest {
	@SerializedName("Level")
	private String level;

	@SerializedName("Expand")
	private boolean expand;

	@SerializedName("Query")
	private Query query;

	public String getLevel() {
		return level;
	}

	public FindRequest(String level, boolean expand, Query query) {
		this.level = level;
		this.expand = expand;
		this.query = query;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public boolean getExpand() {
		return expand;
	}

	public void setExpand(boolean expand) {
		this.expand = expand;
	}

	public Query getQuery() {
		return query;
	}

	public void setQuery(Query query) {
		this.query = query;
	}
}

