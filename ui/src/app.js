import Framework7 from 'framework7/lite-bundle';
import Framework7Svelte from 'framework7-svelte';
import App from './App.svelte';

import 'framework7/framework7-bundle.min.css';
import './css/app.css';

import Dialog from 'framework7/components/dialog/';


Framework7.use([Dialog, Framework7Svelte]);


// Init Svelte App
const app = new App({
  target: document.getElementById('app'),
});

export default app;
