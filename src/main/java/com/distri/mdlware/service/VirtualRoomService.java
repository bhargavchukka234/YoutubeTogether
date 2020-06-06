package com.distri.mdlware.service;

import com.distri.mdlware.dto.NewClientDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/create/room/{roomName}")
    public ResponseEntity<NewClientDTO> createRoom(@PathVariable String roomName){

        Room room = roomDAO.createRoom(roomName, new Room());
        String clientId = UUID.randomUUID().toString();
        roomDAO.addClientToRoom(roomName , clientId);
        return new ResponseEntity<>(new NewClientDTO(clientId, new Room()), HttpStatus.OK);
    }
}
