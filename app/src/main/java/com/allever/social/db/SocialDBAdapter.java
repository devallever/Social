package com.allever.social.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by XM on 2016/5/6.
 */
public class SocialDBAdapter {
    // User表
    static final String KEY_TABLE_ID = "t_id";
    static final String KEY_USERNAME = "username";
    static final String KEY_NICKNAME = "nickname";
    static final String KEY_HEADPATH = "headpath";

    static final String DBNAME = "SocialDB";
    static final String DBTABLE_USER = "user";
    static final int DBVERSION = 1;


    // 创建表语句
    static final String CREATE_TABLE_USER = "create table user(t_id	integer	primary key	autoincrement,"
            + "username	    text 	not null,"
            + "nickname	    text	not null,"
            + "headpath	    text	not null);";

    final Context context;
    DatabaseHelper dBHelper;
    SQLiteDatabase db;

    public SocialDBAdapter(Context context){
        this.context = context;
        dBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context,DBNAME,null,DBVERSION);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO Auto-generated method stub
            try {
                db.execSQL(CREATE_TABLE_USER);
            } catch (SQLiteException e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO Auto-generated method stub
            Log.w("SocialDBAdapter", "Upgrading database from version" + oldVersion + "to" + newVersion +
                    "which will destory all old data");
            db.execSQL("drop table if exists user");
            onCreate(db);
        }

    }


    //打卡数据库
    public SocialDBAdapter open() throws SQLiteException{
        db = dBHelper.getWritableDatabase();
        return this;
    }

    //关闭数据库
    public void close(){
        dBHelper.close();
    }

    //添加User记录
    public long addUser(String username,String nickname, String headpath){
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_USERNAME,username);
        contentValues.put(KEY_NICKNAME, nickname);
        contentValues.put(KEY_HEADPATH,headpath);
        long id = db.insert(DBTABLE_USER, null, contentValues);
        return id;
    }

    //查询User
    public Cursor getUserByUsername(String username){
        // Cursor cursor = db.rawQuery("select user_id,session_id from user where", null);
        Cursor cursor = db.rawQuery("select *from user where username=?", new String[]{username});
        return cursor;
    }

    //检查是否存在用户
    public boolean checkUser(String username) {
        Cursor cursor = db.rawQuery("select *from user where username=?", new String[]  {username});
        if(cursor.moveToFirst()){
            return true;//有记录
        }else {
            return false;//无记录
        }
    }

    //获取用户昵称
    public String getNickName(String username){
        String nickname;
        Cursor cursor = db.rawQuery("select nickname from user where username=?", new String[]  {username});
        if (cursor.moveToFirst()) {
            nickname = cursor.getString(0);
            return nickname;
        }else {
            return "";//查不到
        }
    }

    //获取用户投向路径
    public String getHeadpath(String username){
        String headpath;
        Cursor cursor = db.rawQuery("select headpath from user where username=?", new String[]  {username});
        if (cursor.moveToFirst()) {
            headpath = cursor.getString(0);
            return headpath;
        }else {
            return "";//查不到
        }
    }

    //更新User
    //更新用户昵称
    public boolean updateUserInfo(String username, String nickname){
        ContentValues contentValues =  new ContentValues();
        contentValues.put(KEY_NICKNAME, nickname);
        db.update(DBTABLE_USER, contentValues, "username=?", new String[]{username});
        return true;
    }

}
