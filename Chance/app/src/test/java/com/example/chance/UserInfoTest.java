package com.example.chance;

import org.junit.Test;

import static org.junit.Assert.*;

import com.example.chance.model.User;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class UserInfoTest {

    private final User mock_user = new User();

    @Test
    public void common_user_name() {
        mock_user.setUsername("John Doe");
        assertEquals("John Doe", mock_user.getUsername());
    }

    @Test
    public void common_user_password() {
        mock_user.setPassword("2123w21");
        assertEquals("2123w21", mock_user.getPassword());
    }

    @Test
    public void common_user_email() {
        mock_user.setEmail("john.quincy.adams@examplepetstore.com");
        assertEquals("john.quincy.adams@examplepetstore.com", mock_user.getEmail());
    }

    @Test
    public void overflow_user_name() {
        mock_user.setUsername("John Doe John Doe John Doe John Doe John Doe John Doe John Doe John Doe John Doe John Doe John Doe John Doe John Doe John Doe John Doe John Doe John Doe John Doe John Doe John Doe John Doe John Doe John Doe John Doe John Doe John Doe John Doe John Doe John Doe John Doe John Doe John Doe John Doe John Doe John Doe John Doe John Doe John Doe John Doe John Doe John Doe John Doe John Doe John Doe John Doe John Doe John Doe John Doe");
        int maxLength = 50;
        assertTrue("Username is too long", mock_user.getUsername().length() > maxLength);
    }

    @Test
    public void overflow_user_password() {
        mock_user.setPassword("21432213213ew42314eqwsed32421wqeq234213eqwed1233412eqwe3423eqweq34213eqwe123123eqweq342q3aweq23423q4e");
        assertTrue("Password is too long", mock_user.getPassword().length() > 15);
    }

   @Test
    public void wrong_email_format() {
        mock_user.setEmail("john.doe.com");
        assertFalse("Email is not in correct format", mock_user.getEmail().contains("@"));
   }

   @Test
    public void overflow_email_length() {
        mock_user.setEmail("john.doe.comjohn.doe.comjohn.doe.comjohn.doe.comjohn.doe.comjohn.doe.comjohn.doe.comjohn.doe.com comjohn.doe.comjohn.doe.com");
        assertTrue("Email is too long", mock_user.getEmail().length() > 40);
   }

   @Test
    public void common_user_phone_number() {
       mock_user.setPhoneNumber("123-456-7890");
       assertEquals("123-456-7890", mock_user.getPhoneNumber());
   }

   @Test
    public void overflow_user_phone_number() {
       mock_user.setPhoneNumber("123-456-7890123-456-7890123-456-7890123-456-7890123-456-7890123-456-7890123-456-7890123-456-7890123-456-7890123-456-7890123-456-7890123-456-7890123-456-7890123-456-7890123-456-7890123-456-7890123-456-7890123-456-7890");
       assertTrue("Phone number is too long", mock_user.getPhoneNumber().length() > 50);
   }

   @Test
    public void wrong_phone_number_length() {
        mock_user.setPhoneNumber("1234567890");
        assertFalse("Phone number is not in correct length", mock_user.getPhoneNumber().length() != 10);
   }
}