package shikhar.personalassistant;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by user on 18-08-2015.
 */
public class dialog_entry extends Activity {

    EditText sub;
    Button ok;
    DatabaseHelper dbhelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry_subject);

        sub=(EditText) findViewById(R.id.edit_entry_subject);
        dbhelper=new DatabaseHelper(this);
        ok=(Button) findViewById(R.id.button_ok);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str=sub.getText().toString();
                int id=dbhelper.getid();
                dbhelper.addSubject(str, id);
                Intent intent=new Intent();
                intent.putExtra("sub", str);
                if(str!=""){
                    Log.d("subjectname,subid", str+":"+ id);//checks what gets filled in subid and subject name field of subjects table
                    setResult(RESULT_OK,intent);
                    finish();
                }
                else{
                    setResult(-1,intent);
                    finish();
                }

            }
        });
    }
}
