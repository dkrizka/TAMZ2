package Data;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.Buffer;

import Tool.Tools;

public class WeatherHttpClient {

    public String getWeatherData(String location) {
        InputStream inputStream = null;
        URL url;
        HttpURLConnection urlConnection = null;


        try {
            url = new URL("http://api.openweathermap.org/data/2.5/forecast?" + location + "&APPID=a6ef01561f64261f833e988170c4bec9");

            urlConnection = (HttpURLConnection) url.openConnection();

            Log.d("testik","URLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL = " + urlConnection);

            StringBuffer stringBuffer = new StringBuffer();
            inputStream=urlConnection.getInputStream();
            Log.d("testik","inpuuuuuuuuuuuuut = " + inputStream);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            ;
            String line = null;
            while ((line = bufferedReader.readLine())!=null){
                stringBuffer.append(line + "\r\n");
            }
            Log.d("testik","OK_String Buffer= " + stringBuffer);

            inputStream.close();


            return stringBuffer.toString();
        } catch (IOException e) {
            StringBuffer errorBuffer = new StringBuffer();
            errorBuffer.append("{\"cod\":\"404\",\"message\":\"city not found\"}");

            Log.d("testik","ERROR Buffer= " + errorBuffer);
            return errorBuffer.toString();
        }
        finally {
            Log.d("test test test test", "finally = " + urlConnection);
            if (urlConnection != null) {
                urlConnection.disconnect();
            }


        }

    }
}
