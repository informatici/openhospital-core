/**
 * 
 */
package org.isf.menu.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/**
 * @author Nanni
 *
 */

public class Context {
	@Autowired
	private static ApplicationContext applicationContext; // = new ClassPathXmlApplicationContext("applicationContext.xml");
	
	/**
	 * Returns the main {@link ApplicationContext}. 
	 */
	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public static void setApplicationContext(ApplicationContext applicationContext) {
		Context.applicationContext = applicationContext;
	}

}
