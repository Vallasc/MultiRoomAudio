<script>
    import { Page, Range } from "framework7-svelte";

    export let imageUrl
    export let title
    export let artist
    export let currentValue
    export let songDuration
    export let isSpeaker = false;
    export let disabled = false;
    export let playing = false;

    export let onNext = () => {}
    export let onPrev =  () => {}
    export let onPlayPause =  () => {}
    export let onValueChanged =  (value) => {}

    let sliderDragged = false
    let leftTime
    let rightTime
    let rangeValuePressed = 0

    $: currentPerc = currentValue*100/songDuration
    $: rangeValue = !sliderDragged ? currentPerc : rangeValuePressed
    $: leftTime = getMinutesSecondsLeft(currentValue)
    $: rightTime = getMinutesSecondsRight(currentValue, songDuration)

    function n(n){
        return n > 9 ? "" + n: "0" + n;
    }

    function getMinutesSecondsLeft(currentValue){
        let minutes = n(Math.floor(currentValue/60))
        let seconds = n(Math.floor(currentValue % 60))
        return minutes + ":" + seconds;
    }

    function getMinutesSecondsRight(currentValue, songDuration){
        let minutes = n(Math.floor((songDuration- currentValue)/60))
        let seconds = n(Math.floor((songDuration - currentValue) % 60))
        return "-" + minutes + ":" + seconds
    }

    function onPointerUp(){
        // Fix glitch before receiving new command by server
        setTimeout(() => sliderDragged = false, 1000)
        if( rangeValuePressed > currentPerc + 3 ||  rangeValuePressed < currentPerc - 3){
            onValueChanged(songDuration * rangeValuePressed / 100)
        }
    }
</script>

<Page>
    <div class="navbar" data-f7-slot="fixed">
        <div class="navbar-bg" />
        <div class="navbar-inner sliding">
            {#if !isSpeaker}
                <div class="left">
                    <!-- svelte-ignore a11y-invalid-attribute -->
                    <a
                        class="link icon-only popup-close"
                        href="#"
                        iconmd="material:expand_more"
                        ><i class="icon material-icons" style="">expand_more </i>
                    </a>
                </div>
            {/if}
        </div>
    </div>
    <div class="player">
        {#if isSpeaker}
            <div style="flex: 4"></div>
        {/if}
        <div class="player-img">
            <img
                alt="Album"
                src={imageUrl}
            />
        </div>
        <div class="player-buttons">
            <div class="column-flex">
                <div class="row-flex">
                    <div class="song-title">{title}</div>
                </div>
                <div class="row-flex">
                    <div class="song-subtitle">{artist}</div>
                </div>
                <div class="row-flex music-slider" 
                    on:pointerdown = {() => sliderDragged = true} 
                    on:pointerup={onPointerUp}>
                    <Range min={0} max={100} 
                        step={0.1} 
                        value={rangeValue}
                        {disabled}
                        onRangeChange = {(value) => rangeValuePressed = value}/>
                </div>
                <div class="row-flex row-time">
                    <div style="margin-left: 17%;">{leftTime}</div>
                    <div style="margin-right: 17%;">{rightTime}</div>
                </div>
                <div class="row-flex">
                    {#if !isSpeaker}
                        <div class="fab color-white tansparent" on:click={(event) => {
                            event.preventDefault()
                            onPrev()
                          }}>
                            <!-- svelte-ignore a11y-missing-attribute -->
                            <a>
                                <i class="icon material-icons"
                                    >skip_previous
                                </i>
                            </a>
                        </div>
                        <div class="fab" on:click={(event) => {
                            event.preventDefault()
                            onPlayPause()
                          }}>
                            <!-- svelte-ignore a11y-missing-attribute -->
                            <a class="fab-play">
                                {#if playing}
                                    <i class="icon material-icons"
                                        >pause
                                    </i>
                                {:else}
                                    <i class="icon material-icons"
                                        >play_arrow
                                    </i>
                                {/if}
                            </a>
                        </div>
                        <div class="fab color-white tansparent" on:click={(event) => {
                            event.preventDefault()
                            onNext()
                          }}>
                            <!-- svelte-ignore a11y-missing-attribute -->
                            <a>
                                <i class="icon material-icons "
                                    >skip_next
                                </i>
                            </a>
                        </div>
                    {/if}
                </div>
            </div>
        </div>
        <div class="player-bottom" />
    </div>
</Page>

<style>
    :global(.bottomPlayer) {
        height: 80px !important;
    }

    /* Player */
    .player-img {
        flex: 2;
        display: flex;
        align-items: center;
        justify-content: center;
    }

    .player-img img {
        width: 60%;
        max-width: 50vh;
        margin: 10px;
        border-radius: 20px;
    }

    .player-buttons {
        flex: 2;
        background-color: white;
    }

    .player-bottom {
        flex: 1;
        height: 100px;
    }

    .player {
        display: flex;
        flex-direction: column;
        height: 100%;
        max-height: 100px;
    }

    .fab {
        position: relative;
        margin: 5px;
    }

    .tansparent>a {
        box-shadow: none;
    }

    .tansparent i {
        color: #000;
    }

    .fab i {
        font-size: 30px;
    }

    .fab-play {
        height: 80px;
        width: 80px;
        border-radius: 40px;
        margin-right: 10px;
        margin-left: 10px;
    }

    .row-flex {
        display: flex;
        justify-content: center;
        align-items: center;
    }
    .column-flex {
        display: flex;
        justify-content: center;
        align-items: center;
        flex-direction: column;
        height: 100%;
    }

    .row-time {
        width: 100%;
        justify-content: space-between;
        margin-bottom: 30px;
    }

    .song-title {
        position: relative;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        flex-shrink: 10;
        font-weight: var(--f7-navbar-title-font-weight);
        display: inline-block;
        line-height: var(--f7-navbar-title-line-height);
        text-align: var(--f7-navbar-title-text-align);
        font-size: var(--f7-navbar-title-font-size);
        margin-left: var(--f7-navbar-title-margin-left);
        margin-right: var(--f7-navbar-title-margin-left);
        font-size: 25px;
        margin-top: 20px;
        margin-bottom: 5px;
        text-overflow: ellipsis;
        max-width: 50vh;
    }

    .song-subtitle {
        position: relative;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        flex-shrink: 10;
        font-size: 18px;
        margin-bottom: 20px;
    }

    .music-slider {
        margin-top: 20px;
        margin-bottom: 6px;
        width: 65%;
    }

    .navbar-bg {
        background: transparent;
    }

    .navbar-bg:before {
        background: transparent;
    }
</style>
