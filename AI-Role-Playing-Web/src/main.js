import { createApp } from 'vue'
import { createPinia } from 'pinia'
import 'normalize.css'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

const app = createApp(App)
app.config.devtools = false
app.use(createPinia())
app.use(router)
app.use(ElementPlus, { size: 'small', zIndex: 3000 })

app.mount('#app')
