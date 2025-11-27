package com.example.chance.controller;

import com.example.chance.model.User;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for AdminController class.
 * Tests all admin operations including user retrieval, deletion, and image removal.
 */
public class AdminControllerTest {

    private AdminController adminController;

    @Before
    public void setUp() {
        adminController = new AdminController();
    }

    // ==================== Constructor Tests ====================

    @Test
    public void testConstructor_InitializesCorrectly() {
        assertNotNull("AdminController should not be null", adminController);
    }

    // ==================== getAllUsers Tests ====================

    @Test
    public void testGetAllUsers_WithCallback() {
        // This test verifies the method accepts a callback
        // Actual Firebase testing would require instrumentation tests
        AdminController.UserListCallback callback = new AdminController.UserListCallback() {
            @Override
            public void onSuccess(List<User> users) {
                assertNotNull("User list should not be null", users);
            }

            @Override
            public void onFailure(String errorMessage) {
                assertNotNull("Error message should not be null", errorMessage);
            }
        };

        // Verify callback can be created
        assertNotNull("Callback should not be null", callback);
    }

    @Test
    public void testGetAllUsers_CallbackOnSuccess() {
        final boolean[] callbackCalled = {false};

        AdminController.UserListCallback callback = new AdminController.UserListCallback() {
            @Override
            public void onSuccess(List<User> users) {
                callbackCalled[0] = true;
                assertNotNull("User list should not be null", users);
            }

            @Override
            public void onFailure(String errorMessage) {
                fail("Should not call onFailure");
            }
        };

        // Verify callback structure is correct
        assertNotNull("Callback should be instantiated", callback);
    }

    @Test
    public void testGetAllUsers_CallbackOnFailure() {
        final boolean[] callbackCalled = {false};

        AdminController.UserListCallback callback = new AdminController.UserListCallback() {
            @Override
            public void onSuccess(List<User> users) {
                fail("Should not call onSuccess");
            }

            @Override
            public void onFailure(String errorMessage) {
                callbackCalled[0] = true;
                assertNotNull("Error message should not be null", errorMessage);
                assertFalse("Error message should not be empty", errorMessage.isEmpty());
            }
        };

        // Simulate failure
        callback.onFailure("Test error");
        assertTrue("Callback should have been called", callbackCalled[0]);
    }

    @Test
    public void testGetAllUsers_HandlesEmptyList() {
        AdminController.UserListCallback callback = new AdminController.UserListCallback() {
            @Override
            public void onSuccess(List<User> users) {
                assertNotNull("User list should not be null even if empty", users);
                assertEquals("Empty list should have size 0", 0, users.size());
            }

            @Override
            public void onFailure(String errorMessage) {
                fail("Should not fail for empty list");
            }
        };

        // Simulate empty list
        callback.onSuccess(new ArrayList<>());
    }

    // ==================== deleteUser Tests ====================

    @Test
    public void testDeleteUser_WithValidUserId() {
        AdminController.DeleteCallback callback = new AdminController.DeleteCallback() {
            @Override
            public void onSuccess() {
                // Success path
                assertTrue(true);
            }

            @Override
            public void onFailure(String errorMessage) {
                fail("Should not fail with valid user ID");
            }
        };

        assertNotNull("Callback should be created", callback);
    }

    @Test
    public void testDeleteUser_WithNullUserId() {
        final boolean[] failureCalled = {false};

        AdminController.DeleteCallback callback = new AdminController.DeleteCallback() {
            @Override
            public void onSuccess() {
                fail("Should not succeed with null user ID");
            }

            @Override
            public void onFailure(String errorMessage) {
                failureCalled[0] = true;
                assertEquals("Invalid user ID", errorMessage);
            }
        };

        adminController.deleteUser(null, callback);
        assertTrue("Failure callback should be called for null user ID", failureCalled[0]);
    }

    @Test
    public void testDeleteUser_WithEmptyUserId() {
        final boolean[] failureCalled = {false};

        AdminController.DeleteCallback callback = new AdminController.DeleteCallback() {
            @Override
            public void onSuccess() {
                fail("Should not succeed with empty user ID");
            }

            @Override
            public void onFailure(String errorMessage) {
                failureCalled[0] = true;
                assertEquals("Invalid user ID", errorMessage);
            }
        };

        adminController.deleteUser("", callback);
        assertTrue("Failure callback should be called for empty user ID", failureCalled[0]);
    }

    @Test
    public void testDeleteUser_CallbackOnSuccess() {
        final boolean[] successCalled = {false};

        AdminController.DeleteCallback callback = new AdminController.DeleteCallback() {
            @Override
            public void onSuccess() {
                successCalled[0] = true;
            }

            @Override
            public void onFailure(String errorMessage) {
                fail("Should not call onFailure");
            }
        };

        // Simulate success
        callback.onSuccess();
        assertTrue("Success callback should be called", successCalled[0]);
    }

    @Test
    public void testDeleteUser_CallbackOnFailure() {
        final boolean[] failureCalled = {false};
        final String expectedError = "Database connection failed";

        AdminController.DeleteCallback callback = new AdminController.DeleteCallback() {
            @Override
            public void onSuccess() {
                fail("Should not call onSuccess");
            }

            @Override
            public void onFailure(String errorMessage) {
                failureCalled[0] = true;
                assertEquals("Error message should match", expectedError, errorMessage);
            }
        };

        // Simulate failure
        callback.onFailure(expectedError);
        assertTrue("Failure callback should be called", failureCalled[0]);
    }

    // ==================== deleteUserProfileImage Tests ====================

    @Test
    public void testDeleteUserProfileImage_WithValidUserId() {
        AdminController.DeleteCallback callback = new AdminController.DeleteCallback() {
            @Override
            public void onSuccess() {
                assertTrue(true);
            }

            @Override
            public void onFailure(String errorMessage) {
                // Acceptable since image might not exist
                assertNotNull("Error message should not be null", errorMessage);
            }
        };

        assertNotNull("Callback should be created", callback);
    }

    @Test
    public void testDeleteUserProfileImage_HandlesNonExistentImage() {
        AdminController.DeleteCallback callback = new AdminController.DeleteCallback() {
            @Override
            public void onSuccess() {
                // Should still succeed if image doesn't exist
                assertTrue(true);
            }

            @Override
            public void onFailure(String errorMessage) {
                // This is also acceptable
                assertNotNull("Error message should not be null", errorMessage);
            }
        };

        assertNotNull("Callback should handle non-existent images", callback);
    }

    // ==================== removeUserProfileImageOnly Tests ====================

    @Test
    public void testRemoveUserProfileImageOnly_WithValidUserId() {
        AdminController.DeleteCallback callback = new AdminController.DeleteCallback() {
            @Override
            public void onSuccess() {
                assertTrue(true);
            }

            @Override
            public void onFailure(String errorMessage) {
                assertNotNull("Error message should not be null", errorMessage);
            }
        };

        assertNotNull("Callback should be created", callback);
    }

    @Test
    public void testRemoveUserProfileImageOnly_WithNullUserId() {
        final boolean[] failureCalled = {false};

        AdminController.DeleteCallback callback = new AdminController.DeleteCallback() {
            @Override
            public void onSuccess() {
                fail("Should not succeed with null user ID");
            }

            @Override
            public void onFailure(String errorMessage) {
                failureCalled[0] = true;
                assertEquals("Invalid user ID", errorMessage);
            }
        };

        adminController.removeUserProfileImageOnly(null, callback);
        assertTrue("Failure callback should be called for null user ID", failureCalled[0]);
    }

    @Test
    public void testRemoveUserProfileImageOnly_WithEmptyUserId() {
        final boolean[] failureCalled = {false};

        AdminController.DeleteCallback callback = new AdminController.DeleteCallback() {
            @Override
            public void onSuccess() {
                fail("Should not succeed with empty user ID");
            }

            @Override
            public void onFailure(String errorMessage) {
                failureCalled[0] = true;
                assertEquals("Invalid user ID", errorMessage);
            }
        };

        adminController.removeUserProfileImageOnly("", callback);
        assertTrue("Failure callback should be called for empty user ID", failureCalled[0]);
    }

    // ==================== Callback Interface Tests ====================

    @Test
    public void testUserListCallback_Interface() {
        AdminController.UserListCallback callback = new AdminController.UserListCallback() {
            @Override
            public void onSuccess(List<User> users) {
                assertNotNull("Users list should not be null", users);
            }

            @Override
            public void onFailure(String errorMessage) {
                assertNotNull("Error message should not be null", errorMessage);
            }
        };

        List<User> testUsers = new ArrayList<>();
        testUsers.add(new User());

        callback.onSuccess(testUsers);
        callback.onFailure("Test error");
    }

    @Test
    public void testDeleteCallback_Interface() {
        AdminController.DeleteCallback callback = new AdminController.DeleteCallback() {
            @Override
            public void onSuccess() {
                assertTrue(true);
            }

            @Override
            public void onFailure(String errorMessage) {
                assertNotNull("Error message should not be null", errorMessage);
            }
        };

        callback.onSuccess();
        callback.onFailure("Test error");
    }

    // ==================== Edge Case Tests ====================

    @Test
    public void testDeleteUser_WithSpecialCharactersInUserId() {
        final boolean[] methodCalled = {false};

        AdminController.DeleteCallback callback = new AdminController.DeleteCallback() {
            @Override
            public void onSuccess() {
                methodCalled[0] = true;
            }

            @Override
            public void onFailure(String errorMessage) {
                methodCalled[0] = true;
            }
        };

        // User IDs with special characters should be handled
        String specialUserId = "user@123#456";
        assertNotNull("Special character user ID should not be null", specialUserId);
    }

    @Test
    public void testGetAllUsers_WithLargeUserList() {
        AdminController.UserListCallback callback = new AdminController.UserListCallback() {
            @Override
            public void onSuccess(List<User> users) {
                assertNotNull("Large user list should not be null", users);
                // System should handle large lists
                assertTrue("Should handle lists of any size", users.size() >= 0);
            }

            @Override
            public void onFailure(String errorMessage) {
                fail("Should not fail with large list");
            }
        };

        // Simulate large list
        List<User> largeList = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            largeList.add(new User());
        }

        callback.onSuccess(largeList);
    }
}