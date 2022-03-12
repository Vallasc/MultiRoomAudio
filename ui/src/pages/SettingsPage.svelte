<script>
    import { onMount, onDestroy } from "svelte"
    import {
        Page,
        Navbar,
        ListItem,
        List,
        Toggle,
        Range
    } from "framework7-svelte"
    import { webSocket } from "../stores"

    let k = 4
    let useWeights = true
    let confirmRoom = true
    let filterPower = -65
    let clientFingerprintWindowSize = 2

    onMount(() => {
        socketSetup()
        getSettings()
    })

    onDestroy(() => {
        saveSettings()
        socketDestroy()
    })

    function socketSetup() {
        try {
            $webSocket.addEventListener("message", onSocketMessage)
        } catch (e) {
            console.error(e)
        }
    }

    // Socket functions
    function onSocketMessage(event){
        let message = JSON.parse(event.data)
        switch(message.type){
            case "SETTINGS":
                k = message.k
                useWeights = message.useWeights
                confirmRoom = message.confirmRoom
                filterPower = message.filterPower
                clientFingerprintWindowSize = message.clientFingerprintWindowSize
                break
        }
    }

    function socketDestroy() {
        $webSocket.removeEventListener("message", onSocketMessage)
    }

    function getSettings() {
        $webSocket.send(
            JSON.stringify({
                type: "GET_SETTINGS"
            })
        )
    }

    function saveSettings() {
        console.log("Save settings: " + JSON.stringify({
                type: "SETTINGS",
                k : k,
                useWeights : useWeights,
                confirmRoom : confirmRoom,
                filterPower : filterPower,
                clientFingerprintWindowSize : clientFingerprintWindowSize
            }))
        $webSocket.send(
            JSON.stringify({
                type: "SETTINGS",
                k : k,
                useWeights : useWeights,
                confirmRoom : confirmRoom,
                filterPower : filterPower,
                clientFingerprintWindowSize : clientFingerprintWindowSize
            })
        )
    }

</script>

<Page>
    <Navbar title="Settings" backLink="Back" />
    <List mediaList>
        <ListItem title="K" text="Select K value used in KNN algorithm.">
            <div style="height:10px"></div>
            <Range
            min={1}
            max={7}
            label={true}
            step={1}
            value={k}
            scale={true}
            scaleSteps={6}
            scaleSubSteps={1}
            onRangeChanged = {(value) => k = value}
          />
          <div style="height:15px"></div>
        </ListItem>
        <ListItem title="Use WKNN" text="Use te weighted version of KNN algorithm.">
            <span slot="after">
                <Toggle bind:checked = {useWeights}/>
            </span>
        </ListItem>
        <ListItem title="Confirm room" text="When there are not enough samples to locate the room, the user is asked to specify it and the measurement is added to the database.">
            <span slot="after">
                <Toggle bind:checked = {confirmRoom}/>
            </span>
        </ListItem>
        <ListItem title="Signal filter (dBm)" text="Measurements with signal strength below this threshold are discarded.">
            <div style="height:10px"></div>
            <Range
            min={-80}
            max={0}
            label={true}
            step={5}
            value={filterPower}
            scale={true}
            scaleSteps={8}
            scaleSubSteps={2}
            onRangeChange = {(value) => filterPower = value}
          />
          <div style="height:15px"></div>
        </ListItem>
        <ListItem title="Client window size" text="Window of samples collected during online phase.">
            <div style="height:10px"></div>
            <Range
            min={1}
            max={4}
            label={true}
            step={1}
            value={clientFingerprintWindowSize}
            scale={true}
            scaleSteps={3}
            scaleSubSteps={1}
            onRangeChange = {(value) => clientFingerprintWindowSize = value}
          />
          <div style="height:15px"></div>
        </ListItem>
    </List>
</Page>
