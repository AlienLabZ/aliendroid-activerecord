package com.alienlabz.activerecord.module;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.alienlabz.activerecord.DBOpenHelper;
import com.alienlabz.annotation.Module;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

@Module
public class ActiveRecordModule extends AbstractModule {
	private static DBOpenHelper dbOpenHelper;
	private Context context;
	
	public ActiveRecordModule(final Context context) {
		this.context = context;
	}

	@Override
	protected void configure() {
	}

	@Provides
	public DBOpenHelper provideDbOpenHelper() {
		if (dbOpenHelper != null) {
			return dbOpenHelper;
		}

		ApplicationInfo ai;
		String dbname;
		int version;
		try {
			ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			dbname = ai.metaData.getString("DATABASE_NAME");
			version = ai.metaData.getInt("DATABASE_VERSION");
		} catch (Throwable e) {
			dbname = "database.sqlite";
			version = 1;
		}
		dbOpenHelper = new DBOpenHelper(context, dbname, null, version);
		return dbOpenHelper;
	}

}
