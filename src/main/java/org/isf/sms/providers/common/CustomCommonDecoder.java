package org.isf.sms.providers.common;

import java.io.IOException;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.cloud.netflix.feign.support.ResponseEntityDecoder;
import org.springframework.cloud.netflix.feign.support.SpringDecoder;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.fasterxml.jackson.databind.ObjectMapper;

import feign.FeignException;
import feign.Response;
import feign.codec.DecodeException;
import feign.codec.Decoder;

public class CustomCommonDecoder implements Decoder {

	public CustomCommonDecoder() {
		super();
	}

	@Override
	public Object decode(Response response, java.lang.reflect.Type type) throws IOException, DecodeException, FeignException {
		feign.Response responseFeing = (feign.Response) response;
		HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter(new ObjectMapper());
		ObjectFactory<HttpMessageConverters> objectFactory = () -> new HttpMessageConverters(jacksonConverter);
		return new ResponseEntityDecoder(new SpringDecoder(objectFactory)).decode(responseFeing, type);
	}
}