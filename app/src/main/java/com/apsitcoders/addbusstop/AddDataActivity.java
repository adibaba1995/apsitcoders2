package com.apsitcoders.addbusstop;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class AddDataActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PLACE_PICKER_REQUEST = 1;

    @BindView(R.id.location)
    Button location;
    @BindView(R.id.name)
    EditText name;
    @BindView(R.id.add)
    Button add;

    private Unbinder unbinder;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference ref;

    GeoFire geoFire;

    private Place place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data);
        unbinder = ButterKnife.bind(this);
        initView();

        firebaseDatabase = FirebaseDatabase.getInstance();
        ref = firebaseDatabase.getReference("stops");
        geoFire = new GeoFire(ref);
    }

    private void initView() {
        location.setOnClickListener(this);
        add.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.location:
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.add:
                addLocation(place.getId(), place.getLatLng());
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                place = PlacePicker.getPlace(this, data);
//                name.setText(place.getId());
//                String toastMsg = String.format("Place: %s", place.getName()) + String.format(" Location: %s", place.getLatLng().latitude + " " + place.getLatLng().longitude);
//                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void addLocation(String key, LatLng latLng) {
        geoFire.setLocation(key, new GeoLocation(latLng.latitude, latLng.longitude), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                Toast.makeText(AddDataActivity.this, "Data added successfully", Toast.LENGTH_SHORT).show();
                firebaseDatabase.getReference("stop-name").child(key).setValue(name.getText().toString());
            }
        });
    }
}
