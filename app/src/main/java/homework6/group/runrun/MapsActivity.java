package homework6.group.runrun;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public final static String Location_Send = "location.service.send";

    private GoogleMap mMap;

    private UIReceiver receiver;

    private ArrayList<LatLng> LatLnglist;
    private ArrayList<Float> speedlist;

    private long startTime;

    private RunDB runDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mMap = mapFragment.getMap();
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        runDB = new RunDB(getApplicationContext());

        Intent intent = new Intent(MapsActivity.this, LocationService.class);
        startService(intent);

        registerBroadcastReceiver();

        LatLnglist = new ArrayList<>();
        speedlist = new ArrayList<>();

        startTime = System.currentTimeMillis();

        Button btn_end = (Button) findViewById(R.id.button);
        btn_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RunData runData = new RunData();

                int distance = 0;
                for (int i=0; i<LatLnglist.size(); i++){
                    if(i+1 >= LatLnglist.size())
                        break;

                    float[] results = new float[3];
                    Location.distanceBetween(LatLnglist.get(i).latitude, LatLnglist.get(i).longitude, LatLnglist.get(i+1).latitude, LatLnglist.get(i+1).longitude, results);

                    distance += results[0];
                }
                runData.setDistance(distance);

                float speedtotal = 0; // meters/second
                for(int i=0; i<speedlist.size(); i++){
                    speedtotal += speedlist.get(i);
                }
                runData.setSpeed((int) (speedtotal / speedlist.size()));

                int costTime = (int) ((System.currentTimeMillis() - startTime)/1000);
                runData.setTime(costTime);

                SimpleDateFormat dateStringFormat = new SimpleDateFormat( "yyyy-MM-dd" );
                Date date=new Date();
                String dataStr = dateStringFormat.format(date);
                runData.setDate(dataStr);

                runDB.insert(runData);

                finish();
            }
        });
    }

    public void registerBroadcastReceiver(){
        receiver = new UIReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Location_Send);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
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
//        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    public class UIReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(Location_Send)){
                double newLat = intent.getDoubleExtra("Lat", 0.0);
                double newLong = intent.getDoubleExtra("Long", 0.0);
                float newSpeed = intent.getFloatExtra("Speed", 0.0f);

                Log.d("RLat", newLat+"");
                Log.d("RLong", newLong+"");

                LatLnglist.add(new LatLng(newLat, newLong));
                speedlist.add(newSpeed);

                CameraUpdate center = CameraUpdateFactory.newLatLngZoom(new LatLng(newLat, newLong), 15);
                mMap.animateCamera(center);

                drawMapMark();
            }
        }

        private void drawMapMark(){
            mMap.clear();

            if(LatLnglist.size() > 0){
                MarkerOptions markerOpt = new MarkerOptions();
                markerOpt.position(new LatLng(LatLnglist.get(0).latitude, LatLnglist.get(0).longitude));
                markerOpt.title("起始位置");
                markerOpt.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                mMap.addMarker(markerOpt).showInfoWindow();

                MarkerOptions markerOpt2 = new MarkerOptions();
                markerOpt2.position(new LatLng(LatLnglist.get(LatLnglist.size() - 1).latitude, LatLnglist.get(LatLnglist.size() - 1).longitude));
                markerOpt2.title("目前位置");
                markerOpt2.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                mMap.addMarker(markerOpt2).showInfoWindow();

                if(LatLnglist.size() >= 2){
                    for(int i=0; i<LatLnglist.size(); i++){
                        PolylineOptions polylineOpt = new PolylineOptions();
                        polylineOpt.add(LatLnglist.get(i));
                        if(i+1 < LatLnglist.size()) polylineOpt.add(LatLnglist.get(i+1));
                        else break;
                        polylineOpt.color(Color.BLUE);
                        Polyline polyline = mMap.addPolyline(polylineOpt);
                        polyline.setWidth(10);
                    }
                }
            }
        }
    }
}
