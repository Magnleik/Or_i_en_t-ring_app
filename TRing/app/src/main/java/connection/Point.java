package connection;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Eirik on 15-Feb-18.
 */

public class Point implements Serializable {

    private int id = -1;
    private Map<String, String> properties;
    private Geometry geometry;

    /**
     *  Constructor to use when creating a new point on the app - when ID is unknown.
     * @param latitude Latitude of the point, in WGS84
     * @param longitude Longitude of the point, in WGS84
     * @param description Description of the point (optional)
     */
    public Point(@NonNull double latitude,@NonNull double longitude, String description){
        geometry = new Geometry();
        geometry.coordinates = new double[]{latitude,longitude};
        properties = new HashMap<>();
        properties.put("description",description);
    }

    /**
     * Constructor ONLY for the server calls. NO NOT use this constructor from the app.
     */
    public Point(){}

    public int getId() {
        return id;
    }

    public double getLatitude() {
        return geometry.coordinates[0];
    }

    public double getLongitude() {
        return geometry.coordinates[1];
    }

    /**
     * The description CAN be NULL, must be handled by the application if this is the case.
     * @return Returns the String representing this point's description, if there is one. Null otherwise.
     */
    public String getDescription() {
        return (String)properties.get("description");
    }

    public void setLatitude(double latitude) {
        geometry.coordinates[0] = latitude;
    }

    public void setLongitude(double longitude) {
        geometry.coordinates[1] = longitude;
    }

    public void setDescription(String description) {
        properties.put("description", description);
    }


    /**
     * Adds any property to this Point. DO NOT set ID, coordinates or description through this method. Will most likely NOT be saved on the server, though it will be sent.
     * @param key The property name, i.e. "point_title". Use lowercase letters and underscores.
     * @param value The value to save to your parameter. Can be anything, and can be retrieved through getProperty.
     */
    public void addProperty(String key, Object value){
        properties.put(key,value);
    }

    /**
     * Retrieve a property value saved with the key. DO NOT use this to get ID, coordinates or description.
     * @param key A String key used to save a property.
     * @return The object saved as a property
     */
    public Object getProperty(String key){
        return properties.get(key);
    }

    private class Geometry implements Serializable{
        double[] coordinates;
    }

}

