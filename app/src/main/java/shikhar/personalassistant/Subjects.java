package shikhar.personalassistant;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by user on 18-08-2015.
 */
public class Subjects extends AppCompatActivity {
    RecyclerView list;
    DatabaseHelper dbhelper;
    Cursor cur_subjects;
    ArrayList<String> Subjectnames;
    float per;
    RVAdapter rvAdapter;
    Toolbar toolbar;
    ImageView p_menu;
    private int requestCode;
    private int resultCode;
    private Intent data;
    Stack<Integer> mark;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subject_list_2);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("Subjects");


        toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Subjects");

        Subjectnames=new ArrayList<String>();
        mark=new Stack<Integer>();

        list=(RecyclerView) findViewById(R.id.rv);
        LinearLayoutManager llm=new LinearLayoutManager(getApplicationContext());
        list.setLayoutManager(llm);

        dbhelper=new DatabaseHelper(this);
        cur_subjects=dbhelper.getsubjects();

        new SubjectFill().execute(cur_subjects);
        rvAdapter=new RVAdapter(Subjectnames,this);
        list.setAdapter(rvAdapter);
    }

    private class RVAdapter extends RecyclerView.Adapter<RVAdapter.SubjectViewHolder>{
        ArrayList<String> sub;
        Context context;

        public RVAdapter(ArrayList<String> sub, Context context) {
            this.sub = sub;
            this.context = context;
        }

        public class SubjectViewHolder extends RecyclerView.ViewHolder{
            TextView subject_name,percentage;
            CardView card;
            ImageButton at,ab;
            ImageView pmenu;
            public SubjectViewHolder(View v) {
                super(v);
                subject_name=(TextView) v.findViewById(R.id.c);
                percentage=(TextView)v.findViewById(R.id.percentage);
                card=(CardView) v.findViewById(R.id.cv);
                at=(ImageButton) v.findViewById(R.id.button_at);
                ab=(ImageButton) v.findViewById(R.id.button_a);
                pmenu=(ImageView)v.findViewById(R.id.overflowmenu);
            }
        }
        @Override
        public RVAdapter.SubjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.subject_item_layout_2,parent,false);
            SubjectViewHolder holder=new SubjectViewHolder(v);
            return holder;
        }

        @Override
        public void onBindViewHolder(final RVAdapter.SubjectViewHolder holder, final int position) {
            holder.subject_name.setText(sub.get(position));
            holder.percentage.setText("Your current attendance percenatge is: " + dbhelper.getPercentage(position)*100 + "%");
            int pos = position;
            holder.at.setTag(pos);
            holder.at.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mark.push(1);
                    Integer pos = (Integer) v.getTag();//send pos+1 because indexing from 1 this will be used toi send intents
                    per = dbhelper.updateboth(pos);
                    holder.percentage.setText("Your current attendance percenatge is: " + (per * 100) + "%");
                }
            });
            int pos1 = position;
            holder.ab.setTag(pos1);
            holder.ab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mark.push(0);
                    Integer pos1 = (Integer) v.getTag();
                    per = dbhelper.updatetotal(pos1);
                    holder.percentage.setText("Your current attendance percenatge is: " + per * 100 + "%");
                }
            });
            final int pos4 = position;
            Log.i("pos4",""+pos4);
            holder.pmenu.setTag(pos4);
            holder.pmenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Integer pos4 = (Integer) v.getTag();
                    final PopupMenu pm = new PopupMenu(getApplicationContext(), holder.pmenu);
                    pm.getMenuInflater().inflate(R.menu.popup_menu, pm.getMenu());
                    pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            if (item.getItemId() == R.id.Reminder) {
                                Intent entry = new Intent(Subjects.this, Reminder_entry.class);
                                entry.putExtra("subject_id", pos4 + 1);
                                entry.putExtra("position", pos4);
                                startActivityForResult(entry, 1);
                            }
                            if (item.getItemId() == R.id.show_reminders) {
                                Cursor c = dbhelper.getreminders(pos4);
                                c.moveToFirst();
                                Intent show = new Intent(Subjects.this, Reminders.class);
                                show.putExtra("subject_id", pos4 + 1);
                                startActivity(show);
                            }
                            if (item.getItemId() == R.id.undo) {
                                per = undologic(pos4);
                                if (per != -1)
                                    holder.percentage.setText("Your current attendance percenatge is: " + per * 100 + "%");
                            }

                            if (item.getItemId() == R.id.delete) {
                                dbhelper.deletesubject(pos4 + 1);
                                sub.remove(pos4);
                                rvAdapter.notifyDataSetChanged();
                            }
                            return true;
                        }
                    });
                    pm.show();
                }
            });
        }
        @Override
        public int getItemCount() {
            return sub.size();
        }
    }

    private class SubjectFill extends AsyncTask<Cursor,Void,Boolean>{

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog=new ProgressDialog(Subjects.this);
            dialog.setTitle("Searching Your Subject Database");
            dialog.setMessage("We appreciate your pateince");
            dialog.show();
            dialog.setCancelable(false);
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Cursor... params) {
            if(params[0].getCount()<=0) return null;
            else{
                params[0].moveToFirst();
                for(int i=0;i<params[0].getCount();i++){
                    Log.i("SubjectNames",params[0].getString(params[0].getColumnIndex("name")));
                    Subjectnames.add(i, params[0].getString(params[0].getColumnIndex("name")));
                    params[0].moveToNext();
                }
                return true;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean){
                dialog.cancel();
                rvAdapter.notifyDataSetChanged();
            }
            else{
                Toast.makeText(getApplicationContext(),"No subjects present in the database!",Toast.LENGTH_LONG).show();
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        this.requestCode = requestCode;
        this.resultCode = resultCode;
        this.data = data;
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==0 && resultCode==RESULT_OK && data!=null)
        {
            int i=Subjectnames.size();
            Subjectnames.add(i, data.getStringExtra("sub"));
            rvAdapter.notifyDataSetChanged();
        }
    }

    public float undologic(int pos){
        float per=0;
        if(mark.empty()){
            per=dbhelper.getPercentage(pos);
            Toast.makeText(getApplicationContext(),"You have nothing to undo",Toast.LENGTH_LONG).show();
            return per;
        }
        if(mark.peek()==1){
            mark.pop();
            per=dbhelper.undo(pos,1);
            return per;
        }
        if(mark.peek()==0){
            mark.pop();
            per=dbhelper.undo(pos,0);
            return per;
        }
        return per;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.subjects_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.clear){
            dbhelper.deleteall();
            Subjectnames.clear();
            rvAdapter.notifyDataSetChanged();
        }
        if(item.getItemId()==R.id.add_subject){
            Intent entry = new Intent(Subjects.this, dialog_entry.class);
            startActivityForResult(entry, 0);
        }
        return super.onOptionsItemSelected(item);
    }
}

