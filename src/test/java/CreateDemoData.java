import java.util.stream.IntStream;

public class CreateDemoData {

	public static void main(String[] args) {
		int i = 0;
		IntStream.range(0, 553).forEach(ii -> {
				System.out.println(
						"INSERT INTO oh.oh_patient_consensus (PTC_ID, PTC_PAT_ID, PTC_CONSENSUNS, PTC_ADMINISTRATIVE, PTC_SERVICE, PTC_CREATED_BY, PTC_CREATED_DATE, PTC_LAST_MODIFIED_BY, PTC_LAST_MODIFIED_DATE, PTC_ACTIVE) VALUES("
								+ ii + ", " + ii
								+ ", 1, 1, 1, 'admin', '2023-03-22 21:06:29', 'admin', '2023-03-22 21:06:29', 1);");
		});

	}

}
