package com.example.chance;

import com.example.chance.model.WaitingList;

import org.junit.Test;

import static org.junit.Assert.*;


// Unit tests for the WaitingList class
// For the view and join and leave waiting list tests, we will implement them
public class WaitingListTest {

    private WaitingList waitingList = new WaitingList();

    @Test
    public void add_entrant() {
        String entrantId = "user123";
        waitingList.addEntrant(entrantId);
        assertTrue(waitingList.getEntrantIds().contains(entrantId));
    }

    @Test
    public void remove_entrant() {
        String entrantId = "user123";
        waitingList.addEntrant(entrantId);
        waitingList.removeEntrant(entrantId);
        assertFalse(waitingList.getEntrantIds().contains(entrantId));
    }

    @Test
    public void view_waiting_entrants(){}

    @Test
    public void view_waiting_counts(){}

    @Test
    public void leave_waiting_list(){}

    @Test
    public void join_waiting_list(){}

}
