package no.teacherspet.tring;

import java.util.ArrayList;

/**
 * Created by magnus on 01.03.2018.
 */

public class Event {
    private ArrayList<MarkerInfo> points;
    private String eventName;

    public ArrayList<MarkerInfo> getPoints() {
        return points;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setPoints(ArrayList<MarkerInfo> points) {
        this.points = points;
    }
}
