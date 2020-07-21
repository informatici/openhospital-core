package org.isf.dicom.manager;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.isf.generaldata.MessageBundle;
import org.isf.menu.manager.Context;
import org.isf.utils.exception.OHDicomException;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.isf.utils.file.FileTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory for instantiate DicomManager
 * 
 * @author Pietro Castellucci
 * @version 1.0.0
 */
public class DicomManagerFactory {

	private static final Logger logger = LoggerFactory.getLogger(DicomManagerFactory.class);
	
	private static DicomManagerInterface instance = null;

	private static Properties props = new Properties();
	
	public static String getMaxDicomSize() {
		return props.getProperty("dicom.max.size", "4M");
	}
	
	public static Long getMaxDicomSizeLong() throws OHDicomException {
		String sizeHumanReadable = props.getProperty("dicom.max.size", "4M");
		long sizeLong = 4194304L; // default for 4M
		try {
			sizeLong = FileTools.humanReadableByteCountParse(sizeHumanReadable);
		} catch (OHException e) {
			throw new OHDicomException(e, new OHExceptionMessage("DICOM", 
					e.getMessage(), OHSeverityLevel.WARNING));
		}
		return sizeLong;
	}

	/**
	 * return the manager for DICOM acquired files
	 * @throws OHDicomException 
	 */
      public static synchronized DicomManagerInterface getManager() throws OHDicomException {

		if (instance == null) {

			try {
				init();

				instance = (DicomManagerInterface) Context.getApplicationContext().getBean(Class.forName(props.getProperty("dicom.manager.impl"))); //.getConstructor(Class.forName("java.util.Properties")).newInstance(props);
				if (instance instanceof FileSystemDicomManager) {
					((FileSystemDicomManager) instance).setDir(props);
				}
			} catch(Exception e){
				//Any exception
				throw new OHDicomException(e, new OHExceptionMessage(MessageBundle.getMessage("angal.hospital"), 
						props.getProperty("dicom.manager.impl") + " " + MessageBundle.getMessage("angal.dicom.manager.noimpl"), OHSeverityLevel.ERROR));
			}
		}

		return instance;
	}

	private static void init() throws OHDicomException {
		try {

			File f = new File("rsc/dicom.properties");

			if (!f.exists()) {
				throw new OHDicomException(new OHExceptionMessage(MessageBundle.getMessage("angal.hospital"), 
						MessageBundle.getMessage("angal.dicom.nofile") + " rsc/dicom.manager.properties", OHSeverityLevel.ERROR));
			}

			FileInputStream in = new FileInputStream("rsc/dicom.properties");

			props.load(in);

			in.close();
		} catch (Exception exc) {
			throw new OHDicomException(exc, new OHExceptionMessage(MessageBundle.getMessage("angal.hospital"), 
					MessageBundle.getMessage("angal.dicom.manager.err") + " " + exc.getMessage(), OHSeverityLevel.ERROR));
		}
	}
	
	
}
