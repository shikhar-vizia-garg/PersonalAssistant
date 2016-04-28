package shikhar.personalassistant;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by user on 25-08-2015.
 */
public class Reminders extends AppCompatActivity {
    RecyclerView rv;
    ImageView back;
    RVAdapter rvAdapter;
    DatabaseHelper dbhelper;
    Cursor cur_reminders;
    int pos;
    ArrayList<ReminderModel> reminders;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_reminders);
        Intent r=getIntent();
        pos=r.getIntExtra("subject_id",0);
        Log.d("positionreturned", "" + pos);

        back=(ImageView)findViewById(R.id.back2);
        //toolbar=(Toolbar) findViewById(R.id.toolbar4);
        //setSupportActionBar(toolbar);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        reminders=new ArrayList<ReminderModel>();

        dbhelper=new DatabaseHelper(this);
        cur_reminders=dbhelper.getreminders(pos);
        cur_reminders.moveToFirst();

        for(int i=0;i<cur_reminders.getCount();i++){
            Log.i("rem_details_1",cur_reminders.getString(2)+" "+cur_reminders.getString(cur_reminders.getColumnIndex("content")));
            cur_reminders.moveToNext();
        }

        new ReminderFill().execute(cur_reminders);

        rv=(RecyclerView) findViewById(R.id.rv_reminders);
        LinearLayoutManager llm=new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(llm);
        rvAdapter=new RVAdapter(reminders);
        rv.setAdapter(rvAdapter);
    }

    public class ReminderFill extends AsyncTask<Cursor,Void,Boolean>{
        ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog=new ProgressDialog(Reminders.this);
            dialog.setTitle("Reminders");
            dialog.setMessage("We appreciate your patetince");
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected Boolean doInBackground(Cursor... params) {
            if(params[0].getCount()<=0)
                return false;
            else{
                params[0].moveToFirst();
                for(int i=0;i<params[0].getCount();i++){
                    ReminderModel rm=new ReminderModel();
                    rm.setTitle(params[0].getString(2));
                    rm.setContent(params[0].getString(params[0].getColumnIndex("content")));
                    rm.setDeadline(params[0].getString(params[0].getColumnIndex("setDate")));
                    Log.i("rem_details",rm.getTitle()+" "+rm.getContent()+" "+rm.getDeadline());
                    reminders.add(rm);
                    params[0].moveToNext();
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean){
                dialog.cancel();
                rvAdapter.notifyDataSetChanged();
            } else{
                dialog.cancel();
                Toast.makeText(Reminders.this,"You have no reminders for this subject",Toast.LENGTH_LONG).show();
            }
        }
    }

    private class RVAdapter extends RecyclerView.Adapter<RVAdapter.ReminderViewHolder>{
        ArrayList<ReminderModel> rmlist;

        public RVAdapter(ArrayList<ReminderModel> rmlist) {
            this.rmlist = rmlist;
        }
        public class ReminderViewHolder extends RecyclerView.ViewHolder{
            TextView con,title,deadline;
            ImageView cancel;
            CardView card;
            public ReminderViewHolder(View v) {
                super(v);
                card=(CardView) v.findViewById(R.id.cv_reminders);
                title=(TextView) v.findViewById(R.id.textView_title);
                con=(TextView) v.findViewById(R.id.textView_content);
                deadline=(TextView) v.findViewById(R.id.textView_deadline);
                //cancel=(ImageView) v.findViewById(R.id.cancel);
            }
        }
        @Override
        public RVAdapter.ReminderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.list_reminders_adap,parent,false);
            ReminderViewHolder holder=new ReminderViewHolder(v);
            return holder;
        }

        @Override
        public void onBindViewHolder(RVAdapter.ReminderViewHolder holder, final int position) {
            holder.con.setText(rmlist.get(position).getContent());
            holder.title.setText(rmlist.get(position).getTitle());
            holder.deadline.setText(rmlist.get(position).getDeadline());
            /*holder.cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rmlist.remove(position);
                    rvAdapter.notifyDataSetChanged();
                }
            });*/
        }

        @Override
        public int getItemCount() {
            return rmlist.size();
        }
    }
}
