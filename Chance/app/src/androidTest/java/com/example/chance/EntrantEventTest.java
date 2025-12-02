package com.example.chance;

import static org.junit.Assert.*;

import com.example.chance.model.Notification;
import com.example.chance.model.User;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Test class for Entrant Notifications
 * Tests US 01.04.01 - 01.04.03
 */
public class EntrantEventTest {

    private User testUser;
    private Notification winNotification;
    private Notification loseNotification;

    @Before
    public void setUp() {
        testUser = new User("testUser");
        testUser.setID("user123");

        // US 01.04.01: Win notification
        Map<String, String> winMeta = new HashMap<>();
        winMeta.put("title", "You've been invited to join Test Event");
        winMeta.put("description", "Congratulations! Click to accept.");
        winMeta.put("eventID", "event123");

        winNotification = new Notification();
        winNotification.setType(0); // Type 0 = Win
        winNotification.setMeta(winMeta);
        winNotification.setCreationDate(new Date());

        // US 01.04.02: Lose notification
        Map<String, String> loseMeta = new HashMap<>();
        loseMeta.put("title", "Update from Test Event");
        loseMeta.put("description", "Unfortunately, you were not selected.");
        loseMeta.put("eventID", "event123");

        loseNotification = new Notification();
        loseNotification.setType(1); // Type 1 = Lose
        loseNotification.setMeta(loseMeta);
        loseNotification.setCreationDate(new Date());
    }

    /**
     * US 01.04.01: As an entrant, I want to receive notification when chosen (win)
     */
    @Test
    public void testReceiveWinNotification() {
        // Assert
        assertNotNull("Win notification should exist", winNotification);
        assertEquals("Should be win type (0)", 0, winNotification.getType());
        assertTrue("Title should mention invitation",
                winNotification.getMeta().get("title").contains("invited"));
        assertNotNull("Should have event ID",
                winNotification.getMeta().get("eventID"));
    }

    /**
     * US 01.04.02: As an entrant, I want to receive notification when not chosen (lose)
     */
    @Test
    public void testReceiveLoseNotification() {
        // Assert
        assertNotNull("Lose notification should exist", loseNotification);
        assertEquals("Should be lose type (1)", 1, loseNotification.getType());
        assertNotNull("Should have description",
                loseNotification.getMeta().get("description"));
        assertNotNull("Should have event ID",
                loseNotification.getMeta().get("eventID"));
    }

    /**
     * US 01.04.03: As an entrant, I want to opt out of notifications
     */
    @Test
    public void testOptOutOfNotifications() {
        // Act
        testUser.setNotificationsEnabled(false);

        // Assert
        assertFalse("Notifications should be disabled",
                testUser.getNotificationsEnabled());
    }

    /**
     * US 01.04.03: Test opt-in to notifications (default behavior)
     */
    @Test
    public void testOptInToNotifications() {
        // Assert - Default should be enabled
        assertTrue("Notifications should be enabled by default",
                testUser.getNotificationsEnabled());

        // Act - Explicitly enable
        testUser.setNotificationsEnabled(true);

        // Assert
        assertTrue("Notifications should remain enabled",
                testUser.getNotificationsEnabled());
    }

    /**
     * Test: Notification has timestamp
     */
    @Test
    public void testNotificationTimestamp() {
        // Arrange
        Date before = new Date();

        // Act
        Notification notification = new Notification();
        notification.setCreationDate(new Date());

        Date after = new Date();

        // Assert
        assertNotNull("Notification should have timestamp",
                notification.getCreationDate());
        assertTrue("Timestamp should be between before and after",
                !notification.getCreationDate().before(before) &&
                        !notification.getCreationDate().after(after));
    }

    /**
     * Test: Notification metadata contains event information
     */
    @Test
    public void testNotificationMetadata() {
        // Assert
        Map<String, String> meta = winNotification.getMeta();
        assertNotNull("Metadata should not be null", meta);
        assertTrue("Should have title", meta.containsKey("title"));
        assertTrue("Should have description", meta.containsKey("description"));
        assertTrue("Should have eventID", meta.containsKey("eventID"));
    }
}