<script>
  import { App, View } from 'framework7-svelte'
  import MusicListPage from './pages/MusicListPage.svelte'
  import RoomsPage from './pages/RoomsPage.svelte'
  import SpeakerPage from './pages/SpeakerPage.svelte'
  import CalibrationPage from './pages/CalibrationPage.svelte'

  const f7Params = {
    routes: [
      {
        path: '/musiclist',
        component: MusicListPage,
      },
      {
        path: '/rooms',
        component: RoomsPage,
      },
      {
        path: '/calibration',
        component: CalibrationPage,
      },
      {
        path: '/speaker',
        component: SpeakerPage,
      }
		]
  }

  const urlParams = new URLSearchParams(window.location.search)
  let isClient = false
  let isSpeaker = false
  let isNewClient = false

  if(urlParams.get('type') != null && urlParams.get('type') === 'client'){
    isClient = true;
  }else if(urlParams.get('type') != null && urlParams.get('type') === 'newclient'){
     isNewClient = true;
  }else if(urlParams.get('type') != null && urlParams.get('type') === 'speaker'){
    isSpeaker = true;
  } 

  let themeDark = false
</script>

<App {...f7Params} themeDark={themeDark}>
  {#if isClient}
    <View url="/musiclist" />
  {:else if isNewClient}
    <View url="/calibration" />
  {:else if isSpeaker}
    <View url="/speaker" />
  {:else}
    <h1>Open Multiroom Audio on your pc</h1>
  {/if}
</App>
