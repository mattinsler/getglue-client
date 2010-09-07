package com.adaptiveblue.util.digester;

import org.apache.commons.digester.Rule;

public class ConverterRule extends Rule {
	private final Converter _converter;
	
	public ConverterRule(Converter converter) {
		_converter = converter;
	}
	
	@Override
	public void body(String namespace, String name, String text) throws Exception {
		getDigester().push(_converter == null ? text : _converter.convertFrom(text));
	}
	
	@Override
	public void end(String namespace, String name) throws Exception {
		getDigester().pop();
	}
}
