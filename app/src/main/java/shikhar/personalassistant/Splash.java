package shikhar.personalassistant;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Shikhar Garg on 03-04-2016.
 */
public class Splash extends Activity {
    DatabaseHelper dbhelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        dbhelper=new DatabaseHelper(getApplicationContext());
        final Cursor c=dbhelper.getStatus();
        final int status;
        c.moveToFirst();
        if(c.getCount()>0) {
            status = c.getInt(c.getColumnIndex("status"));
        }
        else{
            status=0;
        }
        Thread logo=new Thread(){
            @Override
            public void run() {
                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                    if(status==1 && c.getCount()>0)
                        startActivity(new Intent(Splash.this,Subjects.class));
                    else
                        startActivity(new Intent(Splash.this,Set_up_profile.class));
                    finish();

                }
            }
        };
        logo.start();
    }
}
