/**
 * plugins/vuetify.js
 *
 * Framework documentation: https://vuetifyjs.com`
 */

// Styles
import "@mdi/font/css/materialdesignicons.css";
import "vuetify/styles";
import "@/styles/settings.scss";

// Composables
import { createVuetify } from "vuetify";
import { VDataTableServer,VDataTable } from "vuetify/labs/VDataTable";

// https://vuetifyjs.com/en/introduction/why-vuetify/#feature-guides
export default createVuetify({
  defaults: {
    VBtn: {
      color: "#00ADEF",
    },
    VDivider: {
      color: "#00ADEF",
    },
  },
  theme: {
    themes: {
      light: {
        colors: {
          primary: "#1867C0",
          secondary: "#5CBBF6",
        },
      },
    },
  },
  components: {
    VDataTableServer,
    VDataTable
  }
});
