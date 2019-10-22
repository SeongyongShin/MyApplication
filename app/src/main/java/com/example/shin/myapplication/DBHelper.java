package com.example.shin.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
/*
    private String sender = "";
    private String recipent = "";
    private String subject = "";
    private String fileName = "";
    private String content = "";
 */
    // DBHelper 생성자로 관리할 DB 이름과 버전 정보를 받음
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // DB를 새로 생성할 때 호출되는 함수
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 새로운 테이블 생성
        db.execSQL("CREATE TABLE  IF NOT EXISTS SYTABLE(" +
                "id TEXT PRIMARY KEY NOT NULL UNIQUE," +
                " sender TEXT," +
                " recipent TEXT," +
                " subject TEXT," +
                " fileName TEXT," +
                " content TEXT); ");
        db.execSQL("INSERT INTO SYTABLE VALUES ('1','','','','',''); " );
        db.close();
    }

    // DB 업그레이드를 위해 버전이 변경될 때 호출되는 함수
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(MailVO vo) {
        // 읽고 쓰기가 가능하게 DB 열기
//        SQLiteDatabase db = getWritableDatabase();
//        // DB에 입력한 값으로 행 추가
//        db.execSQL("INSERT INTO SYTABLE VALUES(1, '" + vo.getSender() + "', '" + vo.getRecipent() + "', '" + vo.getSubject() + "', '" + vo.getFileName() + "', '" + vo.getContent() + "');");
//        db.close();
    }
    public void update(MailVO vo) {
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행의 정보 수정
        String sql = "UPDATE SYTABLE SET" +
                " sender = '"+vo.getSender()+"'," +
                "recipent = '"+vo.getRecipent()+"'," +
                "subject = '"+vo.getSubject()+"'," +
                "content='"+vo.getContent()+"'," +
                "fileName='"+vo.getFileName()+"'," +
                " content='"+vo.getContent()+"'" +
                " where id = '1'";
        db.execSQL(sql);
        db.close();
    }

    public void delete() {
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행 삭제
       // db.execSQL("DELETE FROM SYTABLE WHERE item='" + item + "';");
        //db.execSQL("DROP TABLE SYTABLE");
        db.execSQL("CREATE TABLE IF NOT EXISTS SYTABLE(" +
                "id TEXT PRIMARY KEY NOT NULL UNIQUE," +
                " sender TEXT," +
                " recipent TEXT," +
                " subject TEXT," +
                " fileName TEXT," +
                " content TEXT); ");
        try {
            db.execSQL("INSERT INTO SYTABLE VALUES ('1','','','','',''); ");
        }catch (Exception e){
            //e.printStackTrace();
        }
        db.close();
    }

    public MailVO getResult() {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        String result = "";
        MailVO vo = new MailVO();
        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT * FROM SYTABLE", null);
        cursor.moveToNext();
        System.out.println("asdf : here! "+ cursor.getString(0));
        System.out.println("asdf : here! "+ cursor.getString(1));
        System.out.println("asdf : here! "+ cursor.getString(2));
        System.out.println("asdf : here! "+ cursor.getString(3));
        System.out.println("asdf : here! "+ cursor.getString(4));
        System.out.println("asdf : here! "+ cursor.getString(5));

        vo.setSender(cursor.getString(1));
        vo.setRecipent(cursor.getString(2));
        vo.setSubject(cursor.getString(3));
        vo.setFileName(cursor.getString(4));
        vo.setContent(cursor.getString(5));
        return vo;
    }
}

