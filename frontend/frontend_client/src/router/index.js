// Composables
import { createRouter, createWebHistory } from "vue-router";

const routes = [
  {
    path: "/",
    component: () => import("@/layouts/default/Default.vue"),
    children: [
      {
        path: "/home1",
        name: "Home1",
        // route level code-splitting
        // this generates a separate chunk (about.[hash].js) for this route
        // which is lazy-loaded when the route is visited.
        component: () =>
          import(/* webpackChunkName: "home" */ "@/views/Home.vue"),
      },
      {
        path: "",
        name: "Home",
        // route level code-splitting
        // this generates a separate chunk (about.[hash].js) for this route
        // which is lazy-loaded when the route is visited.
        component: () =>
          import(/* webpackChunkName: "home" */ "@/views/Home1.vue"),
      },
      {
        path: "/tai-lieu/:id",
        name: "FileReader",
        // props: true,
        // route level code-splitting
        // this generates a separate chunk (about.[hash].js) for this route
        // which is lazy-loaded when the route is visited.
        component: () =>
          import(/* webpackChunkName: "home" */ "@/views/FileReader.vue"),
      },
      {
        path: "/bo-suu-tap/:id",
        name: "Collections",
        // route level code-splitting
        // this generates a separate chunk (about.[hash].js) for this route
        // which is lazy-loaded when the route is visited.
        component: () =>
          import(/* webpackChunkName: "home" */ "@/views/Collections.vue"),
      },
      {
        path: "/thiet-lap-tai-khoan",
        name: "AccountSettings",
        // route level code-splitting
        // this generates a separate chunk (about.[hash].js) for this route
        // which is lazy-loaded when the route is visited.
        component: () =>
          import(/* webpackChunkName: "home" */ "@/views/AccountSettings.vue"),
      },
    ],
  },
  {
    path: "/read-file-1",
    name: "ReadFile1",
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () =>
      import(/* webpackChunkName: "home" */ "@/views/ReadFile1.vue"),
  },
  {
    path: "/",
    component: () => import("@/layouts/blank.vue"),
    children: [
      {
        path: "dang-nhap",
        name: "Login",
        // route level code-splitting
        // this generates a separate chunk (about.[hash].js) for this route
        // which is lazy-loaded when the route is visited.
        component: () =>
          import(/* webpackChunkName: "home" */ "@/views/Login.vue"),
      },
      {
        path: "dang-ky",
        name: "Signup",
        // route level code-splitting
        // this generates a separate chunk (about.[hash].js) for this route
        // which is lazy-loaded when the route is visited.
        component: () =>
          import(/* webpackChunkName: "home" */ "@/views/Signup.vue"),
      },
      {
        path: "quen-mat-khau",
        name: "ForgotPassword",
        // route level code-splitting
        // this generates a separate chunk (about.[hash].js) for this route
        // which is lazy-loaded when the route is visited.
        component: () =>
          import(/* webpackChunkName: "home" */ "@/views/ForgotPassword.vue"),
      },
      {
        path: "/:pathMatch(.*)*",
        name: "NotFound",
        // route level code-splitting
        // this generates a separate chunk (about.[hash].js) for this route
        // which is lazy-loaded when the route is visited.
        component: () =>
          import(/* webpackChunkName: "home" */ "@/views/NotFound.vue"),
      },
    ],
  },
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});
import { useUserStore } from "../store/user";
router.beforeEach((to, from, next) => {
  if((to.name !== 'Login' && to.name !== 'Signup' && to.name !== 'ForgotPassword'  && to.name !== 'NotFound') && !useUserStore().user?.token) {
    next ({name: 'Login'})
  } 
  else next()
});

export default router;
