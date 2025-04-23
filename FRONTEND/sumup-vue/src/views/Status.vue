<template>
  <div>
    <p>Betaling ID: {{ checkoutId }}</p>
    <p>Status: {{ status }}</p>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const checkoutId = route.params.checkoutId
const status = ref('CREATED')
let intervalId

async function fetchStatus() {
  try {
    const res = await fetch(`http://localhost:8083/api/checkout/${checkoutId}`)
    const data = await res.json()
    status.value = data.status
    if (status.value !== 'CREATED') {
      clearInterval(intervalId)
    }
  } catch (e) {
    console.error(e)
  }
}

onMounted(() => {
  fetchStatus()
  intervalId = setInterval(fetchStatus, 3000)
})

onBeforeUnmount(() => clearInterval(intervalId))
</script>
