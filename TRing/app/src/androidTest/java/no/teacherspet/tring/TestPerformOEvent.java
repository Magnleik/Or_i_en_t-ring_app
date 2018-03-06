package no.teacherspet.tring;

import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Created by petterbjorkaas on 22/02/2018.
 */
@RunWith(MockitoJUnitRunner.class)
public class TestPerformOEvent {
    @Rule
    public final ActivityTestRule<PerformOEvent> main = new ActivityTestRule<PerformOEvent>(PerformOEvent.class);
    

}
