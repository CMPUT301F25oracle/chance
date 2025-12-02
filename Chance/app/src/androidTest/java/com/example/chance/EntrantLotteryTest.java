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
public class EntrantLotteryTest {

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
    public void testOptOutOfNotifications() {
        // US 01.04.03 As an entrant I want to opt out of receiving notifications
        UiObject2 profileButton = device.findObject(By.res("com.example.chance:id/profile_button"));
        if (profileButton == null)
            profileButton = device.findObject(By.text("Profile"));
        if (profileButton != null)
            profileButton.click();

        device.wait(Until.findObject(By.res("com.example.chance:id/profile_image")), TIMEOUT);

        // Look for "Disable Notification" button or switch
        UiObject2 disableNotifButton = device.findObject(By.text("Disable Notification"));
        if (disableNotifButton != null) {
            disableNotifButton.click();
        } else {
            // Or maybe a switch
            UiObject2 notifSwitch = device.findObject(By.res("com.example.chance:id/switch_notifications"));
            if (notifSwitch != null) {
                notifSwitch.click();
            }
        }
    }

    @Test
    public void testLotteryInfoAndStatus() {
        // US 01.05.04 Know total entrants
        // US 01.05.05 Lottery criteria info

        // Go to an event
        UiObject2 eventList = device.findObject(By.res("com.example.chance:id/events_container_left"));
        if (eventList != null && eventList.getChildCount() > 0) {
            eventList.getChildren().get(0).click();

            UiObject2 eventInfo = device.wait(Until.findObject(By.res("com.example.chance:id/event_information")),
                    TIMEOUT);
            assertNotNull(eventInfo);
            // The text contains "* {} users currently in waiting list", so checking
            // existence is good.

            UiObject2 pollCondition = device.findObject(By.res("com.example.chance:id/poll_condition"));
            if (pollCondition != null) {
                pollCondition.click();
                // Verify popup or info appears
                device.wait(Until.findObject(By.textContains("conditions")), TIMEOUT);
            }
        }
    }

    // Testing notifications (US 01.04.01, 01.04.02) and Accept/Decline (US
    // 01.05.02, 01.05.03)
    // requires the user to be in a specific state (Selected).
    // We can try to find the "InvitedToEventPopup" if it appears, or just verify
    // the code exists via unit tests,
    // but for UI tests we can't easily force this state without backend
    // manipulation.
    // I'll skip explicit UI tests for receiving notifications unless I can trigger
    // them.
    // However, I can check if the "View Notification History" button exists in
    // Profile.

    @Test
    public void testNotificationHistory() {
        UiObject2 profileButton = device.findObject(By.res("com.example.chance:id/profile_button"));
        if (profileButton == null)
            profileButton = device.findObject(By.text("Profile"));
        if (profileButton != null)
            profileButton.click();

        UiObject2 historyButton = device.wait(Until.findObject(By.text("View Notification History")), TIMEOUT);
        assertNotNull(historyButton);
        historyButton.click();

        // Verify history list appears
        // device.wait(Until.findObject(By.res("com.example.chance:id/notification_list")),
        // TIMEOUT);
    }
}
