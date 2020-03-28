/**
 * 
 */
package org.isf.utils.file;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author Mwithi
 *
 */
public class FileTools {
	
	protected final static String[][] dateTimeFormats = new String[][] {
		{"yyyy-MM-dd", "\\d{4}-\\d{2}-\\d{2}"},
		{"dd-MM-yyyy", "\\d{2}-\\d{2}-\\d{4}"},
		{"dd-MM-yy", "\\d{2}-\\d{2}-\\d{2}"},
		{"dd/MM/yyyy", "\\d{2}/\\d{2}/\\d{4}"},
		{"dd/MM/yy", "\\d{2}/\\d{2}/\\d{2}"},
		{"yyyy-MM-dd HHmm", "\\d{4}-\\d{2}-\\d{2} \\d{4}"},
		{"yyyy-MM-dd HHmmss", "\\d{4}-\\d{2}-\\d{2} \\d{6}"},
		{"yyyy-MM-dd_HHmmss", "\\d{4}-\\d{2}-\\d{2}_\\d{6}"},
		{"dd-MM-yy_HHmm", "\\d{2}-\\d{2}-\\d{2}_\\d{4}"},
		{"yyyy-MM-dd HHmm", "\\d{4}-\\d{2}-\\d{2} \\d{4}"},
		{"yyyy-MM-dd_HHmm", "\\d{4}-\\d{2}-\\d{2}_\\d{4}"},
	};

	/**
	 * 
	 */
	public FileTools() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Retrieves the last modified date
	 * @param file
	 * @return 
	 */
	public static Date getTimestamp(File file) {
		if (file == null) return null;
		return new Date(file.lastModified());
	}
	
	/**
	 * Retrieve timestamp from filename
	 * @param file
	 * @return the list of retrieved date or <code>null</code> if nothing is found
	 */
	public static List<Date> getTimestampFromName(File file) {
		return getTimestampFromName(file.getName());
	}
	
	/**
	 * Retrieves the timestamp from formattedString
	 * using these date and time formats:<br />
	 * 
	 * - yyyy-MM-dd -> "2020-02-01"<br />
	 * - dd-MM-yyyy -> "02-03-2020"<br />
	 * - dd-MM-yy -> "03-04-20"<br />
	 * - dd/MM/yyyy -> "04/05/2020"<br />
	 * - dd/MM/yy -> "05/06/20"<br />
	 * - yyyy-MM-dd HHmm -> "2020-07-06 0123"<br />
	 * - yyyy-MM-dd HHmmss -> "2020-08-07 012345"<br />
	 * - yyyy-MM-dd_HHmmss -> "2020-09-08_012345"<br />
	 * - dd-MM-yy_HHmm -> "20-10-09_0123"<br />
	 * - yyyy-MM-dd HHmm -> "2020-10-09 0123"<br />
	 * - yyyy-MM-dd_HHmm -> "2020-10-09_0123" <br />
	 * @param string
	 * @return the list of retrieved date (first null)
	 */
	public static List<Date> getTimestampFromName(String formattedString) {
		List<Date> datesFound = new ArrayList<Date>();
		return getTimestampFromName(formattedString, datesFound);
	}
	
	private static List<Date> getTimestampFromName(String formattedString, List<Date> datesFound) {
		Date date = null;
		
		for (String[] dateTimeFormat : dateTimeFormats) {
			String format = dateTimeFormat[0];
			String regex = dateTimeFormat[1];
			
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			
			try
	        {
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(formattedString);
				
				if (matcher.find())
				{
					date = sdf.parse(matcher.group());
					datesFound.add(date);
				}
	        } 
	        catch (ParseException e) {
	        	date = null;
	        }
		}
		
		return datesFound;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("1. " + FileTools.getTimestampFromName("09-03-2020"));
		System.out.println("2. " + FileTools.getTimestampFromName("01-04-2020 1238"));
		System.out.println("3. " + FileTools.getTimestampFromName("02-05-2020_1122"));
		System.out.println("4. " + FileTools.getTimestampFromName("03-06-2020"));
		System.out.println("5. " + FileTools.getTimestampFromName("04/03/2020"));
		System.out.println("6. " + FileTools.getTimestampFromName("05/05/20"));
		System.out.println("7. " + FileTools.getTimestampFromName("2021-12-22 1100"));
		System.out.println("8. " + FileTools.getTimestampFromName("21-11-21_0922"));
		
	}

}
