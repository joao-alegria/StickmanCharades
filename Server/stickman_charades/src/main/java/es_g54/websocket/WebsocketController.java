package es_g54.websocket;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

/**
 *
 * @author joaoalegria
 */
//@Controller
//public class WebsocketController{
//    
//    @MessageMapping("/game/session/{topic}")
//    @SendTo("/session/{topic}")
//    public String send(@DestinationVariable("topic") String topic) throws Exception{
//        simpleMessagingTemplate
//	return "banana";
//    }
//}
