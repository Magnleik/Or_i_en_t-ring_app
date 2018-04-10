package no.teacherspet.tring;

import android.app.Activity;
import android.app.Instrumentation;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import no.teacherspet.tring.activities.OrientationSelector;
import no.teacherspet.tring.activities.StartupMenu;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.assertNotNull;

/**
 * Created by magnus on 20.02.2018.
 */

@RunWith(AndroidJUnit4.class)
public class TestStartupMenu {
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
}
