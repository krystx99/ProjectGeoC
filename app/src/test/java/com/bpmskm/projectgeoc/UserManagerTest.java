
package com.bpmskm.projectgeoc;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

public class UserManagerTest {
    private UserManager userManager;

    @Before
    public void setUp() {
        userManager = new UserManager();
    }

    @Test
    public void testUserScoreUpdate() {
        User user = new User("test", 1000);
        user.setPoints(10);
        userManager.setCurrentUser(user);
        assertEquals(10, userManager.getCurrentUser().getPoints());
    }
}
