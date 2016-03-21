package sms.luciole.com.smsspam;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import sms.luciole.com.smsspam.Model.User;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ArrayList<User> userList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getAllContacts();
        AutoCompleteTextView textView = (AutoCompleteTextView)findViewById(R.id.ContactList);
        textView.setAdapter(getAdapter());

        Button spamButton = (Button)findViewById(R.id.Launch);
        spamButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        AutoCompleteTextView textView = (AutoCompleteTextView)findViewById(R.id.ContactList);
        String number = getNumber(textView.getText().toString());
        EditText editText = (EditText)findViewById(R.id.editText);
        EditText message = (EditText)findViewById(R.id.SpamText);

        int numberToSend = Integer.parseInt(editText.getText().toString());
        if(number != null || !message.getText().toString().isEmpty() || !editText.getText().toString().isEmpty()){
            sendSMS(number, message.getText().toString(), numberToSend);
            //Clear les input
            message.setText(null);
            editText.setText(null);
        }
        else{
            Toast.makeText(getApplicationContext(), "Error when sending sms", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Récupére l'ensemble des contacts
     */
    private void getAllContacts(){
        Cursor phone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        assert phone != null;
        while (phone.moveToNext()){
            String name = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String id = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
            String number = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            userList.add(new User(Integer.parseInt(id), name, number));
        }
    }

    private ArrayAdapter getAdapter(){
        ArrayList<String> names = new ArrayList<>();
        for(int i = 0; i < userList.size(); i++){
            names.add(userList.get(i).getName());
        }

        return new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names);
    }

    /**
     *
     * @param name le nom du contact
     */
    private String getNumber(String name){
        for(int i = 0; i < userList.size(); i++){
            if(userList.get(i).getName().equals(name)){
                return userList.get(i).getNumber();
            }
        }
        return null;
    }

    /**
     * @param number Numéro de téléphone
     * @param message Message a envoyer
     * @param numberToSend Nombre de messages à envoyer
     */
    private void sendSMS(String number, String message, int numberToSend){
        SmsManager sms = SmsManager.getDefault();
        for(int i = 0; i < numberToSend; i++){
            sms.sendTextMessage(number, null, message, null, null);
        }
        sendNotification();
    }

    private void sendNotification(){
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);

        Notification.Builder notificationBuilder = new Notification.Builder(getApplicationContext());
        notificationBuilder.setTicker("SMS Spam notification")
                .setSmallIcon(R.drawable.icon)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentText("All SMS have been sent ! ")
                .setContentTitle("SMS Spam notification")
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }
}