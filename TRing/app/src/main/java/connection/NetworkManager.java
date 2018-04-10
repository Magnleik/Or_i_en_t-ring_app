package connection;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
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
    OkHttpClient.Builder httpClient;
    Retrofit.Builder builder;
    Retrofit retrofit;


    private NetworkManager(){
        init();
        //addCredentials("USERname", "PASSword");
    }

    public static NetworkManager getInstance(){
        if(nm == null){
            nm = new NetworkManager();
        }

        return nm;
    }

    /**
     * When logging on, call this method with the username and password. Will add authentification to the HTTP calls.
     * @param username The username
     * @param password The password
     */
    private void addCredentials(String username, String password){

        String authToken = null;

        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
            authToken = Credentials.basic(username, password);
        }

        if (!TextUtils.isEmpty(authToken)) { //FIXME: Does this handle null?
            AuthenticationInterceptor interceptor =
                    new AuthenticationInterceptor(authToken);

            if (!httpClient.interceptors().contains(interceptor)) {
                httpClient.addInterceptor(interceptor);

                builder.client(httpClient.build());
                retrofit = builder.build();
            }
        }

        client = retrofit.create(Client.class);

        updatePointTest();

    }

    private void init(){

        httpClient = new OkHttpClient.Builder();
        String URL = "http://10.22.19.155";
        builder = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(
                        GsonConverterFactory.create()
                );

        retrofit = builder
                .client(
                        httpClient.build()
                )
                .build();

        client = retrofit.create(Client.class);

        //Just here for testing:

        //updatePointTest();
        //addPointsTest();
        //addEventTest();
        //updateEventPropertiesTest();

    }

    //region Testing methods

    private void updatePointTest(){
        Point testPoint1 =  new Point(10.324, 50, "This is an updated test point");
        testPoint1._setId(80);

        updatePoint(testPoint1, new ICallbackAdapter<Point>() {
            @Override
            public void onResponse(Point object) {
                System.out.println(" ");
            }

            @Override
            public void onFailure(Throwable t) {
                System.out.println(" ");
            }
        });
    }

    private void addPointsTest(){

        Point testPoint1 =  new Point(10.324, 20.420, "This is a test point");
        Point testPoint2 = new Point(123.321, 12.123, "Test point #2");
        Point testPoint3 = new Point(0.0, 0.0, "This is a starting point");

        addPoints(new ICallbackAdapter<List<Point>>() {
            @Override
            public void onResponse(List<Point> object) {
                System.out.println("Recieved points with the first being " + object.get(0).getId());
            }

            @Override
            public void onFailure(Throwable t) {
                System.out.println(t.getMessage());
            }
        }, testPoint1,testPoint2,testPoint3);

    }

    private void addEventTest(){

        Point testPoint1 =  new Point(10.324, 20.420, "This is a test point");
        Point testPoint2 = new Point(123.321, 12.123, "Test point #2");
        Point testPoint3 = new Point(0.0, 0.0, "This is a starting point");
        ArrayList<Point> points = new ArrayList<>();
        points.add(testPoint1); points.add(testPoint2);
        Event testEvent = new Event();
        testEvent.addPosts(points);
        testEvent.setStartPoint(testPoint3);
        testEvent.addProperty("name", "test_property");
        testEvent.addProperty("name", "test_property2");
        testEvent.addProperty("avg_time", "00:00:00");

        addEvent(testEvent, new ICallbackAdapter<Event>() {
            @Override
            public void onResponse(Event object) {
                System.out.println("Recieved event with ID " + object.getId());
            }

            @Override
            public void onFailure(Throwable t) {
                System.out.println("FAILED! : " + t.getMessage());
            }
        });

    }

    private void updateEventPropertiesTest(){
        Point testPoint1 =  new Point(10.324, 20.420, "This is a test point");
        Event testEvent = new Event();
        testEvent._setId(2);
        testEvent.setStartPoint(testPoint1);
        testEvent.addProperty("name", "edited_test_property");
        testEvent.addProperty("name", "edited_test_property2");
        testEvent.addProperty("avg_time", "00:00:01");

        updateEventProperties(testEvent, new ICallbackAdapter<Event>() {
            @Override
            public void onResponse(Event object) {
                System.out.println("Recieved event with ID " + object.getId());
            }

            @Override
            public void onFailure(Throwable t) {
                System.out.println("FAILED! : " + t.getMessage());
            }
        });
    }
    //endregion

    //region POST-methods
    /**
     * Adds points to the database, not affiliated with an Event
     * @param points The Points to be added, can be any number
     * @param callback The callback to handle results. Override its methods to get what you need. onResponse gets a Point
     */
    public void addPoints(final ICallbackAdapter<List<Point>> callback, Point... points){
        Call<List<Point>> call = client.addPoints(points);

        call.enqueue(new Callback<List<Point>>() {
            @Override
            public void onResponse(@NonNull Call<List<Point>> call, @NonNull Response<List<Point>> response) {

                if(!response.isSuccessful()){
                    Log.i("NETWORK", "addPoints got onResponse, without success");
                }
                else {
                    Log.i("NETWORK", "addPoints successful with response: " + response.toString());
                }

                callback.onResponse(response.body());

            }

            @Override
            public void onFailure(@NonNull Call<List<Point>> call, @NonNull Throwable t) {
                Log.e("NETWORK", t.getMessage(), t);
                callback.onFailure(t);
            }
        });
    }

    public void addPointsToEvent(final ICallbackAdapter<Void> callback, int eventID, Point... points){
        Call<Void> call = client.addPointsToEvent(eventID, points);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if(!response.isSuccessful()){
                    Log.i("NETWORK", "addPointsToEvent got onResponse, without success");
                }
                else {
                    Log.i("NETWORK", "addPointsToEvent successful with response: " + response.toString());
                }

                callback.onResponse(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
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
                    Log.i("NETWORK", "addEvent successful with response: " + response.toString());
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
    public void getNearbyPoints(double latitude, double longitude, double maxDist, final ICallbackAdapter<ArrayList<Point>> callback){

        Point sendPoint = new Point(latitude,longitude," ");
        sendPoint.addProperty("max_dist", String.valueOf(maxDist));
        Call<List<Point>> call = client.getNearbyPoints(latitude,longitude,maxDist);

        call.enqueue(new Callback<List<Point>>() {
            @Override
            public void onResponse(@NonNull Call<List<Point>> call, @NonNull Response<List<Point>> response) {
                if(!response.isSuccessful()){
                    Log.i("NETWORK", "getNearbyPoints got onResponse, without success");
                }
                else {
                    Log.i("NETWORK", "getNearbyPoints successfull with response: " + response.toString());
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
        Call<List<Event>> call = client.getNearbyEvents(latitude, longitude, maxDist);

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
     * Updates the existing Events properties if it exists in the database, and returns the updated Event. Ignores points.
     * @param event The state the Event in the database should be updated to (ingnoring Points)
     * @param callback The callback to handle results. Override its methods to get what you need. onResponse gets the updated Event
     */
    public void updateEventProperties(Event event, final ICallbackAdapter<Event> callback){

        Call<Event> call = client.updateEventProperties(event.getId(), event._getAllProperties());

        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
                if(!response.isSuccessful()){
                    Log.i("NETWORK", "updateEventProperties got onResponse, without success");
                }
                else {
                    Log.i("NETWORK", "updateEventProperties successful with response: " + response.toString());
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


    /**
     * Updates the existing Point if it exists in the database, and returns the updated Point.
     * @param point The state the Point in the database should be updated to.
     * @param callback The callback to handle results. Override its methods to get what you need. onResponse gets the updated Point
     */
    public void updatePoint(Point point, final ICallbackAdapter<Point> callback){

        Call<Point> call = client.updatePoint(point);

        call.enqueue(new Callback<Point>() {
            @Override
            public void onResponse(@NonNull Call<Point> call, @NonNull Response<Point> response) {
                if(!response.isSuccessful()){
                    Log.i("NETWORK", "updatePoint got onResponse, without success");
                }
                else {
                    Log.i("NETWORK", "updatePoint successful with response: " + response.toString());
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

    //endregion

    //region DELETE-methods

    /**
     * Removes the Point with the given pointID from the Event with the given eventID.
     * @param eventID The int ID of the Event
     * @param pointID The int ID of the Point
     * @param callback The callback to handle results. Override its methods to check for validity of the response. onResponse returns Void
     */
    public void removePointFromEvent(int eventID, int pointID, final ICallbackAdapter<Void> callback){

        Call<Void> call = client.removePointFromEvent(eventID,pointID);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if(!response.isSuccessful()){
                    Log.i("NETWORK", "removePointFromEvent got onResponse, without success");
                }
                else {
                    Log.i("NETWORK", "removePointFromEvent was successful with response: " + response.toString());
                }
                callback.onResponse(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e("NETWORK", t.getMessage(), t);
                callback.onFailure(t);
            }
        });
    }

    //endregion

    public class AuthenticationInterceptor implements Interceptor {

        private String authToken;

        public AuthenticationInterceptor(String token) {
            this.authToken = token;
        }

        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request original = chain.request();

            Request.Builder builder = original.newBuilder()
                    .header("Authorization", authToken);

            Request request = builder.build();
            return chain.proceed(request);
        }
    }

}
