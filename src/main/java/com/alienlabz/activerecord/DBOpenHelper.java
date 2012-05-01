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
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.sqlite.SQLiteDatabase;
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

	private static String DATABASE_NAME = "database.sqlite";
	private static int VERSION = 1;

	@Inject
	private EventManager eventManager;

	private Context context;

	@Inject
	public DBOpenHelper(final Context context) {
		super(context, DATABASE_NAME, null, VERSION);
		this.context = context;
		ApplicationInfo ai;
		try {
			ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			DATABASE_NAME = ai.metaData.getString("DATABASE_NAME");
			VERSION = ai.metaData.getInt("DATABASE_VERSION");
		} catch (NameNotFoundException e) {
			DATABASE_NAME = "database.sqlite";
			VERSION = 1;
		}
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
	}

}
