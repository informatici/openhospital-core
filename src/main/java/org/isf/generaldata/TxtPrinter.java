/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2021 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isf.generaldata;

public final class TxtPrinter extends ConfigurationProperties {
	
	private static final String FILE_PROPERTIES = "txtPrinter.properties";

	public static boolean USE_DEFAULT_PRINTER;
	public static boolean PRINT_AS_PAID;
    public static boolean PRINT_WITHOUT_ASK;
    public static int TXT_CHAR_HEIGHT;
    public static int TXT_CHAR_WIDTH;
    public static String MODE;
    public static String ZPL_FONT_TYPE;
    public static int ZPL_ROW_HEIGHT;
    public static String PRINTER;
    
    private static final boolean DEFAULT_USE_DEFAULT_PRINTER = true;
    private static final boolean DEFAULT_PRINT_AS_PAID = false;
    private static final boolean DEFAULT_PRINT_WITHOUT_ASK = false;
    private static final int DEFAULT_TXT_CHAR_HEIGHT = 10;
    private static final int DEFAULT_TXT_CHAR_WIDTH = 10;
    private static final String DEFAULT_MODE = "PDF";
    private static final String DEFAULT_ZPL_FONT_TYPE = "A";
    private static final int DEFAULT_ZPL_ROW_HEIGHT = 9;
    
    private static TxtPrinter mySingleData;

    private TxtPrinter(String fileProperties) {
    	super(fileProperties);
			
		USE_DEFAULT_PRINTER = myGetProperty("USE_DEFAULT_PRINTER", DEFAULT_USE_DEFAULT_PRINTER);
		if (!USE_DEFAULT_PRINTER) {
			PRINTER = myGetProperty("USE_DEFAULT_PRINTER");
		}
		PRINT_AS_PAID = myGetProperty("PRINT_AS_PAID", DEFAULT_PRINT_AS_PAID);
		PRINT_WITHOUT_ASK = myGetProperty("PRINT_WITHOUT_ASK", DEFAULT_PRINT_WITHOUT_ASK);
		TXT_CHAR_HEIGHT = myGetProperty("TXT_CHAR_HEIGHT", DEFAULT_TXT_CHAR_HEIGHT);
		TXT_CHAR_WIDTH = myGetProperty("TXT_CHAR_WIDTH", DEFAULT_TXT_CHAR_WIDTH);
		MODE = myGetProperty("MODE", DEFAULT_MODE);
		ZPL_FONT_TYPE = myGetProperty("ZPL_FONT_TYPE", DEFAULT_ZPL_FONT_TYPE);
	    ZPL_ROW_HEIGHT = myGetProperty("ZPL_ROW_HEIGHT", DEFAULT_ZPL_ROW_HEIGHT);
    }

	public static TxtPrinter getTxtPrinter() {
		if (mySingleData == null) {
			initialize();
		}
		return mySingleData;
	}

	public static void initialize() {
		mySingleData = new TxtPrinter(FILE_PROPERTIES);
	}

}
