package no.teacherspet.tring.util;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import connection.Event;
import connection.Point;
import no.teacherspet.tring.Database.Entities.PointOEventJoin;
import no.teacherspet.tring.Database.Entities.RoomOEvent;
import no.teacherspet.tring.Database.Entities.RoomPoint;
import no.teacherspet.tring.Database.LocalDatabase;
import no.teacherspet.tring.Database.ViewModels.OEventViewModel;
import no.teacherspet.tring.Database.ViewModels.PointOEventJoinViewModel;
import no.teacherspet.tring.Database.ViewModels.PointViewModel;

/**
 * Util class for saving and loading events to room
 * Created by Hermann on 28.04.2018.
 */

public class RoomSaveAndLoad {

    private LocalDatabase localDatabase;
    private RoomInteract returnClass;

    /**
     * Uses context to create database, returnClass to return callback
     */
    public RoomSaveAndLoad(Context context, RoomInteract returnClass){
        localDatabase = LocalDatabase.getInstance(context);
        this.returnClass = returnClass;
    }
    /**
     * Saves the Event and corresponding Points, and adds connections between them in the Room database
     * Makes sure that events and points are saved before the connections are saved. The next step
     * is only called after the previous is finished.
     */
    public void saveRoomEvent(Event event) {
        OEventViewModel oEventViewModel = new OEventViewModel(localDatabase.oEventDAO());
        Log.d("Room", "Started saving event");
        RoomOEvent newevent = new RoomOEvent(event.getId(), event._getAllProperties());
        oEventViewModel.addOEvents(newevent).subscribe(longs ->{
            Log.d("Room", String.format("Event %d saved", event.getId()));
            savePoints(event);
        });
    }

    /**
     * Saves all points connected to event with all attributes
     */
    private void savePoints(Event event) {
        RoomPoint[] roomPoints = new RoomPoint[event.getPoints().size()];
        for (int i = 0; i < event.getPoints().size(); i++) {
            Point point = event.getPoints().get(i);
            RoomPoint roomPoint = new RoomPoint(point.getId(), point._getAllProperties(), new LatLng(point.getLatitude(), point.getLongitude()));
            roomPoints[i] = roomPoint;
        }
        PointViewModel pointViewModel = new PointViewModel(localDatabase.pointDAO());
        pointViewModel.addPoints(roomPoints).subscribe(longs -> {
            Log.d("Room", String.format("%d points saved", longs.length));
            joinPointsToEvent(event);
        });
    }

    /**
     * Saves all connections between the event and its points. Runs after both points and event is saved
     */
    private void joinPointsToEvent(Event event) {
        PointOEventJoinViewModel pointOEventJoinViewModel = new PointOEventJoinViewModel(localDatabase.pointOEventJoinDAO());
        PointOEventJoin[] joins = new PointOEventJoin[event.getPoints().size()];
        for (int i = 0; i < event.getPoints().size(); i++) {
            Point point = event.getPoints().get(i);
            boolean start = i == 0;
            joins[i] = new PointOEventJoin(point.getId(), event.getId(), start, false);
        }
        pointOEventJoinViewModel.addJoins(joins).subscribe(longs -> checkSave(longs));
    }

    /**
     * Checks if the save was successful
     * @param longs Which columns were effected by saving, or -1 if an error occurred
     * Returns if everything was saved or not
     */
    private void checkSave(long[] longs) {
        boolean savedAll = true;
        for (long aLong : longs) {
            if (aLong < 0) {
                savedAll = false;
            }
        }
        if(savedAll){
            Log.d("Room", String.format("%d joins saved", longs.length));
        }
        else{
            Log.d("Room","Joins not saved");
        }
        returnClass.whenRoomFinished(savedAll);
    }

    /**
     * Loads all Points connected to Event from room
     * @param event RoomOEvent which we are creating an Event out of
     */
    public void reconstructEvent(RoomOEvent event){
        PointOEventJoinViewModel joinViewModel = new PointOEventJoinViewModel(localDatabase.pointOEventJoinDAO());
        joinViewModel.getPointsForOEvent(event.getId()).subscribe(roomPoints -> {
            Log.d("Room",String.format("%d points found for event %d", roomPoints.size(), event.getId()));
            if(roomPoints.size() > 0){
                joinViewModel.getJoinsForOEvent(event.getId()).subscribe(joins -> createEvent(event, roomPoints, joins));
            }
        });
    }

    /**
     * Creates an Event from an RoomOEvent and its Points
     * @param oEvent the RoomOEvent we are turning into an Event
     * @param roomPoints Points belonging to the Event
     * @param joins Connections between Points and the Event
     * Returns the reconstructed Event
     */
    private void createEvent(RoomOEvent oEvent, List<RoomPoint> roomPoints, List<PointOEventJoin> joins){
        Event event = new Event();
        event._setId(oEvent.getId());
        for(String key : oEvent.getProperties().keySet()){
            event.addProperty(key, oEvent.getProperties().get(key));
        }
        for (RoomPoint roomPoint : roomPoints){
            for (PointOEventJoin join : joins){
                if(roomPoint.getId() == join.getPointID()){
                    Point point = setupPoint(roomPoint, join.isVisited());
                    if(join.isStart()){
                        event.setStartPoint(point);
                    }
                    else{
                        event.addPost(point);
                    }
                }
            }
        }
        Log.d("Room",String.format("Event %d created",event.getId()));
        returnClass.whenRoomFinished(event);
    }

    /**
     * Reconstructs an Point from an RoomPoint
     * @param roomPoint The RoomPoint containing the information of the Point
     * @param visited If the point has been visited or not
     * @return The reconstructed Point
     */
    private Point setupPoint(RoomPoint roomPoint, boolean visited){
        Point point = new Point(roomPoint.getLatLng().latitude, roomPoint.getLatLng().longitude, "placeholder");
        point._setId(roomPoint.getId());
        point.setVisited(visited);
        for(String key : roomPoint.getProperties().keySet()){
            point.addProperty(key, roomPoint.getProperties().get(key));
        }
        return point;
    }

}

