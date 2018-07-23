package com.example.benedictlutab.sidelinetg.modules.viewHome.postTask.setTaskLocation;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.benedictlutab.sidelinetg.R;
import com.example.benedictlutab.sidelinetg.helpers.fontStyleCrawler;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class googleMapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {

    @BindView(R.id.btnBack) Button btnBack;
    @BindView(R.id.btnConfirm) Button btnConfirm;
    @BindView(R.id.ivDeviceLocation) ImageView ivDeviceLocation;
    @BindView(R.id.actvSearchLocation) AutoCompleteTextView actvSearchLocation;

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private PlaceAutoCompleteAdapter placeAutoCompleteAdapter;
    private GoogleApiClient googleApiClient;

    private static final String FINE_LOCATION         = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION       = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_CODE = 100;
    private static final float DEFAULT_ZOOM           = 15f;
    private static final LatLngBounds LAT_LNG_BOUNDS  = new LatLngBounds(new LatLng(-40, -168), new LatLng(71, 136));
    private Boolean isLocationPermissionGranted       = false;

    private String line_one, city;
    private double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settasklocation_activity_google_maps);
        ButterKnife.bind(this);

        changeFontFamily();
        getLocationPermission();
    }

    @OnClick({R.id.btnBack, R.id.ivDeviceLocation, R.id.btnConfirm})
    public void setViewOnClickEvent(View view)
    {
        switch(view.getId())
        {
            case R.id.btnBack:
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                this.finish();
                break;
            case R.id.ivDeviceLocation:
                Log.e("ivDeviceLoc: ", "CLICKED!");
                getDeviceLocation();
                break;
            case R.id.btnConfirm:
                confirmLocation();
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        Log.e("onMapReady: ", "Map - READY");
        mMap = googleMap;

        if (isLocationPermissionGranted)
        {
            getDeviceLocation();
            // Add blue dot marker.
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            }
            mMap.setMyLocationEnabled(true);
        }

        initSearchLocation();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {

    }

    private void getLocationPermission()
    {
        Log.e("getLocationPermission: ", "Getting location permission");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                isLocationPermissionGranted = true;
                initMap();
            }
            else
            {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_CODE);
            }
        }
        else
        {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        isLocationPermissionGranted = false;

        switch(requestCode)
        {
            case LOCATION_PERMISSION_CODE:
            {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    for(int i = 0; i<grantResults.length; i++)
                    {
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED)
                        {
                            isLocationPermissionGranted = false;
                            Log.e("onReqPermissionsRes: ", "FAILED!");
                            return;
                        }
                    }
                    isLocationPermissionGranted = true;
                    Log.e("onReqPermissionsRes: ", "SUCCESS!");
                    // Initialize map.
                    initMap();
                }
            }
        }
    }

    private void getDeviceLocation()
    {
        Log.e("getDeviceLocation: ", "Fetching device's current location...");
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try
        {
            if(isLocationPermissionGranted)
            {
                Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener()
                {
                    @Override
                    public void onComplete(@NonNull Task task)
                    {
                        if(task.isSuccessful() && task.getResult() != null)
                        {
                            Log.e("getDeviceLocation: ", "found");
                            Location currentLocation = (Location) task.getResult();

                            // Geo locate
                            Log.e("getDeviceLocation: ", "geoLocating now...!");
                            Geocoder geocoder  = new Geocoder(googleMapsActivity.this);
                            List<Address> list = new ArrayList<>();

                            try
                            {
                                list = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
                            }
                            catch(IOException ex)
                            {
                                Log.e("getDeviceLocation:", ex.toString());
                            }

                            if(list.size() > 0)
                            {
                                Address address = list.get(0);
                                Log.e("getDeviceLocation: ", address.getAddressLine(0));

                                line_one  = address.getAddressLine(0);
                                city      = address.getLocality();
                                latitude  = address.getLatitude();
                                longitude = address.getLongitude();

                                actvSearchLocation.setText(line_one);

                                // Move camera to the found location
                                moveCamera(new LatLng(latitude, longitude), DEFAULT_ZOOM);
                                Log.e("ADDRESS:", line_one + city);
                            }
                        }
                        else
                        {
                            Log.e("getDeviceLocation: ", "unable to find location.");
                        }
                    }
                });
            }
        }
        catch(SecurityException ex)
        {
            Log.e("getDeviceLocation(ex): ", ex.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, float zoom)
    {
        Log.e("moveCamera: ", String.valueOf(latLng.latitude)+" , "+String.valueOf(latLng.longitude));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        // Add marker, drop pin to the location.
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.main_img_pin_logo));
        mMap.addMarker(markerOptions);
    }

    private void initMap()
    {
        Log.e("initMap: ", "Initializing map...");
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(googleMapsActivity.this);
    }

    private void initSearchLocation()
    {
        Log.e("initSearchLoc:", "Initializing - STARTED!");

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
        .enableAutoManage(this, this).build();

        // Limit to Philippines
        AutocompleteFilter filter = new AutocompleteFilter.Builder().setCountry("PH").build();

        placeAutoCompleteAdapter = new PlaceAutoCompleteAdapter(this, googleApiClient, LAT_LNG_BOUNDS, filter);
        actvSearchLocation.setAdapter(placeAutoCompleteAdapter);

        actvSearchLocation.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                actvSearchLocation.setText("");
            }
        });

        actvSearchLocation.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent)
            {
                if(actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || keyEvent.getAction() == KeyEvent.ACTION_DOWN || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER)
                {
                    geoLocate();
                }
                return false;
            }
        });

        actvSearchLocation.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick (AdapterView<?> parent, View view, int position, long id)
            {
                hideKeyboard();
                geoLocate();
            }
        });
    }

    private void geoLocate()
    {
        Log.e("geoLocate:", "Geolocating - STARTED!");

        String searchString = actvSearchLocation.getText().toString();

        Geocoder geocoder  = new Geocoder(googleMapsActivity.this);
        List<Address> list = new ArrayList<>();

        try
        {
           list = geocoder.getFromLocationName(searchString, 1);
        }
        catch(IOException ex)
        {
            Log.e("geoLocation(EX):", ex.toString());
        }

        if(list.size() > 0)
        {
            Address address = list.get(0);
            Log.e("geoLocate LOC-FOUND: ", address.getAddressLine(0));

            line_one  = address.getAddressLine(0);
            city      = address.getLocality();
            latitude  = address.getLatitude();
            longitude = address.getLongitude();

            // Move camera to the found location
            moveCamera(new LatLng(latitude, longitude), DEFAULT_ZOOM);
            Log.e("ADDRESS:", line_one + city);
        }
    }

    private void confirmLocation()
    {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("line_one",line_one);
        returnIntent.putExtra("city",city);
        returnIntent.putExtra("latitude",latitude);
        returnIntent.putExtra("longitude",longitude);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

    private void hideKeyboard()
    {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void changeFontFamily()
    {
        // Change Font Style.
        fontStyleCrawler fontStyleCrawler = new fontStyleCrawler(getAssets(), "fonts/ralewayRegular.ttf");
        fontStyleCrawler.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));
    }


}
