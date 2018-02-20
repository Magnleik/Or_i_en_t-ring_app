package no.teacherspet.tring;

import android.app.Instrumentation;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;

/**
 * Created by magnus on 20.02.2018.
 */

@RunWith(AndroidJUnit4.class)
public class TestStartupMenu {
    @Rule
    public final ActivityTestRule<OrientationSelector> main = new ActivityTestRule<OrientationSelector>(OrientationSelector.class);

    Instrumentation.ActivityMonitor createMonitor = getInstrumentation().addMonitor(MapsActivity.class.getName(), null, false);
}
