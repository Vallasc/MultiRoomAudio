<script>
    import NowPlaying from "./NowPlaying.svelte"
    import { Page, f7 } from "framework7-svelte"
    import { onMount } from 'svelte'
    import { f7ready } from 'framework7-svelte'


    let socket = null
    const audio = new Audio()
    audio.muted = true
    let songId = -1
    let currentTimeSec = 0
    let songDurationSec = 0

    const blankSong = "http://" + location.hostname + ":80/imgs/blank_album.png"
    let imageUrl = blankSong
    let title = "Waiting for music..."
    let artist = ""

    let speakerId
    let speakerName

    onMount(() => {
        f7ready(() => {
            loadId()
            loadName()
            dialogInsertName()
        })
    })

    function loadId(){
        speakerId = localStorage.getItem("id")
        if(speakerId == null){
            speakerId = Math.random().toString(36).substring(2, 15) + 
                        Math.random().toString(36).substring(2, 15) + 
                        Math.random().toString(36).substring(2, 15) + 
                        Math.random().toString(36).substring(2, 15)
            localStorage.setItem("id", speakerId)
        }
        console.log("Id : " + speakerId)
    }

    function loadName(){
        speakerName = localStorage.getItem("name")
        if(speakerName == null){
            speakerName = "Bed room"
            localStorage.setItem("name", speakerName)
        }
    }

    function saveName(value){
        if(value == speakerName)
            return
        speakerName = value
        localStorage.setItem("name", value)
    }

    function socketSetup(){
        socket = new WebSocket("ws://" + location.hostname + "/websocket")

        socket.onopen = () => {
            sendInitMessage()
        }

        socket.onmessage = (event) => {
            processMessage(JSON.parse(event.data))
        }

        socket.onclose = (event) => {
            console.log(event)
            window.clearTimeout(intervalResponse)
        }
    }

    let alertShowed = false
    function dialogInsertName(){
        f7.dialog.prompt('Insert speaker name', 'Multiroom Audio', (value) => {
                alertShowed = true
                saveName(value)
                console.log("Name : " + speakerId)
                socketSetup()
        }, () => {
            dialogInsertName()
        }, speakerName)
    }

    function sendInitMessage(){
        socket.send(JSON.stringify({
            type : "HELLO",
            deviceType : 1, // speaker type
            id: speakerId,
            name: speakerName
        }))
    }

    function processMessage(message) {
        console.log(message)
        switch(message.type){
            case "PLAY":
                play(message)
                break
            case "PAUSE":
                pause()
                break
            case "STOP":
                stop()
                break
            case "MUTE":
                audio.muted = message.isMuted
                break
        }    
    }

    async function play(message) {
        if(songId != message.songId){
            // Get image and all metadata
            songId = message.songId
            let song = message.song
            console.log("Playing song")
            console.log(song)

            if(song.albumImageUrl == null)
                imageUrl = blankSong
            else 
                imageUrl = "http://" + location.hostname + ":8080/" + song.albumImageUrl.replace("./", "")
            title = song.title
            artist = song.artist

            let res = await fetch("http://" + location.hostname + ":8080/" + song.songUrl.replace("./", ""))
            let blob = await res.blob()
            audio.src = URL.createObjectURL(blob)
            audio.onloadeddata = () => {
                songDurationSec = audio.duration
            }
            audio.load()
            audio.currentTime = message.fromTimeSec
            if(alertShowed)
                audio.play()
        } else {
            // Allign time
            const syncWindow = 4
            if(audio.currentTime > message.fromTimeSec + syncWindow/2 || audio.currentTime < message.fromTimeSec - syncWindow/2)
                audio.currentTime = message.fromTimeSec
            if(audio.paused)
                audio.play()
                
        }
        //console.log("Current time " + audio.currentTime)
        //console.log(message.fromTimeSec)
    }

    function pause(){
        audio.pause()
    }

    function stop(){
        audio.load()
    }

    // Update progress bar
    let updateTime
    //audio.onplay = () => {
    setInterval(() => currentTimeSec = audio.currentTime, 500)
    //}

    audio.onended = () => {
        console.log("Audio ended")
        //state = 0
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
        currentValue = {currentTimeSec} 
        songDuration = {songDurationSec}
        isSpeaker
        disabled />
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