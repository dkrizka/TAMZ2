package Data;

import android.util.Log;

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
            url = new URL("http://api.openweathermap.org/data/2.5/weather?q=" + location + "&APPID=a6ef01561f64261f833e988170c4bec9");
            Log.d("test test test test","boiiiiiiiiiiiiiiiiiiiiiiiiiiiii = "+ url);
           // HttpURLConnection connection =(HttpURLConnection) (new URL("http://api.openweathermap.org/data/2.5/weather?q=London,uk&APPID=a6ef01561f64261f833e988170c4bec9")).openConnection();
            urlConnection = (HttpURLConnection) url
                    .openConnection();

            //connection.setRequestMethod("GET");
            //connection.setDoInput(true);
            //connection.setDoInput(true);
            //connection.connect();

            //read the response
            Log.d("test test test test","urlconnection = "+ urlConnection);
            StringBuffer stringBuffer = new StringBuffer();
            inputStream=urlConnection.getInputStream();
            Log.d("test test test test","inputstream = "+inputStream);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            Log.d("test test test test","boiiiiiiiiiiiiiiiiiiiiiiiiiiiii = "+ bufferedReader);
            String line = null;
            while ((line = bufferedReader.readLine())!=null){
                Log.d("HTTPClient", "index=" + line);
                stringBuffer.append(line + "\r\n");
            }

            inputStream.close();
            //connection.disconnect();
            Log.d("test test test test","stringBuffer = "+ stringBuffer);
            Log.d("test test test test","StringBuffertostring = "+ stringBuffer.toString());
            return stringBuffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            Log.d("test test test test","finally = "+ urlConnection);
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

        }return null;

    }
}
