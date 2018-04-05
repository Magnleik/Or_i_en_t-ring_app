package connection;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Eirik on 20-Feb-18.
 */

public interface Client {

    @GET("/api/test/point/{ID}")
    Call<Point> testGetPointByID(
            @Path("ID") int pointID
    );

    @GET("/")
    Call<List<String>> getTestStrings(
            /*@Path("FILLINPLS") int testStringID*/
    );

    @GET("/api/test/event/{ID}")
    Call<Event> testGetEventByID(
            @Path("ID") int eventID
    );

    @POST("/api/test/point")
    Call<Point> testCreatePoint(@Body Point point);

    @POST("/api/test/event")
    Call<Event> testCreateEvent(@Body Event event);

    @POST
    Call<Point> addPoint(@Body Point point);

    @POST
    Call<Event> addEvent(@Body Event event);

    @PUT
    Call<Event> updateEvent(@Body Event event);

<<<<<<< HEAD
    @GET
    Call<List<Point>> getNearbyPoints (
            @Path("latitude") double latitude,
            @Path("longitude") double longitude,
            @Path("radius") double radius
=======
    @PUT("/api/events/{eventID}/properties")
    Call<Event> updateEventProperties(
            @Path("eventID") int id,
            @Body Map<String,String> properties
    );

    @POST("/api/events/{ID}/points")
    Call<Void> addPointsToEvent(
            @Path("ID") int eventID,
            @Body Point... points
    );

    @DELETE("/api/events/{eventID}/points/{pointID}")
    Call<Void> removePointFromEvent(
            @Path("eventID") int eventID,
            @Path("pointID") int pointID
    );

    @PUT ("/api/points")
    Call<Point> updatePoint(
            @Body Point point
    );

    @GET("/api/points/nearby")
    Call<List<Point>> getNearbyPoints (
            @Query("lat") double latitude,
            @Query("lng") double longitude,
            @Query("dist") double radius
>>>>>>> refs/remotes/origin/eirik/network
    );

    @GET
    Call<List<Event>> getNearbyEvents (
<<<<<<< HEAD
            @Path("latitude") double latitude,
            @Path("longitude") double longitude,
            @Path("radius") double radius
=======
            @Query("lat") double latitude,
            @Query("lng") double longitude,
            @Query("dist") double radius
>>>>>>> refs/remotes/origin/eirik/network
    );

    @GET
    Call<Event> getEventById(
            @Path("ID") int ID
    );

}
