package connection;

/**
 * Created by Eirik on 15-Feb-18.
 */

public class NetworkManager {

    static NetworkManager nm;
    static String URL = "URL GOES HERE";

    private NetworkManager(){
    }

    public static NetworkManager getInstance(){
        if(nm == null){
            nm = new NetworkManager();
        }

        return nm;
    }

    public void addPoint(IPoints point){

    }


}
