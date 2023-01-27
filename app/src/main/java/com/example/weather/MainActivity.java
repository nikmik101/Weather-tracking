package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, GetDataFromInternet.AsyncResponce {

    private static final String TAG = "MainActivity";

    private Button searButton;
    private EditText searchField;
    private TextView cityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchField = findViewById(R.id.searchField);
        cityName = findViewById(R.id.cityName);
        searButton = findViewById(R.id.buttonSearch);
        searButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        URL url = buildURL(searchField.getText().toString());

        cityName.setText(searchField.getText().toString());

        new GetDataFromInternet(this).execute(url);
    }

    private URL buildURL(String city){

        String BASE_url = "https://api.openweathermap.org/data/2.5/weather";
        String PARAM_CITY = "q";
        String PARAM_APPID = "appid";
        String appid_value = "92be68c2d1910c23c060f9247f6f7031";

        Uri buildUri = Uri.parse(BASE_url).buildUpon().appendQueryParameter(PARAM_CITY, city).appendQueryParameter(PARAM_APPID, appid_value).build();
        URL url = null;

        try {
            url = new URL(buildUri.toString());
        } catch (MalformedURLException e){
            e.printStackTrace();
        }

        return url;
    }

    @Override
    public void processFinish(String output) {
        Log.d(TAG, "processFinish:" + output);
        try {
            JSONObject resulJSON = new JSONObject(output);
            JSONObject wheather = resulJSON.getJSONObject("main");
            JSONObject sys = resulJSON.getJSONObject("sys");

            TextView temp = findViewById(R.id.tempValue);
            String temp_k = wheather.getString("temp");
            float temp_c = Float.parseFloat(temp_k);
            temp_c = temp_c - (float) 273.15;
            String temp_c_string = Float.toString(temp_c);
            temp.setText(temp_c_string);

            TextView pressure = findViewById(R.id.pressureValue);
            pressure.setText(wheather.getString("pressure"));

            TextView sunrise = findViewById(R.id.timeSunrise);
            String timeSunrise = sys.getString("sunrise");
            Locale myLocale = new Locale("ru", "RU");
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss", myLocale);

            String dateString = formatter.format(new Date(Long.parseLong(timeSunrise) * 1000 + (60 * 60 * 1000) * 3));

            sunrise.setText(dateString);

            TextView sunset= findViewById(R.id.timeSunset);
            String timeSunset = sys.getString("sunset");

            dateString = formatter.format(new Date(Long.parseLong(timeSunset) * 1000 + (60 * 60 * 1000) * 3));

            sunset.setText(dateString);

        } catch (JSONException e){
            e.printStackTrace();
        }
    }
}