package com.distri.mdlware.cache;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class RoomClientCache {

    private final Map<String, Set<String>> roomClientIdsMap;

    public RoomClientCache(){
        roomClientIdsMap = new HashMap<>();
    }

    public void addRoomClientId(String roomName, String clientId){

        Set<String> roomClientIds = roomClientIdsMap.get(roomName);
        if(roomClientIds == null){
            roomClientIds = new HashSet<>();
            roomClientIdsMap.put(roomName, roomClientIds);
        }
        roomClientIds.add(clientId);
    }

    public Map<String, Set<String>> getRoomClientIdsMap(){
        return roomClientIdsMap;
    }
}
