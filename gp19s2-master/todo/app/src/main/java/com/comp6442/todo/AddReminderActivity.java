package com.comp6442.todo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddReminderActivity extends AppCompatActivity
        implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback,
        TimePickerDialog.OnTimeSetListener {
    final Calendar calendar = Calendar.getInstance();
    Button returnButton;
    Button saveButton;
    Intent mainActivityIntent;
    TodoItemDatabase todoItemDatabase;
    LocationManager locationManager;
    double latitude;
    double longitude;
    private EditText editTitle;
    private EditText editBody;
    private EditText dateEditText;
    private EditText timeEditText;
    private String createDate;
    private String createTime;
    private String reminderDate;
    private String reminderTime;
    private boolean isCompleted;
    private String itemLocation;
    private String title;
    private String body;
    private Button location_bt;

    private CheckBox addToCalendarCheckbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        init();
        locationManager = (LocationManager) getSystemService(Context.
                LOCATION_SERVICE);
        getLocation();

        mainActivityIntent = new Intent(this, MainActivity.class);

        /**
         * set the return button click listener. When click this button, it will check if there is a new item. Then create a dialog to
         *  to notify the user save their item or just return the main activity.
         */
        returnButton.setOnClickListener((e) -> {
            if ((!"".equals(title) || (!"".equals(body)))) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddReminderActivity.this);
                builder.setMessage("Do you want to save your item?");
                builder.setTitle("New todo item");
                builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int index) {
                        saveButton.callOnClick();
                    }
                });
                builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(mainActivityIntent);
                        AddReminderActivity.this.finish();
                    }
                });
                builder.create().show();
            } else {
                startActivity(mainActivityIntent);
                AddReminderActivity.this.finish();
            }
        });

        /**
         * set up the save button listener. when this button is clicked, there will be a dialog to remind the user save the item or not
         * when the user choose the save, all information of the added activity will be save to the database.
         */
        saveButton.setOnClickListener((e) -> {
            title = editTitle.getText().toString();
            body = editBody.getText().toString();
            reminderDate = dateEditText.getText().toString();
            reminderTime = timeEditText.getText().toString();

            //check if there is a title, or bofy and their length.
            boolean flag = true;
            if ("".equals(title)) {
                flag = false;
                Toast.makeText(this, "the title can not be null!", Toast.LENGTH_SHORT).show();
            }
            if (title.length() > 20) {
                Toast.makeText(this, "the title is too long!", Toast.LENGTH_SHORT).show();
                flag = false;
            }
            if (body.length() > 300) {
                Toast.makeText(this, "the body is too long!", Toast.LENGTH_SHORT).show();
                flag = false;
            }
            if (flag) {
                //save to the sql database.
                SQLiteDatabase database = todoItemDatabase.getWritableDatabase();
                ContentValues todoItemContentValues = new ContentValues();
                todoItemContentValues.put(TodoItemDatabase.itemTitle, title);
                todoItemContentValues.put(TodoItemDatabase.itemBody, body);
                todoItemContentValues.put(TodoItemDatabase.itemDate, createDate);
                todoItemContentValues.put(TodoItemDatabase.itemTime, createTime);
                todoItemContentValues.put(TodoItemDatabase.reminderDate, reminderDate);
                todoItemContentValues.put(TodoItemDatabase.reminderTime, reminderTime);
                todoItemContentValues.put(TodoItemDatabase.itemLocation, itemLocation);
                todoItemContentValues.put(TodoItemDatabase.completed, isCompleted);
                long itemId = database.insert(TodoItemDatabase.tableName, null, todoItemContentValues);
                database.close();
                startAlarm(calendar, Long.valueOf(itemId).intValue());
                Toast.makeText(this, "added new todo item", Toast.LENGTH_SHORT).show();
            }

            //use the ckeckbox to check if this item need to be added onto the calendar.
            boolean addToCalendarIsChecked = ((CheckBox) addToCalendarCheckbox).isChecked();
            if (addToCalendarIsChecked && reminderDate != null && reminderTime != null) {
                String[] reminderDateValues = reminderDate.split("-");
                int reminderDay = Integer.valueOf(reminderDateValues[0]);
                int reminderMonth = Integer.valueOf(reminderDateValues[1]);
                int reminderYear = Integer.valueOf(reminderDateValues[2]);

                String[] reminderTimeValues = reminderTime.split(":");
                int reminderHour24 = Integer.valueOf(reminderTimeValues[0]);
                int reminderMinutes = Integer.valueOf(reminderTimeValues[1]);
                Calendar reminderBeginTime = Calendar.getInstance();
                reminderBeginTime.set(reminderYear, reminderMonth, reminderDay, reminderHour24, reminderMinutes);

                //open the google calendar and transfer all the item informaiton to the calendar.
                Intent addToCalendarIntent = new Intent(Intent.ACTION_INSERT)
                        .setData(CalendarContract.Events.CONTENT_URI)
                        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, reminderBeginTime.getTimeInMillis())
                        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, reminderBeginTime.getTimeInMillis() + 600000)
                        .putExtra(CalendarContract.Events.TITLE, title)
                        .putExtra(CalendarContract.Events.DESCRIPTION, body)
                        .putExtra(CalendarContract.Events.EVENT_LOCATION, itemLocation)
                        .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
                startActivity(addToCalendarIntent);
                AddReminderActivity.this.finish();
            } else {
                startActivity(mainActivityIntent);
                AddReminderActivity.this.finish();
            }
        });

        //set the date text and let users select the reminder date.
        dateEditText.setOnClickListener(v -> new DatePickerDialog(AddReminderActivity.this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, monthOfYear);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                    dateEditText.setText(sdf.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))
                .show());

        addToCalendarCheckbox = (CheckBox) findViewById(R.id.addToCalendarCheckBox);

        //set the time text and let users select the reminder time.
        timeEditText.setOnClickListener(v -> new TimePickerDialog(AddReminderActivity.this,
                (view, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    timeEditText.setText(sdf.format(calendar.getTime()));
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true)
                .show());

        location_bt.setOnClickListener((e) -> {
            Intent intent = new Intent(AddReminderActivity.this, MapsActivity.class);
            startActivityForResult(intent, 0);
        });


    }

    /**
     * initialize the variables, button and other views.
     */
    private void init() {
        todoItemDatabase = new TodoItemDatabase(this);
        returnButton = findViewById(R.id.returnButton_edit);
        saveButton = findViewById(R.id.saveButton_edit);
        editTitle = findViewById(R.id.titleEditText_edit);
        editBody = findViewById(R.id.notesEditText_edit);
        dateEditText = findViewById(R.id.dateEditText_edit);
        timeEditText = findViewById(R.id.timeEditText_edit);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        createDate = day + "/" + month + "/" + year;
        createTime = hour + ":" + minute;
        isCompleted = false;
        itemLocation = "";
        location_bt = findViewById(R.id.loc_bt_onadd);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        timeEditText.setText(hourOfDay + ":" + minute);
    }

    /**
     * this function we use the AlarmManager to set up the reminder time and get the notification.
     *
     * @param calendar the calendar is the user set the date and time to remind.
     * @param notificationId check which notification we should open
     *
     */
    private void startAlarm(Calendar calendar, int notificationId) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            Intent intent = new Intent(this, NotificationAlarmReceiver.class);
            intent.putExtra(NotificationAlarmReceiver.NOTIFICATION_ID, notificationId);
            intent.putExtra(NotificationAlarmReceiver.NOTIFICATION_TITLE, title);
            intent.putExtra(NotificationAlarmReceiver.NOTIFICATION_BODY, body);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 101, intent, 0);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    private void cancelAlarm(Calendar c) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            Intent intent = new Intent(this, NotificationAlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
            alarmManager.cancel(pendingIntent);
        }
    }

    /**
     * this funcation will use the location manager to get the current location.
     * The currne location will be set as the dafult location if the user does not choose the locaiton frommap.
     */
    private void getLocation() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(locationGPS == null) {
            return;
        }
        latitude = locationGPS.getLatitude();
        longitude = locationGPS.getLongitude();

        //transfer the latitude and longtitude to the address.
        Geocoder gc = new Geocoder(this, Locale.getDefault());
        List<Address> locationList = null;
        try {
            locationList = gc.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("location", latitude + ", " + longitude);
        Log.i("locationadd", locationList.toString());
        Address address = locationList.get(0);
        String TAG = "namelocation";
        String countryName = address.getCountryName();//get the conutry name
        Log.i(TAG, "countryName = " + countryName);
        String locality = address.getLocality();//city name
        Log.i(TAG, "locality = " + locality);
        for (int i = 0; address.getAddressLine(i) != null; i++) {
            String addressLine = address.getAddressLine(i);//ge the adress
            Log.i(TAG, "addressLine = " + addressLine);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    /**
     *
     * @param requestCode check the code if this intent we need. it should be same with the code we set in the get location button listener.
     * @param resultCode the code from the last activity we just close. it will check if we should get the information from that activity.
     * @param data the data of the intent. in this function it contains the latitude, the longitude and the address (itemLocation).
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK && data != null) {
            longitude = data.getDoubleExtra("longitude", longitude);
            latitude = data.getDoubleExtra("latitude", latitude);
            itemLocation = data.getStringExtra("address");
            Log.i("location", latitude + ", " + longitude);
        }
    }
}
