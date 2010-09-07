package com.adaptiveblue.util.digester;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RuleSetBase;

public final class TypedDigesters {
	private static String combinePattern(String lhs, String rhs) {
		if (lhs.length() == 0)
			return rhs;
		if (rhs.length() == 0)
			return lhs;
		return ('/' == lhs.charAt(lhs.length() - 1) ? lhs.substring(0, lhs.length() - 1) : lhs) + "/" + ('/' == rhs.charAt(0) ? rhs.substring(1, rhs.length()) : rhs);
	}
	
	private static String getFieldName(Field field) {
		DigesterField digestField = field.getAnnotation(DigesterField.class);
		if (digestField != null)
			return digestField.value();
		return field.getName();
	}

	private static PatternRuleSet createListTypeSet(final Field field) {
		final DigesterList digestList = field.getAnnotation(DigesterList.class);
		if (digestList == null)
			throw new RuntimeException("[" + field + "] List elements must have an @DigestList annotation in order to be parsed");
		return createListTypeSet(digestList.type(), digestList.itemName());
	}
	
	private static PatternRuleSet createListTypeSet(final Class<?> type, final String itemName) {
		return new PatternRuleSet() {
			@Override
			public void addRuleInstances(Digester digester) {
				digester.addObjectCreate(getPattern(), ArrayList.class);
				String itemPattern = combinePattern(getPattern(), itemName);
				
				PatternRuleSet itemSet;
				if (isJavaType(type))
					itemSet = createJavaTypeSet(type);
				else
					itemSet = createUserTypeSet(type);
				itemSet.setPattern(itemPattern);
				digester.addRuleSet(itemSet);
				digester.addSetNext(itemPattern, "add");
			}
		};
	}
	
	private static final Package JavaLangPackage = Package.getPackage("java.lang");
	private static boolean isJavaType(Class<?> type) {
		return (type.isPrimitive() || JavaLangPackage.equals(type.getPackage()) || java.util.Date.class.equals(type) || java.sql.Date.class.equals(type));
	}
	
	private static PatternRuleSet createJavaTypeSet(final Class<?> type) {
		return new PatternRuleSet() {
			@Override
			public void addRuleInstances(Digester digester) {
				Converter converter = null;
				
				if (boolean.class.equals(type) || Boolean.class.equals(type)) {
					converter = BooleanConverter;
				} else if (byte.class.equals(type) || Byte.class.equals(type)) {
					converter = ByteConverter;
				} else if (char.class.equals(type) || Character.class.equals(type)) {
					converter = CharacterConverter;
				} else if (double.class.equals(type) || Double.class.equals(type)) {
					converter = DoubleConverter;
				} else if (float.class.equals(type) || Float.class.equals(type)) {
					converter = FloatConverter;
				} else if (int.class.equals(type) || Integer.class.equals(type)) {
					converter = IntegerConverter;
				} else if (long.class.equals(type) || Long.class.equals(type)) {
					converter = LongConverter;
				} else if (short.class.equals(type) || Short.class.equals(type)) {
					converter = ShortConverter;
				} else if (java.util.Date.class.equals(type)) {
					converter = UtilDateConverter;
				} else if (java.sql.Date.class.equals(type)) {
					converter = SqlDateConverter;
				}
				
				digester.addRule(getPattern(), new ConverterRule(converter));
			}
		};
	}
	
	private static class ComposedPatternRuleSet extends PatternRuleSet {
		private final PatternRuleSet[] _ruleSets;
		public ComposedPatternRuleSet(PatternRuleSet... ruleSets) {
			_ruleSets = ruleSets;
		}
		@Override
		public void setPattern(String pattern) {
			for (PatternRuleSet set : _ruleSets)
				set.setPattern(pattern);
		}
		@Override
		public void addRuleInstances(Digester digester) {
			for (PatternRuleSet set : _ruleSets)
				set.addRuleInstances(digester);
		}
	}

	private static class SetFieldOnEndRuleSet extends PatternRuleSet {
		private final Field _field;
		public SetFieldOnEndRuleSet(Field field) {
			_field = field;
		}
		@Override
		public void addRuleInstances(Digester digester) {
			digester.addRule(getPattern(), new SetFieldOnEndRule(_field));
		}
	}
	
	private static class UserTypeRuleSet extends PatternRuleSet {
		private final Class<?> _type;
		public UserTypeRuleSet(Class<?> type) {
			_type = type;
		}
		@Override
		public void addRuleInstances(Digester digester) {
			digester.addObjectCreate(getPattern(), _type);

			for (Field field : _type.getDeclaredFields()) {
				Class<?> fieldType = field.getType();
				PatternRuleSet set;
				if (List.class.isAssignableFrom(fieldType)) {
					set = new ComposedPatternRuleSet(createListTypeSet(field), new SetFieldOnEndRuleSet(field));
				} else if (fieldType.isArray()) {
					throw new UnsupportedOperationException();
				} else if (isJavaType(fieldType)) {
					set = new ComposedPatternRuleSet(createJavaTypeSet(fieldType), new SetFieldOnEndRuleSet(field));
				} else {
					set = new ComposedPatternRuleSet(createUserTypeSet(fieldType), new SetFieldOnEndRuleSet(field));
				}
				set.setPattern(combinePattern(getPattern(), getFieldName(field)));
				digester.addRuleSet(set);
			}
		}
	}
	private static PatternRuleSet createUserTypeSet(Class<?> type) {
		return new UserTypeRuleSet(type);
	}

	public static <T> TypedDigester<T> create(Class<T> type, String pattern) {
		SimpleDigester<T> d = new SimpleDigester<T>();
		
		PatternRuleSet set = createUserTypeSet(type);
		set.setPattern(pattern);
		d.digester.addRuleSet(set);
		
		return d;
	}
	
	public static <T> TypedDigester<List<T>> createList(Class<T> itemType, String pattern, String itemName) {
		SimpleDigester<List<T>> d = new SimpleDigester<List<T>>();
		
		PatternRuleSet set = createListTypeSet(itemType, itemName);
		set.setPattern(pattern);
		d.digester.addRuleSet(set);
		
		return d;
	}

	public static class SimpleDigester<T> implements TypedDigester<T> {
		final Digester digester = new Digester();
		@SuppressWarnings("unchecked")
		public T digest(InputStream stream) throws Exception {
			return (T)digester.parse(stream);
		}
	}
	
	private abstract static class PatternRuleSet extends RuleSetBase {
		private String _pattern;
		public void setPattern(String pattern) {
			_pattern = pattern;
		}
		public String getPattern() {
			return _pattern;
		}
	}
	
	private static final Converter BooleanConverter = new Converter() {
		public Object convertFrom(String value) {
			return Boolean.parseBoolean(value);
		}
	};
	private static final Converter ByteConverter = new Converter() {
		public Object convertFrom(String value) {
			return Byte.valueOf(value);
		}
	};
	private static final Converter CharacterConverter = new Converter() {
		public Object convertFrom(String value) {
			return value.charAt(0);
		}
	};
	private static final Converter DoubleConverter = new Converter() {
		public Object convertFrom(String value) {
			return Double.valueOf(value);
		}
	};
	private static final Converter FloatConverter = new Converter() {
		public Object convertFrom(String value) {
			return Float.valueOf(value);
		}
	};
	private static final Converter IntegerConverter = new Converter() {
		public Object convertFrom(String value) {
			return Integer.valueOf(value);
		}
	};
	private static final Converter LongConverter = new Converter() {
		public Object convertFrom(String value) {
			return Long.valueOf(value);
		}
	};
	private static final Converter ShortConverter = new Converter() {
		public Object convertFrom(String value) {
			return Short.valueOf(value);
		}
	};
	public static final Converter UtilDateConverter = new Converter() {
		private final DateFormat _dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		public Object convertFrom(String value) {
			try {
				return _dateFormat.parse(value);
			} catch (Exception e) {
				try {
					return new java.util.Date(Long.parseLong(value));
				} catch (Exception f) {
					throw new RuntimeException(f);
				}
			}
		}
	};
	public static final Converter SqlDateConverter = new Converter() {
		private final DateFormat _dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		public Object convertFrom(String value) {
			try {
				return new java.sql.Date(_dateFormat.parse(value).getTime());
			} catch (Exception e) {
				try {
					return new java.sql.Date(Long.parseLong(value));
				} catch (Exception f) {
					throw new RuntimeException(f);
				}
			}
		}
	};
}
