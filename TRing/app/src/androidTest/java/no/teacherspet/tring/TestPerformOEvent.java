package no.teacherspet.tring;

import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;

import connection.Event;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by petterbjorkaas on 22/02/2018.
 */
@RunWith(MockitoJUnitRunner.class)
public class TestPerformOEvent {
    @Rule
    public final ActivityTestRule<PerformOEvent> main = new ActivityTestRule<PerformOEvent>(PerformOEvent.class);

    @Mock
    StartupMenu startupMenu;

    private HashMap<Integer, Event> mockedHashMap;
    private Event event;


    PerformOEvent activity;

    @Before
    public void init(){
        activity = main.getActivity();
        mockedHashMap.put(0,event);
    }

    @Test
    public void shouldBeAbleToLaunchMainScreen(){
        onView(withId(R.id.show_position_btn)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.map_used_in_event)).check(ViewAssertions.matches(isDisplayed()));
    }
    @Test
    public void shouldReadPoints(){
        startupMenu = mock(StartupMenu.class);
        when(startupMenu.getTestEvents()).thenReturn(mockedHashMap);

    }


}
