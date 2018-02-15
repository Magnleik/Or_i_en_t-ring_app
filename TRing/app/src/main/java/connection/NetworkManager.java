package connection;

import java.util.List;

/**
 * Created by Eirik on 15-Feb-18.
 */

public class NetworkManager {

    static private NetworkManager nm;
    static private String URL = "URL GOES HERE";

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
     * @param latitude The latitude of given position
     * @param longitude The longitude of given position
     * @param maxDist The radius of search
     * @return A List of Points within the given circle
     */
    public List<Point> getPointsNearby(double latitude, double longitude, double maxDist){
        return null;
    }

    /**
     * Finds all points associated with an Event. First element will always be the starting Point
     * @param event The Event from which you want all points.
     * @return A List of Points associated with the given event in the database, first point is the starting point
     */
    public List<Point> getPointsFromEvent(Event event){
        return null;
    }

    /**
     * Updates the existing event if it exists in the database, will otherwise create it and return the event with its updated information.
     * @param event The Event you wish to update in the database
     * @return Returns the updated event
     */
    public Event updateEvent(Event event){
        return null;
    }

    /**
     * Finds and returns all events with starting location within a radius maxDist from the given location
     * @param latitude The latitude of given position
     * @param longitude The longitude of given position
     * @param maxDist The radius of search
     * @return Returns all events starting within the given circle
     */
    public List<Event> getNearbyEvents(double latitude, double longitude, double maxDist){
        return null;
    }

    /**
     * Returns the event with the given ID if it exists, null otherwise.
     * @param id The ID of the Event you wish to have returned.
     * @return Returns the Event with the given ID.
     */
    public Event getEventById(int id){
        return null;
    }


}
