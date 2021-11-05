<script>
    import { Page, Range } from "framework7-svelte";
</script>

<Page>
    <div class="navbar" data-f7-slot="fixed">
        <div class="navbar-bg" />
        <div class="navbar-inner sliding">
            <div class="left">
                <a
                    class="link icon-only popup-close"
                    href="#"
                    iconmd="material:expand_more"
                    ><i class="icon material-icons" style="">expand_more </i>
                </a>
            </div>
        </div>
    </div>
    <div class="player">
        <div class="player-img">
            <img
                alt=""
                src="https://www.nuovecanzoni.com/wp-content/uploads/2021/07/Una-Direzione-Giusta-supreme.jpg"
            />
        </div>
        <div class="player-buttons">
            <div class="column-flex">
                <div class="row-flex">
                    <div class="song-title">Sei la mia città</div>
                </div>
                <div class="row-flex">
                    <div class="song-subtitle">Cosmo</div>
                </div>
                <div class="row-flex music-slider">
                    <Range min={0} max={100} step={1} value={50} />
                </div>
                <div class="row-flex row-time">
                    <div style="margin-left: 13%;">00:05</div>
                    <div style="margin-right: 13%;">00:05</div>
                </div>
                <div class="row-flex">
                    <div class="fab">
                        <!-- svelte-ignore a11y-missing-attribute -->
                        <a>
                            <i class="icon material-icons" style=""
                                >skip_previous
                            </i>
                        </a>
                    </div>
                    <div class="fab">
                        <!-- svelte-ignore a11y-missing-attribute -->
                        <a class="fab-play">
                            <i class="icon material-icons" style=""
                                >play_arrow
                            </i>
                        </a>
                    </div>
                    <div class="fab">
                        <!-- svelte-ignore a11y-missing-attribute -->
                        <a>
                            <i class="icon material-icons" style=""
                                >skip_next
                            </i>
                        </a>
                    </div>
                </div>
            </div>
        </div>
        <div class="player-bottom" />
    </div>
</Page>

<style>
    img {
        border-radius: 5px;
    }

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

        margin: 10px;
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
    }

    .fab {
        position: relative;
        margin: 5px;
    }

    .fab-play {
        height: 80px;
        width: 80px;
        border-radius: 40px;
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
        width: 75%;
    }

    .navbar-bg {
        background: transparent;
    }

    .navbar-bg:before {
        background: transparent;
    }
</style>
