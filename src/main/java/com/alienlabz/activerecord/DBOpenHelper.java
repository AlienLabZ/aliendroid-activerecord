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

import java.util.List;

import roboguice.event.EventManager;
import roboguice.util.Ln;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.alienlabz.activerecord.event.DatabaseCreated;
import com.alienlabz.activerecord.event.DatabaseCreation;
import com.alienlabz.util.Dex;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Android Database Open Helper.
 * 
 * @author Marlon Silva Carvalho
 * @since 1.0.0
 */
@Singleton
public class DBOpenHelper extends SQLiteOpenHelper {

	@Inject
	private EventManager eventManager;

	private Context context;

	public DBOpenHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		this.context = context;
	}

	@Override
	public void onOpen(final SQLiteDatabase db) {
		Ln.d("Opening database named [" + db.getPath() + "]");
		super.onOpen(db);
	}

	@Override
	public void onCreate(final SQLiteDatabase db) {
		Ln.d("Firing DatabaseCreation Event.");
		eventManager.fire(new DatabaseCreation(db));

		Ln.d("Creating Tables.");
		List<Class<?>> tables = Dex.searchForClass(context, "Model");
		for (Class<?> table : tables) {
			db.execSQL(Model.getSQLCreateTable(table));
			Ln.d("Table for class " + table.toString() + " created.");
		}

		Ln.d("Firing DatabaseCreated Event.");
		eventManager.fire(new DatabaseCreated(db));
	}

	@Override
	public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
		eventManager.fire(new DatabaseCreation(db));
	}

}
