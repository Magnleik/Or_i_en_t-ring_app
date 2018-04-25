package no.teacherspet.tring;

import android.support.test.runner.AndroidJUnit4;

import org.junit.runner.RunWith;

/**
 * Created by magnus on 20.02.2018.
 */

@RunWith(AndroidJUnit4.class)
public class TestStartupMenu {
    /*
    @Rule
    public final ActivityTestRule<StartupMenu> main = new ActivityTestRule<StartupMenu>(StartupMenu.class);

    Instrumentation.ActivityMonitor orientationMonitor = getInstrumentation().addMonitor(OrientationSelector.class.getName(), null, false);

    private StartupMenu startupMenu;

    @Before
    public void setUp(){
        startupMenu= main.getActivity();
    }

    @Test
    public void ShouldBeAbleToLaunchMainScreen(){
        onView(withId(R.id.orientering_btn)).check(ViewAssertions.matches(isDisplayed()));
    }

    @Test
    public void ShouldGoToOrientationSelector(){
        onView(withId(R.id.orientering_btn)).perform(click());
        Activity activity=getInstrumentation().waitForMonitorWithTimeout(orientationMonitor, 1000);
        assertNotNull(activity);
        activity.finish();
    }
    */
}
