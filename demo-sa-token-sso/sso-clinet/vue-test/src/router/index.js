import VueRouter from "vue-router";

//引入组件
import Home from "@/views/home/home"
import Link from "@/views/home/link"
import Login from "@/views/login/login"




//创建路由器并暴露
export default new VueRouter({
    mode: "history",
    //多个路由
    routes: [{
            path: '/',
            name: 'Home',
            component: Home,
        },
        {
            path: '/link',
            name: 'Link',
            component: Link,
        },
        {
            path: '/login',
            name: 'Login',
            component: Login
        },
    ]
})