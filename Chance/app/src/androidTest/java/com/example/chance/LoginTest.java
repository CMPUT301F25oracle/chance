package com.example.chance;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.Until;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginTest {

    private UiDevice device;
    private static final int TIMEOUT = 5000; // 5 seconds timeout

    @Before
    public void setUp() {
        // Initialize UiDevice instance
        device = UiDevice.getInstance(getInstrumentation());
        
        // Start from the home screen
        device.pressHome();
        
        // Wait for launcher
        device.wait(Until.hasObject(By.pkg(device.getLauncherPackageName()).depth(0)), TIMEOUT);
        
        // Launch the app
        getInstrumentation().getContext()
                .getPackageManager()
                .getLaunchIntentForPackage("com.example.chance")
                .addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
        
        getInstrumentation().getContext().startActivity(
                getInstrumentation().getContext()
                        .getPackageManager()
                        .getLaunchIntentForPackage("com.example.chance")
        );
        
        // Wait for the app to appear
        device.wait(Until.hasObject(By.pkg("com.example.chance").depth(0)), TIMEOUT);
    }

    @Test
    public void testLoginAndNavigateToProfile() throws IOException {
        // Wait for splash screen to finish (adjust timeout based on your splash duration)
        device.wait(Until.gone(By.res("com.example.chance:id/splash_screen")), 10000);
        
        // If user is not authenticated, the Authentication screen should appear
        // Wait for login/authentication elements to appear
        UiObject2 authScreen = device.wait(
                Until.findObject(By.res("com.example.chance:id/authentication_layout")), 
                TIMEOUT
        );
        
        if (authScreen != null) {
            // Perform login - adjust these resource IDs based on your actual layout
            // Find email/username field
            UiObject2 emailField = device.findObject(By.res("com.example.chance:id/username"));
            if (emailField != null) {
                assertTrue(false);
                UiObject2 innerEdit = emailField.findObject(By.clazz("android.widget.EditText"));
                innerEdit.click();
                innerEdit.setText("lamersc");

            }
            
            // Find password field
            UiObject2 passwordField = device.findObject(
                    By.res("com.example.chance:id/password")
            );
            if (passwordField != null) {
                passwordField.setText("pppppp");
            }
            
            // Click login button
            UiObject2 loginButton = device.findObject(
                    By.res("com.example.chance:id/login_button")
            );
            if (loginButton != null) {
                loginButton.click();
            }
        }
        
        // Wait for home screen to appear after authentication
        UiObject2 homeScreen = device.wait(
                Until.findObject(By.res("com.example.chance:id/home_layout")), 
                TIMEOUT
        );
        assertTrue("Home screen should be displayed after login", homeScreen != null);
        
        // Navigate to profile screen
        // Option 1: If you have a profile button in navbar
        UiObject2 profileButton = device.findObject(
                By.res("com.example.chance:id/profile_button")
        );
        if (profileButton != null) {
            profileButton.click();
        }
        
        // Option 2: If using text-based navigation
        // UiObject2 profileButton = device.findObject(By.text("Profile"));
        // if (profileButton != null) {
        //     profileButton.click();
        // }
        
        // Verify profile screen is displayed
        UiObject2 profileScreen = device.wait(
                Until.findObject(By.res("com.example.chance:id/profile_layout")), 
                TIMEOUT
        );
        assertTrue("Profile screen should be displayed", profileScreen != null);
    }

    @Test
    public void testSplashScreenTransition() {
        // Verify splash screen appears
        UiObject2 splashScreen = device.wait(
                Until.findObject(By.res("com.example.chance:id/splash_screen")), 
                TIMEOUT
        );
        assertTrue("Splash screen should appear", splashScreen != null);
        
        // Wait for splash screen to disappear
        boolean splashGone = device.wait(
                Until.gone(By.res("com.example.chance:id/splash_screen")), 
                10000
        );
        assertTrue("Splash screen should disappear", splashGone);
        
        // Verify navigation occurred (either to Home or Authentication)
        boolean navigated = device.wait(
                Until.hasObject(
                        By.res("com.example.chance:id/home_layout")
                                .clazz("android.widget.FrameLayout")
                ), 
                TIMEOUT
        ) || device.wait(
                Until.hasObject(
                        By.res("com.example.chance:id/authentication_layout")
                ), 
                TIMEOUT
        );
        assertTrue("Should navigate to either Home or Authentication screen", navigated);
    }
}