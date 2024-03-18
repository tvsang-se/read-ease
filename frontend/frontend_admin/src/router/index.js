// Composables
import { createRouter, createWebHistory } from "vue-router";

const routes = [
  {
    path: "/",
    component: () => import("@/layouts/default/Default.vue"),
    children: [
      {
        path: "/",
        name: "Home",
        // route level code-splitting
        // this generates a separate chunk (about.[hash].js) for this route
        // which is lazy-loaded when the route is visited.
        component: () =>
          import(/* webpackChunkName: "home" */ "@/views/Home.vue"),
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
      {
        path: "/quan-ly-tai-khoan-admin-2",
        name: "Admin2Management",
        // route level code-splitting
        // this generates a separate chunk (about.[hash].js) for this route
        // which is lazy-loaded when the route is visited.
        component: () =>
          import(/* webpackChunkName: "home" */ "@/views/Admin2Management.vue"),
      },
    ],
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
  if (
    to.name === "Admin2Management" &&
    useUserStore().user?.token &&
    useUserStore().user.role !== "admin1"
  )
    next({
      name: "NotFound",
      params: { pathMatch: to.path.split("/").slice(1) },
      query: to.query,
      hash: to.hash,
    });

  if (to.name !== "Login" && !useUserStore().user?.token) {
    next({ name: "Login" });
  } else next();
});

export default router;
