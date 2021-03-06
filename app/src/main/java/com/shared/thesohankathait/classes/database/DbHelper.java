package com.shared.thesohankathait.classes.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.shared.thesohankathait.classes.Utill.Equals;
import com.shared.thesohankathait.classes.model.NoticeRequest;

import org.apache.commons.lang3.SerializationUtils;

import java.util.ArrayList;
import java.util.UUID;


//This class makes it easy for ContentProvider implementations to defer opening and upgrading the database until first use, to avoid blocking application startup with long-running database upgrades.
public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "com.umangSRTC.thesohankathait.classes.database.Umang";//name of the database
    private static final int DATABASE_VERSION = 1;//database version
    private Context context;

    //syntax of sql to create a table
    private static final String CREATE = "create table " + DbContract.TABLE_NAME +
            "(id integer primary key autoincrement," + DbContract.NOTICE + " BLOB,"+DbContract.UUID+" text);";

    // if table is exist than drop this table(syntax)
    private static final String DROP_TABLE = "drop table if exists " + DbContract.TABLE_NAME;

    //
    //constructor to create a DbHelper object
    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;  //saving context
    }

    // calling this method results in database creation
    @Override
    public SQLiteDatabase getWritableDatabase() {
        return super.getWritableDatabase();
    }

    //called when a database is created for the first time
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE);

    }

    //called when a database need to be upgrade
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DROP_TABLE);
        onCreate(sqLiteDatabase);
    }

    // custom method to save list of content in database

    public void saveNotice(NoticeRequest noticeRequest){

        SQLiteDatabase sqLiteDatabase=getWritableDatabase();

        //checking weather incoming notice reqeust is already save in database or not
        ArrayList<NoticeRequest> existingNoticeList=new ArrayList<>();
        existingNoticeList=readNoticeList();
        boolean exists=false;
        for(int i=0;i<existingNoticeList.size();i++){
            if(Equals.BothEquals(existingNoticeList.get(i).getNotices(),noticeRequest.getNotices())){
                exists=true;
                break;
            }
        }
        if(!exists){
            byte[] data=SerializationUtils.serialize(noticeRequest);
            String uuid=UUID.randomUUID().toString();
            ContentValues contentValues=new ContentValues();
            contentValues.put(DbContract.NOTICE,data);
            contentValues.put(DbContract.UUID,uuid);
            sqLiteDatabase.insert(DbContract.TABLE_NAME,null,contentValues);
            Toast.makeText(context, "Notice saved", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(context, "This notice is already saved", Toast.LENGTH_SHORT).show();
        }
    }

//    public void saveNoticeList(List<Notices> NoticeList, String schoolName, String noticeType) {
//
//        DbHelper dbHelper = new DbHelper(context); //creating DbHelper object
//
//        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();//creating database
//
//        // Fetching the already existing list
//        List<Notices> existingNoticeList = dbHelper.readNoticeList(schoolName,noticeType);
//
//        //creating a ContentValues object, it will contain values in a set
//        ContentValues contentValues = new ContentValues();
//
//        for (int i = 0; i < NoticeList.size(); i++) {
//
//            // Don't add the object to database if it already exists..
//            if(existingNoticeList.contains(NoticeList.get(i)))
//            {
//                continue;
//            }
//
//            //serializing the object, since we can't store objects in sqlite
//            byte[] data = SerializationUtils.serialize( NoticeList.get(i));
//
//            //adding values in databse
//
//            contentValues.put(DbContract.NOTICE, data);
//            contentValues.put(DbContract.SCHOOL_NAME, schoolName);
//            contentValues.put(DbContract.NOTICE_TYPE,noticeType);
//            sqLiteDatabase.insert(DbContract.TABLE_NAME, null, contentValues);
//
//        }
//        Log.i("savedtodb:",NoticeList.toString());
//    }

//    public List<Notices> readNoticeList(String schoolName, String noticeType) {
//
//        List<Notices> noticeList = new ArrayList<>();
//
//        DbHelper dbHelper = new DbHelper(context);
//        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
//
//        // ALWAYS MAKE SURE (, ' and spaces) DON'T mentally harass you for hours. :(
//
//        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + DbContract.TABLE_NAME + " WHERE "
//                + DbContract.SCHOOL_NAME + "= '" + schoolName + "' AND "
//                + DbContract.NOTICE_TYPE + "= '" + noticeType+"'", null);
//
//        if (cursor.getCount() > 0) {
//
//            while (cursor.moveToNext()) {
//                int contentIndex = cursor.getColumnIndex(DbContract.NOTICE);
//                byte[] data = cursor.getBlob(contentIndex);
//                Notices notices = SerializationUtils.deserialize(data);
//                noticeList.add(notices);
//            }
//            //closing the database
//            cursor.close();
//            dbHelper.close();
//        }
//
//        return noticeList;
//    }

    //since we need the school name hence we are fetching notice as a request.

    public ArrayList<NoticeRequest> readNoticeList(){
        ArrayList<NoticeRequest> noticeList=new ArrayList<>();

        SQLiteDatabase sqLiteDatabase=getWritableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery("SELECT * FROM "+DbContract.TABLE_NAME,null);

        if(cursor.getCount()>0){
            while(cursor.moveToNext()){
                int noticeIndex=cursor.getColumnIndex(DbContract.NOTICE);
                byte[] data=cursor.getBlob(noticeIndex);
                NoticeRequest notices=SerializationUtils.deserialize(data);
                noticeList.add(notices);
            }
        }
        cursor.close();

        return noticeList;
    }


    public void deleteNotice(NoticeRequest noticeRequest){
        SQLiteDatabase sqLiteDatabase=getWritableDatabase();
        ArrayList<NoticeRequest> existingNotice=new ArrayList<>();

        Cursor cursor=sqLiteDatabase.rawQuery("SELECT * FROM "+DbContract.TABLE_NAME,null);

        //since i was not able to delete the data via bolb field index, so i created uuid
        if(cursor.getCount()>0){
            while(cursor.moveToNext()){
                int noticeIndex=cursor.getColumnIndex(DbContract.NOTICE);
                byte[] data=cursor.getBlob(noticeIndex);
                NoticeRequest notice=SerializationUtils.deserialize(data);
                //if this data is same than we can go for uuid index.
                if(Equals.BothEquals(notice.getNotices(),noticeRequest.getNotices())){
                   int uuidIndex=cursor.getColumnIndex(DbContract.UUID);
                    String uuid=cursor.getString(uuidIndex);
                    int a= sqLiteDatabase.delete(DbContract.TABLE_NAME,DbContract.UUID+"=?",new String[]{uuid});
                    Toast.makeText(context, "deleted"+a, Toast.LENGTH_SHORT).show();
                }
                 }
        }
        cursor.close();
        sqLiteDatabase.close();
    }

//    public void deleteNoticeList(String schoolName,String noticeType)
//    {
//        DbHelper dbHelper=new DbHelper(context);
//        SQLiteDatabase sqLiteDatabase=dbHelper.getWritableDatabase();
//
//        int rowsDeleted= 0;
//        rowsDeleted = sqLiteDatabase.delete(DbContract.TABLE_NAME,DbContract.SCHOOL_NAME
//                +"=? and "+DbContract.NOTICE_TYPE+"=?",new String[]{schoolName,noticeType});
//        Log.i("valuedeleted",Integer.toString(rowsDeleted));
//
//        sqLiteDatabase.close();
//        dbHelper.close();
//        Log.i("Deleted","Old list cleared");
//    }


}