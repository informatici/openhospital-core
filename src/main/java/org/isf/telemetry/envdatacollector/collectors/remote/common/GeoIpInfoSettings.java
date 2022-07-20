package org.isf.telemetry.envdatacollector.collectors.remote.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:telemetry.properties")
public class GeoIpInfoSettings {

	@Autowired
	private Environment env;

	public String retrieveBaseUrl(String serviceName) {
		return this.env.getProperty(serviceName + ".ribbon.base-url");
	}
	
	public String get(String key) {
		return this.env.getProperty(key);
	}

}
