package com.cambricon.productdisplay.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by dell on 18-2-5.
 */

public class CommDB {

    public static final String DATABASE_NAME = "BenchMarkDB.db";
    //数据库名称一定要xxxx.db
    public static final int DATABASE_VERSION = 6;

    //创建图片分类表
    private static final String CREATE_TABLE_Classification = "CREATE TABLE if not exists " + ClassificationDB.SQLITE_TABLE + " (" +
            ClassificationDB.KEY_ROWID + " integer PRIMARY KEY autoincrement," +
            ClassificationDB.KEY_NAME + "," +
            ClassificationDB.KEY_TIME + "," +
            ClassificationDB.KEY_FPS + "," +
            ClassificationDB.KEY_RESULT + "," +
            " UNIQUE (" + ClassificationDB.KEY_NAME + ")" + "ON CONFLICT REPLACE" + ");";

    private static final String CREATE_TABLE_IPU_Classification = "CREATE TABLE if not exists " + ClassificationDB.SQLITE_TABLE_IPU + " (" +
            ClassificationDB.KEY_ROWID_IPU + " integer PRIMARY KEY autoincrement," +
            ClassificationDB.KEY_NAME_IPU + "," +
            ClassificationDB.KEY_TIME_IPU + "," +
            ClassificationDB.KEY_FPS_IPU + "," +
            ClassificationDB.KEY_RESULT_IPU + "," +
            " UNIQUE (" + ClassificationDB.KEY_NAME_IPU + ")" + "ON CONFLICT REPLACE" + ");";

    //创建目标检测分类表
    private static final String CREATE_TABLE_Detection = "CREATE TABLE if not exists " + DetectionDB.SQLITE_TABLE + " (" +
            DetectionDB.KEY_ROWID + " integer PRIMARY KEY autoincrement," +
            DetectionDB.KEY_NAME + "," +
            DetectionDB.KEY_TIME + "," +
            DetectionDB.KEY_FPS + "," +
            " UNIQUE (" + DetectionDB.KEY_NAME + ")" + "ON CONFLICT REPLACE" + ");";

    //创建人脸检测分类表
    private static final String CREATE_TABLE_Face_Detection = "CREATE TABLE if not exists " + FaceDetectDB.SQLITE_TABLE + " (" +
            FaceDetectDB.KEY_ROWID + " integer PRIMARY KEY autoincrement," +
            FaceDetectDB.KEY_NAME + "," +
            FaceDetectDB.KEY_TIME + "," +
            FaceDetectDB.KEY_FPS + "," +
            " UNIQUE (" + FaceDetectDB.KEY_NAME + ")" + "ON CONFLICT REPLACE" + ");";


    private final Context context;
    private CommDB.DataBaseHelper dataBaseHelper;
    private SQLiteDatabase mSQLiteDatabase;

    public CommDB(Context context) {
        this.context = context;
        this.dataBaseHelper = new DataBaseHelper(this.context);
    }

    private class DataBaseHelper extends SQLiteOpenHelper {
        private final String TAG = "DataBaseHelper";

        public DataBaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_TABLE_Face_Detection);
            sqLiteDatabase.execSQL(CREATE_TABLE_Classification);
            sqLiteDatabase.execSQL(CREATE_TABLE_Detection);
            sqLiteDatabase.execSQL(CREATE_TABLE_IPU_Classification);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }

    /**
     * open mSQLiteDatabase
     *
     * @return
     * @throws SQLException
     */

    public CommDB open() throws SQLException {
        dataBaseHelper = new CommDB.DataBaseHelper(context);
        mSQLiteDatabase = this.dataBaseHelper.getWritableDatabase();
        return this;
    }

    /**
     * close mSQLiteDatabase
     */
    public void close() {
        if (this.dataBaseHelper != null) {
            this.dataBaseHelper.close();
        }
    }
}
