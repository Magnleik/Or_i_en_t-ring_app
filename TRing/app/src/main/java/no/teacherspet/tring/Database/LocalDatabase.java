package no.teacherspet.tring.Database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import no.teacherspet.tring.Database.DAOs.DeleteDAO;
import no.teacherspet.tring.Database.DAOs.OEventDAO;
import no.teacherspet.tring.Database.DAOs.PointDAO;
import no.teacherspet.tring.Database.DAOs.PointOEventJoinDAO;
import no.teacherspet.tring.Database.DAOs.UserDAO;
import no.teacherspet.tring.Database.Entities.OEvent;
import no.teacherspet.tring.Database.Entities.Point;
import no.teacherspet.tring.Database.Entities.PointOEventJoin;
import no.teacherspet.tring.Database.Entities.User;

/**
 * Created by Hermann on 13.02.2018.
 */

@Database(entities = {User.class, Point.class, OEvent.class, PointOEventJoin.class}, version = 1, exportSchema = false)
@TypeConverters(LatLngConverter.class)
public abstract class LocalDatabase extends RoomDatabase {

    private static final String DB_NAME = "localDatabase.db";
    private static LocalDatabase instance;

    public static LocalDatabase getInstance(Context context){
        if(instance == null){
            instance = create(context);
        }
        return instance;
    }
    private static LocalDatabase create(Context context){
        return Room.databaseBuilder(context, LocalDatabase.class, DB_NAME).build();
    }

    public abstract UserDAO getUserDAO();

    public abstract PointDAO getPointDAO();

    public abstract OEventDAO getOEventDAO();

    public abstract PointOEventJoinDAO getPointOEventJoinDAO();

    public abstract DeleteDAO getDeleteDAO();

}
