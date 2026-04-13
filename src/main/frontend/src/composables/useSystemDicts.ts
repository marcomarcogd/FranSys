import { reactive } from 'vue'
import { api } from '../api/fransys'

const dicts = reactive<Record<string, any[]>>({})
let loadingPromise: Promise<Record<string, any[]>> | null = null

function replaceDicts(nextDicts: Record<string, any[]>) {
  Object.keys(dicts).forEach((key) => {
    delete dicts[key]
  })
  Object.assign(dicts, nextDicts || {})
}

export async function loadSystemDicts(force = false) {
  if (!force && Object.keys(dicts).length) {
    return dicts
  }
  if (!loadingPromise || force) {
    loadingPromise = api.dictsGrouped()
      .then((result) => {
        replaceDicts(result || {})
        return dicts
      })
      .finally(() => {
        loadingPromise = null
      })
  }
  return loadingPromise
}

export function clearSystemDictsCache() {
  replaceDicts({})
}

export function useSystemDicts() {
  return {
    dicts,
    loadSystemDicts,
    clearSystemDictsCache,
  }
}
