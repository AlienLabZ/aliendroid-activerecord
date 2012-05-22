/*
 *  Copyright 2012 AlienLabZ
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.alienlabz.activerecord;

import java.lang.reflect.Field;
import java.util.Date;

import android.database.Cursor;

import com.alienlabz.util.DateUtils;
import com.alienlabz.util.Reflection;

/**
 * Responsible to map attributes to database columns and vice-versa.
 * 
 * @author Marlon Silva Carvalho
 * @since 1.0.0
 */
public class ColumnMapper {

	// http://www.sqlite.org/datatype3.html#datetime
	// http://en.wikipedia.org/wiki/ISO_8601
	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	public void setValueToObject(final Cursor cursor, final Field field, final Object object) {
		final int index = cursor.getColumnIndex(field.getName());
		final Class<?> type = field.getType();
		if (type.equals(Boolean.class) || type.equals(boolean.class)) {
			setBooleanValue(cursor, index, field, object);
		} else if (type.equals(Date.class)) {
			setDateValue(cursor, index, field, object);
		} else if (type.equals(Integer.class) || type.equals(int.class)) {
			setIntegerValue(cursor, index, field, object);
		} else if (type.equals(Long.class) || type.equals(long.class)) {
			setLongValue(cursor, index, field, object);
		} else if (type.equals(Float.class) || type.equals(float.class)) {
			setFloatValue(cursor, index, field, object);
		} else if (type.equals(Short.class) || type.equals(short.class)) {
			setShortValue(cursor, index, field, object);
		} else if (type.equals(Double.class) || type.equals(double.class)) {
			setDoubleValue(cursor, index, field, object);
		} else if (type.equals(String.class)) {
			setStringValue(cursor, index, field, object);
		} else if (type.isEnum()) {
			setEnumValue(cursor, index, field, object);
		}
	}

	public String getValueFromObject(final Field field, final Object object) {
		String resValue = null;
		final Class<?> type = field.getType();
		if (type.equals(Boolean.class) || type.equals(boolean.class)) {
			resValue = getBooleanValue(field, object);
		} else if (type.equals(Date.class)) {
			resValue = getDateValue(field, object);
		} else if (type.equals(Integer.class) || type.equals(int.class)) {
			resValue = getIntegerValue(field, object);
		} else if (type.equals(Long.class) || type.equals(long.class)) {
			resValue = getLongValue(field, object);
		} else if (type.equals(Float.class) || type.equals(float.class)) {
			resValue = getFloatValue(field, object);
		} else if (type.equals(Short.class) || type.equals(short.class)) {
			resValue = getShortValue(field, object);
		} else if (type.equals(Double.class) || type.equals(double.class)) {
			resValue = getDoubleValue(field, object);
		} else if (type.equals(String.class)) {
			resValue = getStringValue(field, object);
		} else if (type.isEnum()) {
			resValue = getEnumValue(field, object);
		}
		return resValue;
	}

	private String getStringValue(final Field field, final Object object) {
		Object result = getRawValue(field, object);
		if (result == null) {
			result = "";
		}
		return result.toString();
	}

	private String getDoubleValue(final Field field, final Object object) {
		Object result = getRawValue(field, object);
		if (result == null) {
			result = "";
		}
		return result.toString();
	}

	private String getFloatValue(final Field field, final Object object) {
		Object result = getRawValue(field, object);
		if (result == null) {
			result = "";
		}
		return result.toString();
	}

	private String getShortValue(final Field field, final Object object) {
		Object result = getRawValue(field, object);
		if (result == null) {
			result = "";
		}
		return result.toString();
	}

	private String getLongValue(final Field field, final Object object) {
		Object result = getRawValue(field, object);
		if (result == null) {
			result = "";
		}
		return result.toString();
	}

	private String getIntegerValue(final Field field, final Object object) {
		Object result = getRawValue(field, object);
		if (result == null) {
			result = "";
		}
		return result.toString();
	}

	private String getDateValue(final Field field, final Object object) {
		String resValue = null;
		Object value = getRawValue(field, object);
		if (value != null && value instanceof Date) {
			resValue = DateUtils.format((Date) value, DATE_FORMAT);
		}
		return resValue;
	}

	private String getBooleanValue(final Field field, final Object object) {
		String resValue = null;
		Object value = getRawValue(field, object);
		if (value != null && value instanceof Boolean) {
			resValue = ((Boolean) value).booleanValue() ? "1" : "0";
		}
		return resValue;
	}

	@SuppressWarnings("rawtypes")
	private String getEnumValue(final Field field, final Object object) {
		String resValue = null;
		Object value = getRawValue(field, object);
		if (value != null) {
			int valEnum = ((Enum) value).ordinal();
			resValue = String.valueOf(valEnum);
		}
		return resValue;
	}

	private void setStringValue(final Cursor cursor, final int index, final Field field, final Object object) {
		final String value = cursor.getString(index);
		Reflection.setFieldValue(field.getName(), object, value);
	}

	private void setDoubleValue(final Cursor cursor, final int index, final Field field, final Object object) {
		final Double value = (!cursor.isNull(index) ? cursor.getDouble(index) : null);
		Reflection.setFieldValue(field.getName(), object, value);
	}

	private void setShortValue(final Cursor cursor, final int index, final Field field, final Object object) {
		final Short value = (!cursor.isNull(index) ? cursor.getShort(index) : null);
		Reflection.setFieldValue(field.getName(), object, value);
	}

	private void setFloatValue(final Cursor cursor, final int index, final Field field, final Object object) {
		final Float value = (!cursor.isNull(index) ? cursor.getFloat(index) : null);
		Reflection.setFieldValue(field.getName(), object, value);
	}

	private void setLongValue(final Cursor cursor, final int index, final Field field, final Object object) {
		final Long value = (!cursor.isNull(index) ? cursor.getLong(index) : null);
		Reflection.setFieldValue(field.getName(), object, value);
	}

	private void setIntegerValue(final Cursor cursor, final int index, final Field field, final Object object) {
		final Integer value = (!cursor.isNull(index) ? cursor.getInt(index) : null);
		Reflection.setFieldValue(field.getName(), object, value);
	}

	private void setDateValue(final Cursor cursor, final int index, final Field field, final Object object) {
		final String value = cursor.getString(index);
		final Date result = (!cursor.isNull(index) && value != null ? DateUtils.format(value, DATE_FORMAT) : null);
		Reflection.setFieldValue(field.getName(), object, result);
	}

	private void setBooleanValue(final Cursor cursor, final int index, final Field field, final Object object) {
		final int value = (!cursor.isNull(index) ? cursor.getInt(index) : 0);
		final boolean result = (value == 1);
		Reflection.setFieldValue(field.getName(), object, result);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void setEnumValue(final Cursor cursor, final int index, final Field field, final Object object) {
		final int value = cursor.getInt(index);
		final Object result = (!cursor.isNull(index) ? valueOf((Class<Enum>) field.getType(), value) : null);
		Reflection.setFieldValue(field.getName(), object, result);
	}

	private static <E extends Enum<E>> E valueOf(final Class<E> type, final int ordinal) {
		final E[] enums = type.getEnumConstants();
		return (ordinal >= 0 && ordinal < enums.length ? enums[ordinal] : null);
	}

	public Object getRawValue(final Field field, final Object object) {
		final Object result;
		try {
			boolean accessible = field.isAccessible();
			field.setAccessible(true);
			result = field.get(object);
			field.setAccessible(accessible);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		return result;
	}

}
