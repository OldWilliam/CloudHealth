
package db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbManager {
	private SQLiteOpenHelper dbHelper;
	private SQLiteDatabase db;
	public DbManager(Context context) {
		dbHelper = new DbHelper(context);
		db = dbHelper.getWritableDatabase();
	}

}
