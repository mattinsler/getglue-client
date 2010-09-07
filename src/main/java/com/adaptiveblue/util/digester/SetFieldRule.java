package com.adaptiveblue.util.digester;

import java.lang.reflect.Field;

import org.apache.commons.digester.Rule;

public class SetFieldRule extends Rule {
	private final Field _field;
	private final Converter _converter;
	
	public SetFieldRule(Field field) {
		this(field, null);
	}
	
	public SetFieldRule(Field field, Converter converter) {
		field.setAccessible(true);
		_field = field;
		_converter = converter;
	}
	
	@Override
	public void body(String namespace, String name, String text) throws Exception {
		_field.set(getDigester().peek(), (_converter == null ? text : _converter.convertFrom(text)));
	}
}
