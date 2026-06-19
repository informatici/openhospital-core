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
package org.isf.stat.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Describes a stat report that can be launched by the user: where its compiled {@code .jasper} file lives, its file name (without extension), its localized
 * title and the names of the parameters the user is expected to fill in (e.g. {@code month}/{@code year} or {@code fromdate}/{@code todate}).
 */
public class ReportLauncherDto implements Serializable {

	private String folder;
	private String fileName;
	private String title;
	private List<String> userInputParameters = new ArrayList<>();

	public ReportLauncherDto() {
	}

	public ReportLauncherDto(String folder, String fileName, String title, List<String> userInputParameters) {
		this.folder = folder;
		this.fileName = fileName;
		this.title = title;
		this.userInputParameters = userInputParameters;
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<String> getUserInputParameters() {
		return userInputParameters;
	}

	public void setUserInputParameters(List<String> userInputParameters) {
		this.userInputParameters = userInputParameters;
	}
}
