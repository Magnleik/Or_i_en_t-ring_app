package no.teacherspet.tring.util;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

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
 * Util class for saving events to room
 * Created by Hermann on 28.04.2018.
 */

public class RoomSaving {

    private LocalDatabase localDatabase;
    private SaveToRoom returnClass;

    public RoomSaving(Context context, SaveToRoom returnClass){
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
}

