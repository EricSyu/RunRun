package homework6.group.runrun;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by WEI-ZHE on 2016/5/25.
 */
public class PaceChart extends LinearLayout {

    private View vChart;

    private  PaceDB paceDB;

    public PaceChart(Context context) {
        super(context);
        init(context);
    }

    public PaceChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PaceChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public PaceChart(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public void init(Context context){
        paceDB = new PaceDB(context);

        if(paceDB.getAll().size() == 0){
            paceDB.insert(new PaceData("2016-05-22", 130));
            paceDB.insert(new PaceData("2016-05-23", 250));
            paceDB.insert(new PaceData("2016-05-24", 370));
            paceDB.insert(new PaceData("2016-05-25", 512));
        }

        try {
            vChart = getChart(paceDB.getAll());
            removeAllViews();
            addView(vChart, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }catch (Exception e){

        }
    }

    private View getChart(ArrayList<PaceData> array){

        XYSeries series = new XYSeries("Pace");

        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(series);

        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        XYSeriesRenderer yRenderer = new XYSeriesRenderer();
        renderer.addSeriesRenderer(yRenderer);

        renderer.setMarginsColor(0xFFFDFDFD);
        renderer.setTextTypeface(null, Typeface.BOLD);

        renderer.setShowGrid(true);
        renderer.setGridColor(Color.GRAY);

        renderer.setChartTitle("");
        renderer.setLabelsColor(Color.BLACK);
        renderer.setAxesColor(Color.BLACK);
        renderer.setBarSpacing(0.5);
        renderer.setBarWidth(80);

        renderer.setXLabelsColor(Color.BLACK);
        renderer.setYLabelsColor(0, Color.BLACK);
        renderer.setXLabelsAlign(Paint.Align.CENTER);
        renderer.setYLabelsAlign(Paint.Align.CENTER);
//        renderer.setXLabelsAngle(-18);

        renderer.setXLabels(0);
        renderer.setLabelsTextSize(30);
        renderer.setYAxisMin(0);
        renderer.setShowLegend(false);
        renderer.setShowAxes(false);

        yRenderer.setColor(0xff1097ff);
        yRenderer.setDisplayChartValues(true);
        yRenderer.setChartValuesTextSize(30);


//        for (int i=array.size()-1; i>=0; i--){
//            try {
//                String n = date2Day(array.get(i).getDate());
//
//                for (int j=0; j<7; j++){
//                    if(n.equals(week[j])){
//                        pace[j] = array.get(i).getPace();
//                        break;
//                    }
//                }
//            }catch (ParseException e){
//            }
//        }

//        series.add(0, 0);
//        renderer.addXTextLabel(0, "");
//        for (int i=0; i<week.length; i++){
//            renderer.addXTextLabel(i, week[i]);
//            series.add(i, pace[i]);
//            Log.d("date", week[i]);
//            Log.d("pace",  pace[i]+"");
//        }
//        series.add(8, 0);
//        renderer.addXTextLabel(8, "");


        series.add(0, 0);
        renderer.addXTextLabel(0, "");
        for (int i=0; i<array.size(); i++){
            renderer.addXTextLabel(i+1, array.get(i).getDate());
            series.add(i+1, array.get(i).getPace());
        }
        series.add(array.size()+1, 0);
        renderer.addXTextLabel(array.size()+1, "");

        View view = ChartFactory.getBarChartView(getContext(), dataset, renderer, BarChart.Type.DEFAULT);

        return view;
    }

    public String date2Day( String dateString ) throws ParseException
    {
        SimpleDateFormat dateStringFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        Date date = dateStringFormat.parse( dateString );
        SimpleDateFormat date2DayFormat = new SimpleDateFormat( "E" );
        return date2DayFormat.format( date );
    }
}
