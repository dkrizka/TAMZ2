package com.example.theweatherapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
import java.util.Date;

import Data.CityPreference;
import Data.JSONWeatherParser;
import Data.WeatherHttpClient;
import Model.Weather;
import Tool.Tools;

public class MainActivity extends AppCompatActivity {

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

    Weather weather = new Weather();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        CityPreference cityPreference = new CityPreference(MainActivity.this);

        renderWeatherData(cityPreference.getCity());

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
    private class WeatherTask extends AsyncTask<String, Void, Weather>{
        //check this wrong thread
        @SuppressLint("WrongThread")
        @Override
        protected Weather doInBackground(String... params) {

            String data = ( (new WeatherHttpClient().getWeatherData(params[0])));

            weather = JSONWeatherParser.getWeather(data);
            weather.iconData = weather.currentCondition.getIcon();

            Log.v("Data: ", weather.currentCondition.getDescription());

            new DownloadImageAsyncTask().execute(weather.iconData);

            return weather;
        }
        @Override
        protected void onPostExecute(Weather weather) {
            super.onPostExecute(weather);

            DateFormat dateFormat = DateFormat.getTimeInstance();
            String sunriseDate = dateFormat.format(new Date(weather.location.getSunrise()));
            String sunsetDate = dateFormat.format(new Date(weather.location.getSunset()));
            String updateDate = dateFormat.format(new Date(weather.location.getLastupdate()));

            DecimalFormat decimalFormat = new DecimalFormat("#.#");
            String tempFormat = decimalFormat.format(weather.currentCondition.getTemperature());

            cityName.setText(weather.location.getCity() + "," + weather.location.getCountry());
            temp.setText("" + tempFormat + "°C");
            humidity.setText("Vlhkost: " +  weather.currentCondition.getHumidity() + "%");
            pressure.setText("Tlak: " + weather.currentCondition.getPressure() + "hPa");
            wind.setText("Vítr: " + weather.wind.getSpeed() + "mps");
            sunrise.setText("Svítání: " + sunriseDate );
            sunset.setText("Stmívání " + sunsetDate);
            updated.setText("Naposledy aktualizováno: " + updateDate);
            description.setText("Podmínky: " + weather.currentCondition.getCondition() + "(" + weather.currentCondition.getDescription()+")");

        }

    }
    private void showInputDialog()
    {
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

                renderWeatherData(newCity);
            }
        });
        builder.show();
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
        return super.onOptionsItemSelected(item);
    }
}
