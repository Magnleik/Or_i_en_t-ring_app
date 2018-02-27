package connection;

import android.util.Log;

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
    static private String URL = "http://10.22.17.21";
    OkHttpClient.Builder httpClient;
    Client client;
    Retrofit.Builder builder;
    Retrofit retrofit;

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

        httpClient = new OkHttpClient.Builder();
        builder = new Retrofit.Builder()
                    .baseUrl(URL)
                    .addConverterFactory(
                            GsonConverterFactory.create()
                    );

        retrofit =
                builder
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
        sendEventTest();
    }

    private void connectionPointTest(){

        Call<Point> call =
                client.testGetPointByID(100);

        call.enqueue(new Callback<Point>() {
            @Override
            public void onResponse(Call<Point> call, Response<Point> response) {
                Point point = response.body();
                System.out.println("We have a point!");
                System.out.println("It's ID is: " + point.getId());
            }

            @Override
            public void onFailure(Call<Point> call, Throwable t) {
                System.out.println("Point test has crashed and burned");
                Log.d("ERROR: ", t.getMessage());
            }
        });

    }

    private void connectionEventTest(){

        Call<Event> call =
                client.testGetEventByID(0);

        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                Event event = response.body();
                System.out.println("We have an event!");
                System.out.println("It's ID is: " + event.getId());
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                System.out.println("Event test has crashed and burned");
                Log.d("ERROR: ", t.getMessage());
            }
        });

    }

    private void connectionStringTest(){

        Call<List<String>> call =
                client.getTestStrings();

        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                System.out.println("Call Test successful, strings: " + response.body());
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                System.out.println("Call Test failed");
                Log.d("ERROR: ", t.getMessage());
            }
        });

    }

    private void sendPointTest(){

        Point testPoint =  new Point(10.324, 20.420, "This is a test point");

        Call<Point> call = client.testCreatePoint(testPoint);

        call.enqueue(new Callback<Point>() {
            @Override
            public void onResponse(Call<Point> call, Response<Point> response) {
                Point resp = response.body();
                System.out.println("Call sent");
            }

            @Override
            public void onFailure(Call<Point> call, Throwable t) {
                System.out.println("Call not sent");
                Log.d("sendPointTest_ERROR", t.getMessage());
            }
        });

    }

    private void sendEventTest(){

        Point testPoint1 =  new Point(10.324, 20.420, "This is a test point");
        Point testPoint2 = new Point(123.321, 12.123, "Test point #2");
        Point testPoint3 = new Point(0.0, 0.0, "This is a starting point");
        ArrayList<Point> points = new ArrayList<Point>();
        points.add(testPoint1); points.add(testPoint2);
        Event testEvent = new Event();
        testEvent.addPosts(points);
        testEvent.setStartPoint(testPoint3);

        Call<Event> call = client.testCreateEvent(testEvent);

        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                Event resp = response.body();
                System.out.println("Call sent");
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                System.out.println("Call not sent");
                Log.d("sendEventTest_ERROR", t.getMessage());
            }
        });

    }

    /**
     * Adds a point to the database, not affiliated with an Event
     * @param point The Point to be added
     * @return Returns the Point from the database, with updated ID.
     */
    public Point addPoint(Point point){
        Point result = null;
        Call<Point> call = client.addPoint(point);

        call.enqueue(new Callback<Point>() {
            @Override
            public void onResponse(Call<Point> call, Response<Point> response) {
                Log.i("addPoint_SUCCESS", "addPoint successfull with response: "+ response.toString());
            }

            @Override
            public void onFailure(Call<Point> call, Throwable t) {
                Log.e("addPoint_ERROR", t.getMessage(), t);
            }
        });

        return result;
    }

    /**
     * Finds all points within a circle with radius maxDist from the given location
     * @param latitude The latitude of given position
     * @param longitude The longitude of given position
     * @param maxDist The radius of search
     * @return A List of Points within the given circle
     */
    public ArrayList<Point> getPointsNearby(double latitude, double longitude, double maxDist){

        ArrayList<Point> result = new ArrayList<>();
        Call<List<Point>> call = client.getNearbyPoints(latitude,longitude,maxDist);

        call.enqueue(new Callback<List<Point>>() {
            @Override
            public void onResponse(Call<List<Point>> call, Response<List<Point>> response) {
                Log.i("getPointsNearby_SUCCESS", "getPointsNearby successfull with response: "+ response.toString());
            }

            @Override
            public void onFailure(Call<List<Point>> call, Throwable t) {
                Log.e("getPointsNearby_ERROR", t.getMessage(), t);
            }
        });


        return result;

    }

    /**
     * Updates the existing event if it exists in the database, and returns the updated Event.
     * @param event The Event you wish to update in the database
     * @return Returns the updated event
     */
    public Event updateEvent(Event event){

        if(event.getId()<0){
            throw new IllegalArgumentException("Cannot update an Event with ID < 0, as this will not be an event in the database");
        }

        Event result = null;
        Call<Event> call = client.updateEvent(event);

        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                Log.i("updateEvent_SUCCESS", "updateEvent successfull with response: "+ response.toString());
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                Log.e("updateEvent_ERROR", t.getMessage(), t);
            }
        });


        return result;
    }


    /**
     * ADDS an Event to the database, will not update existing events. Ignores set ID.
     * @param event The Event to add to the database
     * @return Returns the same Event, with updated information from the database - including its generated ID.
     */
    public Event addEvent(Event event){
        Event result = null;
        Call<Event> call = client.addEvent(event);

        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                Log.i("addEvent_SUCCESS", "addEvent successfull with response: "+ response.toString());
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                Log.e("addEvent_ERROR", t.getMessage(), t);
            }
        });

        return result;
    }

    /**
     * Finds and returns all events with starting location within a radius maxDist from the given location
     * @param latitude The latitude of given position
     * @param longitude The longitude of given position
     * @param maxDist The radius of search
     * @return Returns all events starting within the given circle
     */
    public ArrayList<Event> getNearbyEvents(double latitude, double longitude, double maxDist){
        ArrayList result = null;
        Call<List<Event>> call = client.getNearbyEvents(latitude,longitude,maxDist);

        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                Log.i("getNearbyEvents_SUCCESS", "getNearbyEvents successfull with response: "+ response.toString());
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                Log.e("getNearbyEvents_ERROR", t.getMessage(), t);
            }
        });

        return result;
    }

    /**
     * Returns the event with the given ID if it exists, null otherwise.
     * @param id The ID of the Event you wish to have returned.
     * @return Returns the Event with the given ID.
     */
    public Event getEventById(int id){
        Event result = null;
        Call<Event> call = client.getEventById(id);

        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                Log.i("getEventById_SUCCESS", "getEventById successfull with response: "+ response.toString());
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                Log.e("getEventById_ERROR", t.getMessage(), t);
            }
        });

        return result;
    }


}
