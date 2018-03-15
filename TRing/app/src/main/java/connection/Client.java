package connection;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by Eirik on 20-Feb-18.
 */

public interface Client {

    //region test-calls
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

    //endregion

    @POST("/api/points")
    Call<List<Point>> addPoints(@Body Point... points);

    @POST("/api/events")
    Call<Event> addEvent(@Body Event events);

    @PUT("/api/events/{ID}")
    Call<Event> updateEvent(
            @Path("ID") int ID,
            @Body Event event
    );

    @POST("/api/events/{ID}/points")
    Call<Event> addPointsToEvent(
            @Path("ID") int eventID,
            @Body Point... points
    );

    @GET("/api/points/nearby")
    Call<List<Point>> getNearbyPoints (
            @Body double latitude,
            @Body double longitude,
            @Body double radius
    );

    @GET("/api/events/nearby")
    Call<List<Event>> getNearbyEvents (
            @Body double latitude,
            @Body double longitude,
            @Body double radius
    );

    @GET("/api/events/{ID}/points")
    Call<Event> getEventById(
            @Path("ID") int ID
    );

}
