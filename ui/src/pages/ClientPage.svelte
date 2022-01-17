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
        Block,
        Chip,
    } from "framework7-svelte"
    import { deviceId, webSocket } from "../stores"
    import NowPlaying from "./NowPlaying.svelte"

    let socket
    const blankSong =
        "http://" + location.hostname + ":80/imgs/blank_album.png"

    onMount(() => {
        fetchSongs()
        socketSetup()
    })

    onDestroy(() => {
        socketDestroy()
    })

    function socketSetup() {
        socket = new WebSocket("ws://" + location.hostname + "/websocket")
        socket.addEventListener("open", onSocketOpen)
        socket.addEventListener("message", onSocketMessage)
        socket.addEventListener("close", onSocketClose)
        webSocket.set(socket)
    }

    // Socket functions
    function onSocketOpen(){
        sendInitMessage()
    }
    function onSocketMessage(event){
        processMessage(JSON.parse(event.data))
    }
    function onSocketClose(event) {
        // TODO print connection lost
        console.log(event)
    }

    function socketDestroy() {
        socket.removeEventListener("open", onSocketOpen)
        socket.removeEventListener("message", onSocketMessage)
        socket.removeEventListener("close", onSocketClose)
        webSocket.set(null)
    }

    function sendInitMessage() {
        console.log($deviceId)
        socket.send(
            JSON.stringify({
                type: "HELLO",
                deviceType: 0, // client type
                id: $deviceId,
                name: "vallascClient", // TODO change name
            })
        )
    }

    function makeImageurl(albumImageUrl) {
        if (albumImageUrl != null)
            albumImageUrl =
                "http://" + location.hostname + ":8080/" + albumImageUrl.replace("./", "")
        else albumImageUrl = blankSong
        return albumImageUrl
    }

    let songs = []
    let speakerList = []
    let popupOpened = false
    let state = 0 // 0 stop, 1 play, 2 pause
    let playingSong = null
    let progress = 0

    function processMessage(message) {
        //console.log(message)
        switch (message.type) {
            case "PLAY":
                console.log(message)
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
                speakerList = message.speakerList
                break
        }
    }

    async function fetchSongs() {
        let res = await fetch("http://" + location.hostname + ":8080/songs", {
            method: "GET",
        })
        songs = await res.json()
        songs.forEach((song) => {
            if (song.albumImageUrl != null)
                song.albumImageUrl =
                    "http://" +
                    location.hostname +
                    ":8080/" +
                    song.albumImageUrl.replace("./", "")
            else song.albumImageUrl = "./imgs/blank_album.png"
            song.songUrl =
                "http://" +
                location.hostname +
                ":8080/" +
                song.songUrl.replace("./", "")
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
                            iconMd="material:volume_mute"
                        />
                    {:else}
                        <Chip
                            text={speaker.name}
                            color="#6200ee"
                            iconMd="material:volume_up"
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
                <div class="toolbarBox" on:click={() => (popupOpened = true)}>
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
                    on:click={() => (popupOpened = true)}
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

    <Block>Bella zio se ti piace il nostro lavoro mettici un bel 30 😆</Block>
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
</Page>

<Popup
    opened={popupOpened}
    onPopupClosed={() => (popupOpened = false)}
    swipeToClose
>
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

    /*:global(.list ul:before) {
    background-color: transparent;
  }

  :global(.list ul:after) {
    background-color: transparent;
  }*/

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
</style>
