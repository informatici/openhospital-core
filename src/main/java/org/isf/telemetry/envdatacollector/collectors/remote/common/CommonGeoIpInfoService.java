package org.isf.telemetry.envdatacollector.collectors.remote.common;

import org.isf.sms.providers.common.CustomCommonDecoder;
import org.isf.sms.providers.common.CustomCommonEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import feign.Feign;
import feign.slf4j.Slf4jLogger;


public abstract class CommonGeoIpInfoService {


	
	public abstract GeoIpInfoBean retrieveIpInfo();

	public abstract String getServiceName();

	protected <RemoteServiceClass, ServiceClass> RemoteServiceClass buildHttlClient(String baseUrl, 
			Class<RemoteServiceClass> remoteServiceClass, Class<ServiceClass> serviceClass) {
		// For debug remember to update log level to: feign.Logger.Level.FULL. Happy
		// debugging!

		return Feign.builder().encoder(new CustomCommonEncoder()).decoder(new CustomCommonDecoder())
				.logger(new Slf4jLogger(serviceClass)).logLevel(feign.Logger.Level.BASIC).contract(new SpringMvcContract())
				.target(remoteServiceClass, baseUrl);
	}
}
