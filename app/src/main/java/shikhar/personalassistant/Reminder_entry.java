package shikhar.personalassistant;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by user on 25-08-2015.
 */
public class Reminder_entry extends AppCompatActivity {
    ImageView date,timep,back;
    TextView datetext,timetext,titletoolbar;
    EditText content;
    Button set;
    DatabaseHelper dbhelper;
    Spinner title;
    int i;
    String co;
    int tid;
    Toolbar toolbar;
    String[] t={"Assignments","Projects","Class Test","Mid Semester","End Semester","Notes"};
    Calendar dateAndTime=Calendar.getInstance();
    DatePickerDialog.OnDateSetListener d=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        }
    };
    TimePickerDialog.OnTimeSetListener ti=new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay,
                              int minute) {
            dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTime.set(Calendar.MINUTE, minute);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_reminder_2);

        Intent r=getIntent();
        final int subid=r.getIntExtra("subject_id", 0);
        Log.i("Reminder_entry_subid",""+subid);
        back=(ImageView)findViewById(R.id.back1);
        //toolbar=(Toolbar) findViewById(R.id.toolbar3);
        //setSupportActionBar(toolbar);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        /*LayoutInflater layoutInflater=(LayoutInflater) Reminder_entry.this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View v=layoutInflater.inflate(R.layout.customtoolbarview,null);
        getSupportActionBar().setCustomView(v);
        titletoolbar=(TextView)v.findViewById(R.id.titletoolbar);

        titletoolbar.setText("Reminders");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });*/

        content=(EditText) findViewById(R.id.reminder_content);
        set=(Button) findViewById(R.id.button_set_reminder);
        title=(Spinner) findViewById(R.id.spinner_title);
        date=(ImageView) findViewById(R.id.datepicker);
        timep=(ImageView) findViewById(R.id.timepicker);
        datetext=(TextView) findViewById(R.id.datepickertext);
        timetext=(TextView) findViewById(R.id.timetext);

        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,t);
        title.setAdapter(adapter);
        title.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                i=parent.getSelectedItemPosition();
                co=parent.getItemAtPosition(position).toString();
                Log.d("abcsubid: ", "" + subid);
                Log.d("abctitleid: ",""+i);
                Log.d("abctitlename",co);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        dbhelper = new DatabaseHelper(this);
        final Cursor c=dbhelper.getReminderCount();
        final int rno;
        if(c.moveToFirst()){
            c.moveToLast();
            rno=c.getInt(c.getColumnIndex("rno"));
        }
        else{
            rno=0;
        }

        dbhelper.deleteall_titles();
        for(int i=1;i<=6;i++)
        {
            dbhelper.addtitles(i,t[i-1]);
        }

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(Reminder_entry.this,
                        d,
                        dateAndTime.get(Calendar.YEAR),
                        dateAndTime.get(Calendar.MONTH),
                        dateAndTime.get(Calendar.DAY_OF_MONTH)).show();
                UpdateDate();
            }


        });
        timep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(Reminder_entry.this, ti,
                        dateAndTime.get(Calendar.HOUR_OF_DAY),
                        dateAndTime.get(Calendar.MINUTE),
                        true).show();
                UpdateTime();
            }

        });
        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tid=i+1;
                int rno2=rno+1;
                Log.i("Reminder_Row_Detials",subid+" "+tid+" "+content.getText().toString()+" "+rno2);
                dbhelper.addreminders(subid,tid,content.getText().toString(),rno2,datetext.getText().toString());

                AlarmManager alarmManager = (AlarmManager) Reminder_entry.this.getSystemService(Reminder_entry.this.ALARM_SERVICE);
                Intent intent = new Intent(Reminder_entry.this, ReminderService.class);
                intent.putExtra("Content",content.getText().toString());
                intent.putExtra("Title",co.toString());
                intent.putExtra("rno",rno2);
                PendingIntent pendingIntent = PendingIntent.getService(Reminder_entry.this, (int) System.currentTimeMillis(),intent, PendingIntent.FLAG_ONE_SHOT);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, dateAndTime.getTimeInMillis(), pendingIntent);

                Intent in=new Intent();
                setResult(RESULT_OK, in);
                finish();
            }
        });
    }
    void UpdateDate(){
        datetext.setText(dateAndTime.get(Calendar.DAY_OF_MONTH)+"-"+dateAndTime.get(Calendar.MONTH)+"-"+dateAndTime.get(Calendar.YEAR));
    }
    void UpdateTime(){
        timetext.setText(dateAndTime.get(Calendar.HOUR)+":"+dateAndTime.get(Calendar.MINUTE));
    }
}
