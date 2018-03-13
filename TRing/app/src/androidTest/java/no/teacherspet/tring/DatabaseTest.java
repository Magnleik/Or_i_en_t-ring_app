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
    Integer id;
    Disposable idDisposable;

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
        id = 0;
    }
    @After
    public void tearDown(){
        db.close();
    }

    @Test
    public void pointTest(){
        LatLng latlng = createLatLng();
        String description = "testPoint";
        idDisposable = pointDAO.getMaxID().subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread())
                .defaultIfEmpty(0).subscribe(integer -> id = integer + 1);;
        Point testPoint = new Point(id, description, latlng);
        idDisposable.dispose();

        idDisposable = pointDAO.getAll().subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread())
                .test().assertEmpty();
        idDisposable.dispose();
        assertNotSame(-1, pointDAO.insert(testPoint));

        idDisposable = pointDAO.findById(testPoint.getId()).subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(point -> assertEquals(testPoint, point));
        idDisposable.dispose();
        pointDAO.delete(testPoint);
        idDisposable = pointDAO.getAll().subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread())
                .test().assertEmpty();
        idDisposable.dispose();
    }
    private LatLng createLatLng(){
        return new LatLng(37.377166, -122.086966);
    }

    @Test
    public void userTest(){
        String firstname = "firstname";
        String lastname = "lastname";

        idDisposable = userDAO.getMaxID().subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread())
                .defaultIfEmpty(0).subscribe(integer -> id = integer);
        User testUser1 = new User(id + 1, true, firstname, lastname);
        idDisposable.dispose();

        idDisposable = userDAO.getAll().subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread())
                .test().assertEmpty();
        idDisposable.dispose();
        assertNotSame(-1, userDAO.insert(testUser1));

        idDisposable = userDAO.findById(testUser1.getId()).subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> assertEquals(testUser1, user));
        idDisposable.dispose();
        idDisposable = userDAO.getPersonalUser().subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> assertEquals(testUser1, user));
        idDisposable.dispose();

        userDAO.delete(testUser1);
        idDisposable = userDAO.getAll().subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread())
                .test().assertEmpty();
        idDisposable.dispose();
    }
    @Test
    public void oEventTest(){
        String testname = "testname";
        idDisposable = oEventDAO.getMaxID().subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread())
                .defaultIfEmpty(0).subscribe(integer -> id = integer);
        OEvent testoEvent = new OEvent(id, testname);
        idDisposable.dispose();

        assertNotSame(-1, oEventDAO.insert(testoEvent));
        idDisposable = oEventDAO.getAll().subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(oEvents -> assertNotNull(oEvents));
        idDisposable.dispose();
        idDisposable = oEventDAO.findById(testoEvent.getId()).subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(oEvent -> assertEquals(testoEvent, oEvent));
        idDisposable.dispose();
        oEventDAO.delete(testoEvent);
        idDisposable = oEventDAO.findById(testoEvent.getId()).subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(oEvent -> assertNotSame(testoEvent, oEvent));
        idDisposable.dispose();
    }

    @Test
    public void joinTest(){
        LatLng latlng = createLatLng();
        String description = "testPoint";
        Point testPoint1 = new Point(id, description, latlng);
        pointDAO.insert(testPoint1);
        idDisposable = pointDAO.getMaxID().subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread())
                .defaultIfEmpty(0).subscribe(integer -> id = integer);

        Point testPoint2 = new Point(id +1, description, latlng);
        pointDAO.insert(testPoint2);
        assertNotSame(testPoint1.getId(), testPoint2.getId());
        idDisposable.dispose();

        String testname = "testoEvent";
        OEvent testoEvent = new OEvent(0, testname);
        assertNotSame(-1, oEventDAO.insert(testoEvent));
        assertNotSame(testPoint1.getId(), testPoint2.getId());

        PointOEventJoin testJoin1 = new PointOEventJoin(testPoint1.getId(), testoEvent.getId(), true);
        PointOEventJoin testJoin2 = new PointOEventJoin(testPoint2.getId(), testoEvent.getId(), false);

        assertNotSame(-1, pointOEventJoinDAO.insert(testJoin1));
        assertNotSame(-1, pointOEventJoinDAO.insert(testJoin2));

        idDisposable = pointOEventJoinDAO.getPointsForOEvent(testoEvent.getId()).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(points -> assertTrue(points.size() == 2 &&
                testPoint1.id == points.get(0).getId()));
        idDisposable.dispose();
        idDisposable = pointOEventJoinDAO.getStartPoint(testoEvent.getId()).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(point -> assertEquals(testPoint1, point));
        idDisposable.dispose();
        idDisposable = pointOEventJoinDAO.getPointsNotStart(testoEvent.getId()).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(points -> assertTrue(testPoint2.id == points.get(0).getId()));
        idDisposable.dispose();
        idDisposable = pointOEventJoinDAO.getOEventsForPoint(testPoint2.getId()).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(oEvents -> assertTrue(testoEvent.id == oEvents.get(0).getId()));
        idDisposable.dispose();
        pointDAO.delete(testPoint1);

        idDisposable = pointOEventJoinDAO.getStartPoint(testoEvent.getId()).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(point -> assertNotSame(testPoint1, point));
        idDisposable.dispose();
        pointOEventJoinDAO.delete(testJoin1);
        idDisposable = pointOEventJoinDAO.getStartPoint(testoEvent.getId()).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(point -> assertNotSame(testPoint1, point));
        idDisposable.dispose();
        pointOEventJoinDAO.delete(testPoint2.getId(), testoEvent.getId());
        idDisposable = pointOEventJoinDAO.getPointsForOEvent(testoEvent.getId()).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(points -> assertFalse(points.contains(testPoint2)));
        idDisposable.dispose();
    }


}
