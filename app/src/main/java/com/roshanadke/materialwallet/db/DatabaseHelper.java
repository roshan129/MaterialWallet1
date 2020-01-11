package com.roshanadke.materialwallet.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME="practice.db";
        private static final String CATE_NAME ="cate_table";
        private static final String PAY_NAME ="pay_table";
    //private static final String TEST="cutomer";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);

        // SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("CREATE TABLE " + CATE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, CATEGORY TEXT)");
        sqLiteDatabase.execSQL("CREATE TABLE " + PAY_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT,PAY_OPT TEXT)");
        //sqLiteDatabase.execSQL("CREATE TABLE " +TEST+ "(ID INTEGER PRIMARY KEY AUTOINCREMENT,NAME TEXT,MOBILE_NUMBER INTEGER,EMAIL TEXT )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CATE_NAME);
        onCreate(sqLiteDatabase);
    }

    //===============Insert Data=======================
    public  boolean insertCateData(String cate)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("CATEGORY",cate);

        Long result = sqLiteDatabase.insert(CATE_NAME,null,contentValues);

        if (result==-1)

            return  false;
        else
            return true;
    }

    public  boolean insertPayData(String pay)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("PAY_OPT",pay);

        Long result = sqLiteDatabase.insert(PAY_NAME,null,contentValues);

        if (result==-1)

            return  false;
        else
            return true;
    }

    public Cursor getCateTable()
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        String query="SELECT * FROM " + CATE_NAME;
        Cursor cursor = sqLiteDatabase.rawQuery(query,null);

        return cursor;
    }

    public Cursor getPayTable()
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        String query="SELECT * FROM " + PAY_NAME;
        Cursor cursor = sqLiteDatabase.rawQuery(query,null);
        return cursor;
    }

    public void deleteCateItem(String category){
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        sqLiteDatabase.delete(CATE_NAME,"CATEGORY=?", new String[]{category});
    }

    public void deletePayItem(String pay){
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        sqLiteDatabase.delete(PAY_NAME,"PAY_OPT=?", new String[]{pay});
    }

    public void deleteAllCate(){
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        sqLiteDatabase.execSQL("delete from "+ CATE_NAME);
    }

    public void deleteAllPay(){
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        sqLiteDatabase.execSQL("delete from "+ PAY_NAME);
    }


}
