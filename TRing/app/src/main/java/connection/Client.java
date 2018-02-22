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

    @GET
    Call<List<Point>> getNearbyPoints (
            @Path("latitude") double latitude,
            @Path("longitude") double longitude,
            @Path("radius") double radius
    );

    @GET
    Call<List<Event>> getNearbyEvents (
            @Path("latitude") double latitude,
            @Path("longitude") double longitude,
            @Path("radius") double radius
    );

    @GET
    Call<Event> getEventById(
            @Path("ID") int ID
    );

}
