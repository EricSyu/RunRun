package homework6.group.runrun;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private PaceDB paceDB;

    private PaceChart paceChart;
    private TextView textView_avgPace, textView_totalPace;

    private static final int REQUEST_LOCATION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        paceDB = new PaceDB(getApplicationContext());

        paceChart = (PaceChart) findViewById(R.id.chart);
        textView_avgPace = (TextView) findViewById(R.id.textView2);
        textView_totalPace = (TextView) findViewById(R.id.textView4);

        Intent intent = new Intent(MainActivity.this, PaceService.class);
        startService(intent);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(MainActivity.this, RunListActivity.class);
                startActivity(intent2);
            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    finish();
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        paceChart.init(this);

        calTotalPace();
    }

    public void calTotalPace(){
        ArrayList<PaceData> paceDatas = paceDB.getAll();
        int totalPace = 0;
        if(paceDatas.size() != 0){
            for(int i=0; i<paceDatas.size(); i++){
                totalPace += paceDatas.get(i).getPace();
            }
            textView_avgPace.setText(totalPace/paceDatas.size()+"");
            textView_totalPace.setText(totalPace+"");
        }
    }
}
