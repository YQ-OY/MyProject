import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: "/",
    name: "main",
    component: () => import('@/views/Main.vue'),
    redirect: "/login",
    children: [
      {
        path: "home",
        name: "home",
        component: () => import('@/views/Home.vue'),
      },
      {
        path: "employee",
        name: "employee",
        component: () => import('@/views/Employee.vue'),
      },
      {
        path: "department",
        name: "department",
        component: () => import('@/views/Department.vue')
      },
      {
        path: "salary",
        name: "salary",
        component: () => import('@/views/Salary.vue')
      },
    ]
  },
  {
    path: "/login",
    name: "login",
    component: () => import('@/views/Login.vue'),
  }
];

const router = createRouter({
  // 设置路由模式
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
   // 关键：允许同路由重复导航
  onSameUrlNavigation: 'reload' 
});

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  // 非登录页且无token，跳转到登录页
  if (to.path !== '/login' && !token) {
    next('/login')
  } else {
    next()
  }
})


export default router;
