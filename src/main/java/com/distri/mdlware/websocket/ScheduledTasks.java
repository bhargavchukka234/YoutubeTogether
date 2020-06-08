package com.distri.mdlware.websocket;

import com.distri.mdlware.cache.RoomClientCache;
import com.distri.mdlware.sync.SyncManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uci.middleware.project.dao.RoomDAO;
import uci.middleware.project.dto.Room;

import java.util.Map;
import java.util.Set;

@Component
public class ScheduledTasks {

    @Autowired
    SyncManager syncManager;

    @Autowired
    RoomDAO roomDAO;

    @Autowired
    RoomClientCache roomClientCache;

    @Autowired
    @Qualifier("brokerMessagingTemplate")
    private SimpMessagingTemplate simpMessagingTemplate;

    public static final int ROOM_IDEAL_POSITION_UPDATE_TIMEOUT_SECONDS = 20;

    public static final String IDEAL_POSITION= "idealPosition";

    @Scheduled(fixedRate = 10000)
    public void scheduleTaskWithFixedRate() {
        // implement the logic to fetch timing data of each client of each room from
        // redis
        // calculate the ideal time for each room
        syncManager.computeSyncPositions();
        // publish the event to the redis
        // publish the event to the websocket channels.
        getAndSendRoomClientsSyncPosition();
        System.out.println("Scheduler called");
    }

    private void getAndSendRoomClientsSyncPosition() {

        Map<String, Set<String>> roomClientIdsMap = roomClientCache.getRoomClientIdsMap();
        for (Map.Entry<String, Set<String>> entry : roomClientIdsMap.entrySet()) {

            String roomName = entry.getKey();
            Room room = roomDAO.getRoomInformation(entry.getKey());
            if (room.getVideoPositionUpdateTimestamp() == null) {
                //compute room ideal position
                syncManager.addRoomToSyncManager(roomName);
                syncManager.computeSyncPosition(roomName, room);
                room = roomDAO.getRoomInformation(entry.getKey());
            }
            else if (room.getVideoPositionUpdateTimestamp() != null) {

                if (room.getVideoPositionUpdateTimestamp() < System.currentTimeMillis() - ROOM_IDEAL_POSITION_UPDATE_TIMEOUT_SECONDS * 1000) {

                    //compute room ideal position
                    syncManager.addRoomToSyncManager(roomName);
                    syncManager.computeSyncPosition(roomName, room);
                    room = roomDAO.getRoomInformation(entry.getKey());
                }
            }
            if (room.getVideoStatus() != null && room.getVideoStatus().equals("play")) {
                if (room.getVideoPositionUpdateTimestamp() < room.getVideoStatusUpdateTimestamp()) {

                    syncManager.computeSyncPosition(roomName, room);
                    room = roomDAO.getRoomInformation(entry.getKey());
                }
                sendRoomClientsSyncPosition(entry.getKey(), room);
            }
        }
    }

    private void sendRoomClientsSyncPosition(String roomName, Room room) {

        System.out.println("Sending ideal position for room : " + roomName + " is " + room.getVideoPosition());
        simpMessagingTemplate.convertAndSend("/topic/" + roomName, new IdealPosition(IDEAL_POSITION, room.getVideoPosition(), room.getVideoPositionUpdateTimestamp()));
    }


}