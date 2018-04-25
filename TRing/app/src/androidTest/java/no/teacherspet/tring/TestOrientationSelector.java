package no.teacherspet.tring;

import android.support.test.runner.AndroidJUnit4;

import org.junit.runner.RunWith;

/**
 * Created by petterbjorkaas on 13/02/2018.
 */

@RunWith(AndroidJUnit4.class)
public class TestOrientationSelector {
    /*
    @Rule
    public final ActivityTestRule<OrientationSelector> main = new ActivityTestRule<OrientationSelector>(OrientationSelector.class);

    Instrumentation.ActivityMonitor listOfEventMonitor = getInstrumentation().addMonitor(ListOfSavedEvents.class.getName(), null, false);

    Instrumentation.ActivityMonitor createMonitor = getInstrumentation().addMonitor(CreateOEvent.class.getName(), null, false);

    OrientationSelector orientationSelector;

    @Before
    public void setUp() {
        orientationSelector=main.getActivity();
    }
    @Test
    public void shouldBeAbleToLaunchMainScreen() {

        onView(withId(R.id.perform_btn)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.create_event_btn)).check(ViewAssertions.matches(isDisplayed()));
    }

    @Test
    public void shouldGoToPerformOEventActivity() {

        onView(withId(R.id.perform_btn)).perform(click());
        Activity activity=getInstrumentation().waitForMonitorWithTimeout(listOfEventMonitor, 1000);
        assertNotNull(activity);
        activity.finish();
    }
    @Test
    public void shouldGoToCreateOEventActivity() {

        onView(withId(R.id.create_event_btn)).perform(click());
        Activity activity=getInstrumentation().waitForMonitorWithTimeout(createMonitor, 1000);
        assertNotNull(activity);
        activity.finish();
    }




*/
}
