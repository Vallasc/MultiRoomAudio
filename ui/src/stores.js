import { writable } from 'svelte/store'

export const deviceId = writable("")
export const webPort = writable(-1)
export const musicPort = writable(-1)
export const hostname = writable("")
export const webSocket = writable(null)