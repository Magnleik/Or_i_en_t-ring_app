package no.teacherspet.tring.Database.DAOs;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import io.reactivex.Maybe;
import no.teacherspet.tring.Database.Entities.EventResult;

/**
 * Created by Hermann on 29.04.2018.
 */
@Dao
public interface ResultDAO {

    @Query("SELECT * FROM event_result")
    Maybe<List<EventResult>> getAll();

    @Query("SELECT * FROM event_result WHERE id = :resultID")
    Maybe<List<EventResult>> getResult(int resultID);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insert(EventResult... eventResults);

    @Update
    void update(EventResult... eventResults);

    @Query("DELETE FROM event_result WHERE id = :resultID")
    int deleteResult(int resultID);

    @Delete
    int delete(EventResult... eventResults);

}
