package com.distri.mdlware.websocket;

public class TimingBroadcastEvent {

    private String eventName;
    private Float videoPosition;
    private Long videoPositionUpdateTimestamp;

    public TimingBroadcastEvent(String eventName, Float videoPosition, Long videoPositionUpdateTimestamp){
        this.videoPosition = videoPosition;
         this.videoPositionUpdateTimestamp = videoPositionUpdateTimestamp;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Float getVideoPosition() {
        return videoPosition;
    }

    public void setVideoPosition(Float videoPosition) {
        this.videoPosition = videoPosition;
    }

    public Long getVideoPositionUpdateTimestamp() {
        return videoPositionUpdateTimestamp;
    }

    public void setVideoPositionUpdateTimestamp(Long videoPositionUpdateTimestamp) {
        this.videoPositionUpdateTimestamp = videoPositionUpdateTimestamp;
    }


}
