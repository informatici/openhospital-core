package org.isf.utils.report;

import java.util.List;
import java.util.ResourceBundle;

public class ReportUtil {

	public static String translateAdmissionState(String admissionState, String separator, ResourceBundle resourceBundle) {
		if (admissionState == null) return "";
		StringBuilder sb = new StringBuilder();

		List<String> admissionStateList = java.util.Arrays.asList(admissionState.split("\\s*" + separator + "\\s*"));

		for (int i = 0; i < admissionStateList.size(); i++) {
			try {
				sb.append(resourceBundle.getString(admissionStateList.get(i).trim()));
			} catch (Exception e) {
				sb.append(admissionStateList.get(i).trim());
			}
			if (i < admissionStateList.size() - 1) {
				sb.append(", ");
			}
		}

		return sb.toString();
	}
}
