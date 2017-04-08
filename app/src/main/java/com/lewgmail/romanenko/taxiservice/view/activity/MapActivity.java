package com.lewgmail.romanenko.taxiservice.view.activity;

/**
 * Created by Lev on 28.11.2016.
 */

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lewgmail.romanenko.taxiservice.R;
import com.lewgmail.romanenko.taxiservice.presenter.MapGooglePresenter;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapActivity extends Activity implements OnMapReadyCallback, IView, PlaceSelectionListener {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private final static String FIRST_CORD = "longitude", SECONF_CORD = "latitude";
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));
    @BindView(R.id.button_ok)
    Button buttonOK;
    //@BindView(R.id.place_autocomplete_fragment)
    // PlaceAutocompleteFragment placeAutocompleteFragment;
    //@BindView(R.id.text_location)
    //  EditText searchText;
    GoogleMap mGoogleMap;
    // Technical Object
    private MapFragment googleMap;
    private LatLng position;
    private Geocoder geo;
    private MapGooglePresenter mapGooglePresenter;
    private SharedPreferences sPref;
    private Intent intentMy;
    // Business object
    private String addressFromMarker;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private MarkerOptions markerOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.google_map);
        ButterKnife.bind(this);
        intentMy = getIntent();
        createMapView();
        setElementLocationOnMapActivity();
        initializeSearchPlaceFragment();
        //  mapGooglePresenter = new MapGooglePresenter(this);
        // addMarker();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(50.448541, 30.507144), 10));
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //User has previously accepted this permission
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mGoogleMap.setMyLocationEnabled(true);
            }
        } else {
            //Not in api-23, no need to prompt
            mGoogleMap.setMyLocationEnabled(true);
        }
        //  googleMap.setMyLocationEnabled(true);
        googleMap.setTrafficEnabled(true);
        googleMap.setIndoorEnabled(true);
        googleMap.setBuildingsEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);

        /*googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(50.44854153, 30.50714493))
                .title("Marker")
                .draggable(true)
                .snippet("Hello"));*/


        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {

                // Locale aLocale = new Locale.Builder().setLanguage("ru").build();
                //   Locale asd = new Locale("ru");
                position = marker.getPosition(); //
                try {
                    geo = new Geocoder(MapActivity.this.getApplicationContext(), new Locale("ru"));
                    List<Address> addresses = geo.getFromLocation(position.latitude, position.longitude, 1);
                    if (addresses.isEmpty()) {
                        Toast.makeText(
                                MapActivity.this,
                                "Please wait",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        if (addresses.size() > 0) {
                          /*  Toast.makeText(getApplicationContext(),
                                    "Address:- " + addresses.get(0).getFeatureName() +
                                            addresses.get(0).getAddressLine(0)+
                                            addresses.get(0).getAdminArea() +
                                            addresses.get(0).getLocality(), Toast.LENGTH_LONG).show();
                            addressFromMarker = addresses.get(0).getFeatureName() +
                                    addresses.get(0).getAdminArea() +
                                    addresses.get(0).getLocality();*/
                            Toast.makeText(getApplicationContext(),
                                    "Address:- " +
                                            addresses.get(0).getAddressLine(0) +
                                            addresses.get(0).getAdminArea() +
                                            addresses.get(0).getLocality(), Toast.LENGTH_LONG).show();
                            //addresses.get(0).getSub

                            addressFromMarker = addresses.get(0).getAddressLine(0) +
                                    addresses.get(0).getAdminArea() +
                                    addresses.get(0).getLocality();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace(); // getFromLocation() may sometimes fail
                }
            }
        });

    }

   /* public LatLng culculateDistance() {

    }*/

    private void createMapView() {
        /**
         * Catch the null pointer exception that
         * may be thrown when initialising the map
         */
        try {
            if (null == googleMap) {
                googleMap = (MapFragment) getFragmentManager()
                        .findFragmentById(R.id.mapView);
                googleMap.getMapAsync(this);

                /**
                 * If the map is still null after attempted initialisation,
                 * show an error to the user
                 */
                if (null == googleMap) {
                    Toast.makeText(getApplicationContext(),
                            "Error creating map", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (NullPointerException exception) {
            Log.e("mapApp", exception.toString());
        }

    }

    @OnClick(R.id.button_ok)
    public void buttonOk() {
       /* Intent returnIntent = getIntent();
        returnIntent.putExtra(getDataFromPreviousActivity(intentMy), addressFromMarker);
        returnIntent.putExtra(FIRST_CORD, position.longitude);
        returnIntent.putExtra(SECONF_CORD, position.latitude);
        // returnIntent.putExtra(FIRST_CORD,50.4546600 );
        //  returnIntent.putExtra(SECONF_CORD, 30.5238000);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();*/
        checkLocationPermission();
    }


    private String getDataFromPreviousActivity(Intent intent) {
        return intent.getStringExtra("keyAddressFromMarker");
    }

    ///////////////////////////////////// Up date View  ////////////////////////////////////
    @Override
    public void showError(String error) {
        Toast.makeText(
                MapActivity.this,
                error,
                Toast.LENGTH_LONG).show();
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                //  TODO: Prompt with explanation!

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay!
                    if (ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mGoogleMap.setMyLocationEnabled(true);
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }

    public void onMapSearch(String location) {
        //EditText locationSearch = (EditText) findViewById(R.id.editText);
        //  String location = searchText.getText().toString();
        List<Address> addressList = null;

        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);

            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            mGoogleMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        }
    }

    private void setElementLocationOnMapActivity() {
        View mapView = (View) findViewById(R.id.mapView);
        View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();

        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        rlp.setMargins(0, 1250, 180, 0);
    }

    private void initializeSearchPlaceFragment() {
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_fragment);
        autocompleteFragment.setBoundsBias(BOUNDS_MOUNTAIN_VIEW);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
               // Log.i(TAG, "Place: " + place.getName());
                // searchText.setText(place.getName());
                //  onMapSearch(place.getAddress().toString());
                markerOptions = new MarkerOptions().position(place.getLatLng()).title("Marker");
                mGoogleMap.addMarker(markerOptions);
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
              //  Log.i(TAG, "An error occurred: " + status);
            }
        });
    }

    @Override
    public void onPlaceSelected(Place place) {
        //   Log.i(LOG_TAG, "Place Selected: " + place.getName());
       /* locationTextView.setText(getString(R.string.formatted_place_data, place
                .getName(), place.getAddress(), place.getPhoneNumber(), place
                .getWebsiteUri(), place.getRating(), place.getId()));*/
     /*  searchText.setText(getString(R.string.formatted_place_data, place
               .getName(), place.getAddress(), place.getPhoneNumber(), place
               .getWebsiteUri(), place.getRating(), place.getId()));*/
        onMapSearch(place.getName().toString());
        if (!TextUtils.isEmpty(place.getAttributions())) {
            // attributionsTextView.setText(Html.fromHtml(place.getAttributions().toString()));
        }
    }

    @Override
    public void onError(Status status) {
        Log.e("error", "onError: Status = " + status.toString());
        Toast.makeText(this, "Place selection failed: " + status.getStatusMessage(),
                Toast.LENGTH_SHORT).show();
    }
}

