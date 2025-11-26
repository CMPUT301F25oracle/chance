package com.example.chance;

import com.example.chance.model.Event;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Date;

/**
 * Unit tests for Event model
 */
public class EventTest {

    private Event event;

    @Before
    public void setUp() {
        event = new Event("Test Event", "Test Location", 50, 10.0,
                "Test Description", new Date(), new Date(), "organizer123", 30);
    }

    @Test
    public void testEventCreation() {
        // TODO: Implement test
    }

    @Test
    public void testAddToWaitingList() {
        // TODO: Implement test
    }

    @Test
    public void testLeaveWaitingList() {
        // TODO: Implement test
    }

    @Test
    public void testAcceptInvitation() {
        // TODO: Implement test
    }

    @Test
    public void testRejectInvitation() {
        // TODO: Implement test
    }

    @Test
    public void testViewWaitingListEntrants() {
        // TODO: Implement test
    }

    @Test
    public void testViewWaitingListEntrantsCount() {
        // TODO: Implement test
    }

    @Test
    public void testPollForInvitation() {
        // TODO: Implement test
    }

    @Test
    public void testDuplicateWaitingListEntry() {
        // TODO: Implement test
    }

    @Test
    public void testEventCapacity() {
        // TODO: Implement test
    }

    @Test
    public void testEventIsFull() {
        // TODO: Implement test
    }

    @Test
    public void testGettersAndSetters() {
        // TODO: Implement test
    }
}