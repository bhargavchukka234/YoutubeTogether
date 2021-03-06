var stompClient = null;
var clientID = ''
var error = false;
// This code loads the IFrame Player API code asynchronously. From YouTube API webpage.
var tag = document.createElement('script');
tag.src = "https://www.youtube.com/iframe_api";
var firstScriptTag = document.getElementsByTagName('script')[0];
firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);
var curr_room = ""
// This function creates an <iframe> (and YouTube player) after the API code downloads.
var player;
var vidId = "xHcPhdZBngw";  // The youtube Video ID for your starting video - dQw4w9WgXcQ
var ours = false;
function onYouTubeIframeAPIReady() {
    player = new YT.Player('player', {
        height: '390',
        width: '640',
        videoId: vidId, // Starting video ID
        events: {
            'onStateChange': onPlayerStateChange
        }
    });
}
var vis = false;

function joinRoom(room) {
    //client makes http POST request to the sync manager to register the room and receives the unique client ID in the same call
    //If the room already exists, client should be added to the existing room in the redis
    //else new room should be created and this client should be added to the room 
    curr_room = room
    var socket = new SockJS('http://localhost:8082/gs-guide-websocket'); //this websocket connection has to go through nginx load balancer
    // var socket = new SockJS('http://7005f8138314.ngrok.io/gs-guide-websocket'); //this websocket connection has to go through nginx load balancer 
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        // $.post('http://localhost:8082/create/room/' + curr_room,   // url
        if (!error) {
            $.post('http://localhost:8082/create/room/' + curr_room,   // url

                // $.post('http://7005f8138314.ngrok.io/create/room/' + curr_room,   // url
                { myData: 'This is my data.' }, // data to be submit
                function (data, status, jqXHR) {// success callback
                    $('p').append('status: ' + status + ', data: ' + data);
                    clientID = data.clientID
                    if (data.room.videoID == "") {
                        data.room.videoID = vidId
                    }
                    if (data.room.videoPosition) {
                        vid_pos = data.room.videoPosition + (parseFloat(Date.now()) - data.room.videoPositionUpdateTimestamp) / 1000
                    } else {
                        console.log("VIDEO POSITION MOVED TO 0")
                        vid_pos = 0
                    }
                    player.cueVideoById(data.room.videoID, vid_pos, "large");
                    // player.seekTo(vid_pos, true);

                    vis = true
                    if (data.room.videoStatus == "play") {
                        player.playVideo();
                        // stompClient.send("/app/youtube/timing_event", {}, JSON.stringify({
                        //     'clientID': clientID, 'roomName': curr_room, 'streamPosition': player.getCurrentTime(),
                        //     'positionSnapshotTime': Date.now()
                        // }))
                    }
                    else if (data.room.videoStatus == "pause") {
                        player.pauseVideo();
                    }
                    var interval = setInterval(function () {

                        stompClient.send("/app/youtube/timing_event", {}, JSON.stringify({
                            'clientID': clientID, 'roomName': curr_room, 'streamPosition': player.getCurrentTime(),
                            'positionSnapshotTime': Date.now()
                        }))
                    }, 10000)
                    //get room information and start video from received ideal position
                });
        } else {
            stompClient.send("/app/youtube/" + curr_room, {}, JSON.stringify({ 'name': 'reconnectEvent', 'value': clientID }));
            error = false;
        }
        setConnected(true);
        console.log('Connected: ' + frame)

        stompClient.subscribe('/topic/' + room, function (greeting) {
            if (ours == false) {
                vis = true
                console.log("Message received to room: " + room)
                event = JSON.parse(greeting.body)
                if (event.name == "play") {
                    player.playVideo();
                    stompClient.send("/app/youtube/timing_event", {}, JSON.stringify({
                        'clientID': clientID, 'roomName': curr_room, 'streamPosition': player.getCurrentTime(),
                        'positionSnapshotTime': Date.now()
                    }))
                }
                else if (event.name == "pause") {
                    player.pauseVideo();
                } else if (event.name == "buffering") {
                    player.seekTo(event.value, true);
                } else if (event.name == "idealPosition") {
                    streamPosition = event.videoPosition + (parseFloat(Date.now()) - event.videoPositionUpdateTimestamp) / 1000;
                    if (Math.abs(player.getCurrentTime() - streamPosition) > 0.01)
                        player.seekTo(streamPosition, true);
                }
                else {
                    player.cueVideoById(event.value, 0, "large");
                }
            } else {
                ours = false

            }
        });
    }, function (message) {
        console.log("STOMP CLIENT DISCONNECTED")
        console.log("The message is " + message)
        error = true
        handleError()
    });
    // var interval = setInterval(function () {

    //     stompClient.send("/app/youtube/timing_event", {}, JSON.stringify({
    //         'clientID': clientID, 'roomName': curr_room, 'streamPosition': player.getCurrentTime(),
    //         'positionSnapshotTime': Date.now()
    //     }))
    // }, 10000)
}

function handleError() {
    joinRoom(curr_room)
}


function onPlayerStateChange(event) {
    console.log("Client ID is " + clientID)

    if (vis == false) {
        ours = true
        switch (event.data) {
            case YT.PlayerState.PLAYING:
                console.log("sending play event")
                sendEvent("play", '')
                break;
            case YT.PlayerState.PAUSED:
                console.log("sending play event")
                sendEvent("pause", '')
                break;
            case YT.PlayerState.BUFFERING: // If they seeked, dont send this.
                if (event.target.playerInfo.currentTime > 2)
                    sendEvent("buffering", event.target.playerInfo.currentTime)
        }
    } else {
        ours = false;
        vis = false
    }
}

function cueVideoFromURL(url) {
    if (!url) return alert("Enter a valid YouTube URL");
    var video_id = url.split('v=')[1];
    var ampersandPosition = video_id.indexOf('&');
    if (ampersandPosition != -1) {
        video_id = video_id.substring(0, ampersandPosition);
    }
    player.cueVideoById(video_id, 0, "large");
    ours = true
    sendEvent("cue", video_id)
}

// function cueVideoFromID(videoID) {
//     player.cueVideoById(video_id, 0, "large");
// }

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendEvent(name, val) {
    stompClient.send("/app/youtube/" + curr_room, {}, JSON.stringify({ 'name': name, 'value': val }));
}

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $("#connect").click(function () { connect(); });
    $("#disconnect").click(function () { disconnect(); });
    $("#send").click(function () { sendName(); });
});
