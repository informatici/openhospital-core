package org.isf.telemetry.envdatacollector.collectors.remote.common;

import org.isf.sms.providers.common.CustomCommonDecoder;
import org.isf.sms.providers.common.CustomCommonEncoder;
import org.springframework.cloud.openfeign.support.SpringMvcContract;

import feign.Feign;
import feign.slf4j.Slf4jLogger;


public abstract class GeoIpInfoCommonService {


	
	public abstract GeoIpInfoBean retrieveIpInfo();

	public abstract String getServiceName();

	protected <T, S> T buildHttlClient(String baseUrl, 
			Class<T> remoteServiceClass, Class<S> serviceClass) {
		// For debug remember to update log level to: feign.Logger.Level.FULL. Happy
		// debugging!
		return Feign.builder().encoder(new CustomCommonEncoder()).decoder(new CustomCommonDecoder())
				.logger(new Slf4jLogger(serviceClass)).logLevel(feign.Logger.Level.BASIC).contract(new SpringMvcContract())
				.target(remoteServiceClass, baseUrl);
	}
}