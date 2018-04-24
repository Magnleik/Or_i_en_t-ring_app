package no.teacherspet.tring.Database;

import android.arch.persistence.room.TypeConverter;

import com.google.android.gms.maps.model.LatLng;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Hermann on 20.02.2018.
 */

public class DataConverters {

    private final String stringSplit = "¥";

    @TypeConverter
    public String StringFromLatLng(LatLng latLng){
        Double lat = latLng.latitude;
        Double lng = latLng.longitude;
        return lat.toString() + "," + lng.toString();
    }
    @TypeConverter
    public LatLng latLngFromString(String string){
        String[] latlng =  string.split(",");
        return new LatLng(Double.parseDouble(latlng[0]), Double.parseDouble(latlng[1]));
    }
    @TypeConverter
    public Map<String, String> MapFromString(String string){
        Map<String, String> properties = new HashMap<>();
        String[] fromString = string.split(stringSplit);
        for (int i = 0; i < fromString.length; i+=2) {
            properties.put(fromString[i], fromString[i+1]);
        }
        return properties;
    }
    @TypeConverter
    public String StringFromMap(Map<String, String> map){
        String properties = "";
        for(String key : map.keySet()){
            String entry = map.get(key);
            properties = properties.concat(key+ stringSplit + entry + stringSplit);
        }
        return properties;
    }

}
