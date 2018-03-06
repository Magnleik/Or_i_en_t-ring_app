package connection;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Eirik on 15-Feb-18.
 */

public class Event {

    @SerializedName("features")
    private ArrayList<Point> points;
    private Map<String, Object> properties;
    private int id = -1;


    /**
     * Constructor for use when creating an Event object from a saved instance in the database
     * @param id Server generated ID for an Event
     * @param points The different Points added to the event. The first point will be counted as the starting position
     * @param minDistance The calculated minimum distance between points
     * @param avgTime The average time of this event
     */
    public Event(int id, ArrayList<Point> points, double minDistance, String avgTime){
        this.id = id;
        this.points = points;
        properties.put("dist", minDistance);
        properties.put("avg_time", avgTime);
    }

    /**
     * Basic constructor to use when creating an O-Event from the app - no fields will be instantiated
     */
    public Event(){}


    /**
     * Add a post to this event, duplicated Points will not be added.
     * @param post The Point to be added
     */
    public void addPost(Point post){

        if(points == null){ //Instantiate the array.
            points = new ArrayList<>();
            points.add(null); //Add a placeholder location for the starting position;
        }

        if(!points.contains(post))
          points.add(post);
    }

    /**
     * Add a list of posts, duplicated Points will not be added (Event will never have more than one instance of each Point)
     * @param posts A list of points to be added, list can be of any type extending Collection
     */
    public void addPosts(Collection<Point> posts){

        if(points == null){ //Instantiate the array.
            points = new ArrayList<>();
            points.add(null); //Add a placeholder location for the starting position;
        }

        for (Point p : posts) {
            if(!points.contains(p))
                points.add(p);
        }
    }

    /**
     * Removes a post from this event
     * @param post Point to be removed
     */
    public void removePost(Point post){
        points.remove(post);
    }

    /**
     * Removes a post from this event
     * @param index The index of the Point to be removed
     */
    public void removePost(int index){
        if(points.size()>=index){
            points.remove(index);
        }
    }

    /**
     * Remove all posts from this event, starting position will persist.
     */
    public void clearPosts(){
        Point startPoint = points.get(0);
        points.clear();
        points.add(startPoint);
    }

    public Point getStartPoint() {
        if (points == null){
            return null;
        }
        return points.get(0);
    }

    /**
     * Sets the starting location for this Event. Overrides any existing starting point. Is saved as the first point in the list of all points.
     * @param startPoint The Point you wish to add as a starting location
     */
    public void setStartPoint(Point startPoint) {

        if(points == null){ //Instantiate the array.
            points = new ArrayList<>();
            points.add(null); //Add a placeholder location for the starting position;
        }

        points.set(0, startPoint);
    }

    public int getId(){
        return this.id;
    }

    public double getMinDistance() {
        return (double) properties.get("dist");
    }

    /**
     * Can be used if there is a wish to calculate the minimum distance before sending the event to the server and receiving it back.
     * @param minDistance Distance in meters
     */
    public void setMinDistance(double minDistance) {
        properties.put("dist", minDistance);
    }

    /**
     * String of average time spent on this event
     * @return Returns the avgTime as a string in format hh:mm:ss
     */
    public String getAvgTime() {
        return (String) properties.get("avg_time");
    }


    /**
     * Get all the points in this event, the first point is always the starting location.
     * @return ArrayList of all Points associated with the Event. Can be NULL if no points have been added yet.
     */
    public ArrayList<Point> getPoints(){
        return points;
    }
    

    /**
     * Adds any property to this Event. DO NOT set ID, minDistance or avgTime through this method. Will most likely NOT be saved on the server, though it will be sent.
     * @param key The property name, i.e. "event_title". Use lowercase letters and underscores.
     * @param value The value to save to your parameter. Can be anything, and can be retrieved through getProperty.
     */
    public void addProperty(String key, Object value){
        if(properties==null){
            properties=new HashMap<>();
        }
        properties.put(key,value);
    }

    /**
     * Retrieve a property value saved with the key. DO NOT use this to get ID, minDistance or avgTime.
     * @param key A String key used to save a property.
     * @return The object saved as a property
     */
    public Object getProperty(String key){
        return properties.get(key);
    }
}
