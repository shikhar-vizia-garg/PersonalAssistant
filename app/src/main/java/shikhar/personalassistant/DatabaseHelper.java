package shikhar.personalassistant;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by user on 19-08-2015.
 */
public class DatabaseHelper{



    private final static String DATABASE_NAME = "Manager.db";
    private final static String TABLE_NAME_1 = "attendance1";
    private final static String TABLE_NAME_2 = "reminder1";
    private final static String TABLE_NAME_3 = "title1";
    private final static String TABLE_NAME_4="User";

    SQLiteDatabase sqlitedatabase;
    private Database db;

    public DatabaseHelper(Context context) {
        db= new Database(context);
        sqlitedatabase=db.getWritableDatabase();

    }

    private class Database extends SQLiteOpenHelper {
        public Database(Context context) {
            super(context,DATABASE_NAME, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String query = "create table " + TABLE_NAME_1 +
                    " (_id INTEGER,subid INTEGER , name TEXT,attended INTEGER,total INTEGER)";
            db.execSQL(query);

            String query2 = "create table " + TABLE_NAME_2 +
                    " (_id INTEGER,subid INTEGER,titleid INTEGER,content TEXT,setDate TEXT,rno INTEGER)";
            db.execSQL(query2);

            String query3 = "create table " + TABLE_NAME_3 +
                    " (_id INTEGER, titlename TEXT,titleid INTEGER)";
            db.execSQL(query3);

            String query4 = "create table " + TABLE_NAME_4 +
                    " (_id INTEGER, Username TEXT,College TEXT,Course TEXT,status INT)";
            db.execSQL(query4);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
        }
        //return the subject id no which is last+1;
        public int getid()
        {
            Cursor cursor;
            cursor = sqlitedatabase.rawQuery("Select * FROM " + TABLE_NAME_1, null);
            if(cursor.getCount()<=0)
            {
                Log.i("subidreturned",""+1);
                return 1;
            }
            else{
                cursor.moveToLast();
                int last=cursor.getInt(cursor.getColumnIndex("subid"));
                Log.i("subidreturned",""+last);
                return ++last;
            }
        }

        public void addSubject(String name,int i) {
            ContentValues cv = new ContentValues();
            cv.put("name", name);
            cv.put("attended", 0);
            cv.put("total", 0);
            cv.put("subid", i);
            sqlitedatabase.insert(TABLE_NAME_1, null, cv);
        }
        //return the entire subject table(1)
        public Cursor getsubjects() {
            // TODO Auto-generated method stub
            Cursor cursor;
            cursor = sqlitedatabase.rawQuery("Select * FROM " + TABLE_NAME_1, null);
            return cursor;
        }

        public Cursor getReminderCount(){
            String query="Select rno from reminder1";
            Cursor c=sqlitedatabase.rawQuery(query,null);
            return c;
        }

        //return reminder table
        public Cursor getreminders(int sid)
        {
            // TODO Auto-generated method stub
            Cursor cursor;
            cursor = sqlitedatabase.rawQuery("SELECT "+TABLE_NAME_3+"._id,"+TABLE_NAME_2+"._id, "
                +TABLE_NAME_3+".titlename,"+TABLE_NAME_2+".content, " +TABLE_NAME_2+".setDate "+
                "FROM title1,reminder1 WHERE title1.titleid=reminder1.titleid AND reminder1.subid="+sid,null);
            return cursor;
        }

        //Checking whther user has signed up or not
        public Cursor getStatus(){
            Cursor cursor;
            String query="Select status FROM "+TABLE_NAME_4;
            cursor=sqlitedatabase.rawQuery(query,null);
            return cursor;
        }

        //sets subid and titleid in tables title and reminders
        public void addreminders(int sid,int tid,String c,int rno,String date){
            ContentValues cv=new ContentValues();
            cv.put("subid",sid);
            cv.put("titleid",tid);
            cv.put("content",c);
            cv.put("rno",rno);
            cv.put("setDate",date);
            long rowid=sqlitedatabase.insert(TABLE_NAME_2,null,cv);
            Log.d("rowid_reminder", "" + rowid);
        }

        public void addtitles(int tid,String c){
            ContentValues cv=new ContentValues();
            cv.put("titleid",tid);
            cv.put("titlename",c);
            long rowid=sqlitedatabase.insert(TABLE_NAME_3,null,cv);
            Log.d("rowid_title", "" + rowid);

        }

        public void addUser(String name,String college,String course,int status){
            ContentValues cv=new ContentValues();
            cv.put("Username",name);
            cv.put("College",college);
            cv.put("Course", course);
            cv.put("status", status);
            sqlitedatabase.insert(TABLE_NAME_4, null, cv);
        }
        public void deleteall()
        {
            sqlitedatabase.delete(TABLE_NAME_1, null, null);
            sqlitedatabase.delete(TABLE_NAME_2, null, null);
            sqlitedatabase.delete(TABLE_NAME_3, null, null);
        }

        public void deletereminder(int rno){
            String query1="Delete From "+TABLE_NAME_2+" where rno="+rno;
            sqlitedatabase.execSQL(query1);
        }

        public void deletesubject(int subid){

            Log.i("database_subid",""+subid);

            String query1="Delete From "+TABLE_NAME_2+" where subid="+subid;
            sqlitedatabase.execSQL(query1);

            String query2="Delete From "+TABLE_NAME_1+" where subid="+subid;
            sqlitedatabase.execSQL(query2);

            String query3="Update "+TABLE_NAME_2+" SET subid=subid-1 where subid>"+subid;
            sqlitedatabase.execSQL(query3);

            String query4="Update "+TABLE_NAME_1+" SET subid=subid-1 where subid>"+subid;
            sqlitedatabase.execSQL(query4);
        }
        public void deleteall_subjects() {sqlitedatabase.delete(TABLE_NAME_1, null, null);}
        public void deleteall_reminder() {sqlitedatabase.delete(TABLE_NAME_2, null, null);}
        public void deleteall_titles() {sqlitedatabase.delete(TABLE_NAME_3, null, null);}

        public float getPercentage(int position){

            float per;
            Cursor c=sqlitedatabase.rawQuery("Select * FROM " + TABLE_NAME_1, null);
            if(position<c.getCount()){
                c.moveToPosition(position);
                float newtotal=c.getFloat(c.getColumnIndex("total"));
                float newattended=c.getFloat(c.getColumnIndex("attended"));
                if(newtotal==0 && newattended==0){
                    return 1;
                }
                else {
                    per = newattended / newtotal;
                    return per;
                }
            }
            return 0;
        }

        //button functions
        public float updateboth(int position) //function of button attended
        {
            Cursor c=sqlitedatabase.rawQuery("Select * FROM " + TABLE_NAME_1, null);
            c.moveToPosition(position);

            float newtotal=c.getFloat(c.getColumnIndex("total"))+1;
            float newattended=c.getFloat(c.getColumnIndex("attended"))+1;

            String []args={c.getString(c.getColumnIndexOrThrow("name"))};
            ContentValues value=new ContentValues();
            value.put("total", newtotal);
            value.put("attended", newattended);
            sqlitedatabase.update(TABLE_NAME_1, value, "name=?", args);
            float per=newattended/newtotal;
            Log.d("checktotal", "  " +c.getString(c.getColumnIndexOrThrow("name"))+":"+ newtotal);
            Log.d("checkattended",""+newattended);
            Log.d("checkname",c.getString(c.getColumnIndexOrThrow("name")));
            return per;
        }
        public float updatetotal(int position)//function of button absent
        {
            Cursor c=sqlitedatabase.rawQuery("Select * FROM " + TABLE_NAME_1, null);
            c.moveToPosition(position);

            float newtotal=c.getFloat(c.getColumnIndex("total"))+1;
            float newattended=c.getFloat(c.getColumnIndex("attended"));

            String []args={c.getString(c.getColumnIndexOrThrow("name"))};
            ContentValues value=new ContentValues();
            value.put("total",newtotal);
            sqlitedatabase.update(TABLE_NAME_1, value, "name=?", args);
            float per=newattended/newtotal;
            Log.d("checktotal", "  " + c.getString(c.getColumnIndexOrThrow("name")) + ":" + newtotal);
            Log.d("checkattended", "  " + c.getString(c.getColumnIndexOrThrow("name")) + ":" + newattended);
            Log.d("checkname", c.getString(c.getColumnIndexOrThrow("name")));
            return per;
        }

        public float undo(int position,int flag1)//function of undo button
        {
            float per,newtotal,newattended;
            per= (float)0.0;
            Cursor c = sqlitedatabase.rawQuery("Select * FROM " + TABLE_NAME_1, null);
            c.moveToPosition(position);

            if(flag1==1) {//attended
                newtotal = c.getFloat(c.getColumnIndex("total")) - 1;
                newattended = c.getFloat(c.getColumnIndex("attended")) - 1;

                if(newtotal<0 || newattended<0){
                    per=-1;
                }
                else{
                    String[] args = {c.getString(c.getColumnIndexOrThrow("name"))};
                    ContentValues value = new ContentValues();
                    value.put("total", newtotal);
                    value.put("attended", newattended);
                    sqlitedatabase.update(TABLE_NAME_1, value, "name=?", args);

                    if(newattended==0 && newtotal==0){
                        per=1;
                    }
                    else{
                        per=newattended/newtotal;
                    }
                }
            }
            else if(flag1==0) { //absent
                newtotal = c.getFloat(c.getColumnIndex("total")) - 1;
                newattended = c.getFloat(c.getColumnIndex("attended"));

                String[] args = {c.getString(c.getColumnIndexOrThrow("name"))};
                ContentValues value = new ContentValues();
                value.put("total", newtotal);
                sqlitedatabase.update(TABLE_NAME_1, value, "name=?", args);

                if(newattended==0 && newtotal==0){
                    per=1;
                }
                else{
                    per=newattended/newtotal;
                }
            }
            return per;
        }
    }
