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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;

import com.alienlabz.annotation.Transient;
import com.alienlabz.util.Beans;
import com.alienlabz.util.Reflection;

/**
 * Base class for models.</br>
 * 
 * @author Marlon Silva Carvalho
 * @author Rodrigo Hjort
 * @since 1.0.0
 */
abstract public class Model {

	public Integer _id;

	public Model() {
		super();
	}

	public Model(final Integer id) {
		this._id = id;
	}

	private String getTableName() {
		return Reflection.getSimpleClassName(this);
	}

	private DBOpenHelper getHelper() {
		return Beans.getBean(DBOpenHelper.class);
	}

	private ColumnMapper getColumnMapper() {
		return Beans.getBean(ColumnMapper.class);
	}

	public void load(final Integer id) {
		final Cursor cursor = getHelper().getReadableDatabase().query(getTableName(),
				Reflection.getNonStaticDeclaredFieldsNames(this.getClass()), "_id=?", new String[] { id.toString() },
				null, null, null);
		if (cursor.moveToFirst()) {
			transform(cursor);
		}
		cursor.close();
	}

	public static <T extends Model> Model findFirst(final Class<T> cls, final String query, final String... params) {
		final List<T> list = Model.where(cls, query, params);
		Model model = null;
		if (list.size() > 0) {
			model = list.iterator().next();
		}
		return model;
	}

	public static <T extends Model> Model findFirst(final Class<T> cls) {
		final List<T> list = Model.where(cls, null);
		Model model = null;
		if (list.size() > 0) {
			model = list.iterator().next();
		}
		return model;
	}

	/**
	 * Save the model to the database.
	 */
	public void save() {
		final ColumnMapper mapper = getColumnMapper();
		final ContentValues values = new ContentValues();

		final Field[] fields = Reflection.getNonStaticDeclaredFields(getClass());
		for (Field field : fields) {
			if (!isMultiValued(field)) {
				values.put(field.getName(), mapper.getValueFromObject(field, this));
			}
		}

		final String tableName = getTableName();
		final DBOpenHelper helper = getHelper();
		if (_id != null) {
			helper.getWritableDatabase().update(tableName, values, "_id=?", new String[] { _id.toString() });
		} else {
			helper.getWritableDatabase().insertOrThrow(tableName, null, values);
		}
	}

	/**
	 * Delete the model from database.
	 */
	public void delete() {
		getHelper().getWritableDatabase().delete(getTableName(), "_id=?", new String[] { _id.toString() });
	}

	/**
	 * Assigns the instantiated class with field values retrieved from the cursor.
	 * 
	 * @param cursor
	 */
	protected void transform(final Cursor cursor) {
		final ColumnMapper mapper = getColumnMapper();

		final Field[] fields = Reflection.getNonStaticDeclaredFields(this.getClass());
		for (Field field : fields) {
			if (!isMultiValued(field) && !field.isAnnotationPresent(Transient.class)) {
				mapper.setValueToObject(cursor, field, this);
			}
		}

		final int posId = cursor.getColumnIndex("_id");
		if (posId != -1) {
			this._id = cursor.getInt(posId);
		}
	}

	/**
	 * Returns true whether the given object was already persisted.
	 * 
	 * @return	boolean
	 */
	public boolean isSaved() {
		return this._id != null;
	}

	/**
	 * Returns all occurrencies of the given class type.
	 * 
	 * @param cls
	 * @return
	 */
	public static <T extends Model> List<T> findAll(final Class<T> cls) {
		return where(cls, null);
	}

	public static <T extends Model> List<T> where(final Class<T> cls, final String query, final String... params) {

		final String tableName = Reflection.getSimpleClassName(cls);
		final List<T> result = new ArrayList<T>();

		final StringBuffer sql = new StringBuffer();
		sql.append("SELECT * FROM ");
		sql.append(tableName);
		if (query != null) {
			sql.append(" WHERE ");
			sql.append(query);
		}

		final DBOpenHelper helper = Beans.getBean(DBOpenHelper.class);
		final Cursor cursor = helper.getReadableDatabase().rawQuery(sql.toString(), params);
		while (cursor.moveToNext()) {
			T model = Reflection.instantiate(cls);
			model.transform(cursor);
			result.add(model);
		}

		cursor.close();
		return result;
	}

	/**
	 * Returns the amount of records written in the table.
	 * 
	 * @param cls
	 * @return
	 */
	public static <T extends Model> int count(final Class<T> cls) {
		return count(cls, null);
	}

	public static <T extends Model> int count(final Class<T> cls, final String query, final String... params) {

		final String tableName = Reflection.getSimpleClassName(cls);
		int result = 0;

		final StringBuffer sql = new StringBuffer();
		sql.append("SELECT count(1) FROM ");
		sql.append(tableName);
		if (query != null) {
			sql.append(" WHERE ");
			sql.append(query);
		}

		final DBOpenHelper helper = Beans.getBean(DBOpenHelper.class);
		final Cursor cursor = helper.getReadableDatabase().rawQuery(sql.toString(), params);
		if (cursor.moveToNext()) {
			result = cursor.getInt(0);
		}

		cursor.close();
		return result;
	}

	/**
	 * Generates the DDL table creation statement corresponding to the given class type.
	 * 
	 * @param cls	the class
	 * @return	the statement
	 */
	public static String getSQLCreateTable(final Class<?> cls) {

		final String tableName = Reflection.getSimpleClassName(cls);
		final StringBuilder sql = new StringBuilder();

		sql.append("CREATE TABLE ");
		sql.append(tableName);
		sql.append(" (_id INTEGER PRIMARY KEY");

		final Field[] fields = Reflection.getNonStaticDeclaredFields(cls);
		for (Field field : fields) {
			if (isMultiValued(field) || field.isAnnotationPresent(Transient.class)) {
				continue;
			}
			sql.append(", ");
			sql.append(field.getName());
			sql.append(" ");
			sql.append(getType(field));
		}
		sql.append(");");

		return sql.toString();
	}

	/**
	 * Returns the SQL data type associated with the given Java field type.
	 * 
	 * @param field
	 * @return	a String
	 */
	private static String getType(final Field field) {
		final String cls = field.getType().getSimpleName().toLowerCase();

		String type = null;
		if (cls.equals("string") || cls.equals("char") || cls.equals("character")) {
			type = "TEXT";
		} else if (cls.equals("integer") || cls.equals("int") || cls.equals("long") || cls.equals("short")
				|| cls.equals("byte") || cls.equals("boolean")) {
			type = "INTEGER";
		} else if (cls.equals("double") || cls.equals("float")) {
			type = "REAL";
		} else if (cls.equals("date")) {
			type = "DATE";
		} else if (field.getType().isEnum()) {
			type = "INTEGER";
		} else {
			type = "TEXT";
		}

		return type;
	}

	/**
	 * Returns true whether the given field type is a core collection interface.
	 * 
	 * @param field	the field
	 * @return	a boolean
	 */
	private static boolean isCollection(final Field field) {
		if (Collection.class.isAssignableFrom(field.getType()))
			return true;
		if (Map.class.isAssignableFrom(field.getType()))
			return true;
		return false;
	}

	/**
	 * Returns true whether the given field type is an array.
	 * 
	 * @param field	the field
	 * @return	a boolean
	 */
	private static boolean isArray(final Field field) {
		return field.getType().isArray();
	}

	/**
	 * Returns true whether the given field type is multi-valued.
	 * 
	 * @param field	the field
	 * @return	a boolean
	 */
	private static boolean isMultiValued(final Field field) {
		return isCollection(field) || isArray(field);
	}

	/**
	 * Issues a given native SQL query, returning a list of a given class type instances. 
	 * 
	 * @param cls	the class type
	 * @param sql	the complete SQL statement
	 * @param params	optional parameters
	 * @return	a list of class type
	 */
	public static <T extends Model> List<T> createSQLQuery(final Class<T> cls, final String sql, final String... params) {

		final List<T> result = new ArrayList<T>();

		final DBOpenHelper helper = Beans.getBean(DBOpenHelper.class);
		final Cursor cursor = helper.getReadableDatabase().rawQuery(sql, params);
		while (cursor.moveToNext()) {
			T model = Reflection.instantiate(cls);
			model.transform(cursor);
			result.add(model);
		}

		cursor.close();
		return result;
	}

	/**
	 * Executes the given native SQL statement.
	 * 
	 * @param sql	the SQL statement
	 * @param params	optional arguments
	 */
	public static void executeSQL(final String sql, final Object... params) {
		final DBOpenHelper helper = Beans.getBean(DBOpenHelper.class);
		if (params != null && params.length > 0) {
			helper.getReadableDatabase().execSQL(sql, params);
		} else {
			helper.getReadableDatabase().execSQL(sql);
		}
	}

}
