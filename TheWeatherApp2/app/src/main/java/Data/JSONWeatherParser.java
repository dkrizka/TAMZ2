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
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
