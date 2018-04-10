package no.teacherspet.tring;

import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import no.teacherspet.tring.activities.PerformOEvent;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by petterbjorkaas on 22/02/2018.
 */
@RunWith(AndroidJUnit4.class)
public class TestPerformOEvent {
    @Rule
    public final ActivityTestRule<PerformOEvent> main = new ActivityTestRule<PerformOEvent>(PerformOEvent.class);

    private PerformOEvent activity;
    


    @Before
    public void init(){
        activity = main.getActivity();
    }

    @Test
    public void shouldBeAbleToLaunchMainScreen(){
        onView(withId(R.id.show_position_btn)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.map_used_in_event)).check(ViewAssertions.matches(isDisplayed()));
    }


}
