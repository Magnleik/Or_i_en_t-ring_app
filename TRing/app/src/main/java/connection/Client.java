package connection;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Eirik on 20-Feb-18.
 */

public interface Client {

    @GET("/api/test/point/{ID}")
    Call<Point> getPointByID(
            @Path("ID") int pointID
    );

    @GET("/")
    Call<List<String>> getTestStrings(
            /*@Path("FILLINPLS") int testStringID*/
    );

}
