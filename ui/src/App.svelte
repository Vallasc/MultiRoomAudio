<script>
    import { App, View, f7 } from "framework7-svelte"
    import ClientPage from "./pages/ClientPage.svelte"
    import RoomsPage from "./pages/RoomsPage.svelte"
    import SpeakerPage from "./pages/SpeakerPage.svelte"
    import SettingsPage from "./pages/SettingsPage.svelte"
    import { deviceId, hostname, webPort, musicPort, isMoving } from "./stores"

    const f7Params = {
        id: "com.unibo.multiroomaudio",
        theme : "md",
        routes: [
            {
                path: "/musiclist",
                component: ClientPage,
            },
            {
                path: "/rooms",
                component: RoomsPage,
            },
            {
                path: "/speaker",
                component: SpeakerPage,
            },
            {
                path: "/settings",
                component: SettingsPage,
            },
        ],
    }

    const urlParams = new URLSearchParams(window.location.search)
    let isClient = false
    let isSpeaker = false
    
    $hostname = location.hostname
    $deviceId = urlParams.get("id")
    $webPort = urlParams.get("wPort")
    $musicPort = urlParams.get("mPort")

    if ( urlParams.get("type") && urlParams.get("type") === "client" ) {
        isClient = true
    } else if ( urlParams.get("type") && urlParams.get("type") === "speaker") {
        isSpeaker = true
    }

    let themeDark = false

    function setIsMoving(value){
        $isMoving = value
        console.log("isMoving: " + value)
    }
    window.setIsMoving = setIsMoving
</script>

<App {...f7Params} {themeDark}>
    {#if isClient}
        <View url="/musiclist" />
    {:else if isSpeaker}
        <View url="/speaker" stackPages={true} main={true} />
    {:else}
        <h1>Open a Multiroom-Audio Client</h1>
    {/if}
</App>
