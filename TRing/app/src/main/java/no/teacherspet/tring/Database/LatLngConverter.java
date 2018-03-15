package no.teacherspet.tring.Database;

import android.arch.persistence.room.TypeConverter;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Hermann on 20.02.2018.
 */

public class LatLngConverter {

    @TypeConverter
    public String fromLatLng(LatLng latLng){
        Double lat = latLng.latitude;
        Double lng = latLng.longitude;
        return lat.toString() + "," + lng.toString();
    }
    @TypeConverter
    public LatLng fromString(String string){
        String[] latlng =  string.split(",");
        return new LatLng(Double.parseDouble(latlng[0]), Double.parseDouble(latlng[1]));
    }
}
