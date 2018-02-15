package no.teacherspet.tring.Database.Entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Hermann on 13.02.2018.
 */
@Entity(tableName = "o_event")
public class OEvent {

    public OEvent(/*int oEventID,*/ String name) {
        //this.oEventID = oEventID;
        this.name = name;
    }


    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "o_event_id")
    private int oEventID;

    @ColumnInfo
    private String name;


}
