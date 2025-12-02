package com.example.chance.controller;

import static org.junit.Assert.*;

import com.example.chance.model.Event;
import com.example.chance.model.User;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for Admin functionality.
 */
public class AdminUnitTest {

    private AdminController adminController;
    private User regularUser;
    private Event testEvent;

    @Before
    public void setUp() {
        adminController = new AdminController();

        regularUser = new User("testuser");
        regularUser.setID("user123");
        regularUser.setEmail("test@example.com");

        testEvent = new Event("Test Event", "Test Location", 10, 0.0,
                "Test Description", new java.util.Date(), new java.util.Date(),
                "organizer123", 50);
        testEvent.setID("event123");
    }

    /**
     * US 03.02.01: As an admin, I want to delete user profiles.
     */
    @Test
    public void testDeleteUser() {
        adminController.deleteUser(regularUser);
        assertNull("Username should be null after deletion", regularUser.getUsername());
        assertNull("Email should be null after deletion", regularUser.getEmail());
    }

    /**
     * US 03.01.01: As an admin, I want to delete events.
     */
    @Test
    public void testDeleteEvent() {
        adminController.deleteEvent(testEvent);
        assertNull("Event name should be null after deletion", testEvent.getName());
        assertNull("Event location should be null after deletion", testEvent.getLocation());
    }

    /**
     * US 03.04.01: As an admin, I want to browse events.
     */
    @Test
    public void testBrowseEvents() {
        assertNotNull("Event name should exist", testEvent.getName());
        assertNotNull("Event location should exist", testEvent.getLocation());
    }

    /**
     * US 03.05.01: As an admin, I want to browse user profiles.
     */
    @Test
    public void testBrowseUsers() {
        assertNotNull("User should have username", regularUser.getUsername());
        assertNotNull("User should have email", regularUser.getEmail());
    }
}
