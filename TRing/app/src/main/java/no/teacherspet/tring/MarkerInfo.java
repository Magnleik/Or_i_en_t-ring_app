package no.teacherspet.tring;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by magnus on 27.02.2018.
 */

public class MarkerInfo {
    private String markerName;
    private LatLng position;
    public MarkerInfo(String markerName, LatLng position){
        setPosition(position);
        setMarkerName(markerName);
    }
    public void setMarkerName(String name){
        markerName=name;
    }

    public String getMarkerName(){return markerName;}

    public void setPosition(LatLng position){
        this.position=position;
    }

    public LatLng getPosition(){return position;}
}
