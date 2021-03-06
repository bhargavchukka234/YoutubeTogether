package com.distri.mdlware.service;


import com.distri.mdlware.cache.RoomClientCache;
import com.distri.mdlware.dto.NewClientDTO;
import com.distri.mdlware.sync.SyncManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import uci.middleware.project.dao.ClientDAO;
import uci.middleware.project.dao.RoomDAO;
import uci.middleware.project.dto.Room;

import java.util.UUID;

@RestController
public class VirtualRoomService {

    @Autowired
    private RoomDAO roomDAO;

    @Autowired
    private ClientDAO clientDAO;

    @Autowired
    private SyncManager syncManager;

    @Autowired
    private RoomClientCache roomClientCache;

    @CrossOrigin
    @PostMapping("/create/room/{roomName}")
    public ResponseEntity<NewClientDTO> createRoom(@PathVariable String roomName) {

        Room room = new Room();
        room.setvideoID("xHcPhdZBngw");
        room = roomDAO.createRoom(roomName, room);
        syncManager.addRoomToSyncManager(roomName);
        String clientId = UUID.randomUUID().toString();
        roomDAO.addClientToRoom(roomName, clientId);
        System.out.println("Adding client : " + clientId + " to room : " + roomName);
        roomClientCache.addRoomClientId(roomName, clientId);
        return new ResponseEntity<>(new NewClientDTO(clientId, room), HttpStatus.OK);
    }
}
