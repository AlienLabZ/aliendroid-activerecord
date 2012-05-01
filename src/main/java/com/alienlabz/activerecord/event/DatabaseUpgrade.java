/*
 * AlienDroid Framework - ActiveRecord Module.
 * Copyright (C) 2012 AlienLabZ
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.alienlabz.activerecord.event;

import android.database.sqlite.SQLiteDatabase;

/**
 * Event raised when a new database is created.
 * 
 * @author Marlon Silva Carvalho
 * @since 1.0.0
 */
public class DatabaseUpgrade {
	private SQLiteDatabase database;
	private int newVersion;
	private int oldVersion;
	
	public DatabaseUpgrade(final SQLiteDatabase db, final int newVersion, int oldVersion) {
		this.setDatabase(db);
		this.newVersion = newVersion;
		this.oldVersion = oldVersion;
	}

	public SQLiteDatabase getDatabase() {
		return database;
	}

	public void setDatabase(SQLiteDatabase database) {
		this.database = database;
	}

	public int getNewVersion() {
		return newVersion;
	}

	public int getOldVersion() {
		return oldVersion;
	}

}
