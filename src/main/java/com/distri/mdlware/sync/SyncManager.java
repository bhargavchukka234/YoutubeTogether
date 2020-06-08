package com.distri.mdlware.sync;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uci.middleware.project.dao.ClientDAO;
import uci.middleware.project.dao.RoomDAO;
import uci.middleware.project.dto.Client;
import uci.middleware.project.dto.Room;
import uci.middleware.project.dto.RoomClient;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

import static com.distri.mdlware.websocket.ScheduledTasks.ROOM_IDEAL_POSITION_UPDATE_TIMEOUT_SECONDS;
import static uci.middleware.project.utils.Constants.CONSECUTIVE_SLOW_CLIENT_COUNT;
import static uci.middleware.project.utils.Constants.VIDEO_POSITION_UPDATE_TIME;

@Component
public class SyncManager {

    private Set<String> rooms;

    @Autowired
    private RoomDAO roomDAO;

    @Autowired
    private ClientDAO clientDAO;

    public SyncManager(){

        this.rooms = new HashSet<>();
    }

    public void addRoomToSyncManager(String roomName){

        rooms.add(roomName);
    }

    public void computeSyncPositions(){

        Iterator<String> iterator = rooms.iterator();
        while (iterator.hasNext()) {
            String roomName = iterator.next();
            Room room = roomDAO.getRoomInformation(roomName);
            if(room.getVideoPositionUpdateTimestamp() != null && room.getVideoPositionUpdateTimestamp() > System.currentTimeMillis() - ROOM_IDEAL_POSITION_UPDATE_TIMEOUT_SECONDS/2){
                iterator.remove();
            }
            computeSyncPosition(roomName , room);
        }
    }

    public void computeSyncPosition(String roomName, Room room){

        if(room.getVideoStatus() != null && room.getVideoStatus().equals("pause")) {
            roomDAO.updateRoom(roomName, VIDEO_POSITION_UPDATE_TIME, String.valueOf(System.currentTimeMillis()));
            return;
        }

        Map<String, RoomClient> roomClients = clientDAO.getAllClientStatusInRoom(roomName);

        long currentTime = System.currentTimeMillis();
        Float idealPosition = 0f;
        Float slowestClientPosition = Float.MAX_VALUE;
        int nonNullClientPositionEntries = 0 ;

        Map<String, Float> currentEstimatedPositions = getCurrentEstimatedPositions(roomClients, room, currentTime);

        for(String key : currentEstimatedPositions.keySet()){

            Float clientCurrentPosition = currentEstimatedPositions.get(key);

            if(clientCurrentPosition != null){

                nonNullClientPositionEntries++;
                slowestClientPosition = Math.min(slowestClientPosition, clientCurrentPosition);
                idealPosition += clientCurrentPosition;
            }
        }
        if(nonNullClientPositionEntries != 0) {

            idealPosition = nonNullClientPositionEntries != 0 ? idealPosition/nonNullClientPositionEntries : 0f;

            Room updatedRoom = new Room();
            updatedRoom.setVideoPosition(idealPosition);
            updatedRoom.setVideoPositionUpdateTimestamp(currentTime);
            roomDAO.updateRoom(roomName, updatedRoom);
            markSlowClients(roomName, roomClients, currentEstimatedPositions, idealPosition, slowestClientPosition);
        }
    }

    private Map<String, Float> getCurrentEstimatedPositions(Map<String, RoomClient> roomClients, Room room, long currentTime) {

        Map<String, Float> currentEstimatedPositions = new HashMap<>();
        for(Map.Entry<String, RoomClient> entry : roomClients.entrySet()) {

            RoomClient roomClient = entry.getValue();
            Float currentPosition = null;
            if (room.getVideoStatus() != null && room.getVideoStatus().equals("play")) {

                currentPosition = roomClient.getStreamPosition() == null || roomClient.getPositionSnapshotTime() < room.getVideoStatusUpdateTimestamp()
                        ? null : roomClient.getStreamPosition() + 1.0f*(currentTime - roomClient.getPositionSnapshotTime())/1000;
            }
            currentEstimatedPositions.put(entry.getKey(), currentPosition);
        }
        return currentEstimatedPositions;
    }

    private void markSlowClients(String roomName, Map<String, RoomClient> roomClients, Map<String, Float> currentEstimatedPositions, Float idealPosition, Float slowestClientPosition){

        for(Map.Entry<String, Float> entry : currentEstimatedPositions.entrySet()){

            RoomClient roomClient = roomClients.get(entry.getKey());
            Integer consecutiveSlowClientCount = roomClient.getConsecutiveSlowClientCount();
            if(entry.getValue() != null && entry.getValue() < idealPosition && entry.getValue().equals(slowestClientPosition)){

               if(consecutiveSlowClientCount == null){
                   consecutiveSlowClientCount = 1;
               }
               else{
                   consecutiveSlowClientCount++;
               }
               clientDAO.updateRoomClient(roomName, entry.getKey(), CONSECUTIVE_SLOW_CLIENT_COUNT, String.valueOf(consecutiveSlowClientCount));
            }
            else if(consecutiveSlowClientCount != null && consecutiveSlowClientCount > 0){

                clientDAO.updateRoomClient(roomName, entry.getKey(), CONSECUTIVE_SLOW_CLIENT_COUNT, String.valueOf(0));
            }
        }
    }
}
