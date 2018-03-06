package no.teacherspet.tring;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.HashMap;

import connection.Event;
import connection.Point;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by magnus on 06.03.2018.
 */

public class TestPerformOEvent {

    private PerformOEvent activity;
    private Event event;
    private HashMap<Integer,Event> mockedHashMap;

    @Mock
    StartupMenu startupMenu;

    @Before
    public void init(){
        activity = new PerformOEvent();
        startupMenu = mock(StartupMenu.class);
        mockedHashMap = new HashMap<>();
        event = new Event();
        event.addPost(new Point(10.416136, 10.405297,"test1"));
        event.addPost(new Point(10.416136, 10.405241,"test2"));
        event.addPost(new Point(10.416140, 10.405297,"test3"));
        mockedHashMap.put(0,event);
    }

    @Test
    public void shouldReadPoints(){
        ArrayList<Point> mockedArray = event.getPoints();
        when(startupMenu.getTestEvents()).thenReturn(mockedHashMap);
        ArrayList<Point> testArray = activity.readPoints();
        assertEquals(testArray,mockedHashMap.get(0).getPoints());
    }
}
