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
public class OrganizerLotteryTest {

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

    private void navigateToMyEvent() {
        UiObject2 createdEventsButton = device.findObject(By.res("com.example.chance:id/button_event_created"));
        if (createdEventsButton != null) {
            createdEventsButton.click();
            // Select first event
            UiObject2 list = device.wait(Until.findObject(By.res("com.example.chance:id/events_container_left")),
                    TIMEOUT); // Assuming same ID for list
            if (list != null && list.getChildCount() > 0) {
                list.getChildren().get(0).click();
            }
        }
    }

    @Test
    public void testOrganizerLotteryActions() {
        // US 02.05.02 Sample attendees (Draw Entrants)
        navigateToMyEvent();

        UiObject2 drawButton = device.wait(Until.findObject(By.res("com.example.chance:id/draw_entrants_button")),
                TIMEOUT);
        if (drawButton != null) {
            drawButton.click();
            // Verify popup or action
        }

        // US 02.05.03 Draw replacement
        UiObject2 replacementButton = device.findObject(By.res("com.example.chance:id/draw_replacement_button"));
        if (replacementButton != null) {
            replacementButton.click();
        }
    }

    @Test
    public void testOrganizerLists() {
        // US 02.06.01 View invited entrants (Chosen List)
        // US 02.06.02 View cancelled entrants
        // US 02.06.03 View final enrolled list
        navigateToMyEvent();

        UiObject2 viewChosenButton = device
                .wait(Until.findObject(By.res("com.example.chance:id/view_chosen_list_button")), TIMEOUT);
        if (viewChosenButton != null)
            viewChosenButton.click();

        device.pressBack();

        UiObject2 viewCancelledButton = device.findObject(By.res("com.example.chance:id/view_cancelled_list_button"));
        if (viewCancelledButton != null)
            viewCancelledButton.click();

        device.pressBack();

        UiObject2 viewFinalButton = device.findObject(By.res("com.example.chance:id/view_final_entrants_button"));
        if (viewFinalButton != null)
            viewFinalButton.click();
    }

    @Test
    public void testExportFinalList() {
        // US 02.06.05 Export final list
        navigateToMyEvent();
        UiObject2 exportButton = device
                .wait(Until.findObject(By.res("com.example.chance:id/export_final_entrants_button")), TIMEOUT);
        if (exportButton != null)
            exportButton.click();
    }
}
