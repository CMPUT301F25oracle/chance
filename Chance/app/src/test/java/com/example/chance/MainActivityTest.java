package com.example.chance;

import static org.junit.Assert.assertTrue;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.chance.views.Authentication;
import com.example.chance.views.Home;
import com.example.chance.views.Profile;


import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)

// For the Main activity testing, the issue is when you test the class as a whole, it will fail
// but when you test the cases individually, it will pass
// For this problem, we will fix it very soon

public class MainActivityTest {

    private ActivityScenario<MainActivity> scenario;

    private Fragment currentFragment(MainActivity activity) {
        FragmentManager fm = activity.getSupportFragmentManager();
        return fm.findFragmentById(R.id.content_view);
    }

    @Test
    public void starts_with_authentication_fragment() {
        scenario = ActivityScenario.launch(MainActivity.class);
        scenario.onActivity(activity -> {
            Fragment f = currentFragment(activity);
            assertTrue("Should start on Authentication fragment", f instanceof Authentication);
        });
    }

    @Test
    public void nav_home_loads_home_fragment() {
        scenario = ActivityScenario.launch(MainActivity.class);
        onView(withId(R.id.navbar_home_button)).perform(click());
        scenario.onActivity(activity -> {
            Fragment f = currentFragment(activity);
            assertTrue("Home fragment should be shown", f instanceof Home);
        });
    }

    @Test
    public void nav_profile_loads_profile_fragment() {
        scenario = ActivityScenario.launch(MainActivity.class);
        onView(withId(R.id.navbar_profile_button)).perform(click());
        scenario.onActivity(activity -> {
            Fragment f = currentFragment(activity);
            assertTrue("Profile fragment should be shown", f instanceof Profile);
        });
    }
}