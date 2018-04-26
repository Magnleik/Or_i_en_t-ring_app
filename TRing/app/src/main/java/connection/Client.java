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


    @POST("/api/points")

    Call<List<Point>> addPoints(@Body Point... points);



    @POST("/api/events")

    Call<Event> addEvent(@Body Event events);



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

    @POST("/api/users")
    Call<Boolean> createNewUser(
            @Body User user
    );

    @POST("/api/events/{eventID}/time/{time}")
    Call<Event> postTime(
            @Path("eventID") int eventID,
            @Path("time") String time
    );

    @POST("/api/events/{eventID}/score/{score}")
    Call<Event> postScore(
            @Path("eventID") int eventID,
            @Path("score") int score
    );

    @POST("/api/login")
    Call<Boolean> logIn();

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

    );



    @GET("/api/events/nearby")

    Call<List<Event>> getNearbyEvents (

            @Query("lat") double latitude,

            @Query("lng") double longitude,

            @Query("dist") double radius

    );



    @GET("/api/events/{ID}/points")

    Call<Event> getEventById(

            @Path("ID") int ID

    );

}

