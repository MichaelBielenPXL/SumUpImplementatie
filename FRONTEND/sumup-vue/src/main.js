import { createApp } from 'vue'
import App from './App.vue'
import router from './router'      // <-- import router
import './style.css'       // of waar je css staat

createApp(App)
    .use(router)                    // <-- gebruik router
    .mount('#app')
