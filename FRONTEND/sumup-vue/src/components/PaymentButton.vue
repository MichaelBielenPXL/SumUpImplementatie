<!-- src/components/PaymentButton.vue -->
<template>
  <button :disabled="loading" @click="startPayment" class="btn">
    {{ loading ? 'Even geduld…' : 'Betaal €10,00' }}
  </button>
</template>

<script setup>
import { ref } from 'vue'

const loading = ref(false)

async function startPayment() {
  loading.value = true
  try {
    // 1) POST naar je Spring endpoint
    const response = await fetch('http://localhost:8083/api/create-checkout', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ amount: '00.01' })
    })

    // 2) Altijd eerst de JSON parsen
    const data = await response.json()
    console.log('Backend response:', data)

    // 3) Foutstatus detecteren
    if (!response.ok) {
      // neem de server-error uit data.error of val terug op de HTTP-status
      throw new Error(data.error || `Server gaf status ${response.status}`)
    }

    // 4) Kies de juiste URL-key uit de response
    // (afhankelijk van hoe jouw controller 'checkoutUrl' mapt)
    const checkoutUrl =
        data.checkoutUrl ||
        data.hosted_checkout_url ||
        data.checkout_url

    if (!checkoutUrl) {
      console.error('Response keys:', Object.keys(data))
      throw new Error('Geen checkout URL ontvangen')
    }

    // 5) Redirect de browser
    window.location.href = checkoutUrl

  } catch (err) {
    console.error('Betaling starten mislukt:', err)
    alert('Kon betaling niet starten:\n' + err.message)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.btn {
  padding: 0.5rem 1rem;
  border-radius: 4px;
  background: #42b983;
  color: white;
  font-size: 1rem;
  border: none;
  cursor: pointer;
}
.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>
