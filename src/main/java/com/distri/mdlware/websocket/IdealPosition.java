package com.distri.mdlware.websocket;

public class IdealPosition {

    private String name;
    private Float videoPosition;
    private Long videoPositionUpdateTimestamp;

    public IdealPosition(String name, Float videoPosition, Long videoPositionUpdateTimestamp){
        this.name = name;
        this.videoPosition = videoPosition;
         this.videoPositionUpdateTimestamp = videoPositionUpdateTimestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @Override
    public String toString() {
        return "IdealPosition{" +
                "eventName='" + name + '\'' +
                ", videoPosition=" + videoPosition +
                ", videoPositionUpdateTimestamp=" + videoPositionUpdateTimestamp +
                '}';
    }
}
