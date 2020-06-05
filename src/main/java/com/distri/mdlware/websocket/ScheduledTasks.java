package com.distri.mdlware.websocket;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

	@Scheduled(fixedRate = 2000)
	public void scheduleTaskWithFixedRate() {
		// implement the logic to fetch timing data of each client of each room from
		// redis
		// calculate the ideal time for each room
		// publish the event to the redis
		// publish the event to the websocket channels.
		System.out.println("Scheduler called");
	}

}