package com.example.chance;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertNotNull;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.Until;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class OrganizerEventTest {

    private UiDevice device;
    private static final int TIMEOUT = 5000;

    @Before
    public void setUp() {
        device = UiDevice.getInstance(getInstrumentation());
        device.pressHome();
        device.wait(Until.hasObject(By.pkg(device.getLauncherPackageName()).depth(0)), TIMEOUT);

        getInstrumentation().getContext()
                .getPackageManager()
                .getLaunchIntentForPackage("com.example.chance")
                .addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);

        getInstrumentation().getContext().startActivity(
                getInstrumentation().getContext()
                        .getPackageManager()
                        .getLaunchIntentForPackage("com.example.chance"));

        device.wait(Until.hasObject(By.pkg("com.example.chance").depth(0)), TIMEOUT);
        loginIfNeeded();
    }

    private void loginIfNeeded() {
        device.wait(Until.gone(By.res("com.example.chance:id/splash_screen")), 10000);
        UiObject2 authScreen = device.wait(Until.findObject(By.res("com.example.chance:id/authentication_layout")),
                2000);
        if (authScreen != null) {
            UiObject2 usernameField = device.findObject(By.res("com.example.chance:id/username"));
            if (usernameField != null) {
                UiObject2 innerEdit = usernameField.findObject(By.clazz("android.widget.EditText"));
                if (innerEdit != null)
                    innerEdit.setText("testuser");
            }
            UiObject2 loginButton = device.findObject(By.res("com.example.chance:id/login_button"));
            if (loginButton != null)
                loginButton.click();
        }
        device.wait(Until.findObject(By.res("com.example.chance:id/home_layout")), TIMEOUT);
    }

    @Test
    public void testCreateEvent() {
        // US 02.01.01 Create a new event
        UiObject2 createEventButton = device.findObject(By.res("com.example.chance:id/button_create_event"));
        assertNotNull("Create Event button should be visible", createEventButton);
        createEventButton.click();

        // Fill form
        UiObject2 nameInput = device.wait(Until.findObject(By.res("com.example.chance:id/event_name_input")), TIMEOUT);
        nameInput.setText("New Test Event");

        device.findObject(By.res("com.example.chance:id/event_address_input")).setText("123 Test St");
        device.findObject(By.res("com.example.chance:id/candidate_maximum_input")).setText("10");
        device.findObject(By.res("com.example.chance:id/description_input")).setText("Test Description");
        device.findObject(By.res("com.example.chance:id/price_input")).setText("0");

        // US 02.03.01 Limit waiting list
        UiObject2 limitInput = device.findObject(By.res("com.example.chance:id/waitinglist_restriction"));
        if (limitInput != null)
            limitInput.setText("50");

        // US 02.04.01 Upload poster (Button check)
        UiObject2 bannerButton = device.findObject(By.res("com.example.chance:id/add_banner_button"));
        assertNotNull(bannerButton);
        // bannerButton.click(); // Might open gallery, which is hard to test.

        // Submit
        UiObject2 submitButton = device.findObject(By.res("com.example.chance:id/submit_button"));
        submitButton.click();

        // Verify creation (maybe check if it appears in "Created Events")
    }

    @Test
    public void testUpdateEventPoster() {
        // US 02.04.02 Update event poster
        // Go to created events
        UiObject2 createdEventsButton = device.findObject(By.res("com.example.chance:id/button_event_created"));
        if (createdEventsButton != null) {
            createdEventsButton.click();
            // Select an event
            // Click update poster/banner button if available in edit mode
        }
    }
}
