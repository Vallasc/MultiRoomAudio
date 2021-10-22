<script>
  import { App, View } from 'framework7-svelte'
  import MusicListPage from './pages/MusicListPage.svelte'
  import RoomsPage from './pages/RoomsPage.svelte'
  import CalibrationPage from './pages/CalibrationPage.svelte'

  const f7Params = {
    routes: [
		{
			path: '/',
			component: MusicListPage,
      main: true
		},
    {
			path: '/rooms',
			component: RoomsPage,
      main: true
		},
    {
			path: '/calibration',
			component: CalibrationPage,
      main: true
		}
		]
  };
</script>

<App {...f7Params}>
  <View url="/" class="safe-areas"/>
</App>
