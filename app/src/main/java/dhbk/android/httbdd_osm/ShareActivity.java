package dhbk.android.httbdd_osm;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;


public class ShareActivity extends AppCompatActivity {
    LocationManager locationManager;
    RoadManager roadManager = new OSRMRoadManager(this);
    ArrayList<GeoPoint> route;
    Marker mStart, mEnd;
    MapView map;
    LocationListener locationListenerGPS, locationListenerNetWork;
    private IMapController mapController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        map = (MapView) findViewById(R.id.map);

        map.setTileSource(TileSourceFactory.MAPNIK);

        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        mapController = map.getController();
        mapController.setZoom(18);
        GeoPoint startPoint = new GeoPoint(10.772241, 106.657676);
        mapController.setCenter(startPoint);
        route = new ArrayList<GeoPoint>();
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        mStart = new Marker(map);
        Drawable startIcon = getResources().getDrawable(R.drawable.start);
        mStart.setIcon(startIcon);
        mEnd = new Marker(map);
        Drawable end = getResources().getDrawable(R.drawable.end);
        mEnd.setIcon(end);


        locationListenerNetWork = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                GeoPoint temp = new GeoPoint(location.getLatitude(), location.getLongitude(), 16);
                // Toast.makeText(getBaseContext(), String.valueOf(location.getLatitude()), Toast.LENGTH_SHORT).show();
                mapController.setCenter(temp);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        locationListenerGPS = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                GeoPoint temp = new GeoPoint(location.getLatitude(), location.getLongitude(), location.getAltitude());
                route.add(temp);
                Road road = new Road(route);
                Polyline line = roadManager.buildRoadOverlay(road, getBaseContext());
                map.getOverlays().add(line);
                map.invalidate();
                if (route.size() == 1) {
                    mStart.setPosition(temp);
                    map.getOverlays().add(mStart);
                    map.invalidate();
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        locationManager.removeUpdates(locationListenerNetWork);
                    }

                } else if (route.size() > 2) {
                    if (mEnd.getPosition() != null)
                        map.getOverlays().remove(mEnd);
                    mEnd.setPosition(temp);
                    map.getOverlays().add(mEnd);
                    map.invalidate();
                }

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 10000, 0, locationListenerNetWork);
            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 0, locationListenerGPS);
            map.getOverlays().clear();
        }

    }
}
