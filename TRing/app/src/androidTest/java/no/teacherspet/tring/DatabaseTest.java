package no.teacherspet.tring;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.persistence.room.Room;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.android.gms.maps.model.LatLng;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import no.teacherspet.tring.Database.DAOs.OEventDao;
import no.teacherspet.tring.Database.DAOs.PointDao;
import no.teacherspet.tring.Database.DAOs.PointOEventJoinDao;
import no.teacherspet.tring.Database.DAOs.UserDao;
import no.teacherspet.tring.Database.Entities.Point;
import no.teacherspet.tring.Database.LocalDatabase;
import no.teacherspet.tring.Database.ViewModels.UserViewModel;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNotSame;

/**
 * Created by Hermann on 15.02.2018.
 */
@RunWith(AndroidJUnit4.class)
public class DatabaseTest {

    LocalDatabase db;
    PointDao pointDAO;
    UserDao userDAO;
    OEventDao oEventDAO;
    PointOEventJoinDao pointOEventJoinDAO;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule() ;

    @Before
    public void setUp(){
        db = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(), LocalDatabase.class)
            //Allow mainthread for testing
            .allowMainThreadQueries().build();

        pointDAO = db.pointDAO();
        userDAO = db.userDAO();
        oEventDAO = db.oEventDAO();
        pointOEventJoinDAO = db.pointOEventJoinDAO();
    }
    @After
    public void tearDown(){
        db.close();
    }

    @Test
    public void pointTest(){
        pointDAO.getAll().test().assertNoValues();

        LatLng latlng = createLatLng();
        String description = "testPoint";
        Point testPoint = new Point(description, latlng);

        pointDAO.insert(testPoint);

        pointDAO.findById(testPoint.getId()).test().awaitDone(1, TimeUnit.SECONDS)
        .assertValue(point -> point != null && point.equals(testPoint));

        pointDAO.delete(testPoint);
        pointDAO.findById(testPoint.getId()).test().awaitDone(1, TimeUnit.SECONDS)
                .assertEmpty();
    }
    private LatLng createLatLng(){
        return new LatLng(37.377166, -122.086966);
    }

    @Test
    public void userTest(){

    }
    @Test
    public void oEventTest(){

    }
    @Test
    public void joinTest(){

    }


}
