package connection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Eirik on 15-Feb-18.
 */

public abstract class Event {

    protected final int id;
    /**
     * The list of all the POSTS, not the starting location. The starting location CAN be a post, but will then be specified in the startPoint variable
     */
    protected ArrayList<Point> posts;
    protected Point startPoint;
    protected float minDistance;
    protected String avgTime;


    /**
     * Constructor for use when creating an Event object from a saved instance in the database
     * @param id Server generated ID for an Event
     * @param posts The different posts added to the event
     * @param startPoint The starting location, Point element
     * @param minDistance The calculated minimum distance between points
     * @param avgTime The average time of this event
     */
    public Event(int id, ArrayList<Point> posts, Point startPoint, float minDistance, String avgTime){
        this.id = id;
        this.posts = posts;
        this.startPoint = startPoint;
        this.minDistance = minDistance;
        this.avgTime = avgTime;
    }

    /**
     * Basic constructor to use when creating an O-Event from the app - no fields will be instantiated
     */
    public Event(){
        this(-1, new ArrayList<Point>(), null, -1, "NaN");
    }


    /**
     * Add a post to this event
     * @param post The Point to be added
     */
    public void addPost(Point post){
        if(!posts.contains(post))
          posts.add(post);
    }

    /**
     * Add a list of points
     * @param posts A list of points to be added, list can be of any type extending Collection
     */
    public void addPosts(Collection<Point> posts){
        posts.addAll(posts);
    }

    /**
     * Removes a post from this event
     * @param post Point to be removed
     */
    public void removePost(Point post){
        posts.remove(post);
    }

    /**
     * Remove all posts from this event
     */
    public void clearPosts(){
        posts.clear();
    }

    public Point getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(Point startPoint) {
        this.startPoint = startPoint;
    }

    public float getMinDistance() {
        return minDistance;
    }

    /**
     * Can be used if there is a wish to calculate the minimum distance before sending the event to the server and receiving it back.
     * @param minDistance Distance in meters
     */
    public void setMinDistance(float minDistance) {
        this.minDistance = minDistance;
    }

    /**
     * String of average time spent on this event
     * @return Returns the avgTime as a string in format hh:mm:ss
     */
    public String getAvgTime() {
        return avgTime;
    }

    /**
     * Sets the average time, any edits will NOT be reflected on the server. Can be used to change the shown text. avgTime is per default set to "NaN"
     * @param avgTime The time in hh:mm:ss
     */
    public void setAvgTime(String avgTime) {
        this.avgTime = avgTime;
    }
}
