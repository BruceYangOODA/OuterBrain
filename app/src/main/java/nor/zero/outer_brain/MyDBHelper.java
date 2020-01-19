package nor.zero.outer_brain;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;
import static nor.zero.outer_brain.MyDatabaseDAO.*;

public class MyDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "mydata.db";
    public static final int VERSION = 1;
    private static SQLiteDatabase database;

    public MyDBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_ITEM_GROCERY);
        db.execSQL(CREATE_TABLE_BUY_GROCERY);
        db.execSQL(MyDatabaseDAO.CREATE_TABLE_SHOP_LOCATION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static SQLiteDatabase getDatabase (Context context){
        if(database == null || !database.isOpen()){
            database = new MyDBHelper(context,DATABASE_NAME,null,VERSION).getWritableDatabase();
        }
        return database;
    }
}
