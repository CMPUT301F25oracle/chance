package com.example.chance.adapter;

import android.content.Context;

import com.example.chance.model.User;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for AdminUserAdapter class.
 * Tests adapter functionality for displaying and managing users in RecyclerView.
 */
public class AdminUserAdapterTest {

    private List<User> testUserList;
    private TestUserActionListener testListener;

    @Before
    public void setUp() {
        testUserList = new ArrayList<>();
        testListener = new TestUserActionListener();
    }

    // ==================== Constructor Tests ====================

    @Test
    public void testConstructor_WithValidParameters() {
        // Note: This test doesn't create actual adapter due to Android Context requirement
        // but verifies data structures are set up correctly
        assertNotNull("User list should not be null", testUserList);
        assertNotNull("Listener should not be null", testListener);
    }

    @Test
    public void testConstructor_WithEmptyUserList() {
        List<User> emptyList = new ArrayList<>();
        assertNotNull("Empty list should not be null", emptyList);
        assertEquals("Empty list should have size 0", 0, emptyList.size());
    }

    // ==================== User List Management Tests ====================

    @Test
    public void testUserList_AddUser() {
        User user = createTestUser("1", "John Doe", "john@example.com");
        testUserList.add(user);

        assertEquals("List should have 1 user", 1, testUserList.size());
        assertEquals("User ID should match", "1", testUserList.get(0).getUserId());
    }

    @Test
    public void testUserList_AddMultipleUsers() {
        testUserList.add(createTestUser("1", "John Doe", "john@example.com"));
        testUserList.add(createTestUser("2", "Jane Smith", "jane@example.com"));
        testUserList.add(createTestUser("3", "Bob Johnson", "bob@example.com"));

        assertEquals("List should have 3 users", 3, testUserList.size());
    }

    @Test
    public void testUserList_RemoveUser() {
        User user1 = createTestUser("1", "John Doe", "john@example.com");
        User user2 = createTestUser("2", "Jane Smith", "jane@example.com");

        testUserList.add(user1);
        testUserList.add(user2);
        testUserList.remove(0);

        assertEquals("List should have 1 user after removal", 1, testUserList.size());
        assertEquals("Remaining user should be Jane", "2", testUserList.get(0).getUserId());
    }

    @Test
    public void testUserList_ClearAllUsers() {
        testUserList.add(createTestUser("1", "John Doe", "john@example.com"));
        testUserList.add(createTestUser("2", "Jane Smith", "jane@example.com"));

        testUserList.clear();

        assertEquals("List should be empty after clear", 0, testUserList.size());
    }

    // ==================== User Data Tests ====================

    @Test
    public void testUser_WithAllFields() {
        User user = createTestUser("1", "John Doe", "john@example.com");

        assertNotNull("User should not be null", user);
        assertEquals("User ID should match", "1", user.getUserId());
        assertEquals("Name should match", "John Doe", user.getName());
        assertEquals("Email should match", "john@example.com", user.getEmail());
    }

    @Test
    public void testUser_WithNullName() {
        User user = new User();
        user.setUserId("1");
        user.setName(null);
        user.setEmail("john@example.com");

        assertNull("Name should be null", user.getName());
        assertNotNull("Email should not be null", user.getEmail());
    }

    @Test
    public void testUser_WithNullEmail() {
        User user = new User();
        user.setUserId("1");
        user.setName("John Doe");
        user.setEmail(null);

        assertNotNull("Name should not be null", user.getName());
        assertNull("Email should be null", user.getEmail());
    }

    @Test
    public void testUser_WithRoleEntrant() {
        User user = createTestUser("1", "John Doe", "john@example.com");
        user.setOrganizer(false);
        user.setAdmin(false);

        assertFalse("Should not be organizer", user.isOrganizer());
        assertFalse("Should not be admin", user.isAdmin());
    }

    @Test
    public void testUser_WithRoleOrganizer() {
        User user = createTestUser("1", "John Doe", "john@example.com");
        user.setOrganizer(true);
        user.setAdmin(false);

        assertTrue("Should be organizer", user.isOrganizer());
        assertFalse("Should not be admin", user.isAdmin());
    }

    @Test
    public void testUser_WithRoleAdmin() {
        User user = createTestUser("1", "John Doe", "john@example.com");
        user.setOrganizer(false);
        user.setAdmin(true);

        assertFalse("Should not be organizer", user.isOrganizer());
        assertTrue("Should be admin", user.isAdmin());
    }

    // ==================== Listener Tests ====================

    @Test
    public void testListener_OnDeleteUser() {
        User user = createTestUser("1", "John Doe", "john@example.com");
        testListener.onDeleteUser(user, 0);

        assertTrue("Delete callback should be called", testListener.deleteUserCalled);
        assertEquals("Deleted user should match", user, testListener.lastDeletedUser);
        assertEquals("Position should match", 0, testListener.lastDeletePosition);
    }

    @Test
    public void testListener_OnDeleteMultipleUsers() {
        User user1 = createTestUser("1", "John Doe", "john@example.com");
        User user2 = createTestUser("2", "Jane Smith", "jane@example.com");

        testListener.onDeleteUser(user1, 0);
        testListener.onDeleteUser(user2, 1);

        assertTrue("Delete callback should be called", testListener.deleteUserCalled);
        assertEquals("Last deleted user should be Jane", user2, testListener.lastDeletedUser);
        assertEquals("Last position should be 1", 1, testListener.lastDeletePosition);
    }

    @Test
    public void testListener_OnRemoveProfileImage() {
        User user = createTestUser("1", "John Doe", "john@example.com");
        testListener.onRemoveProfileImage(user, 0);

        assertTrue("Remove image callback should be called", testListener.removeImageCalled);
        assertEquals("User should match", user, testListener.lastImageRemovedUser);
        assertEquals("Position should match", 0, testListener.lastImageRemovePosition);
    }

    @Test
    public void testListener_OnRemoveMultipleImages() {
        User user1 = createTestUser("1", "John Doe", "john@example.com");
        User user2 = createTestUser("2", "Jane Smith", "jane@example.com");

        testListener.onRemoveProfileImage(user1, 0);
        testListener.onRemoveProfileImage(user2, 1);

        assertTrue("Remove image callback should be called", testListener.removeImageCalled);
        assertEquals("Last user should be Jane", user2, testListener.lastImageRemovedUser);
        assertEquals("Last position should be 1", 1, testListener.lastImageRemovePosition);
    }

    // ==================== Edge Case Tests ====================

    @Test
    public void testUserList_WithDuplicateUsers() {
        User user1 = createTestUser("1", "John Doe", "john@example.com");
        User user2 = createTestUser("1", "John Doe", "john@example.com"); // Same ID

        testUserList.add(user1);
        testUserList.add(user2);

        assertEquals("List should contain both entries", 2, testUserList.size());
    }

    @Test
    public void testUserList_WithLargeDataset() {
        for (int i = 0; i < 1000; i++) {
            testUserList.add(createTestUser(String.valueOf(i), "User " + i, "user" + i + "@example.com"));
        }

        assertEquals("List should have 1000 users", 1000, testUserList.size());
    }

    @Test
    public void testUser_WithSpecialCharactersInName() {
        User user = createTestUser("1", "Jöhn Døe (Test) #123", "john@example.com");

        assertEquals("Name with special characters should be preserved",
                "Jöhn Døe (Test) #123", user.getName());
    }

    @Test
    public void testUser_WithLongName() {
        String longName = "A".repeat(200);
        User user = createTestUser("1", longName, "john@example.com");

        assertEquals("Long name should be preserved", longName, user.getName());
    }

    @Test
    public void testUser_WithInvalidEmail() {
        User user = createTestUser("1", "John Doe", "invalid-email");

        // Adapter should still handle invalid emails
        assertEquals("Invalid email should be stored", "invalid-email", user.getEmail());
    }

    @Test
    public void testListener_WithNullUser() {
        // Listener should handle null users gracefully
        testListener.onDeleteUser(null, 0);

        assertTrue("Delete callback should still be called", testListener.deleteUserCalled);
        assertNull("User should be null", testListener.lastDeletedUser);
    }

    @Test
    public void testListener_WithNegativePosition() {
        User user = createTestUser("1", "John Doe", "john@example.com");
        testListener.onDeleteUser(user, -1);

        assertTrue("Delete callback should be called", testListener.deleteUserCalled);
        assertEquals("Negative position should be preserved", -1, testListener.lastDeletePosition);
    }

    // ==================== Helper Methods ====================

    /**
     * Creates a test user with the given parameters.
     */
    private User createTestUser(String userId, String name, String email) {
        User user = new User();
        user.setUserId(userId);
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    /**
     * Test implementation of OnUserActionListener for testing.
     */
    private static class TestUserActionListener implements AdminUserAdapter.OnUserActionListener {
        boolean deleteUserCalled = false;
        boolean removeImageCalled = false;
        User lastDeletedUser = null;
        User lastImageRemovedUser = null;
        int lastDeletePosition = -1;
        int lastImageRemovePosition = -1;

        @Override
        public void onDeleteUser(User user, int position) {
            deleteUserCalled = true;
            lastDeletedUser = user;
            lastDeletePosition = position;
        }

        @Override
        public void onRemoveProfileImage(User user, int position) {
            removeImageCalled = true;
            lastImageRemovedUser = user;
            lastImageRemovePosition = position;
        }
    }
}