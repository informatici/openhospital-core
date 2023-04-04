package org.isf.serviceprinting.manager;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;

public interface Action {
	void Print(JasperPrint jasperPrint, String filename) throws JRException;
}
