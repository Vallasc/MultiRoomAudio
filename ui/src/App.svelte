<script>
    import { App, View } from "framework7-svelte"
    import ClientPage from "./pages/ClientPage.svelte"
    import RoomsPage from "./pages/RoomsPage.svelte"
    import SpeakerPage from "./pages/SpeakerPage.svelte"
    import AboutPage from "./pages/AboutPage.svelte"
    import { deviceId } from "./stores"

    const f7Params = {
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
                path: "/load",
                component: AboutPage,
            },
        ],
    }

    const urlParams = new URLSearchParams(window.location.search)
    let isClient = false
    let isSpeaker = false
    let clientConnection = false

    deviceId.set(urlParams.get("clientId"))

    if (
        urlParams.get("type") &&
        (urlParams.get("type") === "client" ||
            urlParams.get("type") === "rejected")
    ) {
        isClient = true
    } else if (urlParams.get("type") && urlParams.get("type") === "speaker") {
        isSpeaker = true
    }

    let themeDark = false
</script>

<App {...f7Params} {themeDark}>
    {#if clientConnection}
        <View url="/load" />
    {:else if isClient}
        <View url="/musiclist" />
    {:else if isSpeaker}
        <View url="/speaker" stackPages={true} main={true} />
    {:else}
        <h1>Open Multiroom Audio on your pc</h1>
    {/if}
</App>
