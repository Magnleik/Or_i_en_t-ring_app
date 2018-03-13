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
import org.mockito.internal.matchers.And;


import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
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
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertTrue;

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

        pointDAO.getAll().test().assertValue(points -> {
            return points.size() == 0;
        });

        Point testPoint = new Point(0, description, latlng);
        assertNotSame(-1, pointDAO.insert(testPoint));
        pointDAO.findById(testPoint.getId()).test().assertValue(point -> {
            return testPoint.getId() == point.getId();
        });
        pointDAO.getMaxID().test().assertValue(integer -> {
            return testPoint.getId() == integer;
        });
        pointDAO.delete(testPoint);
        pointDAO.findById(testPoint.getId()).test().assertNoValues();

    }
    private LatLng createLatLng(){
        return new LatLng(37.377166, -122.086966);
    }

    @Test
    public void userTest(){
        String firstname = "firstname";
        String lastname = "lastname";

        User testUser1 = new User(0, true, firstname, lastname);
        userDAO.getAll().test().assertValue(users -> {
            return users.size() == 0;
        });
        assertNotSame(-1, userDAO.insert(testUser1));

        userDAO.findById(testUser1.getId()).test().assertValue(user -> {
            return testUser1.getId() == user.getId();
        });
        userDAO.getPersonalUser().test().assertValue(user -> {
            return testUser1.getId() == user.getId();
        });
        userDAO.getMaxID().test().assertValue(integer -> {
            return testUser1.getId() == integer;
        });
        userDAO.delete(testUser1);
        userDAO.getAll().test().assertValue(users -> {
            return users.size() == 0;
        });
    }
    @Test
    public void oEventTest(){
        String testname = "testname";
        OEvent testoEvent = new OEvent(0, testname);
        oEventDAO.getAll().test().assertValue(oEvents -> {
            return oEvents.size() == 0;
        });
        assertNotSame(-1, oEventDAO.insert(testoEvent));
        oEventDAO.findById(testoEvent.getId()).test().assertValue(oEvent -> {
            return testoEvent.getId() == oEvent.getId();
        });
        oEventDAO.delete(testoEvent);

        oEventDAO.getAll().test().assertValue(oEvents -> {
            return oEvents.size() == 0;
        });
    }

    @Test
    public void joinTest(){
        LatLng latlng = createLatLng();
        String description = "testPoint";
        Point testPoint1 = new Point(0, description, latlng);
        Point testPoint2 = new Point(1, description, latlng);
        assertNotSame(-1, pointDAO.insert(testPoint1));
        assertNotSame(-1, pointDAO.insert(testPoint2));

        String testname = "testoEvent";
        OEvent testoEvent = new OEvent(0, testname);
        assertNotSame(-1, oEventDAO.insert(testoEvent));
        assertNotSame(testPoint1.getId(), testPoint2.getId());

        PointOEventJoin testJoin1 = new PointOEventJoin(testPoint1.getId(), testoEvent.getId(), true);
        PointOEventJoin testJoin2 = new PointOEventJoin(testPoint2.getId(), testoEvent.getId(), false);

        assertNotSame(-1, pointOEventJoinDAO.insert(testJoin1));
        assertNotSame(-1, pointOEventJoinDAO.insert(testJoin2));

        pointOEventJoinDAO.getPointsForOEvent(testoEvent.getId()).test().assertValue(points -> {
            return points.size() == 2 && (points.get(0).getId() == testPoint1.getId() || points.get(0).getId() == testPoint2.getId());
        });
        pointOEventJoinDAO.getStartPoint(testoEvent.getId()).test().assertValue(point -> {
            return testPoint1.getId() == point.getId();
        });
        pointOEventJoinDAO.getPointsNotStart(testoEvent.getId()).test().assertValue(points -> {
            return testPoint2.getId() == points.get(0).getId();
        });
        pointOEventJoinDAO.getOEventsForPoint(testPoint2.getId()).test().assertValue(oEvents -> {
            return testoEvent.getId() == oEvents.get(0).getId();
        });
        pointDAO.delete(testPoint1);
        //Point should not be there
        //pointOEventJoinDAO.getStartPoint(testoEvent.getId()).test().assertValue()

        assertNotSame(-1, pointOEventJoinDAO.delete(testJoin1));
        pointOEventJoinDAO.getOEventsForPoint(testPoint1.getId()).test().assertValue(oEvents -> {
            return oEvents.size() == 0;
        });
        assertNotSame(-1, pointOEventJoinDAO.delete(testPoint2.getId(), testoEvent.getId()));
        pointOEventJoinDAO.getPointsForOEvent(testoEvent.getId()).test().assertValue(points -> {
            return points.size() == 0;
        });
    }


}
