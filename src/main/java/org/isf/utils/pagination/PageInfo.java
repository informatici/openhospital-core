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
package org.isf.utils.pagination;

import org.springframework.data.domain.Page;

public class PageInfo {
	int size;
	int page;
	int nbOfElements;
	long totalNbOfElements;
	int totalPages;
	boolean hasPreviousPage;
	boolean hasNextPage;
	
	public PageInfo() {
		super();
	}
	
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getNbOfElements() {
		return nbOfElements;
	}
	public void setNbOfElements(int nbOfElements) {
		this.nbOfElements = nbOfElements;
	}
	public boolean isHasPreviousPage() {
		return hasPreviousPage;
	}
	public void setHasPreviousPage(boolean hasPreviousPage) {
		this.hasPreviousPage = hasPreviousPage;
	}
	public boolean isHasNextPage() {
		return hasNextPage;
	}
	public void setHasNextPage(boolean hasNextPage) {
		this.hasNextPage = hasNextPage;
	}

	public long getTotalNbOfElements() {
		return totalNbOfElements;
	}

	public void setTotalNbOfElements(long totalNbOfElements) {
		this.totalNbOfElements = totalNbOfElements;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}
	
	public PageInfo(int size, int page, int nbOfElements, long totalNbOfElements, int totalPages,
			boolean hasPreviousPage, boolean hasNextPage) {
		super();
		this.size = size;
		this.page = page;
		this.nbOfElements = nbOfElements;
		this.totalNbOfElements = totalNbOfElements;
		this.totalPages = totalPages;
		this.hasPreviousPage = hasPreviousPage;
		this.hasNextPage = hasNextPage;
	}

	public static PageInfo from(Page page) {
		return new PageInfo(
				page.getSize(), page.getNumber(), page.getNumberOfElements(), page.getTotalElements(), page.getTotalPages(), page.hasPrevious(), page.hasNext());
	}
	
}