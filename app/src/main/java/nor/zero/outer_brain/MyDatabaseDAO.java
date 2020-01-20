package nor.zero.outer_brain;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

//DAO data access object
public class MyDatabaseDAO {

    private SQLiteDatabase db ;
    public static final String TEXT_TYPE = " TEXT";
    public static final String INT_TYPE = " INT";
    public static final String FLOAT_TYPE = " FLOAT";
    public static final String DATE_TYPE = " DATE";
    public static final String TIME_TYPE = " TIME";
    public static final String BIT_TYPE = " BIT";
    public static final String SEP_COMMA = ",";
    public static final String SEP_SEMI = ";";
    public static final String SEP_GATE = "&404!";
    public static final String NOT_NULL = " NUT NULL";
    public static final String KEY_ID = "_id";

    public static final String TABLE_SHOP_LOCATION = "ShopLocation";
    public static final String COLUMN_SHOP_NAME = "shopName";
    public static final String COLUMN_SHOP_LATITUDE = "latitude";
    public static final String COLUMN_SHOP_LONGITUDE = "longitude";
    public static final String COLUMN_SHOP_CLASSIFY = "classify";
    public static final String COLUMN_SHOP_ADDRESS = "address";
    public static final String COLUMN_SHOP_NOTE = "note";

    public static final String TABLE_ITEM_GROCERY = "ItemGrocery";
    public static final String COLUMN_ITEM_GROCERY_NAME = "name";
    public static final String COLUMN_ITEM_GROCERY_UNIT = "unit";

    public static final String TABLE_BUY_SHOPPING= "BuyGrocery";
    public static final String COLUMN_BUY_SUMMARY = "summary";
    public static final String COLUMN_BUY_GROCERY = "grocery";
    public static final String COLUMN_BUY_SHOP_NAME = "shopName";
    public static final String COLUMN_BUY_LATITUDE = "latitude";
    public static final String COLUMN_BUY_LONGITUDE = "longitude";
    public static final String COLUMN_BUY_DATE ="date";
    public static final String COLUMN_BUY_TIME ="time";
    public static final String COLUMN_BUY_REMINDER ="reminder";
    public static final String COLUMN_BUY_RING_TITLE ="ringTitle";
    public static final String COLUMN_BUY_RING_URI ="uri";
    public static final String COLUMN_BUY_SHOW_ON ="showOn";

    public static final String CREATE_TABLE_SHOP_LOCATION = "CREATE TABLE "+TABLE_SHOP_LOCATION
            +" ("+KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
            +COLUMN_SHOP_NAME+TEXT_TYPE+NOT_NULL+SEP_COMMA
            +COLUMN_SHOP_LATITUDE+FLOAT_TYPE+NOT_NULL+SEP_COMMA
            +COLUMN_SHOP_LONGITUDE+FLOAT_TYPE+NOT_NULL+SEP_COMMA
            +COLUMN_SHOP_CLASSIFY+TEXT_TYPE+SEP_COMMA
            +COLUMN_SHOP_ADDRESS+TEXT_TYPE+SEP_COMMA
            +COLUMN_SHOP_NOTE+TEXT_TYPE
            +")";
    public static final String CREATE_TABLE_ITEM_GROCERY = "CREATE TABLE "+TABLE_ITEM_GROCERY
            +" ("+KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
            +COLUMN_ITEM_GROCERY_NAME+TEXT_TYPE+NOT_NULL+SEP_COMMA
            +COLUMN_ITEM_GROCERY_UNIT+TEXT_TYPE
            +")";
    public static final String CREATE_TABLE_BUY_GROCERY = "CREATE TABLE "+TABLE_BUY_SHOPPING
            +" ("+KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
            +COLUMN_BUY_SUMMARY+TEXT_TYPE+SEP_COMMA
            +COLUMN_BUY_GROCERY+TEXT_TYPE+SEP_COMMA
            +COLUMN_BUY_SHOP_NAME+TEXT_TYPE+SEP_COMMA
            +COLUMN_BUY_LATITUDE+FLOAT_TYPE+SEP_COMMA
            +COLUMN_BUY_LONGITUDE+FLOAT_TYPE+SEP_COMMA
            +COLUMN_BUY_DATE+DATE_TYPE+SEP_COMMA
            +COLUMN_BUY_TIME+TIME_TYPE+SEP_COMMA
            +COLUMN_BUY_REMINDER+INT_TYPE+SEP_COMMA
            +COLUMN_BUY_RING_TITLE+TEXT_TYPE+SEP_COMMA
            +COLUMN_BUY_RING_URI+TEXT_TYPE+SEP_COMMA
            +COLUMN_BUY_SHOW_ON+BIT_TYPE
            +")";

    public MyDatabaseDAO(Context context){
        db = MyDBHelper.getDatabase(context);
        //reset
      //  db.execSQL("Drop Table if exists "+TABLE_SHOP_LOCATION);
        //  db.execSQL("Drop Table if exists "+TABLE_ITEM_GROCERY);
          //db.execSQL("Drop Table if exists "+TABLE_BUY_SHOPPING);
        //db.execSQL(CREATE_TABLE_SHOP_LOCATION);
        //db.execSQL(CREATE_TABLE_ITEM_GROCERY);
        //db.execSQL(CREATE_TABLE_BUY_GROCERY);
    }

    public Cursor getAllCursor(String tableName){
        return db.query(tableName,null,null,null,
                null,null,null);
    }

    public long insert(String tableName, ContentValues values){
        long id = db.insert(tableName,null,values);
        return id;
    }
    public boolean update(String tableName,String id,ContentValues values){
        String where = KEY_ID + "=" +id;
        return db.update(tableName,values,where,null) > 0;
    }
    public boolean delete(String tableName,String id){
        String where = KEY_ID + "=" +id;
        return db.delete(tableName,where,null) > 0;
    }

    public void close(){
        db.close();
    }


}
