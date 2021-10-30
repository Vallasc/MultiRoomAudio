import { writable } from 'svelte/store'

export const text = writable('Testo prova')

window.setText = (t) => {
    text.set(t);
}