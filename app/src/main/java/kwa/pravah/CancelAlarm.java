package kwa.pravah;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import kwa.pravah.database.DbManager;

public class CancelAlarm extends AppCompatActivity {

    private static final int CONTACT_PICK = 1;
    String PhoneNo,Name ;
    Button cancel;
    EditText Ph;
    DbManager db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_alarm);

        db=new DbManager(CancelAlarm.this);


        cancel=findViewById(R.id.Cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Ph=findViewById(R.id.Number);
                String phone=Ph.getText().toString();
                if (db.getnumber(phone))
                {
                    Cursor cursor=db.getPendingIntent(phone);
                    if(cursor.getCount()!=0) {

                        cursor.moveToFirst();
                        String Pending_intent_to_on = cursor.getString(cursor.getColumnIndex(db.PENDING_INTENT_ON));
                        String Pending_intent_to_off = cursor.getString(cursor.getColumnIndex(db.PENDING_INTENT_OFF));

                        cancelAlarm(Pending_intent_to_on);
                        cancelAlarm(Pending_intent_to_off);

                        db.deleteRow(phone);
                        Ph.setText("");
                        Toast.makeText(CancelAlarm.this, "Alarm cleared", Toast.LENGTH_SHORT).show();

                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "No alarms to clear...!!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    public void cancelAlarm(String pndIntent)
    {
        AlarmManager aManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        PendingIntent pIntent = PendingIntent.getBroadcast(getApplicationContext(),
                Integer.parseInt(pndIntent),intent,0);
        aManager.cancel(pIntent);
    }

    public void contactPickerOnClick(View view) {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(contactPickerIntent, CONTACT_PICK);

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (resultCode == RESULT_OK) {
            // Check for the request code, we might be usign multiple startActivityForReslut
            switch (requestCode) {
                case CONTACT_PICK:
                    contactPicked(data);
                    break;
            }
        } else {
            Log.e("MainActivity", "Failed to pick contact");
        }
    }


    private void contactPicked(Intent data) {
        Cursor cursor = null;
        try {
            String name = null;
            // getData() method will have the Content Uri of the selected contact
            Uri uri = data.getData();
            //Query the content uri
            cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            // column index of the phone number
            int  phoneIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            // column index of the contact name
            int  nameIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            PhoneNo = cursor.getString(phoneIndex);
            Name = cursor.getString(nameIndex);
            // Set the value to the textviews
            //textView1.setText(name);
            if(PhoneNo.length()==13)
                PhoneNo=PhoneNo.substring(3,13);
            Ph.setText(PhoneNo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
