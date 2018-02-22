package no.teacherspet.tring;

import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.LiveData;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.android.gms.maps.model.LatLng;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import no.teacherspet.tring.Database.DAOs.DeleteDAO;
import no.teacherspet.tring.Database.DAOs.OEventDAO;
import no.teacherspet.tring.Database.DAOs.PointDAO;
import no.teacherspet.tring.Database.DAOs.PointOEventJoinDAO;
import no.teacherspet.tring.Database.DAOs.UserDAO;
import no.teacherspet.tring.Database.Entities.Point;
import no.teacherspet.tring.Database.LocalDatabase;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNotSame;

/**
 * Created by Hermann on 15.02.2018.
 */
@RunWith(AndroidJUnit4.class)
public class PointDAOTest {

    LocalDatabase db;
    PointDAO pointDAO;
    UserDAO userDAO;
    OEventDAO oEventDAO;
    PointOEventJoinDAO pointOEventJoinDAO;

    @Before
    public void setUp(){
        db = LocalDatabase.getInstance(InstrumentationRegistry.getTargetContext());

        DeleteDAO delete = db.getDeleteDAO();
        delete.deleteAllPoints();
        pointDAO = db.getPointDAO();
        userDAO = db.getUserDAO();
        oEventDAO = db.getOEventDAO();
        pointOEventJoinDAO = db.getPointOEventJoinDAO();
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

}
