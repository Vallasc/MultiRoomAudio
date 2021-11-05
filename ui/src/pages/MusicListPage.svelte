<script>
  import { onMount } from "svelte";
  import {
    Page,
    Navbar,
    Toolbar,
    Link,
    NavRight,
    ListItem,
    List,
    Popup
  } from "framework7-svelte"
  import { text } from "../stores.js"
  import NowPlaying from "./NowPlaying.svelte"


  let songs = []
  let popupOpened = false
  let playingSong

  async function fetchSongs() {
    let res = await fetch("http://localhost:8080/player/list", {
      method: "GET",
      mode: "cors",
    });
    songs = await res.json()
    songs.forEach((song) => {
      if(song.albumImageUrl != null)
        song.albumImageUrl = "http://localhost:8080/" + song.albumImageUrl.replace("./", "")
      else
        song.albumImageUrl = "./imgs/blank_album.png"

      song.songUrl = "http://localhost:8080/" + song.songUrl.replace("./", "")
      
      /*if(song.albumImageUrl != null)
        song.albumImageUrl = "http://localhost:8080/" + encodeURIComponent(song.albumImageUrl.replace("./", ""))
      else
        song.albumImageUrl = "./imgs/blank_album.png"

      song.songUrl = "http://localhost:8080/" + encodeURIComponent(song.songUrl.replace("./", "")) */
      song.isPlaying = false
    })
    console.log(songs)
  }

  function palyPauseSong(song) {
    console.log(song)
    if (!song.isPlaying) {
      if (playingSong != null) playingSong.pause();
      playingSong = new Audio(song.songUrl);
      playingSong.play();
    } else {
      playingSong.pause();
    }
    song.isPlaying = !song.isPlaying;
    songs = songs; // Update for svelte
  }

  onMount(async () => {
    fetchSongs();
    document.getElementsByClassName("toolbar-bottom")[0].onclick = () =>
      (popupOpened = true);
  });

 </script>

<Page>
  <!-- Top Navbar -->
  <Navbar title="Multiroom Audio">
    <NavRight>
      <Link iconMd="material:leak_add" iconOnly href="/rooms/" />
      <Link iconMd="material:settings" iconOnly />
    </NavRight>
  </Navbar>
  <!-- Toolbar -->
  <Toolbar bottom >
    <div class="toolbarBox">
      <img
        id="album"
        alt=""
        src="https://www.nuovecanzoni.com/wp-content/uploads/2021/07/Una-Direzione-Giusta-supreme.jpg"
      />
      <div class="item-inner">
        <div class="item-title-row" style="display: flex">
          <div class="item-title">Yellow Submarine</div>
        </div>
        <div class="item-subtitle">Beatles</div>
      </div>
    </div>
    <div style="margin-right:14px">
      <Link iconMd="material:play_arrow" />
    </div>
  </Toolbar>

  <List mediaList>
    <div class="block-title" style="margin-bottom:0px;">Connected speakers</div>
    <li class="list-group-title">
      <div class="list-speakers">
        <div class="item-speaker">
          <Link iconMd="material:add" iconOnly />
        </div>
        <div class="item-speaker" />
        <div class="item-speaker" />
        <div class="item-speaker" />
        <div class="item-speaker" />
        <div class="item-speaker" />
        <div class="item-speaker" />
        <div class="item-speaker" />
      </div>
    </li>

    <div class="block-title">SLAC songs</div>
    <!-- {#each Array(12) as _, i}
      <ListItem title="Yellow Submarine" subtitle="Beatles">
        <img
          slot="media"
          src="https://cdn.framework7.io/placeholder/fashion-88x88-1.jpg"
          width="44"
        />
      </ListItem>
    {/each}-->
    {#each songs as song}
      <ListItem title={song.title} subtitle={song.artist}>
        <img
          slot="media"
          alt={song.title}
          src={song.albumImageUrl}
          width="44"
        />
        <span slot="after">
          {#if song.isPlaying}
            <Link
              iconMd="material:pause_arrow"
              iconOnly
              on:click={palyPauseSong(song)}
            />
          {:else}
            <Link
              iconMd="material:play_arrow"
              iconOnly
              on:click={palyPauseSong(song)}
            />
          {/if}
        </span>
      </ListItem>
    {/each}
  </List>
</Page>

<Popup
  opened={popupOpened}
  onPopupClosed={() => (popupOpened = false)}
  swipeToClose
  >
  <NowPlaying></NowPlaying>
</Popup>

<style>
  .list-speakers {
    height: auto;
    white-space: nowrap;
    display: flex;
    flex-wrap: nowrap;
    overflow: auto;
    margin-left: -14px;
    margin-right: -14px;
    background-color: var(--f7-list-bg-color);
    padding-left: 7px;
    padding-right: 7px;
    padding-top: var(--f7-block-title-margin-bottom);
    padding-bottom: var(--f7-block-title-margin-bottom);
  }
  .item-speaker {
    border: 1px solid rgba(0, 0, 0, 0.247);
    width: 80px;
    height: 80px;
    flex: 0 0 auto;
    margin-left: 7px;
    margin-right: 7px;
    display: flex;
    align-items: center;
    justify-content: center;
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

  :global(.list ul:before) {
    background-color: transparent;
  }

  .list-group-title {
    height: auto;
  }
  .item-title{
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      flex-shrink: 10;
      font-weight: var(--f7-navbar-title-font-weight);
      line-height: var(--f7-navbar-title-line-height);
      text-align: var(--f7-navbar-title-text-align);
      font-size: 16px;
      padding-top: 14px;
    }
  
  .block-title {
    font-size: 14px !important;
  }
</style>
