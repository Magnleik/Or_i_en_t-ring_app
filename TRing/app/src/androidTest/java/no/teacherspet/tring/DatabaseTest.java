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


import io.reactivex.android.schedulers.AndroidSchedulers;
import no.teacherspet.tring.Database.DAOs.OEventDao;
import no.teacherspet.tring.Database.DAOs.PointDao;
import no.teacherspet.tring.Database.DAOs.PointOEventJoinDao;
import no.teacherspet.tring.Database.DAOs.UserDao;
import no.teacherspet.tring.Database.Entities.OEvent;
import no.teacherspet.tring.Database.Entities.Point;
import no.teacherspet.tring.Database.Entities.PointOEventJoin;
import no.teacherspet.tring.Database.Entities.User;
import no.teacherspet.tring.Database.LocalDatabase;

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
        LatLng latlng = createLatLng();
        String description = "testPoint";
        Point testPoint = new Point(description, latlng);

        pointDAO.getAll().subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread())
                .test().assertEmpty();

        assertNotSame(-1, pointDAO.insert(testPoint));
        pointDAO.findById(testPoint.getId()).subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(point -> assertEquals(testPoint, point));
        pointDAO.delete(testPoint);
        pointDAO.getAll().subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread())
                .test().assertEmpty();

    }
    private LatLng createLatLng(){
        return new LatLng(37.377166, -122.086966);
    }

    @Test
    public void userTest(){
        String firstname = "firstname";
        String lastname = "lastname";
        User testUser1 = new User(0, true, firstname, lastname);

        userDAO.getAll().subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread())
                .test().assertEmpty();
        assertNotSame(-1, userDAO.insert(testUser1));

        userDAO.getPersonalUser().subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> assertEquals(testUser1, user));
        userDAO.findById(testUser1.getId()).subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> assertEquals(testUser1, user));

        userDAO.delete(testUser1);
        userDAO.getAll().subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread())
                .test().assertEmpty();

    }
    @Test
    public void oEventTest(){
        String testname = "testname";
        OEvent testoEvent = new OEvent(testname);

        assertNotSame(-1, oEventDAO.insert(testoEvent));
        oEventDAO.getAll().subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(oEvents -> assertNotNull(oEvents));
        oEventDAO.findById(testoEvent.getId()).subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(oEvent -> assertEquals(testoEvent, oEvent));
        oEventDAO.delete(testoEvent);
        oEventDAO.findById(testoEvent.getId()).subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(oEvent -> assertNotSame(testoEvent, oEvent));
    }

    @Test
    public void joinTest(){
        LatLng latlng = createLatLng();
        String description = "testPoint";
        Point testPoint1 = new Point(description, latlng);
        pointDAO.insert(testPoint1);
        Point testPoint2 = new Point(description, latlng);
        pointDAO.insert(testPoint2);
        //pointDAO.insert(testPoint1, testPoint2);

        String testname = "testoEvent";
        OEvent testoEvent = new OEvent(testname);
        assertNotSame(-1, oEventDAO.insert(testoEvent));
        assertNotSame(testPoint1.getId(), testPoint2.getId());

        PointOEventJoin testJoin1 = new PointOEventJoin(testPoint1.getId(), testoEvent.getId(), true);
        PointOEventJoin testJoin2 = new PointOEventJoin(testPoint2.getId(), testoEvent.getId(), false);

        assertNotSame(-1, pointOEventJoinDAO.insert(testJoin1));
        assertNotSame(-1, pointOEventJoinDAO.insert(testJoin2));

        pointOEventJoinDAO.getPointsForOEvent(testoEvent.getId()).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(points -> assertEquals(points.get(0), testPoint1));
        pointOEventJoinDAO.getStartPoint(testoEvent.getId()).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(point -> assertEquals(testPoint1, point));
        pointOEventJoinDAO.getPointsNotStart(testoEvent.getId()).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(points -> assertEquals(testPoint2, points.get(0)));

        pointOEventJoinDAO.getOEventsForPoint(testPoint2.getId()).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(oEvents -> assertEquals(testoEvent, oEvents.get(0)));

        pointDAO.delete(testPoint1);

        pointOEventJoinDAO.getStartPoint(testoEvent.getId()).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(point -> assertNotSame(testPoint1, point));

        pointOEventJoinDAO.delete(testJoin1);
        pointOEventJoinDAO.getStartPoint(testoEvent.getId()).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(point -> assertNotSame(testPoint1, point));

        pointOEventJoinDAO.delete(testPoint2.getId(), testoEvent.getId());
        pointOEventJoinDAO.getPointsForOEvent(testoEvent.getId()).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(points -> assertNotSame(testPoint2, points.get(0)));

    }


}
