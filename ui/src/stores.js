import { writable } from 'svelte/store'

export const deviceId = writable('')
export const webSocket = writable(null)