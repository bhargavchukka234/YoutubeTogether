package com.distri.mdlware.websocket;

import com.distri.mdlware.cache.RoomClientCache;
import com.distri.mdlware.dto.RedisPubSubDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import uci.middleware.project.dao.ClientDAO;
import uci.middleware.project.dao.RoomDAO;
import uci.middleware.project.dto.RoomClient;

import java.util.Map;

import static uci.middleware.project.utils.Constants.VIDEO_STATUS;
import static uci.middleware.project.utils.Constants.VIDEO_URL;

@Controller
public class EventController {

    @Autowired
    @Qualifier("brokerMessagingTemplate")
    private SimpMessagingTemplate template;

    private EventController() {
    }

    @Autowired
    private RoomDAO roomDAO;

    @Autowired
    private ClientDAO clientDAO;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ChannelTopic channelTopic;

    @Autowired
    private RoomClientCache roomClientCache;

//	@MessageMapping("/hello")
//	@SendTo("/topic/greetings")
//	public Greeting greeting(HelloMessage message) throws Exception {
//		System.out.println("Entered greeting method");
//		Thread.sleep(1000); // simulated delay
//		return new Greeting("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!");
//	}
//
//	@MessageMapping("/hello2")
//	@SendTo("/topic/greet")
//	public Greeting greeting2(HelloMessage message) throws Exception {
//		System.out.println("Entered greeting2 method");
//		Thread.sleep(1000); // simulated delay
//		return new Greeting("Hello2, " + HtmlUtils.htmlEscape(message.getName()) + "!");
//	}

//	@MessageMapping("/youtube")
//	@SendTo("/topic/youtube")
//	public Greeting youtubeControl(HelloMessage message) throws Exception {
//		System.out.println("Entered youtube control method");
//		// Thread.sleep(1000); // simulated delay
//		System.out.println("message name is " + message.getName());
//		if (message.getName().equals("play")) {
//			return new Greeting("PLAY");
//		} else {
//			return new Greeting("PAUSE");
//		}
//	}

    @MessageMapping("/youtube/{room}")
    public void youtubeControl(@DestinationVariable String room, Event event) throws Exception {
//        System.out.println("Entered youtube control method");
//        System.out.println("message name is " + event.getName());
//        System.out.println("current room is " + room);
        if (event.getName().equals("cue")) {
            roomDAO.updateRoomVideoUrl(room, event.getValue());

        } else if(event.getName().equals("reconnectEvent")){

            roomClientCache.addRoomClientId(room, event.getValue());
        }
        else {

            roomDAO.updateRoomVideoStatus(room, event.getName());
        }
        ObjectMapper jsonMapper = new ObjectMapper();
        //Converting the Object to JSONString
        String jsonString = jsonMapper.writeValueAsString(new RedisPubSubDTO("", room, event));
        redisTemplate.convertAndSend(channelTopic.getTopic(), jsonString);
    }

	@MessageMapping("/youtube/timing_event")
	public void timingEvent(TimingEvent event) throws Exception {
		System.out.println("Received timing event : " + event.toString());

		clientDAO.updateRoomClient(event.getRoomName(), event.getClientID(),
                new RoomClient(Float.parseFloat(event.getStreamPosition()), Long.parseLong(event.getPositionSnapshotTime())));
	}
}
