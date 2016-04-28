package shikhar.personalassistant;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by user on 18-08-2015.
 */

public class Set_up_profile extends AppCompatActivity {
    EditText name,college,course;
    String sname,scollege,scourse;
    CardView message,details,subjects_add;
    Button go,next;
    FloatingActionButton add,go_next;
    ArrayList<String> subjectlist;
    DatabaseHelper db;
    TextView default_text;
    private int i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_up_profile_2);
        Initialise();
        Toolbar toolbar=new Toolbar(getApplicationContext());
        toolbar=(Toolbar) findViewById(R.id.toolbar5);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("     PROFILE");
        final Animation as= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.setupprofile);
        final Animation as2= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.setupprofile);
        as.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                details.setVisibility(View.GONE);
                subjects_add.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        as2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                subjects_add.setVisibility(View.GONE);
                message.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sname=name.getText().toString();
                scollege=college.getText().toString();
                scourse=college.getText().toString();
                db.addUser(sname,scollege,scourse,1);
                details.startAnimation(as);
            }
        });
        go_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(subjectlist.size()>0)
                    subjects_add.startAnimation(as2);
                else{
                    Toast.makeText(getApplicationContext(),"You have not added any subject, Please do so to proceed",Toast.LENGTH_LONG).show();
                }

            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent entry = new Intent(Set_up_profile.this, dialog_entry.class);
                startActivityForResult(entry, 0);
            }
        });
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sublist = new Intent(Set_up_profile.this, Subjects.class);
                startActivity(sublist);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==0 && resultCode==RESULT_OK && data!=null)
        {
            subjectlist.add(i,data.getStringExtra("sub"));
            i++;
            default_text.setText("You have just added " + data.getStringExtra("sub")+" in your subjects list!");
        }
        if(resultCode==-1){
            Toast.makeText(getApplicationContext(),"You didn't fill any subject",Toast.LENGTH_LONG).show();
        }
    }
    private void Initialise() {
        name=(EditText)findViewById(R.id.edit_name);
        college=(EditText)findViewById(R.id.edit_college);
        course=(EditText)findViewById(R.id.edit_course);

        add=(FloatingActionButton)findViewById(R.id.button_add_subjects);
        go_next=(FloatingActionButton) findViewById(R.id.button_go_next);

        next=(Button) findViewById(R.id.setupprofile_next);
        go=(Button)findViewById(R.id.button_go_profile);

        message=(CardView) findViewById(R.id.cv_message);
        details=(CardView) findViewById(R.id.cv_details);
        subjects_add=(CardView) findViewById(R.id.cv_subject_add);

        default_text=(TextView) findViewById(R.id.message);

        subjectlist=new ArrayList<String>();
        db=new DatabaseHelper(this);
        i=0;
    }
}
