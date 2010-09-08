package com.adaptiveblue.util.digester;

import java.lang.reflect.Field;

import org.apache.commons.digester.Rule;

public class SetFieldOnEndWithDefaultRule extends Rule {
	private final Field _field;
	private final String _pattern;
	private final Object _defaultValue;
	
	public SetFieldOnEndWithDefaultRule(Field field, String pattern, Object defaultValue) {
		field.setAccessible(true);
		_field = field;
		_pattern = pattern;
		_defaultValue = defaultValue;
	}
	
	@Override
	public void end(String namespace, String name) throws Exception {
		Object last = null;
		Object current = getDigester().peek();
		if (!getDigester().isEmpty(_pattern))
			last = getDigester().pop(_pattern);
		_field.set(current, (last == null ? _defaultValue : last));
	}
}
