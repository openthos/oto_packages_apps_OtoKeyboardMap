package com.openthos.keyboardmap;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
public class MappingSQLiteOpenHelper extends SQLiteOpenHelper {
    private static final int mVersion = 1;
    private static final String mDbName = "mappingConfigurationFile";
    public String mDirectionKeyTableName = "DirectionKeyConfigurationData";
    public String mFunctionKeyTableName = "FunctionKeyConfigurationData";

    public MappingSQLiteOpenHelper(Context context) {
        super(context, mDbName, null, mVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String directionKeyTableSql = "create table " + mDirectionKeyTableName +
                "(id integer primary key," + " packageName varchar, schemeName varchar," +
                " leftKeyCode integer," + " topKeyCode integer, rightKeyCode integer," +
                " bottomKeyCode integer, circleCenterX integer, circleCenterY integer," +
                " distance integer)";
        String functionKeyTableSql = "create table " + mFunctionKeyTableName +
                "(id integer primary key," + " packageName varchar, schemeName varchar," +
                " keyCode integer, valueX integer, valueY integer)";
        db.execSQL(directionKeyTableSql);
        db.execSQL(functionKeyTableSql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            onCreate(db);
        }
    }
}