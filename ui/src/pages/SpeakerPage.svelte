<script>
    import NowPlaying from "./NowPlaying.svelte";
    import { Page } from "framework7-svelte"


    let socket = new WebSocket("ws://localhost/websocket");
    const audio = new Audio();
    audio.mute = true;

    let songList = []
    let songId = 0;

    let imageUrl = "http://localhost/imgs/blank_album.png"
    let title = "Waiting for music..."
    let artist = ""

    socket.onmessage = function(event) {
        let message = JSON.parse(event.data)
        console.log(message)

        songList = message.songList
        songId = message.songId

        let song = songList[songId]
        imageUrl = "http://localhost:8080/" + song.albumImageUrl.replace("./", "")
        title = song.title
        artist = song.artist

        audio.src = "http://localhost:8080/" + song.songUrl.replace("./", "")
        audio.play();
    };

    document.body.addEventListener("mousemove", function () {
        if(audio.mute == true)
            audio.mute = false;
    })

    function unmute(){

    }


</script>

<Page>
    <div class="navbar" data-f7-slot="fixed">
        <div class="navbar-bg" />
        <div class="navbar-inner sliding">
            <div class="title" >Multiroom Audio  </div>
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