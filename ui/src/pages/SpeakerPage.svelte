<script>
    import NowPlaying from "./NowPlaying.svelte";
    import { Page, f7 } from "framework7-svelte"
    import { onMount } from 'svelte';
    import { f7ready } from 'framework7-svelte';


    let socket = null;
    const audio = new Audio();
    console.log(audio)
    audio.mute = true;

    let songList = []
    let songId = -1;

    let imageUrl = "http://" + location.hostname + ":80/imgs/blank_album.png"
    let title = "Waiting for music..."
    let artist = ""

    let speakerId;
    onMount(() => {
        f7ready(() => {
            loadId()
            socketSetup()
        })
    })

    function loadId(){
        console.log("okookkook")
        speakerId = localStorage.getItem("id")
        console.log(speakerId)
        if(speakerId == null){
            speakerId = Math.random().toString(36).substring(2, 15) + 
                        Math.random().toString(36).substring(2, 15) + 
                        Math.random().toString(36).substring(2, 15)
            localStorage.setItem("id", speakerId)
        }
    }

    function socketSetup(){
        socket = new WebSocket("ws://" + location.hostname + "/websocket")

        socket.onopen = () => {
            f7.dialog.alert('Speaker connected')
        }

        socket.onmessage = (event) => {
            let message = JSON.parse(event.data)
            console.log(message)

            songList = message.songList
            songId = message.songId

            let song = songList[songId]
            imageUrl = "http://" + location.hostname + ":8080/" + song.albumImageUrl.replace("./", "")
            title = song.title
            artist = song.artist

            audio.src = "http://" + location.hostname + ":8080/" + song.songUrl.replace("./", "")
            audio.play()
            sendCurrentTime()
        }

        socket.onclose = (event) => {
            console.log(event)
            window.clearTimeout(intervalResponse)
        }
    }


    function sendInitMessage(){
        socket.send(JSON.stringify({
            type : "HELLO",
            deviceType : 1, // speaker type
            id: speakerId
        }))
    }



    audio.onplay = () => {

    }

    audio.onended = () => {
        let songId = -1;
        console.log("Audio ended")
    }

    let intervalResponse
    function sendCurrentTime() {
        intervalResponse = window.setInterval(() => {
            socket.send(JSON.stringify({
                songId : songId,
                fromTimeMs : audio.currentTime
            }));
        }, 1000); 
    }

</script>

<Page>
    <div class="navbar" data-f7-slot="fixed">
        <div class="navbar-bg" />
        <div class="navbar-inner sliding">
            <div class="title" >Multiroom Audio</div>
        </div>
    </div>
    <NowPlaying 
        imageUrl = {imageUrl} 
        title = {title} 
        artist = {artist}
        sliderValue = {0} 
        songDurationMs = {100}
        isSpeaker = {true} />
</Page>

<style>
    .navbar-bg {
        background: transparent;
    }

    .navbar-bg:before {
        background: transparent;
    }

    .title {
        text-align: center;
        color: var(--f7-fab-bg-color,var(--f7-theme-color));
    }

    .navbar-inner {
        justify-content: center;
    }
</style>