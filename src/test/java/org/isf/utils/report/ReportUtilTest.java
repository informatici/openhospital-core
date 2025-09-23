package org.isf.utils.report;

import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ReportUtilTest {

	@Test
	void testTranslateAdmissionState() {
		GeneralData.LANGUAGE = "en";
		MessageBundle.getBundle();

		String admissionState = "[\"airway_obstruction\",\"respiratory_distress\",\"shock\",\"convulsion\",\"coma\",\"dehydration\",\"hypoglycemia\"]";
		String admissionStateTransformed = String.join(", ", java.util.Arrays.asList(admissionState.replace("[", "") .replace("]", "").replace("\"", " ").split("\\s*,\\s*")));

		String admissionStateTranslated = ReportUtil.translateAdmissionState(admissionStateTransformed, ",", MessageBundle.getBundle());

		assertThat(admissionStateTranslated).isNotNull();
		assertThat(admissionStateTranslated).isNotBlank();
	}
}
