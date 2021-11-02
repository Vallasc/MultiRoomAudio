<script>
  import { onMount } from 'svelte'
  import {
    Page,
    Navbar,
    Toolbar,
    Link,
    Block,
    BlockTitle,
    NavRight,
    ListItem,
    List,
    Popup,  
    Col,
    Row
  } from "framework7-svelte"
  import { text } from "../stores.js"


  // Calc image color
  function get_average_rgb(img) {
      var context = document.createElement('canvas').getContext('2d');
      if (typeof img == 'string') {
          var src = img;
          img = new Image;
          img.setAttribute('crossOrigin', ''); 
          img.src = src;
      }
      context.imageSmoothingEnabled = true;
      context.drawImage(img, 0, 0, 1, 1);
      return context.getImageData(1, 1, 1, 1).data.slice(0,3);
  }

  let songs = []
  let popupOpened = true
  let playingSong

  async function fetchSongs() {
    let res = await fetch("http://localhost:8080/player/list", {
      method: "GET",
      mode: "cors",
    })
    songs = await res.json()
    songs.forEach((song) => {
      song.albumImageUrl =
        "http://localhost:8080" + song.albumImageUrl.replace(".", "")
      song.songUrl = "http://localhost:8080" + song.songUrl.replace(".", "")
      song.isPlaying = false
    })
    console.log(songs)
  }

  function palyPauseSong(song) {
    if (!song.isPlaying) {
      if (playingSong != null) playingSong.pause()
      playingSong = new Audio(song.songUrl)
      playingSong.play()
    } else {
      playingSong.pause()
    }
    song.isPlaying = !song.isPlaying
    songs = songs // Update for svelte
  }

  onMount(async () => {
    fetchSongs()
    document.getElementsByClassName("bottomPlayer")[0].onclick = () => popupOpened = true
    document.getElementById("album").onload = () => console.log(get_average_rgb(document.getElementById("album")))
	})


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
    <Toolbar bottom class="bottomPlayer">
      <div class="toolbarBox">
        <img
          id = "album"
          alt=""
          src="http://localhost:8080/MRA_tmp/Una-Direzione-Giusta-supreme.jpg"
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

  <BlockTitle>Connected speakers</BlockTitle>
  <div class="container">
    <div class="item">
      <Link iconMd="material:add" iconOnly />
    </div>
    <div class="item" />
    <div class="item" />
    <div class="item" />
    <div class="item" />
    <div class="item" />
  </div>
  <BlockTitle>SLAC song list</BlockTitle>
  <List mediaList>
    <ListItem title="Yellow Submarine" subtitle="Beatles">
      <img
        slot="media"
        src="https://cdn.framework7.io/placeholder/fashion-88x88-1.jpg"
        width="44"
      />
    </ListItem>
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
<Popup opened={popupOpened} onPopupClosed={() => popupOpened = false} swipeToClose>
  <Page>
    <Navbar title="Popup Title">
      <NavRight>
        <Link popupClose>Close</Link>
      </NavRight>
    </Navbar>
    <div class="player">
      <div class="playerImg">
        <img
          alt=""
          src="https://www.nuovecanzoni.com/wp-content/uploads/2021/07/Una-Direzione-Giusta-supreme.jpg"
        />
      </div>
      <div class="playerTitle">

      </div>
      <div class="playerButtons">

      </div>
    </div>
  </Page>
</Popup>

<style>
  .container {
    margin: 5px;
    height: 100px;
    white-space: nowrap;
    display: flex;
    flex-wrap: nowrap;
    overflow: auto;
  }
  .item {
    border: 1px solid rgba(0, 0, 0, 0.247);
    width: 100px;
    flex: 0 0 auto;
    margin-left: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .toolbarBox {
    width: 60px;
    height: 60px;
    display: flex;
    margin-left: 14px;
    margin-right: 14px;
  }

  img {
    border-radius: 5px;
  }

  :global(.bottomPlayer) {
    height: 80px !important;
  }


  /* Player */
  .playerImg {
    flex: 3;
    background-color:green;
  }

  .playerImg {
    flex: 3;
    background-color:green;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .playerImg img {
    width: 70%;
    margin: 10px;
  }

  .playerTitle {
    flex: 1;
    background-color:blue;
  }
  .playerButtons {
    flex: 2;
    background-color:yellow;
  }

  .player {
    display: flex;
    flex-direction: column;
    background-color: red;
    height: 100%;
  }
</style>
