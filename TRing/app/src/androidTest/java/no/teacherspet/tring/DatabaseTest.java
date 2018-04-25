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

import java.util.HashMap;
import java.util.Map;

import no.teacherspet.tring.Database.DAOs.OEventDao;
import no.teacherspet.tring.Database.DAOs.PointDao;
import no.teacherspet.tring.Database.DAOs.PointOEventJoinDao;
import no.teacherspet.tring.Database.DAOs.UserDao;
import no.teacherspet.tring.Database.Entities.PointOEventJoin;
import no.teacherspet.tring.Database.Entities.RoomOEvent;
import no.teacherspet.tring.Database.Entities.RoomPoint;
import no.teacherspet.tring.Database.Entities.RoomUser;
import no.teacherspet.tring.Database.LocalDatabase;

import static junit.framework.Assert.assertNotSame;

/**
 * Created by Hermann on 15.02.2018.
 */
@RunWith(AndroidJUnit4.class)
public class DatabaseTest {

    private LocalDatabase db;
    private PointDao pointDAO;
    private UserDao userDAO;
    private OEventDao oEventDAO;
    private PointOEventJoinDao pointOEventJoinDAO;

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
        Map<String, String> properties = new HashMap<>();
        properties.put("name", "testRoomPoint");

        pointDAO.getAll().test().assertValue(points -> {
            return points.size() == 0;
        });

        RoomPoint testRoomPoint = new RoomPoint(0, properties, latlng);
        assertNotSame(-1, pointDAO.insert(testRoomPoint));
        pointDAO.findById(testRoomPoint.getId()).test().assertValue(point -> {
            return testRoomPoint.getId() == point.getId();
        });
        pointDAO.findById(testRoomPoint.getId()).test().assertValue(point -> {
            return point.getProperties().get("name").equals(properties.get("name"));
        });
        pointDAO.getAll().test().assertValue(roomPoints -> {
            return roomPoints.get(0).getLatLng().equals(latlng);
        });

        pointDAO.getMaxID().test().assertValue(integer -> {
            return testRoomPoint.getId() == integer;
        });
        pointDAO.delete(testRoomPoint);
        pointDAO.findById(testRoomPoint.getId()).test().assertNoValues();

    }
    private LatLng createLatLng(){
        return new LatLng(37.377166, -122.086966);
    }

    @Test
    public void userTest(){
        String firstname = "firstname";
        String lastname = "lastname";

        RoomUser testRoomUser1 = new RoomUser(0, true, firstname, lastname);
        userDAO.getAll().test().assertValue(users -> {
            return users.size() == 0;
        });
        assertNotSame(-1, userDAO.insert(testRoomUser1));

        userDAO.findById(testRoomUser1.getId()).test().assertValue(user -> {
            return testRoomUser1.getId() == user.getId();
        });
        userDAO.getPersonalUser().test().assertValue(user -> {
            return testRoomUser1.getId() == user.get(0).getId();
        });
        userDAO.getPersonalUser().test().assertValue(user -> {
            return testRoomUser1.getFullName().equals(user.get(0).getFullName());
        });
        userDAO.getMaxID().test().assertValue(integer -> {
            return testRoomUser1.getId() == integer;
        });
        userDAO.delete(testRoomUser1);
        userDAO.getAll().test().assertValue(users -> {
            return users.size() == 0;
        });
    }
    @Test
    public void oEventTest(){
        Map<String, String> properties = new HashMap<>();
        properties.put("name", "testname");
        RoomOEvent testoEventRoom = new RoomOEvent(0, properties);
        oEventDAO.getAll().test().assertValue(oEvents -> {
            return oEvents.size() == 0;
        });
        assertNotSame(-1, oEventDAO.insert(testoEventRoom));
        oEventDAO.findById(testoEventRoom.getId()).test().assertValue(oEvent -> {
            return testoEventRoom.getId() == oEvent.getId();
        });
        oEventDAO.findById(testoEventRoom.getId()).test().assertValue(oEvent -> {
            return properties.get("name").equals(oEvent.getProperties().get("name"));
        });
        oEventDAO.delete(testoEventRoom);

        oEventDAO.getAll().test().assertValue(oEvents -> {
            return oEvents.size() == 0;
        });
    }

    @Test
    public void joinTest(){
        LatLng latlng = createLatLng();
        Map<String, String> properties = new HashMap<>();
        properties.put("name", "testname");

        RoomPoint testRoomPoint1 = new RoomPoint(0, properties, latlng);
        RoomPoint testRoomPoint2 = new RoomPoint(1, properties, latlng);
        assertNotSame(-1, pointDAO.insert(testRoomPoint1));
        assertNotSame(-1, pointDAO.insert(testRoomPoint2));

        String testname = "testoEventRoom";
        RoomOEvent testoEventRoom = new RoomOEvent(0, properties);
        assertNotSame(-1, oEventDAO.insert(testoEventRoom));
        assertNotSame(testRoomPoint1.getId(), testRoomPoint2.getId());

        PointOEventJoin testJoin1 = new PointOEventJoin(testRoomPoint1.getId(), testoEventRoom.getId(), true);
        PointOEventJoin testJoin2 = new PointOEventJoin(testRoomPoint2.getId(), testoEventRoom.getId(), false);

        assertNotSame(-1, pointOEventJoinDAO.insert(testJoin1));
        assertNotSame(-1, pointOEventJoinDAO.insert(testJoin2));

        pointOEventJoinDAO.getPointsForOEvent(testoEventRoom.getId()).test().assertValue(points -> {
            return points.size() == 2 && (points.get(0).getId() == testRoomPoint1.getId() || points.get(0).getId() == testRoomPoint2.getId());
        });
        pointOEventJoinDAO.getStartPoint(testoEventRoom.getId()).test().assertValue(point -> {
            return testRoomPoint1.getId() == point.get(0).getId();
        });
        pointOEventJoinDAO.getPointsNotStart(testoEventRoom.getId()).test().assertValue(points -> {
            return testRoomPoint2.getId() == points.get(0).getId();
        });
        pointOEventJoinDAO.getOEventsForPoint(testRoomPoint2.getId()).test().assertValue(oEvents -> {
            return testoEventRoom.getId() == oEvents.get(0).getId();
        });
        pointDAO.delete(testRoomPoint1);
        oEventDAO.findById(testoEventRoom.getId()).test().assertValue(roomOEvent -> {
            return testoEventRoom.getId() == roomOEvent.getId();
        });

        pointOEventJoinDAO.getStartPoint(testoEventRoom.getId()).test().assertValue(roomPoints -> {
                    return !(roomPoints.size() >0);
        });

        assertNotSame(-1, pointOEventJoinDAO.delete(testJoin1));
        pointOEventJoinDAO.getOEventsForPoint(testRoomPoint1.getId()).test().assertValue(oEvents -> {
            return oEvents.size() == 0;
        });
        assertNotSame(-1, pointOEventJoinDAO.delete(testRoomPoint2.getId(), testoEventRoom.getId()));
        pointOEventJoinDAO.getPointsForOEvent(testoEventRoom.getId()).test().assertValue(points -> {
            return points.size() == 0;
        });
    }


}
