package sg.edu.np.twq2.gmap2;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    boolean mLocationPermissionGranted;
    private GoogleMap mMap;
    final int LOCATION_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        moveToPos(-34,151,"Marker in Sydney");

        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            requestPermission(Manifest.permission.ACCESS_FINE_LOCATION,
                    101);
        }

    }

    public void moveToPos(double lat, double lng, String title)
    {
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions().position(sydney).title(title));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));
    }

    public void search(View v)
    {
        List<Address> geocodeMatches = null;
        try {
            geocodeMatches = new Geocoder(this).getFromLocationName (
                    ((EditText)findViewById(R.id.editText)).getText().toString(), 1);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (!geocodeMatches.isEmpty())
        {
            double latitude = geocodeMatches.get(0).getLatitude();
            double longitude = geocodeMatches.get(0).getLongitude();

            moveToPos(latitude, longitude, ((EditText)findViewById(R.id.editText)).getText().toString());
        }

    }

    public void locate(View v)
    {
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            LocationManager locMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria crit = new Criteria();
            Location loc = locMan.getLastKnownLocation(locMan.getBestProvider(crit, false));

            CameraPosition camPos = new CameraPosition.Builder()
                    .target(new LatLng(loc.getLatitude(), loc.getLongitude()))
                    .zoom(12.8f)
                    .build();

            CameraUpdate camUpdate = CameraUpdateFactory.newCameraPosition(camPos);
            mMap.moveCamera(camUpdate);
        } else {
            requestPermission(Manifest.permission.ACCESS_FINE_LOCATION,
                    101);
        }


    }

    protected void requestPermission(String permissionType, int requestCode)
    {
        ActivityCompat.requestPermissions(this, new String[]{permissionType}, requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                if (grantResults.length == 0 || grantResults[0]
                        !=  PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Unable to show location - permission required",
                            Toast.LENGTH_LONG).show();
                } else {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }


}
