/**
 * main.js
 *
 * Bootstraps Vuetify and other plugins then mounts the App`
 */

// Components
import App from "./App.vue";
import GoogleSignInPlugin from "vue3-google-signin"

// Composables
import { createApp } from "vue";

// Plugins
import { registerPlugins } from "@/plugins";
import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'

const app = createApp(App);

registerPlugins(app);

app.use(GoogleSignInPlugin, {
  clientId: import.meta.env.VITE_GOOGLE_OAUTH_CLIENT_ID,
});

const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)
app.use(pinia)

app.mount("#app");
