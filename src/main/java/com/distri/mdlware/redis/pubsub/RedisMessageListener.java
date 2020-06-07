package com.distri.mdlware.redis.pubsub;

import com.distri.mdlware.dto.RedisPubSubDTO;
import com.distri.mdlware.exception.InvalidFormatException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class RedisMessageListener implements MessageListener {

    private SimpMessagingTemplate template;
    private ObjectMapper jsontMapper;
    public RedisMessageListener(SimpMessagingTemplate template){

        this.template = template;
        this.jsontMapper = new ObjectMapper();
    }

    @Override
    public void onMessage( final Message message, final byte[] pattern ) {

        try {
            RedisPubSubDTO redisPubSubDTO = jsontMapper.readValue(message.toString(), RedisPubSubDTO.class);
            template.convertAndSend("/topic/" + redisPubSubDTO.getRoom(), redisPubSubDTO.getEvent());
            System.out.println("Message received: " + message.toString());
        } catch (JsonProcessingException e) {
        throw new InvalidFormatException(e);
    }
    }
}