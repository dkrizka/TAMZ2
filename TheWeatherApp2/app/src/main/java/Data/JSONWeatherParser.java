package Data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Model.Location;
import Model.Weather;
import Tool.Tools;

public class JSONWeatherParser {

    public static Weather getWeather(String data){
        Weather weather = new Weather();
        try {
            JSONObject jsonObject = new JSONObject(data);
            Location place = new Location();

            JSONObject coordObj = Tools.getObject("coord", jsonObject);
            place.setLat(Tools.getFloat("lat",coordObj));
            place.setLon(Tools.getFloat("lon",coordObj));

            //sys
            JSONObject sysObj = Tools.getObject("sys", jsonObject);
            place.setCountry(Tools.getString("country", sysObj));
            place.setLastupdate(Tools.getInt("dt", jsonObject));
            place.setSunrise(Tools.getInt("sunrise", sysObj));
            place.setSunset(Tools.getInt("sunset", sysObj));
            place.setCity(Tools.getString("name", jsonObject));
            weather.location = place;

            //weather
            JSONArray jsonArray = jsonObject.getJSONArray("weather");
            JSONObject jsonWeather = jsonArray.getJSONObject(0);
            weather.currentCondition.setWeatherId(Tools.getInt("id",jsonWeather));
            weather.currentCondition.setDescription(Tools.getString("description",jsonWeather));
            weather.currentCondition.setCondition(Tools.getString("main",jsonWeather));
            weather.currentCondition.setIcon(Tools.getString("icon",jsonWeather));

            JSONObject mainObj = Tools.getObject("main", jsonObject);
            weather.currentCondition.setHumidity(Tools.getInt("humidity", mainObj));
            weather.currentCondition.setPressure(Tools.getInt("pressure", mainObj));
            weather.currentCondition.setMinTemp(Tools.getFloat("temp_min", mainObj));
            weather.currentCondition.setMaxTemp(Tools.getFloat("temp_max", mainObj));
            weather.currentCondition.setTemperature(Tools.getFloat("temp", mainObj));

            JSONObject windObj = Tools.getObject("wind", jsonObject);
            weather.wind.setSpeed(Tools.getFloat("speed",windObj));
            weather.wind.setDeg(Tools.getFloat("deg",windObj));

            JSONObject cloudObj = Tools.getObject("clouds", jsonObject);
            weather.clouds.setPrecipitation(Tools.getInt("all",cloudObj));

            return weather;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }


    }
}
