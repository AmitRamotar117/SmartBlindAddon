package ca.t10.blinddev.it.smartblindaddon;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.internal.DoNotInstrument;

import static org.junit.Assert.*;

import android.os.Build;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.R)
@DoNotInstrument
public class  ExampleUnitTest{
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }
}