package no.teacherspet.tring;

import android.support.test.runner.AndroidJUnit4;

import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNotSame;

/**
 * Created by Hermann on 15.02.2018.
 */
@RunWith(AndroidJUnit4.class)
public class PointDAOTest {
/*
    LocalDatabase db;
    PointDao pointDAO;
    UserDao userDAO;
    OEventDao oEventDAO;
    PointOEventJoinDao pointOEventJoinDAO;

    @Before
    public void setUp(){
        db = LocalDatabase.getInstance(InstrumentationRegistry.getTargetContext());

        DeleteDao delete = db.deleteDAO();
        delete.deleteAllPoints();
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

        assertNotNull(testPoint.getId());
        assertEquals(description, testPoint.getDescription());
        assertEquals(latlng, testPoint.getLatLng());

        long[] success = pointDAO.insert(testPoint);
        assertNotSame(-1, success[0]);
        LiveData<List<Point>> points = pointDAO.getAll();
        assertNotNull(points.getValue());

        //assertNotNull(pointDAO.getAll());


        //TODO Problem: Lagrer null i databasen- prøv å fiks med liveData og viewModel class
        assertNotNull(pointDAO.findById(testPoint.getId()));
        assertEquals(testPoint, pointDAO.findById(testPoint.getId()));
        android.util.Log.d("Testpoint id",testPoint.getId() + "");

        pointDAO.delete(testPoint);
        assertNotSame(testPoint, pointDAO.findById(testPoint.getId()));

    }

    private LatLng createLatLng(){
        return new LatLng(37.377166, -122.086966);

    }
*/
}
