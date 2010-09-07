package com.adaptiveblue.util.digester;

import java.lang.reflect.Field;

import org.apache.commons.digester.Rule;

public class SetFieldOnEndRule extends Rule {
	private final Field _field;
	
	public SetFieldOnEndRule(Field field) {
		field.setAccessible(true);
		_field = field;
	}
	
	@Override
	public void end(String namespace, String name) throws Exception {
		Object last = getDigester().peek();
		Object current = getDigester().peek(1);
		_field.set(current, last);
	}
}
