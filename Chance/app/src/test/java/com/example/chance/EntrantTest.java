package com.example.chance;

import org.junit.Test;

import static org.junit.Assert.*;

import com.example.chance.model.WaitingList;

/**
 * Unit tests for WaitingList model.
 */
public class EntrantTest {

    private WaitingList mock_waitingList = new WaitingList();

    @Test
    public void common_waiting_list_id() {
        mock_waitingList.setId("wl123");
        assertEquals("wl123", mock_waitingList.getId());
    }

    @Test
    public void common_waiting_list_event_id() {
        mock_waitingList.setEventId("evt456");
        assertEquals("evt456", mock_waitingList.getEventId());
    }

    @Test
    public void empty_waiting_list_id() {
        mock_waitingList.setId("");
        assertTrue("ID is empty", mock_waitingList.getId().isEmpty());
    }

    @Test
    public void null_event_id() {
        mock_waitingList.setEventId(null);
        assertNull("Event ID should be null", mock_waitingList.getEventId());
    }
}
