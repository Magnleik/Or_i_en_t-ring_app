package connection;

/**
 * Created by Eirik on 27-Feb-18.
 */

public interface ICallbackAdapter<T>{

    /**
     * Will run if a networking command is successfull.
     * @param object The object the command is returned - CAN be null, if there is an unhandled error.
     */
    void onResponse(T object);


    /**
     * Will run if a networking command throws an Error, i.e. a timeout.
     * @param t The error Throwable, can be used if errors should be handled differently - or displayed to the user.
     */
    void onFailure(Throwable t);
}
