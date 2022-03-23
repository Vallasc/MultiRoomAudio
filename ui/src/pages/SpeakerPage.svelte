<script>
    import NowPlaying from "./NowPlaying.svelte"
    import { Page, f7, f7ready, Navbar, NavRight, Link, NavTitle } from "framework7-svelte"
    import { onMount } from 'svelte'
    import { deviceId, hostname, webPort, musicPort } from "../stores"

    let socket = null
    const audio = new Audio()
    audio.volume = 0
    let muted = true

    let songId = -1
    let currentTimeSec = 0
    let songDurationSec = 0

    const blankSong = "http://" + $hostname + ":" + $webPort + "/imgs/blank_album.png"
    let imageUrl = blankSong
    let title = "Waiting for music..."
    let artist = ""

    let speakerName
    onMount(() => {
        f7ready(() => {
            loadId()
            loadName()
            setTimeout( () => dialogInsertName(), 1000)
        })
    })

    function loadId(){
        $deviceId = localStorage.getItem("id")
        if($deviceId == null){
            $deviceId = Math.random().toString(36).substring(2, 15) + 
                        Math.random().toString(36).substring(2, 15) + 
                        Math.random().toString(36).substring(2, 15) + 
                        Math.random().toString(36).substring(2, 15)
            localStorage.setItem("id", $deviceId)
        }
        console.log("Id : " + $deviceId)
    }

    function loadName(){
        speakerName = localStorage.getItem("name")
        if(speakerName == null){
            speakerName = "Bed room"
            localStorage.setItem("name", speakerName)
        }
    }

    function alertConnectionClosed(){
        setMute(true)
        f7.dialog.preloader('Connection closed...')
        setTimeout( () => location.reload(), 5000)
    }

    function socketSetup() {
        function onSocketOpen(){
            sendInitMessage()
        }
        function onSocketMessage(event){
            processMessage(JSON.parse(event.data))
        }
        function onSocketClose(event) {
            // TODO print connection lost
            console.log(event)
            alertConnectionClosed()
        }

        socket = new WebSocket("ws://" + $hostname + ":" + $webPort + "/websocket")
        socket.addEventListener("open", onSocketOpen)
        socket.addEventListener("message", onSocketMessage)
        socket.addEventListener("close", onSocketClose)
    }

    let alertShowed = false
    function dialogInsertName(){
        f7.dialog.prompt('Insert speaker name', 'Multiroom Audio', (value) => {
                if(value.trim() === "")
                    dialogInsertName()
                else {
                    alertShowed = true
                    speakerName = value
                    localStorage.setItem("name", speakerName)
                    console.log("Name : " + speakerName)
                    socketSetup()
                }
        }, () => {
            dialogInsertName()
        }, speakerName)
    }

    function sendInitMessage(){
        socket.send(JSON.stringify({
            type : "HELLO",
            deviceType : 1, // speaker type
            id: $deviceId,
            name: speakerName
        }))
    }

    function processMessage(message) {
        //console.log(message)
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
                console.log(message)
                setMute(message.isMuted)
                break
        }    
    }

    async function play(message) {
        if(songId != message.songId){
            // Get image and all metadata
            songId = message.songId
            let song = message.song
            console.log("Playing song")
            console.log("http://" + $hostname + ":" + $musicPort + "/" + song.songUrl.replace("./", ""))
            console.log(song)

            if(song.albumImageUrl == null)
                imageUrl = blankSong
            else 
                imageUrl = "http://" + $hostname + ":" + $musicPort + "/" + song.albumImageUrl.replace("./", "")
            title = song.title ? song.title : song.fileName
            artist = song.artist ? song.artist : ""

            let res = await fetch("http://" + $hostname + ":" + $musicPort + "/" + song.songUrl.replace("./", ""))
            let blob = await res.blob()

            audio.pause()
            audio.src = URL.createObjectURL(blob)
            audio.onloadeddata = () => {
                songDurationSec = audio.duration
            }
            audio.load()
            audio.currentTime = message.fromTimeSec
            audio.play()
        } else {
            // Allign time
            const syncWindow = 4
            if(audio.currentTime > message.fromTimeSec + syncWindow/2 || audio.currentTime < message.fromTimeSec - syncWindow/2)
                audio.currentTime = message.fromTimeSec
            if(audio.paused && !audio.ended)
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

    function sleep(ms) {
        return new Promise(resolve => setTimeout(resolve, ms))
    }

    async function setMute(value) {
        if(!value && audio.volume < 1)
            for(let i=0; i<=10; i++){
                audio.volume = i/10
                await sleep(200)
            }
        if(value && audio.volume > 0)
            for(let i=10; i>=0; i--){
                audio.volume = i/10
                await sleep(200)
            }
        muted = audio.volume == 0
    }

    // Update progress bar
    setInterval(() => currentTimeSec = audio.currentTime, 500)


</script>

<Page>
    <Navbar title="Multi-Room Audio - {speakerName}" transparent noShadow>
        <NavRight>
            {#if muted}
                <Link iconMd="material:volume_mute" iconOnly/>
            {:else}
                <Link iconMd="material:volume_up" iconOnly/>
            {/if}
        </NavRight>
    </Navbar>
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
    :global(.title) {
        opacity: 1 !important;
    }
</style>