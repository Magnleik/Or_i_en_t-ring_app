package no.teacherspet.tring.Database;

import android.arch.persistence.room.TypeConverter;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Hermann on 20.02.2018.
 */

public class LatLngConverter {

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
    public Map MapFromString(String string){
        Map<String, String> properties = new HashMap<>();


        return properties;
    }
    @TypeConverter
    public String StringFromMap(HashMap map){
        //for(String key : map.keySet()){
        //    String entry = map.get(key);
        //}
        return null;
    }


}
