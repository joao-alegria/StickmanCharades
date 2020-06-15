package pt.ua.deti.es.g54.services;

import java.util.Collections;
import org.json.simple.JSONObject;
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
    }

}
