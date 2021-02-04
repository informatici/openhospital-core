/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2020 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.utils.db;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Properties;

/**
 * ImportTranslatedProperties.java - 04/set/2013
 *
 * @author Mwithi
 */
public class ImportTranslatedProperties {

	/**
	 * This utility import from specified path
	 * the keys in the original language properties.
	 * 
	 * Results are unsorted and unaligned so a refresh in
	 * ResourceBundleEditor plugin is needed before commit
	 */
	public ImportTranslatedProperties() {
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/*
		 * path where to find new translations
		 */
		String pathOriginal = "bundle/";
		String pathIn = "E:\\translations\\fromTranslators\\"; //translations from translators
		String pathOut = "E:\\translations\\bundle\\"; //new generated bundle to copy (replace) in OH
		File folderIn = new File(pathIn);
		
		FileFilter fileFilter = new FileFilter() {
			public boolean accept(File file) {
				return !file.isDirectory();
			}
		};
		
		for (final File file : folderIn.listFiles(fileFilter)) {
			String filename = file.getName();
			System.out.println("Processing... " + filename);

			// if (true) continue;

			//FileInputStream in;
			InputStream inputStream;
			Reader in;
			FileOutputStream out;
			try {

				//in = new FileInputStream(pathOriginal + filename);
				inputStream = new FileInputStream(pathOriginal + filename);
				in = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
				Properties propsOri = new Properties();
				propsOri.load(in);
				in.close();
				Enumeration<?> oriKeys = propsOri.propertyNames();

				//in = new FileInputStream(pathIn + filename);
				inputStream = new FileInputStream(pathIn + filename);
				in = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
				Properties propsIn = new Properties();
				propsIn.load(in);
				in.close();
				Enumeration<?> newKeys = propsIn.propertyNames();

				out = new FileOutputStream(pathOut + filename);
				// keys already present
				while (oriKeys.hasMoreElements()) {
					String key = (String) oriKeys.nextElement();
					String value = propsOri.getProperty(key);

					if (propsIn.containsKey(key)) {
						value = propsIn.getProperty(key);
					}

					
					propsOri.setProperty(key, value);
					
				}
				
				// new keys
				while (newKeys.hasMoreElements()) {
					String key = (String) newKeys.nextElement();
					
					if (!propsOri.containsKey(key)) {
						String value = propsIn.getProperty(key);

						//System.out.println(key + " = " + value);
						propsOri.setProperty(key, value);
						
						
					}
				}
				propsOri.store(out, null);
				out.close();
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Done.");
	}
}
