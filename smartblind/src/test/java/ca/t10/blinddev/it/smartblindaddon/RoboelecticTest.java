package ca.t10.blinddev.it.smartblindaddon;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.BuildConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.internal.DoNotInstrument;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowIntent;
import org.robolectric.shadows.ShadowToast;


import ca.t10.blinddev.it.smartblindaddon.ui.home.HomeFragment;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@Config(manifest = "src/main/AndroidManifest.xml")
@RunWith(RobolectricTestRunner.class)
@DoNotInstrument
public class RoboelecticTest {

    Activity settingsActivity, mainActivity;
    Switch note,dark;
    Button setbtn;

    @Before
    public void setup(){
        settingsActivity = Robolectric.buildActivity(SettingsActivity.class).create().get();
        mainActivity = Robolectric.buildActivity(MainActivity.class).create().get();

    }
    // to test to see that the main activity is not null
    @Test
    public void notNull() throws Exception{
        assertNotNull(mainActivity);
    }

    @Test
    public void haveHomeFrag() throws Exception{
        assertNotNull(mainActivity.getFragmentManager().findFragmentById(R.id.nav_home));
    }

    @Test
    public void checkIfToastDisplay() throws Exception{
        note = settingsActivity.findViewById(R.id.settings_notification_mode);
        note.performClick();
        assertEquals(settingsActivity.getString(R.string.enable_notifications), ShadowToast.getTextOfLatestToast());
        assertEquals(settingsActivity.getString(R.string.notif_disable), ShadowToast.getTextOfLatestToast());
    }

    @Test
    public void darkModeEnabled(){
        setbtn = settingsActivity.findViewById(R.id.apply_settings_button);
        dark = settingsActivity.findViewById(R.id.settings_dark_mode);
        dark.performClick();
        SharedPreferences anotherSharedPreferences = settingsActivity.getSharedPreferences("saved", Context.MODE_PRIVATE);
        assertTrue(anotherSharedPreferences.getBoolean("dark", true));

    }


    @Test
    public void openLoginActivity(){
       ShadowActivity shadowActivity = Shadows.shadowOf(mainActivity);

       shadowActivity.clickMenuItem(R.id.menu_login);

       Intent intent = Shadows.shadowOf(mainActivity).peekNextStartedActivity();

       assertEquals(LoginActivity.class.getCanonicalName(),intent.getComponent().getClassName());
    }
}