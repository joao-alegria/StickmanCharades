/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.ua.deti.es.g54.services;

import java.util.Collections;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import pt.ua.deti.es.g54.repository.UserRepository;

/**
 *
 * @author joaoalegria
 */
@RunWith(MockitoJUnitRunner.class)
public class FriendServiceTest {
    
    @TestConfiguration
    static class Config {
  
        @Bean
        public FriendService friendService() {
            return new FriendService();
        }
    }
    
    @InjectMocks
    FriendService instance;
    
    @Mock
    private UserRepository userRepository;
    
    public FriendServiceTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getAllFriends method, of class FriendService.
     */
    @Test
    public void testGetAllFriends() {
        System.out.println("getAllFriends");
        JSONObject expResult = new JSONObject();
        Mockito.when(userRepository.getUserByUsername("teste")).thenReturn(Collections.EMPTY_LIST);
        JSONObject result = instance.getAllFriends("teste");
        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of addNewFriend method, of class FriendService.
     */
    @Test
    public void testAddNewFriend() {
        System.out.println("addNewFriend");
//        String username = "";
//        String friendname = "";
//        FriendService instance = new FriendService();
//        JSONObject expResult = null;
//        JSONObject result = instance.addNewFriend(username, friendname);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of deleteFriend method, of class FriendService.
     */
    @Test
    public void testDeleteFriend() {
        System.out.println("deleteFriend");
//        String username = "";
//        String friendname = "";
//        FriendService instance = new FriendService();
//        JSONObject expResult = null;
//        JSONObject result = instance.deleteFriend(username, friendname);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of inviteFriend method, of class FriendService.
     */
    @Test
    public void testInviteFriend() {
        System.out.println("inviteFriend");
//        String username = "";
//        String friendname = "";
//        Long sessionId = null;
//        FriendService instance = new FriendService();
//        JSONObject expResult = null;
//        JSONObject result = instance.inviteFriend(username, friendname, sessionId);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }
    
}
