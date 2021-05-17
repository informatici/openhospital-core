package org.isf.sms.providers.common;

import java.lang.reflect.Type;

import feign.RequestTemplate;
import feign.codec.EncodeException;
import feign.codec.Encoder;
import feign.gson.GsonEncoder;

public class CustomCommonEncoder implements Encoder {

	public CustomCommonEncoder() {
		super();
	}

	@Override
	public void encode(Object object, Type bodyType, RequestTemplate template) throws EncodeException {
		GsonEncoder gson = new GsonEncoder();
		gson.encode(object, bodyType, template);
	}
}