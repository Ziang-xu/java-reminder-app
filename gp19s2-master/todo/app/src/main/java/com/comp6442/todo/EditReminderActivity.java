package com.comp6442.todo;

import android.Manifest;
import android.app.DatePickerDialog;
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
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EditReminderActivity extends AppCompatActivity implements GoogleMap.OnMyLocationClickListener, OnMapReadyCallback {

    private static final String TAG = "EditReminderActivity";

    private Button savebt;
    private Button returnbt;

    private TextView titleView;
    private TextView createDateView;
    private TextView createTimeView;
    private TextView remindDateView;
    private TextView remindTimeView;
    private TextView notesView;

    private int id;
    private String title;
    private String createDate;
    private String createTime;
    private String remindDate;
    private String remindTime;
    private String body;
    private String itemLocation;
    private boolean isCompleted;
    final Calendar calendar = Calendar.getInstance();

    private TodoItem todoItem;
    TodoItemDatabase db;
    private Intent mainActivityIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        init();
        getLocation();

        /**
         * change the reminder date
         */
        remindDateView.setOnClickListener(v -> new DatePickerDialog(EditReminderActivity.this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, monthOfYear);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                    remindDateView.setText(sdf.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))
                .show());

        /**
         * change the reminder time
         */
        remindTimeView.setOnClickListener(v -> {
            new TimePickerDialog(EditReminderActivity.this,
                    (TimePicker view, int hourOfDay, int minute) -> {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                        remindTimeView.setText(sdf.format(calendar.getTime()));
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true)
                    .show();
        });

        /**
         * set up the save button listener. when this button is clicked, there will be a dialog to remind the user save the item or not
         * when the user choose the save, all information of the added activity will be save to the database.
         */
        savebt.setOnClickListener((e) -> {
            title = titleView.getText().toString();
            body = notesView.getText().toString();
            remindDate = remindDateView.getText().toString();
            remindTime = remindTimeView.getText().toString();

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
                SQLiteDatabase database;
                database = db.getWritableDatabase();
                ContentValues table = new ContentValues();
                table.put(TodoItemDatabase.itemID, id);
                table.put(TodoItemDatabase.itemTitle, title);
                table.put(TodoItemDatabase.itemBody, body);
                table.put(TodoItemDatabase.itemDate, createDate);
                table.put(TodoItemDatabase.itemTime, createTime);
                table.put(TodoItemDatabase.reminderDate, remindDate);
                table.put(TodoItemDatabase.reminderTime, remindTime);
                table.put(TodoItemDatabase.itemLocation, itemLocation);
                table.put(TodoItemDatabase.completed, isCompleted);
                database.update(TodoItemDatabase.tableName, table, TodoItemDatabase.itemID + "=?",
                        new String[]{Integer.toString(todoItem.getItemId())});
                database.close();
                Toast.makeText(this, "edit successfully", Toast.LENGTH_SHORT).show();
            }


            startActivity(mainActivityIntent);
            EditReminderActivity.this.finish();
        });

        /**
         * set the return button click listener. When click this button, it will check if there is a new item. Then create a dialog to
         * to notify the user save their item or just return the main activity.
         */
        returnbt.setOnClickListener((e) -> {
            if ((!"".equals(title) || (!"".equals(body)))) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditReminderActivity.this);
                builder.setMessage("Do you want to save your todo item?");
                builder.setTitle("notice");
                builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int index) {
                        savebt.callOnClick();
                    }
                });
                builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(mainActivityIntent);
                        EditReminderActivity.this.finish();
                    }
                });
                builder.create().show();
            } else {
                startActivity(mainActivityIntent);
                EditReminderActivity.this.finish();
            }
        });

    }

    /**
     * initialize the variables, button and other views.
      */
    private void init() {
        savebt = findViewById(R.id.saveButton_edit);
        returnbt = findViewById(R.id.returnButton_edit);
        titleView = findViewById(R.id.titleEditText_edit);
        createDateView = findViewById(R.id.dateEditText_edit);
        createTimeView = findViewById(R.id.create_timeEditText_edit);
        remindDateView = findViewById(R.id.remind_dateEditText_edit);
        remindTimeView = findViewById(R.id.timeEditText_edit);
        notesView = findViewById(R.id.notesEditText_edit);
        db = new TodoItemDatabase(this);
        mainActivityIntent = new Intent(this, MainActivity.class);
        Intent intent = this.getIntent();
        Bundle bundle = intent.getBundleExtra(TodoItemDatabase.tableName);
        if(bundle == null) {
            return;
        }

        //get the todoItem information
        id = bundle.getInt(TodoItemDatabase.itemID);
        createDate = (bundle.getString(TodoItemDatabase.itemDate));
        createTime = (bundle.getString(TodoItemDatabase.itemTime));
        remindDate = (bundle.getString(TodoItemDatabase.reminderDate));
        remindTime = (bundle.getString(TodoItemDatabase.reminderTime));
        title = (bundle.getString(TodoItemDatabase.itemTitle));
        body = (bundle.getString(TodoItemDatabase.itemBody));
        itemLocation = bundle.getString(TodoItemDatabase.itemLocation);
        isCompleted = bundle.getBoolean(TodoItemDatabase.completed);

        //set up new todoItem
        todoItem = new TodoItem();
        todoItem.setItemId(id);
        todoItem.setCreatedDate(createDate);
        todoItem.setCreatedTime(createTime);
        todoItem.setReminderDate(remindDate);
        todoItem.setReminderTime(remindTime);
        todoItem.setItemCompleted(isCompleted);
        todoItem.setItemBody(body);
        todoItem.setItemLocation(itemLocation);

        //set up the textview contend.
        titleView.setText(title);
        createDateView.setText(createDate);
        createTimeView.setText(createTime);
        remindDateView.setText(remindDate);
        remindTimeView.setText(remindTime);
        notesView.setText(body);
    }

    /**
     * this funcation will use the location manager to get the current location.
     * The currne location will be set as the dafult location if the user does not choose the locaiton frommap.
     */
    private void getLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.
                LOCATION_SERVICE);
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

        //when it cannot get the current location, it will be loged and try to use the last location.
        if(locationManager == null) {
            Log.e(TAG, "LocationManager is null, failed to retrieve last known location");
            return;
        }

        Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if(locationGPS == null) {
            Log.e(TAG, "Failed to get last known location");
            return;
        }

        double latitude = locationGPS.getLatitude();
        double longitude = locationGPS.getLongitude();

        Geocoder gc = new Geocoder(this, Locale.getDefault());
        List<Address> locationList = null;
        try {
            locationList = gc.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(locationList == null || locationList.isEmpty()) {
            Log.e(TAG, "Failed to get location's address");
            return;
        }

        Address address = locationList.get(0);
        String TAG = "namelocation";
        String countryName = address.getCountryName();//get the conutry name
        Log.i(TAG, "countryName = " + countryName);
        String locality = address.getLocality();//city name
        Log.i(TAG, "locality = " + locality);
        itemLocation = "";
        for (int i = 0; address.getAddressLine(i) != null; i++) {
            String addressLine = address.getAddressLine(i);//ge the adress
            itemLocation += addressLine;
            Log.i(TAG, "addressLine = " + addressLine);
        }
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}
