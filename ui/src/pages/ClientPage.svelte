<script>
    import { onMount, onDestroy } from "svelte"
    import {
        Page,
        Navbar,
        Toolbar,
        Link,
        NavRight,
        ListItem,
        List,
        Popup,
        Chip,
        BlockTitle,
        f7
    } from "framework7-svelte"
    import { deviceId, hostname, webPort, musicPort, webSocket } from "../stores"
    import NowPlaying from "./NowPlaying.svelte"

    let socket
    const blankSong =
        "http://" + $hostname + ":" + $webPort + "/imgs/blank_album.png"
    let popupSong;
    let popupRooms;

    let state = 0 // 0 stop, 1 play, 2 pause
    let playingSong = null
    let progress = 0
    let currentSpeaker = null

    let songs = []
    $: songsLenght = songs.length
    let speakerList = []
    $: speakerLenght = speakerList.length
    let rooms = []
    $: roomsLenght = rooms.length

    onMount(() => {
        fetchSongs()
        setTimeout( () => socketSetup(), 1000)
    })

    function alertConnectionClosed(){
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

        try {
            socket = new WebSocket("ws://" + $hostname + ":" + $webPort + "/websocket")
            socket.addEventListener("open", onSocketOpen)
            socket.addEventListener("message", onSocketMessage)
            socket.addEventListener("close", onSocketClose)
            webSocket.set(socket)
        } catch (e) {
            console.error(e)
        }
    }

    function sendInitMessage() {
        console.log($deviceId)
        socket.send(
            JSON.stringify({
                type: "HELLO",
                deviceType: 0, // client type
                id: $deviceId,
                name: "client",
            })
        )
    }

    function makeImageurl(albumImageUrl) {
        if (albumImageUrl != null)
            albumImageUrl =
                "http://" + $hostname + ":" + $musicPort + "/" + albumImageUrl.replace("./", "")
        else albumImageUrl = blankSong
        return albumImageUrl
    }

    function processMessage(message) {
        //console.log(message)
        switch (message.type) {
            case "PLAY":
                state = 1
                playingSong = message
                playingSong.song.albumImageUrl = makeImageurl(
                    playingSong.song.albumImageUrl
                )
                progress =
                    (playingSong.fromTimeSec * 100) /
                    (playingSong.song.durationMs / 1000)
                break
            case "PAUSE":
                state = 2
                playingSong = message
                playingSong.song.albumImageUrl = makeImageurl(
                    playingSong.song.albumImageUrl
                )
                progress =
                    (playingSong.fromTimeSec * 100) /
                    (playingSong.song.durationMs / 1000)
                break
            case "STOP":
                state = 0
                progress = 0
                break
            case "SPEAKER_LIST":
                message.speakerList.sort((a, b) =>{
                    if(!a.isMuted) return -1
                    return 1
                })
                speakerList = message.speakerList
                console.log("Speakers")
                console.log(speakerList)
                break
            case "ROOMS":
                rooms = message.rooms
                console.log("Rooms")
                console.log(rooms)
                break
            case "CONFIRMATION":
                console.log(message)
                confirmRoom(message.rooms)
                break;
        }
    }

    function confirmRoom(rooms){
        /*let timer = setTimeout(() => {
            dialog.close()
            sendConfirmation(null)
        }, 20000)*/
        let buttons = []
        for( let room of rooms ){
            console.log(room)
            buttons.push({
                text: room,
                onClick: () => {
                    sendConfirmation(room)
                    //clearTimeout(timer)
                }
            })
        }
        let dialog = f7.dialog.create({
            title: 'Room confirm',
            text: 'Select the room where you are',
            buttons: buttons,
            verticalButtons: true,
        }).open()
    }

    function sendConfirmation(roomId){
        console.log("Send room confirmation")
        socket.send(
            JSON.stringify({
                type: "CONFIRMATION_ROOM",
                roomId: roomId
        }))
    }

    function bindSpeaker(roomId, speakerId){
        $webSocket.send(
            JSON.stringify({
                type: "BIND_SPEAKER",
                speakerId: speakerId,
                roomId: roomId
            })
        )
        popupRooms.instance().close()
    }

    async function fetchSongs() {
        let res = await fetch("http://" + $hostname + ":" + $musicPort + "/songs", {
            method: "GET",
        })
        songs = await res.json()
        songs.forEach((song) => {
            if (song.albumImageUrl != null)
                song.albumImageUrl = "http://" + location.hostname + ":" + $musicPort + "/" + song.albumImageUrl.replace("./", "")
            else song.albumImageUrl = "./imgs/blank_album.png"
            song.songUrl = "http://" + location.hostname + ":" + $musicPort + "/" + song.songUrl.replace("./", "")
            song.isPlaying = false
        })
        console.log("Song list")
        console.log(songs)
    }

    async function playPause() {
        if (state == 2 && playingSong != null) {
            play(playingSong.song, playingSong.fromTimeSec)
        } else if (state == 1) {
            pause()
        }
    }

    async function play(song, fromTimeSec) {
        console.log(song)
        console.log("Playing " + song.title)
        socket.send(
            JSON.stringify({
                type: "PLAY",
                songId: song.id,
                fromTimeSec: fromTimeSec,
            })
        )
    }

    async function pause() {
        console.log("Pause " + playingSong.song.title)
        socket.send(
            JSON.stringify({
                type: "PAUSE",
            })
        )
    }

    function stop() {
        console.log("Stop playing " + playingSong.song.title)
        socket.send(
            JSON.stringify({
                type: "STOP",
            })
        )
    }

    function next() {
        console.log("Next song ")
        socket.send(
            JSON.stringify({
                type: "NEXT",
            })
        )
    }

    function prev() {
        console.log("Prev song ")
        socket.send(
            JSON.stringify({
                type: "PREV",
            })
        )
    }

    let lastTitleBlock = ""
    function setLastTitleBlock(newTitleBlock) {
        if (lastTitleBlock != newTitleBlock) {
            lastTitleBlock = newTitleBlock
            return true
        }
        return false
    }

</script>

<Page>
    <!-- Top Navbar -->
    <Navbar title="Multiroom Audio">
        <NavRight>
            <Link iconMd="material:other_houses" iconOnly href="/rooms/" />
            <Link iconMd="material:settings" iconOnly />
        </NavRight>
    </Navbar>
    <Toolbar top>
        <div class="list-speakers">
            {#each speakerList as speaker}
                <div class="speaker-chip">
                    {#if speaker.isMuted}
                        <Chip
                            text={speaker.name}
                            onClick={() => {
                                popupRooms.instance().open()
                                currentSpeaker = speaker
                            }}
                            color = {rooms.find(room => room.speakers.find(s => s === speaker.id))}
                            iconMd = "material:volume_mute"
                        />
                    {:else}
                        <Chip
                            text={speaker.name}
                            onClick={() => {
                                popupRooms.instance().open()
                                currentSpeaker = speaker
                            }}
                            color = {rooms.find(room => room.speakers.find(s => s === speaker.id))}
                            iconMd = "material:volume_up"
                        />
                    {/if}
                </div>
            {/each}
        </div>
    </Toolbar>

    <!-- Toolbar -->
    {#if playingSong != null}
        <div class="toolbar toolbar-bottom" data-f7-slot="fixed">
            <span class="progressbar">
                <span
                    style="transform: translate3d({progress - 100}%, 0px, 0px);"
                />
            </span>
            <div class="toolbar-inner">
                <div class="toolbarBox" on:click={() => popupSong.instance().open() }>
                    <img
                        id="album"
                        alt={playingSong.song.title}
                        src={playingSong.song.albumImageUrl}
                    />
                    <div class="item-inner">
                        <div class="item-title-row" style="display: flex;">
                            <div class="item-title">
                                {playingSong.song.title}
                            </div>
                        </div>
                        <div class="item-subtitle">
                            {playingSong.song.artist}
                        </div>
                    </div>
                </div>
                <div
                    style="width: 100%;height: 100%;"
                    on:click={() => popupSong.instance().open()}
                />
                <div style="margin-right:14px;">
                    {#if state != 1}
                        <Link
                            iconMd="material:play_arrow"
                            iconOnly
                            on:click={(event) => {
                                event.preventDefault()
                                play(playingSong.song, 0)
                            }}
                        />
                    {:else}
                        <Link
                            iconMd="material:pause"
                            iconOnly
                            on:click={(event) => {
                                event.preventDefault()
                                pause()
                            }}
                        />
                    {/if}
                </div>
            </div>
        </div>
    {/if}
    {#if songsLenght > 0}
        <List mediaList>
            {#each songs as song}
                {#if setLastTitleBlock(song.dirPath)}
                    <li class="list-group-title">
                        <div class="block-path">{song.dirPath}</div>
                    </li>
                {/if}
                {#if playingSong != null && song.id == playingSong.songId && state == 1}
                    <div style="background-color:#5521f314;">
                        <ListItem title={song.title} subtitle={song.artist}>
                            <img
                                slot="media"
                                alt={song.title}
                                src={song.albumImageUrl}
                                width="44"
                            />
                            <span slot="after" style="margin-right: 10px;">
                                <Link
                                    iconMd="material:pause"
                                    iconOnly
                                    on:click={(event) => {
                                        event.preventDefault()
                                        pause()
                                    }}
                                />
                            </span>
                        </ListItem>
                    </div>
                {:else}
                    <ListItem
                        title={song.title}
                        subtitle={song.artist}
                        on:click={(event) => {
                            event.preventDefault()
                            play(song, 0)
                        }}
                        link={true}
                    >
                        <img
                            slot="media"
                            alt={song.title}
                            src={song.albumImageUrl}
                            width="44"
                        />
                        <span slot="after" style="margin-right: 10px;">
                            <Link iconMd="material:play_arrow" iconOnly />
                        </span>
                    </ListItem>
                {/if}
            {/each}
        </List>
    {:else}
        <div class="center">
            <div/>
            <div class="no-rooms">
                🎵 Add mp3 files 🎵
            </div>
            <div/>
        </div>
    {/if}
    <Popup opened={false} backdrop closeByBackdropClick swipeToClose bind:this={popupSong}>
        {#if playingSong != null}
            <NowPlaying
                imageUrl={playingSong.song.albumImageUrl}
                title={playingSong.song.title}
                artist={playingSong.song.artist}
                currentValue={playingSong.fromTimeSec}
                songDuration={playingSong.song.durationMs / 1000}
                onNext={next}
                onPrev={prev}
                onPlayPause={playPause}
                playing={state == 1}
                onValueChanged={(value) => play(playingSong.song, value)}
            />
        {:else}
            <NowPlaying
                imageUrl={blankSong}
                title={"Waiting for music..."}
                artist={""}
                currentValue={0}
                songDuration={0}
            />
        {/if}
    </Popup>

    <Popup opened={false} backdrop swipeToClose closeByBackdropClick bind:this={popupRooms}>
        <Page>
            <div class="navbar">
                <div class="navbar-bg" />
                <div class="navbar-inner">
                    <div class="navbar-bg" />
                    <div class="title">Select the room of the speaker</div>
                    <div class="right" style="margin-right: 14px;">
                        <!-- svelte-ignore a11y-invalid-attribute -->
                        <a
                            class="link icon-only popup-close"
                            href="#"
                            iconmd="material:close"
                            ><i class="icon material-icons" style="">close</i>
                        </a>
                    </div>
                </div>
            </div>
            {#if roomsLenght > 0}
                <div style="padding-left: 18px; padding-right: 18px;">
                    <BlockTitle>Rooms</BlockTitle>
                    <List>
                        {#each rooms as room}
                            <ListItem
                                radio
                                radioIcon = "end"
                                title = {room.roomId}
                                value= {room.roomId}
                                onClick={() => {
                                    bindSpeaker(room.roomId, currentSpeaker.id)
                                    popupRooms.instance().close()
                                }}
                                checked = {currentSpeaker && room.speakers.find(s => s === currentSpeaker.id)}
                            ></ListItem>
                        {/each}
                            <ListItem
                                radio
                                radioIcon = "end"
                                title = "none"
                                value= "none"
                                onClick={() => {
                                    bindSpeaker(null, currentSpeaker.id)
                                    popupRooms.instance().close()
                                }}
                                checked = {currentSpeaker && !rooms.find(room => room.speakers.find(s => s === currentSpeaker.id))}
                            ></ListItem>
                    </List>
                </div>
            {:else}
                <div class="center" style="height:80%">
                    <div/>
                    <div class="no-rooms">
                        Add a new room using <Link iconMd="material:other_houses" iconOnly />
                    </div>
                    <div/>
                </div>
            {/if}
        </Page>
    </Popup>
</Page>

<style>
    .list-speakers {
        white-space: nowrap;
        overflow: auto;
        padding-left: 7px;
        padding-right: 7px;
    }

    .toolbarBox {
        display: flex;
    }

    .toolbarBox img {
        width: 50px;
        height: 50px;
        margin-left: 14px;
        margin-right: 14px;
        margin-top: 7px;
        margin-bottom: 7px;
    }

    img {
        border-radius: 5px;
    }
    :global(.toolbar-bottom) {
        height: 70px !important;
    }

    :global(.item-title-row:before) {
        display: none !important;
    }
    :global(.item-title-row) {
        padding-right: 0px !important;
    }

    .item-title {
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        flex-shrink: 10;
        font-weight: var(--f7-navbar-title-font-weight);
        line-height: var(--f7-navbar-title-line-height);
        text-align: var(--f7-navbar-title-text-align);
        font-size: 16px;
        padding-top: 14px;
        max-width: 60vw;
    }

    .item-subtitle {
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        max-width: 60vw;
    }

    .block-path {
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        max-width: 80vw;
    }

    .progressbar {
        background: #5521f314;
        height: var(--f7-progressbar-height);
    }

    .speaker-chip {
        margin-left: 7px;
        margin-right: 7px;
        display: inline-flex;
    }

    .navbar-bg {
        background: transparent;
    }

    .navbar-bg:before {
        background: transparent;
    }

    .navbar-inner {
        padding-top: 20px;
    }

    .center {
        display: flex;
        flex-direction: column;
        align-items: center;
        height: 100%;
        width: 100%;
        justify-content: space-between;
    }

    .no-rooms {
        font-size: 28px !important;
        font-weight: 600;
    }

    .center {
        display: flex;
        flex-direction: column;
        align-items: center;
        height: 100%;
        width: 100%;
        justify-content: space-between;
    }
</style>
