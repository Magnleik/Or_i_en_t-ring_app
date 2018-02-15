package connection;

import java.util.List;

/**
 * Created by Eirik on 15-Feb-18.
 */

public class NetworkManager {

    static NetworkManager nm;
    static String URL = "URL GOES HERE";

    private NetworkManager(){
    }

    public static NetworkManager getInstance(){
        if(nm == null){
            nm = new NetworkManager();
        }

        return nm;
    }

    /**
     * Adds a point to the database, not affiliated with an Event
     * @param point The Point to be added
     */
    public void addPoint(Point point){

    }

    /**
     * Finds all points within a circle with radius maxDist from the given location
     * @param latitude
     * @param longitude
     * @param maxDist
     * @return
     */
    public List<Point> getPointsNearby(double latitude, double longitude, double maxDist){
        return null;
    }

    /**
     * Finds all points associated with an Event. First element will always be the starting Point
     * @param event
     * @return
     */
    public List<Point> getPointsFromEvent(Event event){
        return null;
    }

    /**
     * Updates the existing event if it exists in the database, will otherwise create it and return the event with its updated information.
     * @param event
     * @return
     */
    public Event updateEvent(Event event){
        return null;
    }
}
