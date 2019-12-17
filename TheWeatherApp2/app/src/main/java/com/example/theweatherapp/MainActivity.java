package com.example.theweatherapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewDebug;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Data.CityPreference;
import Data.JSONWeatherParser;
import Data.WeatherHttpClient;
import Model.Weather;
import Tool.Tools;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private TextView cityName;
    private TextView temp;
    private ImageView iconView;
    private TextView description;
    private TextView humidity;
    private TextView pressure;
    private TextView wind;
    private TextView sunrise;
    private TextView sunset;
    private TextView updated;
    private TextView timeframe;
    Button geoButton;
    public int position=0;
    Weather weather = new Weather();
    List<Weather> weatherList = new ArrayList<Weather>();

    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected Context context;
    TextView txtLat;
    String lat;
    String provider;
    protected String latitude,longitude;
    protected boolean gps_enabled,network_enabled;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        } else {
            ActivityCompat.requestPermissions(this, new String[] {
                            ACCESS_FINE_LOCATION,
                            ACCESS_COARSE_LOCATION },
                    1);

        }
        //Location loca = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        //onLocationChanged(loca);

        cityName = (TextView) findViewById(R.id.mestoText);
        iconView = (ImageView) findViewById(R.id.thumbnail);
        temp = (TextView) findViewById(R.id.teplotaText);
        description = (TextView) findViewById(R.id.mrakText);
        humidity = (TextView) findViewById(R.id.vlhkostText);
        pressure = (TextView) findViewById(R.id.tlakText);
        wind = (TextView) findViewById(R.id.vitrText);
        sunrise = (TextView) findViewById(R.id.SvitaniText);
        sunset = (TextView) findViewById((R.id.SoumrakText));
        updated = (TextView) findViewById(R.id.AktualizaceText);
        timeframe = (TextView) findViewById(R.id.timeframeText);
        geoButton = findViewById(R.id.button1);

        geoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Location loca = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                onLocationChanged(loca);
                Toast.makeText(getApplicationContext(), "Satellites just targeted you", Toast.LENGTH_LONG).show();//display the text of button1
            }
        });


        // zobrazení předpovědi pro více dnů nebo alespon hodin, swipovat na jine hodiny/dny
        //historii oblibene, možno do listu
        //senzory na zasade GPS, ziskat aktualni polohu

        CityPreference cityPreference = new CityPreference(MainActivity.this);
        renderWeatherData("q=" + cityPreference.getCity());

    }
    @Override
    public void onLocationChanged(Location location) {
        /*int lat = (int)location.getLatitude();
        //DecimalFormat df = new DecimalFormat("#.");

            latitude = String.format("%d",lat);

        int lon = (int)location.getLongitude();

            longitude =  String.format("%d",lon);
*/
        Log.d("test before a test","Latitude:" + latitude + ", Longitude:" + longitude);
        txtLat = (TextView) findViewById(R.id.textview1);
        txtLat.setText("Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());
        Log.d("test test test test","Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());
        renderWeatherData("lat="+ location.getLatitude() + "&lon=" + location.getLongitude());
        locationManager.removeUpdates(this);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude","disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude","enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude","status");
    }

    public void renderWeatherData (String city)
    {
        WeatherTask weatherTask = new WeatherTask();
        weatherTask.execute(new String[]{city + "&APPID=" + Tools.API_KEY + "&units=metric"});
    }
    private class DownloadImageAsyncTask extends AsyncTask<String, Void, Bitmap>
    {
        @Override
        protected Bitmap doInBackground(String... params) {
            return downloadImage(params[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            iconView.setImageBitmap(bitmap);
        }

        private Bitmap downloadImage(String code)
        {
            try {
                URL url = new URL(Tools.ICON_URL + code + ".png");
                Log.d("Data : ", url.toString());
                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap currentBitmap = BitmapFactory.decodeStream(input);
                return currentBitmap;
            }
            catch (IOException e)
            {
                e.printStackTrace();
                return null;
            }
        }


    }
    private class WeatherTask extends AsyncTask<String, Void, List<Weather> >{
        //check this wrong thread
        @SuppressLint("WrongThread")
        @Override
        protected List<Weather> doInBackground(String... params) {
            String data = ( (new WeatherHttpClient().getWeatherData(params[0])));
            Log.d("test test test test","Dataaaaaaaaaaaaaaaaaaaaaaaaa = " + data);
            weatherList = JSONWeatherParser.getWeather(data);

            Log.d("test test test test","weather list size = " + weatherList.size());
            for(int i=0; i < weatherList.size();i++)
            {
                Log.d("test test test test","TEPLOOOOTAAAAAA  = " + weatherList.get(i).currentCondition.getTemperature());
            }

            Log.d("test test test test"," LIST code = " + weatherList.get(0));
            if(weatherList.get(0).code.getCode() != 404) {
                    weatherList.get(0).iconData = weatherList.get(0).currentCondition.getIcon();

                    new DownloadImageAsyncTask().execute(weatherList.get(0).iconData);

            }
            else {
                    weatherList.get(0).iconData = "#FF0000";

            }

            return weatherList;
        }
        @Override
        protected void onPostExecute(List<Weather> weatherList) {
            super.onPostExecute(weatherList);
            int i=0;
                if(weatherList.get(i).code.getCode() !=404) {
                    Date currentDate_date = new Date(weatherList.get(i).location.getSunrise() * 1000);
                    String sunriseDate = DateFormat.getInstance().format(currentDate_date);
                    Date sunsetDate_date = new Date(weatherList.get(i).location.getSunset() * 1000);
                    String sunsetDate = DateFormat.getInstance().format(sunsetDate_date);
                    Date updateDate_date = new Date(weatherList.get(i).location.getLastupdate() * 1000);
                    String updateDate = DateFormat.getInstance().format(updateDate_date);
                    //Date timeframe_date = new Date(weatherList.get(i).location.getTimeframe() * 1000);
                    //String timeFrame = DateFormat.getInstance().format(timeframe_date);

                    DecimalFormat decimalFormat = new DecimalFormat("#.#");
                    String tempFormat = decimalFormat.format(weatherList.get(i).currentCondition.getTemperature());
                    timeframe.setText("Timeframe = " + weatherList.get(position).timeframe.getTimeframe());
                    cityName.setText(weatherList.get(i).location.getCity());
                    temp.setText("" + tempFormat + "°C");
                    humidity.setText("Vlhkost: " + weatherList.get(i).currentCondition.getHumidity() + "%");
                    pressure.setText("Tlak: " + weatherList.get(i).currentCondition.getPressure() + "hPa");
                    wind.setText("Vítr: " + weatherList.get(i).wind.getSpeed() + "mps");
                    sunrise.setText("Svítání: " + sunriseDate);
                    sunset.setText("Stmívání " + sunsetDate);
                    updated.setText("Naposledy aktualizováno: " + updateDate);
                    description.setText("Podmínky: " + weatherList.get(i).currentCondition.getCondition() + "(" + weatherList.get(i).currentCondition.getDescription() + ")");
                }
                else{
                    cityName.setText("Spatne mesto");
                    timeframe.setText("Chyba");
                    temp.setText("Chyba");
                    humidity.setText("Chyba");
                    pressure.setText("Chyba");
                    wind.setText("Chyba");
                    sunrise.setText("Chyba");
                    sunset.setText("Chyba");
                    updated.setText("Format mesta : Ostrava,CZ");
                    description.setText("Chyba");

                }

        }

    }
    public void showInputDialog()
    {
        position=0;
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Změnit město");

        final EditText cityInput = new EditText(MainActivity.this);
        cityInput.setInputType(InputType.TYPE_CLASS_TEXT);
        cityInput.setHint("Brno,CZ");
        builder.setView(cityInput);
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CityPreference cityPreference = new CityPreference(MainActivity.this);
                cityPreference.setCity((cityInput.getText().toString()));

                String newCity = cityPreference.getCity();

                renderWeatherData("q=" + newCity);
            }
        });
        builder.show();
    }
    public void goDayBack()
    {

        if(position !=0) {

            position = position -1;
            if(weatherList.get(0).code.getCode() != 404) {
                weatherList.get(position).iconData = weatherList.get(position).currentCondition.getIcon();

                new DownloadImageAsyncTask().execute(weatherList.get(position).iconData);

            }
            else {
                    weatherList.get(position).iconData = "#FF0000";

            }

            Log.d("test test test test","Position  = " + position + "fakin temperature = " + weatherList.get(position));
            DecimalFormat decimalFormat = new DecimalFormat("#.#");

            String tempFormat = decimalFormat.format(weatherList.get(position).currentCondition.getTemperature());

            //Date timeframe_date = new Date(weatherList.get(position).location.getTimeframe() * 1000);
            //String timeFrame = DateFormat.getInstance().format(timeframe_date);

            description.setText("Podmínky: " + weatherList.get(position).currentCondition.getCondition() + "(" + weatherList.get(position).currentCondition.getDescription() + ")");
            timeframe.setText("Timeframe = " + weatherList.get(position).timeframe.getTimeframe());
            temp.setText("" + tempFormat + "°C");
            humidity.setText("Vlhkost: " + weatherList.get(position).currentCondition.getHumidity() + "%");
            pressure.setText("Tlak: " + weatherList.get(position).currentCondition.getPressure() + "hPa");
            wind.setText("Vítr: " + weatherList.get(position).wind.getSpeed() + "mps");
            Log.d("test test test test","Position  = " + position + "fakin temperature = " + tempFormat);
        }
    }
    public void goDayForward()
    {
        if(position<39) {
            position = position + 1;
            if (weatherList.get(0).code.getCode() != 404) {
                weatherList.get(position).iconData = weatherList.get(position).currentCondition.getIcon();

                new DownloadImageAsyncTask().execute(weatherList.get(position).iconData);

            } else {
                weatherList.get(position).iconData = "#FF0000";

            }
            Log.d("test test test test", "Position  = " + position + "fakin temperature = " + weatherList.get(position));
            wind.setText("Vitr = " + weatherList.get(position).wind.getSpeed() + "mps");
            DecimalFormat decimalFormat = new DecimalFormat("#.#");

            //Date timeframe_date = new Date(weatherList.get(position).location.getTimeframe() * 1000);
            //String timeFrame = DateFormat.getInstance().format(timeframe_date);

            Log.d("test test test test", "Position  = " + position + " fakin temperature = " + weatherList.get(position).currentCondition.getDescription());
            String tempFormat = decimalFormat.format(weatherList.get(position).currentCondition.getTemperature());
            description.setText("Podmínky: " + weatherList.get(position).currentCondition.getCondition() + "(" + weatherList.get(position).currentCondition.getDescription() + ")");
            timeframe.setText("Timeframe = " + weatherList.get(position).timeframe.getTimeframe());
            temp.setText("" + tempFormat + "°C");
            humidity.setText("Vlhkost: " + weatherList.get(position).currentCondition.getHumidity() + "%");
            pressure.setText("Tlak: " + weatherList.get(position).currentCondition.getPressure() + "hPa");
            wind.setText("Vítr: " + weatherList.get(position).wind.getSpeed() + "mps");
            Log.d("test test test test", "Position  = " + position + " fakin temperature = " + tempFormat);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id =item.getItemId();
        if(id == R.id.change_mesto)
        {
            showInputDialog();
        }
        if(id == R.id.change_den_zpet)
        {
            goDayBack();
        }
        if(id == R.id.change_den_dalsi)
        {
            goDayForward();
        }
        return super.onOptionsItemSelected(item);
    }
    public boolean onButtonClickListener(){
        return true;
    }
}
