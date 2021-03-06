package dhbk.android.httbdd_osm;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DirectionActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    // Phong
    private static final String TAG = DirectionActivity.class.getName();
    private GoogleApiClient mGoogleApiClient;
    private IMapController mIMapController;
    private MapView mMapView;
    private FloatingActionButton mFloatingActionButton;
    private CoordinatorLayout mCoordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);

        // Phong - show the map + add 2 zoom button + zoom at a default view point
        mMapView = (MapView) findViewById(R.id.map); // map
        mMapView.setTileSource(TileSourceFactory.MAPNIK);
        mMapView.setBuiltInZoomControls(true);
        mMapView.setMultiTouchControls(true);
        mIMapController = mMapView.getController(); // map controller
        mIMapController.setZoom(10);
        GeoPoint startPoint = new GeoPoint(10.772241, 106.657676);
        mIMapController.setCenter(startPoint);

        // Phong - add GOogle Api to get the current location
        buildGoogleApiClient();

        // Phong - when click FAB, animate to user's current location and zoom.
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGoogleApiClient.isConnected()) {
                    // Phong - animate to user's current location and zoom.
                    if (ActivityCompat.checkSelfPermission(getApplication(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    Location userCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    if (userCurrentLocation != null) {
                        GeoPoint userCurrentPoint = new GeoPoint(userCurrentLocation.getLatitude(), userCurrentLocation.getLongitude());
                        mIMapController.setCenter(userCurrentPoint);
                        mIMapController.zoomTo(mMapView.getMaxZoomLevel());
                        Marker hereMarker = new Marker(mMapView);
                        hereMarker.setPosition(userCurrentPoint);
                        hereMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                        // TODO: 3/25/16 fix this getDrawable
                        hereMarker.setIcon(ContextCompat.getDrawable(getApplication(), R.drawable.ic_face_black_24dp));
                        hereMarker.setTitle("You here");
                        mMapView.getOverlays().add(hereMarker);
                        mMapView.invalidate();
                    } else {
                        Snackbar snackbar = Snackbar.make(mCoordinatorLayout, R.string.no_location_detected, Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                } else {
                    Snackbar snackbar = Snackbar.make(mCoordinatorLayout, R.string.google_not_connected, Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            }
        });
    }

    // Phong - use Location service
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Phong
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Phong
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    // Phong
    @Override
    public void onConnected(Bundle connectionHint) {
        // TODO: 3/24/16 request requently update from google.

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }
}
