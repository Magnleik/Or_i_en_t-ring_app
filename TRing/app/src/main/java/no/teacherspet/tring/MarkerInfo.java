package no.teacherspet.tring;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by magnus on 27.02.2018.
 */

public class MarkerInfo implements Serializable{
    private String markerName;
    private LatLng position;
    public void setMarkerName(String name){
        markerName=name;
    }

    public String getMarkerName(){return markerName;}

    public void setPosition(LatLng position){
        this.position=position;
    }

    public LatLng getPosition(){return position;}
}
