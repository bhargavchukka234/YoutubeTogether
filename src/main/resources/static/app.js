var stompClient = null;

// This code loads the IFrame Player API code asynchronously. From YouTube API webpage.
var tag = document.createElement('script');
tag.src = "https://www.youtube.com/iframe_api";
var firstScriptTag = document.getElementsByTagName('script')[0];
firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);
var curr_room = ""
// This function creates an <iframe> (and YouTube player) after the API code downloads.
var player;
var vidId = "_ZtVOce2_98";  // The youtube Video ID for your starting video - dQw4w9WgXcQ
//var seek = true
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



function joinRoom(room) {
    //client makes http POST request to the sync manager to register the room and receives the unique client ID in the same call
    //If the room already exists, client should be added to the existing room in the redis
    //else new room should be created and this client should be added to the room 
    curr_room = room
    var socket = new SockJS('/gs-guide-websocket'); //this websocket connection has to go through nginx load balancer 
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame)

        stompClient.subscribe('/topic/' + room, function (greeting) {
            if (ours == false) {
                console.log("Message received to room: " + room)
                event = JSON.parse(greeting.body)
                if (event.name == "play") {
                    player.playVideo();
                }
                else if (event.name == "pause") {
                    player.pauseVideo();
                } else if (event.name == "buffering") {
                    player.seekTo(event.value, true);
                } else {
                    player.cueVideoById(event.value, 0, "large");
                }
            }
            ours = false;
        });
    });
    var interval = setInterval(function () {
        var temp = player.getCurrentTime()
        stompClient.send("/app/youtube/timing_event", {}, JSON.stringify({
            'clientID': 'randomID', 'roomName': curr_room, 'streamPosition': temp,
            'positionSnapshotTime' : Date.now()
        }, 50000))
    }, 10000)
}


function onPlayerStateChange(event) {
    // console.log(event);
    ours = true
    switch (event.data) {
        case YT.PlayerState.PLAYING:
            sendEvent("play", '')
            break;
        case YT.PlayerState.PAUSED:
            sendEvent("pause", '')
            break;
        case YT.PlayerState.BUFFERING: // If they seeked, dont send this.
            seek = false;
            sendEvent("buffering", event.target.playerInfo.currentTime)
    }
}

function cueVideoFromURL(url) {
    if (!url) return alert("Enter a valid YouTube URL");
    //var url = form.url.value;
    var video_id = url.split('v=')[1];
    var ampersandPosition = video_id.indexOf('&');
    if (ampersandPosition != -1) {
        video_id = video_id.substring(0, ampersandPosition);
    }
    player.cueVideoById(video_id, 0, "large");
    sendEvent("cue", video_id)
}

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
