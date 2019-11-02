package com.example.bigmood;

import com.google.firebase.Timestamp;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import java.util.Date;

public class DatabaseManagerTest {

    @Test
    void testBasicQuery() {
        DatabaseManager dbMgr = new DatabaseManager();
        User user = dbMgr.getUser("0");
        assertEquals("Test", user.getDisplayName());
        assertEquals("0", user.getUserId());
        assertEquals("https://example.com", user.getProfilePictureUrl());
        assertTrue(new Timestamp(new Date(2019, 11, 1)).compareTo(user.getDateCreated()) == 0);
    }

    @Test
    void testJunit() {
        assertEquals(1, 1);
    }
}
