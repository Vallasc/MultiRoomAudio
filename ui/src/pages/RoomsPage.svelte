<script>
    import { onMount, onDestroy } from "svelte"
    import {
        Page,
        Navbar,
        Fab,
        Icon,
        Link,
        ListItem,
        List,
        f7,
        Popup,
        Button,
        Progressbar
    } from "framework7-svelte"
    import WalkRoomAnimation from "./WalkRoomAnimation.svelte"
    import { webSocket } from "../stores"

    let popupOpened = false
    let rooms = []
    let roomsLenght = 0
    let nscan = 0
    const _maxScan = 4
    onMount(() => {
        socketSetup()
        getRooms()
    })

    onDestroy(() => {
        socketDestroy()
    })

    function socketSetup() {
        $webSocket.addEventListener("message", onSocketMessage)
    }

    // Socket functions
    function onSocketMessage(event){
        let message = JSON.parse(event.data)
        switch(message.type){
            case "ROOMS":
                rooms = message.rooms
                roomsLenght = rooms.length
                break
        }
    }

    function socketDestroy() {
        $webSocket.removeEventListener("message", onSocketMessage)
    }

    function getRooms() {
        $webSocket.send(
            JSON.stringify({
                type: "ROOMS_REQUEST"
            })
        )
    }

    function setRoom(roomId) {
        $webSocket.send(
            JSON.stringify({
                type: "CREATE_ROOM",
                roomId: roomId
            })
        )
    }

    function deleteRoom(roomId) {
        f7.dialog.confirm(
            "Do you really want to delete "+ roomId + "? All the saved fingerprints will be lost.",
            "Multiroom Audio",
            () => {
                $webSocket.send(
                    JSON.stringify({
                        type: "DELETE_ROOM",
                        roomId: roomId
                    })
                )
            }
        )
    }

    function dialogInsertRoomName() {
        f7.dialog.prompt("Insert room name", "Multiroom Audio", (value) => {
            if(value.trim()){
                value = value.substring(0, 15)
                console.log("New room: " + value)
                //setRoom(value)
                startMisuration(value)
            } else {
                dialogInsertRoomName()
            }
        })
    }

    let currentRoomId = ""

    function startMisuration(roomId) {
        setRoom(roomId)
        f7.dialog.confirm(
            "Do you want to start scanning "+ roomId + "?",
            "Multiroom Audio",
            () => {
                popupOpened = true
                currentRoomId = roomId
            }
        )
    }

    function startScan(){
        if(nscan < _maxScan){
            var btn = document.getElementById("start-button")
            btn.disabled = true
            btn.style.visibility = "hidden"
            $webSocket.send(
            JSON.stringify({
                type: "SCAN_ROOM",
                roomId: currentRoomId,
                startScan: true, 
                nScan : nscan
                })
            )
            nscan++
            setTimeout(() => { stopScan(nscan, btn); }, 10000);
        }
        
    }

    function stopScan(n, btn) {
        $webSocket.send(
            JSON.stringify({
                type: "SCAN_ROOM",
                roomId: currentRoomId,
                startScan: false, 
                nScan : n
            })
        )
        btn.disabled = false
        btn.style.visibility = "visible"
        if(nscan == _maxScan){
                nscan = 0
                console.log("FINISHED")
                popupOpened = false
            }
        
    }  


</script>

<Page>
    <!-- Top Navbar -->
    <Navbar title="Rooms" backLink />
    <Fab position="right-bottom" onClick={dialogInsertRoomName} text="New">
        <Icon md="material:add" />
    </Fab>

    {#if roomsLenght > 0}
        <List mediaList>
            {#each rooms as room}
            <ListItem title="{room.roomId}" subtitle="{room.samples + " " +room.nscan} fingerpint{room.samples == 1 ? "" : "s"}">
                    <span slot="after">
                        <Link iconMd="material:radar" onClick={() => startMisuration(room.roomId)} />
                        <!-- svelte-ignore a11y-missing-attribute -->
                        <!-- svelte-ignore a11y-missing-content -->
                        <a style="margin-left: 16px;" />
                        <Link
                            iconMd="material:delete"
                            color="red"
                            onClick={() => deleteRoom(room.roomId)}
                        />
                        <!-- svelte-ignore a11y-missing-attribute -->
                        <!-- svelte-ignore a11y-missing-content -->
                        <a style="margin-left: 8px;" />
                    </span>
                </ListItem>
            {/each}
        </List>
    {:else}
        <div class="center">
            <div/>
            <div class="no-rooms">
                Add a new room ↘️
            </div>
            <div/>
        </div>
    {/if}

    <Popup id="popup" opened={popupOpened} onPopupClosed={() => (popupOpened = false)} backdrop closeByBackdropClick = {false}>
        <Page>
            <div class="center">
                <Progressbar infinite></Progressbar>
                <div class="block text-title">Go to a corner of the room</div>
                <WalkRoomAnimation roomName={currentRoomId.substring(0, 9)} />
                {#if nscan<_maxScan}
                    <div class="button-start">
                        <Button id="start-button" icon="material:start" 
                            large fill color="blue" 
                            onClick={startScan}>Start</Button>
                    </div>           
                {/if}
            </div>
        </Page>
    </Popup>

</Page>


<style>

    .text-title {
        font-size: 26px !important;
        font-weight: 600;
        text-align: center;
        margin-top: 40px !important;
        margin-bottom: 12px !important;
    }

    .no-rooms {
        font-size: 34px !important;
        font-weight: 600;
    }

    .button-stop {
        margin-left: 16px;
        margin-right: 16px;
        margin-bottom: 13%;
        width: 70%;
        max-width: 900px;
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