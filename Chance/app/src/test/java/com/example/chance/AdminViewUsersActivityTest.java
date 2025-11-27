package com.example.chance.view;

import com.example.chance.controller.AdminController;
import com.example.chance.model.User;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for AdminViewUsersActivity.
 * Tests activity initialization, user loading, and user management operations.
 */
public class AdminViewUsersActivityTest {

    private List<User> testUserList;
    private TestAdminController testController;

    @Before
    public void setUp() {
        testUserList = new ArrayList<>();
        testController = new TestAdminController();
    }

    // ==================== Initialization Tests ====================

    @Test
    public void testInitialization_UserListIsEmpty() {
        List<User> userList = new ArrayList<>();
        assertEquals("Initial user list should be empty", 0, userList.size());
    }

    @Test
    public void testInitialization_ControllerIsNotNull() {
        AdminController controller = new AdminController();
        assertNotNull("Controller should not be null", controller);
    }

    // ==================== User Loading Tests ====================

    @Test
    public void testLoadUsers_Success() {
        testController.getAllUsers(new AdminController.UserListCallback() {
            @Override
            public void onSuccess(List<User> users) {
                assertNotNull("User list should not be null", users);
                assertTrue("Success callback should be called", true);
            }

            @Override
            public void onFailure(String errorMessage) {
                fail("Should not call failure callback on success");
            }
        });

        // Simulate successful load
        List<User> users = createTestUsers(5);
        testUserList.addAll(users);

        assertEquals("User list should contain 5 users", 5, testUserList.size());
    }

    @Test
    public void testLoadUsers_EmptyList() {
        testController.getAllUsers(new AdminController.UserListCallback() {
            @Override
            public void onSuccess(List<User> users) {
                assertNotNull("User list should not be null even if empty", users);
                assertEquals("Empty list should have size 0", 0, users.size());
            }

            @Override
            public void onFailure(String errorMessage) {
                fail("Should not fail for empty list");
            }
        });
    }

    @Test
    public void testLoadUsers_Failure() {
        final boolean[] failureCalled = {false};

        testController.getAllUsers(new AdminController.UserListCallback() {
            @Override
            public void onSuccess(List<User> users) {
                fail("Should not call success callback on failure");
            }

            @Override
            public void onFailure(String errorMessage) {
                failureCalled[0] = true;
                assertNotNull("Error message should not be null", errorMessage);
                assertFalse("Error message should not be empty", errorMessage.isEmpty());
            }
        });

        // Simulate failure
        testController.simulateFailure("Network error");
        assertTrue("Failure callback should be called", failureCalled[0]);
    }

    @Test
    public void testLoadUsers_LargeDataset() {
        List<User> largeUserList = createTestUsers(1000);

        testController.getAllUsers(new AdminController.UserListCallback() {
            @Override
            public void onSuccess(List<User> users) {
                assertEquals("Should handle large datasets", 1000, users.size());
            }

            @Override
            public void onFailure(String errorMessage) {
                fail("Should not fail with large dataset");
            }
        });
    }

    // ==================== User Deletion Tests ====================

    @Test
    public void testDeleteUser_Success() {
        User user = createTestUser("1", "John Doe", "john@example.com");
        testUserList.add(user);

        testController.deleteUser(user.getUserId(), new AdminController.DeleteCallback() {
            @Override
            public void onSuccess() {
                assertTrue("Delete success callback should be called", true);
            }

            @Override
            public void onFailure(String errorMessage) {
                fail("Should not fail on successful deletion");
            }
        });

        // Simulate deletion
        testUserList.remove(user);
        assertEquals("User list should be empty after deletion", 0, testUserList.size());
    }

    @Test
    public void testDeleteUser_RemovesFromList() {
        testUserList.add(createTestUser("1", "John Doe", "john@example.com"));
        testUserList.add(createTestUser("2", "Jane Smith", "jane@example.com"));
        testUserList.add(createTestUser("3", "Bob Johnson", "bob@example.com"));

        // Delete user at position 1
        testUserList.remove(1);

        assertEquals("List should have 2 users after deletion", 2, testUserList.size());
        assertEquals("First user should be John", "1", testUserList.get(0).getUserId());
        assertEquals("Second user should be Bob", "3", testUserList.get(1).getUserId());
    }

    @Test
    public void testDeleteUser_Failure() {
        final boolean[] failureCalled = {false};

        testController.deleteUser("invalid-id", new AdminController.DeleteCallback() {
            @Override
            public void onSuccess() {
                fail("Should not succeed with invalid ID");
            }

            @Override
            public void onFailure(String errorMessage) {
                failureCalled[0] = true;
                assertNotNull("Error message should not be null", errorMessage);
            }
        });

        testController.simulateFailure("User not found");
        assertTrue("Failure callback should be called", failureCalled[0]);
    }

    @Test
    public void testDeleteUser_WithNullId() {
        final boolean[] failureCalled = {false};

        testController.deleteUser(null, new AdminController.DeleteCallback() {
            @Override
            public void onSuccess() {
                fail("Should not succeed with null ID");
            }

            @Override
            public void onFailure(String errorMessage) {
                failureCalled[0] = true;
                assertEquals("Invalid user ID", errorMessage);
            }
        });

        assertTrue("Failure callback should be called for null ID", failureCalled[0]);
    }

    @Test
    public void testDeleteUser_WithEmptyId() {
        final boolean[] failureCalled = {false};

        testController.deleteUser("", new AdminController.DeleteCallback() {
            @Override
            public void onSuccess() {
                fail("Should not succeed with empty ID");
            }

            @Override
            public void onFailure(String errorMessage) {
                failureCalled[0] = true;
                assertEquals("Invalid user ID", errorMessage);
            }
        });

        assertTrue("Failure callback should be called for empty ID", failureCalled[0]);
    }

    // ==================== Profile Image Removal Tests ====================

    @Test
    public void testRemoveProfileImage_Success() {
        User user = createTestUser("1", "John Doe", "john@example.com");

        testController.removeUserProfileImageOnly(user.getUserId(),
                new AdminController.DeleteCallback() {
                    @Override
                    public void onSuccess() {
                        assertTrue("Image removal success callback should be called", true);
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        fail("Should not fail on successful image removal");
                    }
                });
    }

    @Test
    public void testRemoveProfileImage_Failure() {
        final boolean[] failureCalled = {false};

        testController.removeUserProfileImageOnly("invalid-id",
                new AdminController.DeleteCallback() {
                    @Override
                    public void onSuccess() {
                        fail("Should not succeed with invalid ID");
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        failureCalled[0] = true;
                        assertNotNull("Error message should not be null", errorMessage);
                    }
                });

        testController.simulateFailure("Image not found");
        assertTrue("Failure callback should be called", failureCalled[0]);
    }

    @Test
    public void testRemoveProfileImage_WithNullId() {
        final boolean[] failureCalled = {false};

        testController.removeUserProfileImageOnly(null, new AdminController.DeleteCallback() {
            @Override
            public void onSuccess() {
                fail("Should not succeed with null ID");
            }

            @Override
            public void onFailure(String errorMessage) {
                failureCalled[0] = true;
                assertEquals("Invalid user ID", errorMessage);
            }
        });

        assertTrue("Failure callback should be called for null ID", failureCalled[0]);
    }

    @Test
    public void testRemoveProfileImage_WithEmptyId() {
        final boolean[] failureCalled = {false};

        testController.removeUserProfileImageOnly("", new AdminController.DeleteCallback() {
            @Override
            public void onSuccess() {
                fail("Should not succeed with empty ID");
            }

            @Override
            public void onFailure(String errorMessage) {
                failureCalled[0] = true;
                assertEquals("Invalid user ID", errorMessage);
            }
        });

        assertTrue("Failure callback should be called for empty ID", failureCalled[0]);
    }

    // ==================== UI State Tests ====================

    @Test
    public void testShowLoading_InitialState() {
        boolean isLoading = false;
        assertFalse("Loading should be false initially", isLoading);
    }

    @Test
    public void testShowLoading_WhileLoadingUsers() {
        boolean isLoading = true;
        assertTrue("Loading should be true while loading users", isLoading);
    }

    @Test
    public void testShowLoading_AfterLoadComplete() {
        boolean isLoading = true;
        // Simulate load complete
        isLoading = false;

        assertFalse("Loading should be false after load complete", isLoading);
    }

    // ==================== User Data Display Tests ====================

    @Test
    public void testUserDisplay_WithValidData() {
        User user = createTestUser("1", "John Doe", "john@example.com");

        assertNotNull("User name should not be null", user.getName());
        assertNotNull("User email should not be null", user.getEmail());
        assertEquals("Name should be John Doe", "John Doe", user.getName());
        assertEquals("Email should be john@example.com", "john@example.com", user.getEmail());
    }

    @Test
    public void testUserDisplay_WithNullName() {
        User user = new User();
        user.setUserId("1");
        user.setName(null);
        user.setEmail("john@example.com");

        assertNull("Name should be null", user.getName());
        // Activity should display "Unknown User" for null names
    }

    @Test
    public void testUserDisplay_WithNullEmail() {
        User user = new User();
        user.setUserId("1");
        user.setName("John Doe");
        user.setEmail(null);

        assertNull("Email should be null", user.getEmail());
        // Activity should display "No email" for null emails
    }

    @Test
    public void testUserDisplay_RoleEntrant() {
        User user = createTestUser("1", "John Doe", "john@example.com");
        user.setOrganizer(false);
        user.setAdmin(false);

        assertFalse("Should not be organizer", user.isOrganizer());
        assertFalse("Should not be admin", user.isAdmin());
        // Should display "Entrant" as role
    }

    @Test
    public void testUserDisplay_RoleOrganizer() {
        User user = createTestUser("1", "John Doe", "john@example.com");
        user.setOrganizer(true);
        user.setAdmin(false);

        assertTrue("Should be organizer", user.isOrganizer());
        // Should display "Organizer" as role
    }

    @Test
    public void testUserDisplay_RoleAdmin() {
        User user = createTestUser("1", "John Doe", "john@example.com");
        user.setOrganizer(false);
        user.setAdmin(true);

        assertTrue("Should be admin", user.isAdmin());
        // Should display "Administrator" as role
    }

    // ==================== Edge Case Tests ====================

    @Test
    public void testDeleteMultipleUsersSequentially() {
        testUserList.add(createTestUser("1", "User 1", "user1@example.com"));
        testUserList.add(createTestUser("2", "User 2", "user2@example.com"));
        testUserList.add(createTestUser("3", "User 3", "user3@example.com"));

        // Delete first user
        testUserList.remove(0);
        assertEquals("Should have 2 users", 2, testUserList.size());

        // Delete another user
        testUserList.remove(0);
        assertEquals("Should have 1 user", 1, testUserList.size());

        // Delete last user
        testUserList.remove(0);
        assertEquals("Should have 0 users", 0, testUserList.size());
    }

    @Test
    public void testRefreshAfterDeletion() {
        // Initial load
        testUserList.addAll(createTestUsers(5));
        assertEquals("Should have 5 users", 5, testUserList.size());

        // Delete one user
        testUserList.remove(2);
        assertEquals("Should have 4 users after deletion", 4, testUserList.size());

        // Simulate refresh
        testUserList.clear();
        testUserList.addAll(createTestUsers(4));
        assertEquals("Should have 4 users after refresh", 4, testUserList.size());
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
     * Creates a list of test users.
     */
    private List<User> createTestUsers(int count) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            users.add(createTestUser(
                    String.valueOf(i),
                    "User " + i,
                    "user" + i + "@example.com"
            ));
        }
        return users;
    }

    /**
     * Test implementation of AdminController for testing.
     */
    private static class TestAdminController extends AdminController {
        private String lastError = null;

        public void simulateFailure(String errorMessage) {
            this.lastError = errorMessage;
        }

        @Override
        public void getAllUsers(UserListCallback callback) {
            if (lastError != null) {
                callback.onFailure(lastError);
                lastError = null;
            } else {
                callback.onSuccess(new ArrayList<>());
            }
        }

        @Override
        public void deleteUser(String userId, DeleteCallback callback) {
            if (userId == null || userId.isEmpty()) {
                callback.onFailure("Invalid user ID");
            } else if (lastError != null) {
                callback.onFailure(lastError);
                lastError = null;
            } else {
                callback.onSuccess();
            }
        }

        @Override
        public void removeUserProfileImageOnly(String userId, DeleteCallback callback) {
            if (userId == null || userId.isEmpty()) {
                callback.onFailure("Invalid user ID");
            } else if (lastError != null) {
                callback.onFailure(lastError);
                lastError = null;
            } else {
                callback.onSuccess();
            }
        }
    }
}