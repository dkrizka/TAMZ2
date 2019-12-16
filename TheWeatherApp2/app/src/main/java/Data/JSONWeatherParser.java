package Data;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.SimpleTimeZone;

import Model.Location;
import Model.Weather;
import Tool.Tools;

public class JSONWeatherParser {

    public static List<Weather> getWeather(String data){

        List<Weather> weatherList = new ArrayList<Weather>();
        try {
            JSONObject jsonObject_pre = new JSONObject(data);

            String tagname = "0";
            JSONArray forecastArray = jsonObject_pre.getJSONArray("list");
            Location place = new Location();
            int code = Tools.getInt("cod", jsonObject_pre);

            if (code != 404) {
                JSONObject cityObj = Tools.getObject("city", jsonObject_pre);
                place.setCity(Tools.getString("name",cityObj));

                place.setSunrise(Tools.getInt("sunrise", cityObj));
                place.setSunset(Tools.getInt("sunset", cityObj));
                JSONObject coordObj = Tools.getObject("coord",cityObj);
                place.setCountry(Tools.getString("country", cityObj));
                place.setLat(Tools.getFloat("lat", coordObj));
                place.setLon(Tools.getFloat("lon", coordObj));
                //pole 39 hodnot v bulku
                for(int i=0; i < forecastArray.length();i++) {
                    Weather weather = new Weather();
                    JSONObject jsonObject= forecastArray.getJSONObject(i);


                    place.setLastupdate(Tools.getInt("dt", jsonObject));
                    Log.d("Testing","Index = " + i + " a LAST UPDATEEEE = " + place.getLastupdate());
                    weather.timeframe.setTimeframe(Tools.getString("dt_txt",jsonObject));
                    Log.d("Testing","Index = " + i + " a TIMEFRAAMEEEE = " + weather.timeframe.getTimeframe());
                    weather.location = place;
                    weather.code.setCode(code);

                    //weather
                    JSONArray jsonArray = jsonObject.getJSONArray("weather");
                    JSONObject jsonWeather = jsonArray.getJSONObject(0);
                    weather.currentCondition.setWeatherId(Tools.getInt("id", jsonWeather));
                    weather.currentCondition.setDescription(Tools.getString("description", jsonWeather));
                    weather.currentCondition.setCondition(Tools.getString("main", jsonWeather));
                    weather.currentCondition.setIcon(Tools.getString("icon", jsonWeather));

                    JSONObject mainObj = Tools.getObject("main", jsonObject);
                    weather.currentCondition.setHumidity(Tools.getInt("humidity", mainObj));
                    weather.currentCondition.setPressure(Tools.getInt("pressure", mainObj));
                    weather.currentCondition.setMinTemp(Tools.getFloat("temp_min", mainObj));
                    weather.currentCondition.setMaxTemp(Tools.getFloat("temp_max", mainObj));
                    weather.currentCondition.setTemperature(Tools.getFloat("temp", mainObj));

                    JSONObject windObj = Tools.getObject("wind", jsonObject);
                    weather.wind.setSpeed(Tools.getFloat("speed", windObj));
                    weather.wind.setDeg(Tools.getFloat("deg", windObj));

                    JSONObject cloudObj = Tools.getObject("clouds", jsonObject);
                    weather.clouds.setPrecipitation(Tools.getInt("all", cloudObj));
                    //Log.d("Testing","Index = " + i + " a Temperature = " +  weather.currentCondition.getTemperature());
                    //Todo zapsani do listu
                    //weatherList.add(i,weather);
                    weatherList.add(weather);
                    //weatherList.set(i,weather);

                    //Log.d("Testing","Index = " + i + " a List = " +  weatherList);
                    //Log.d("Testing","Index = " + i + " a List = " +  weatherList.get(i));
                }
                for(int i=0; i < weatherList.size();i++)
                {
                    Log.d("test test test test","TEPLOOOOTAAAAAA  = " + weatherList.get(i).currentCondition.getTemperature());
                }

            }

            return weatherList;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }


    }
}
