package com.distri.mdlware.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class EventController {

	private SimpMessagingTemplate template;

	@Autowired
	public EventController(SimpMessagingTemplate template) {
		this.template = template;
	}

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
		System.out.println("Entered youtube control method");
		System.out.println("message name is " + event.getName());
		System.out.println("current room is " + room);
		if (event.getName().equals("play")) {
			this.template.convertAndSend("/topic/" + room, event);
			System.out.println("done");
		} else if (event.getName().equals("pause")) {
			this.template.convertAndSend("/topic/" + room, event);
		} else {
			this.template.convertAndSend("/topic/" + room, event);
		}
	}

	@MessageMapping("/youtube/timing_event")
	public void timingEvent(TimingEvent event) throws Exception {
		System.out.println("current client is " + event.clientID);
		System.out.println("current room is " + event.getRoomName());
		System.out.println("timing is " + event.currTimeInSecs);

		// persist timing info to redis
	}
}
