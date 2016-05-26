package homework6.group.runrun;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class RunListActivity extends AppCompatActivity {

    private ListView listView;
    private MyRunListAdapter runListAdapter;

    private RunDB runDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        runDB = new RunDB(getApplicationContext());

        if(runDB.getAll().size() == 0){
            runDB.insert(new RunData("2016-05-22", 500, 20, 5));
            runDB.insert(new RunData("2016-05-23", 130, 40, 2));
            runDB.insert(new RunData("2016-05-24", 250, 80, 6));
            runDB.insert(new RunData("2016-05-25", 370, 10, 1));
        }

        listView = (ListView) findViewById(R.id.listView);
        runListAdapter = new MyRunListAdapter(this, runDB.getAll());
        listView.setAdapter(runListAdapter);

        Button btn_run = (Button) findViewById(R.id.button2);
        btn_run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isOpenGps()){
                    Intent intent = new Intent(RunListActivity.this, MapsActivity.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(RunListActivity.this, "請開啟GPS", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        runListAdapter.setItems(runDB.getAll());
        runListAdapter.notifyDataSetChanged();
    }

    private boolean isOpenGps() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (gps) {
            return true;
        }
        return false;
    }

    class MyRunListAdapter extends BaseAdapter {
        private LayoutInflater myInflater;
        private ArrayList<RunData> items;

        public MyRunListAdapter(Context c, ArrayList<RunData> items){
            myInflater = LayoutInflater.from(c);
            this.items = items;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        public void setItems(ArrayList<RunData> items) {
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = myInflater.inflate(R.layout.run_list, null);
            TextView textView_date = (TextView) convertView.findViewById(R.id.textView5);
            TextView textView_distance = (TextView) convertView.findViewById(R.id.textView6);
            TextView textView_time = (TextView) convertView.findViewById(R.id.textView7);
            TextView textView_speed = (TextView) convertView.findViewById(R.id.textView8);

            textView_date.setWidth(convertView.getWidth()/4);
            textView_distance.setWidth(convertView.getWidth() / 4);
            textView_time.setWidth(convertView.getWidth() / 4);
            textView_speed.setWidth(convertView.getWidth() / 4);

            String[] date = items.get(position).getDate().split("-");
            textView_date.setText(date[0]+"\n"+date[1]+"/"+date[2]);
            textView_distance.setText(items.get(position).getDistance()+" m");
            int min = items.get(position).getTime()/60;
            int second = items.get(position).getTime()%60;
            textView_time.setText(min+"'"+second+"''");
            textView_speed.setText(items.get(position).getSpeed()+" m/s");

            return convertView;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public RunData getItem(int position) {
            return items.get(position);
        }
    }

}
