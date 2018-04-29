package no.teacherspet.tring.Database.Entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;


/**
 * Created by Hermann on 29.04.2018.
 */

@Entity(tableName = "event_result")
public class EventResult {

    public EventResult(int id){
        this.id = id;
        startTime = -1;
        eventTime = -1;
    }

    @PrimaryKey
    private int id;

    private long startTime;

    private long eventTime;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public long getStartTime() {
        return startTime;
    }
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEventTime() {
        return eventTime;
    }
    public void setEventTime(long eventTime) {
        this.eventTime = eventTime;
    }
}
