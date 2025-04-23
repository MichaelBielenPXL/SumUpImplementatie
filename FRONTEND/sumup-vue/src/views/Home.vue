<!-- src/views/Home.vue -->
<template>
  <div class="home">
    <h1>Betaal Demo</h1>
    <!-- hier komt de PaymentButton -->
    <PaymentButton />
  </div>
</template>

<script setup>
import PaymentButton from '../components/PaymentButton.vue'
</script>

<style scoped>
.home {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 2rem;
}
</style>
