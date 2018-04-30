package no.teacherspet.tring.Database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import no.teacherspet.tring.Database.DAOs.DeleteDao;
import no.teacherspet.tring.Database.DAOs.OEventDao;
import no.teacherspet.tring.Database.DAOs.PointDao;
import no.teacherspet.tring.Database.DAOs.PointOEventJoinDao;
import no.teacherspet.tring.Database.DAOs.ResultDAO;
import no.teacherspet.tring.Database.DAOs.UserDao;
import no.teacherspet.tring.Database.Entities.EventResult;
import no.teacherspet.tring.Database.Entities.RoomOEvent;
import no.teacherspet.tring.Database.Entities.RoomPoint;
import no.teacherspet.tring.Database.Entities.PointOEventJoin;
import no.teacherspet.tring.Database.Entities.RoomUser;

/**
 * Created by Hermann on 13.02.2018.
 */

@Database(entities = {RoomUser.class, RoomPoint.class, RoomOEvent.class, PointOEventJoin.class, EventResult.class}, version = 14, exportSchema = false)
@TypeConverters(DataConverters.class)
public abstract class LocalDatabase extends RoomDatabase {

    private static volatile LocalDatabase INSTANCE;

    public static LocalDatabase getInstance(Context context){
        if(INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), LocalDatabase.class,
                    "orienteering.db").build();

        }
        return INSTANCE;
    }

    public abstract UserDao userDAO();

    public abstract PointDao pointDAO();

    public abstract OEventDao oEventDAO();

    public abstract PointOEventJoinDao pointOEventJoinDAO();

    public abstract DeleteDao deleteDAO();

    public abstract ResultDAO resultDAO();

}
