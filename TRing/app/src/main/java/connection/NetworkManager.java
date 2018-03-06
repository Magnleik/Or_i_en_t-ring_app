package connection;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.internal.zzp;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Eirik on 15-Feb-18.
 */

public class NetworkManager {

    static private NetworkManager nm;
    private Client client;

    private NetworkManager(){
        init();
    }

    public static NetworkManager getInstance(){
        if(nm == null){
            nm = new NetworkManager();
        }

        return nm;
    }

    private void init(){

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        String URL = "http://10.22.31.35";
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(
                        GsonConverterFactory.create()
                );

        Retrofit retrofit = builder
                .client(
                        httpClient.build()
                )
                .build();

        client = retrofit.create(Client.class);

        //Just here for testing:

        connectionStringTest();
        connectionPointTest();
        connectionEventTest();
        sendPointTest();
        sendEventTest(new ICallbackAdapter<Event>() {
            @Override
            public void onResponse(Event object) {

            }

            @Override
            public void onFailure(Throwable t) {

            }
        });

        //System.out.println("From the init, we get a event ID of: " + event.getId());
    }

    //region Testing methods
    private void connectionPointTest(){

        Call<Point> call =
                client.testGetPointByID(100);

        call.enqueue(new Callback<Point>() {
            @Override
            public void onResponse(@NonNull Call<Point> call, @NonNull Response<Point> response) {
                Point point = response.body();
                System.out.println("We have a point!");
                System.out.println("It's ID is: " + point.getId());
            }

            @Override
            public void onFailure(@NonNull Call<Point> call, @NonNull Throwable t) {
                System.out.println("Point test has crashed and burned");
                Log.e("NETWORK", t.getMessage(), t);
            }
        });

    }

    private void connectionEventTest(){

        Call<Event> call =
                client.testGetEventByID(0);

        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
                Event event = response.body();
                System.out.println("We have an event!");
                System.out.println("It's ID is: " + event.getId());
            }

            @Override
            public void onFailure(@NonNull Call<Event> call, @NonNull Throwable t) {
                System.out.println("Event test has crashed and burned");
                Log.e("NETWORK", t.getMessage(), t);
            }
        });

    }

    private void connectionStringTest(){

        Call<List<String>> call =
                client.getTestStrings();

        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(@NonNull Call<List<String>> call, @NonNull Response<List<String>> response) {
                System.out.println("Call Test successful, strings: " + response.body());
            }

            @Override
            public void onFailure(@NonNull Call<List<String>> call, @NonNull Throwable t) {
                System.out.println("Call Test failed");
                Log.e("NETWORK", t.getMessage(), t);
            }
        });

    }

    private void sendPointTest(){

        Point testPoint =  new Point(10.324, 20.420, "This is a test point");

        Call<Point> call = client.testCreatePoint(testPoint);

        call.enqueue(new Callback<Point>() {
            @Override
            public void onResponse(@NonNull Call<Point> call, @NonNull Response<Point> response) {
                Point resp = response.body();
                System.out.println("Call sent");
            }

            @Override
            public void onFailure(@NonNull Call<Point> call, @NonNull Throwable t) {
                System.out.println("Call not sent");
                Log.e("NETWORK", t.getMessage(), t);
            }
        });

    }

    private void sendEventTest(final ICallbackAdapter<Event> callback){

        Point testPoint1 =  new Point(10.324, 20.420, "This is a test point");
        Point testPoint2 = new Point(123.321, 12.123, "Test point #2");
        Point testPoint3 = new Point(0.0, 0.0, "This is a starting point");
        ArrayList<Point> points = new ArrayList<Point>();
        points.add(testPoint1); points.add(testPoint2);
        Event testEvent = new Event();
        testEvent.addPosts(points);
        testEvent.setStartPoint(testPoint3);
        testEvent.addProperty("test_latlng", new LatLng(34.04,35.05));
        testEvent.addProperty("test_point", new Point(12.3,32.1, "This is a point from a property"));

        Call<Event> call = client.testCreateEvent(testEvent);

        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
                Event resp = response.body();
                System.out.println("Call sent");
                callback.onResponse(resp);
            }

            @Override
            public void onFailure(@NonNull Call<Event> call, @NonNull Throwable t) {
                System.out.println("Call not sent");
                Log.d("sendEventTest_ERROR", t.getMessage());
            }
        });
    }
    //endregion

    //region POST-methods
    /**
     * Adds a point to the database, not affiliated with an Event
     * @param point The Point to be added
     * @param callback The callback to handle results. Override its methods to get what you need. onResponse gets a Point
     */
    public void addPoint(Point point, final ICallbackAdapter<Point> callback){
        Call<Point> call = client.addPoint(point);

        call.enqueue(new Callback<Point>() {
            @Override
            public void onResponse(@NonNull Call<Point> call, @NonNull Response<Point> response) {

                if(!response.isSuccessful()){
                    Log.i("NETWORK", "addPoint got onResponse, without success");
                }
                else {
                    Log.i("NETWORK", "addPoint successfull with response: " + response.toString());
                }

                callback.onResponse(response.body());

            }

            @Override
            public void onFailure(@NonNull Call<Point> call, @NonNull Throwable t) {
                Log.e("NETWORK", t.getMessage(), t);
                callback.onFailure(t);
            }
        });
    }

    /**
     * ADDS an Event to the database, will not update existing events. Ignores set ID.
     * @param event The Event to add to the database
     * @param callback The callback to handle results. Override its methods to get what you need. onResponse gets the same Event, with updated information from the database - including its generated ID.
     */
    public void addEvent(Event event, final ICallbackAdapter<Event> callback){
        Call<Event> call = client.addEvent(event);

        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
                if(!response.isSuccessful()){
                    Log.i("NETWORK", "addEvent got onResponse, without success");
                }
                else {
                    Log.i("NETWORK", "addEvent successfull with response: " + response.toString());
                }

                callback.onResponse(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<Event> call, @NonNull Throwable t) {
                Log.e("NETWORK", t.getMessage(), t);
                callback.onFailure(t);
            }
        });

    }

    //endregion

    //region GET-methods

    /**
     * Finds all points within a circle with radius maxDist from the given location
     * @param latitude The latitude of given position
     * @param longitude The longitude of given position
     * @param maxDist The radius of search
     * @param callback The callback to handle results. Override its methods to get what you need. onResponse gets an ArrayList of Points within the given circle
     */
    public void getPointsNearby(double latitude, double longitude, double maxDist, final ICallbackAdapter<ArrayList<Point>> callback){

        Call<List<Point>> call = client.getNearbyPoints(latitude,longitude,maxDist);

        call.enqueue(new Callback<List<Point>>() {
            @Override
            public void onResponse(@NonNull Call<List<Point>> call, @NonNull Response<List<Point>> response) {
                if(!response.isSuccessful()){
                    Log.i("NETWORK", "getPointsNearby got onResponse, without success");
                }
                else {
                    Log.i("NETWORK", "getPointsNearby successfull with response: " + response.toString());
                }

                callback.onResponse((ArrayList<Point>) response.body());
            }

            @Override
            public void onFailure(@NonNull Call<List<Point>> call, @NonNull Throwable t) {
                Log.e("NETWORK", t.getMessage(), t);
                callback.onFailure(t);
            }
        });

    }


    /**
     * Finds and returns all events with starting location within a radius maxDist from the given location
     * @param latitude The latitude of given position
     * @param longitude The longitude of given position
     * @param maxDist The radius of search
     * @param callback The callback to handle results. Override its methods to get what you need. onResponse gets all events starting within the given circle
     */
    public void getNearbyEvents(double latitude, double longitude, double maxDist, final ICallbackAdapter<ArrayList<Event>> callback){
        Call<List<Event>> call = client.getNearbyEvents(latitude,longitude,maxDist);

        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(@NonNull Call<List<Event>> call, @NonNull Response<List<Event>> response) {
                if(!response.isSuccessful()){
                    Log.i("NETWORK", "getNearbyEvents got onResponse, without success");
                }
                else {
                    Log.i("NETWORK", "getNearbyEvents successfull with response: " + response.toString());
                }

                callback.onResponse((ArrayList<Event>) response.body());
            }

            @Override
            public void onFailure(@NonNull Call<List<Event>> call, @NonNull Throwable t) {
                Log.e("NETWORK", t.getMessage(), t);
                callback.onFailure(t);
            }
        });

    }

    /**
     * Returns the event with the given ID if it exists, null otherwise.
     * @param id The ID of the Event you wish to have returned.
     * @param callback The callback to handle results. Override its methods to get what you need. onResponse gets the Event with the given ID.
     */
    public void getEventById(int id, final ICallbackAdapter<Event> callback){
        Call<Event> call = client.getEventById(id);

        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
                if(!response.isSuccessful()){
                    Log.i("NETWORK", "getEventById got onResponse, without success");
                }
                else {
                    Log.i("NETWORK", "getEventById successfull with response: " + response.toString());
                }

                callback.onResponse(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<Event> call, @NonNull Throwable t) {
                Log.e("NETWORK", t.getMessage(), t);
                callback.onFailure(t);
            }
        });

    }

    //endregion

    //region PUT-methods
    /**
     * Updates the existing event if it exists in the database, and returns the updated Event.
     * @param event The Event you wish to update in the database
     * @param callback The callback to handle results. Override its methods to get what you need. onResponse gets the updated event
     */
    public void updateEvent(Event event, final ICallbackAdapter<Event> callback){

        if(event.getId()<0){
            throw new IllegalArgumentException("Cannot update an Event with ID < 0, as this will not be an event in the database");
        }
        Call<Event> call = client.updateEvent(event);

        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
                if(!response.isSuccessful()){
                    Log.i("NETWORK", "updateEvent got onResponse, without success");
                }
                else {
                    Log.i("NETWORK", "updateEvent successfull with response: " + response.toString());
                }

                callback.onResponse(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<Event> call, @NonNull Throwable t) {
                Log.e("NETWORK", t.getMessage(), t);
                callback.onFailure(t);
            }
        });

    }

    //endregion

}
