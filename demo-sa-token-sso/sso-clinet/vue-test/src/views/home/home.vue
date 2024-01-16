<template>
  <div>
    <h2>Sa-Token SSO-Client 应用端（前后端分离版-原生h5）</h2>
    <p>当前是否登录：<b class="is-login">{{isLogin}}</b></p>
    <p>
      <router-link :to="loginUrl">登录</router-link>&nbsp;&nbsp;
      <a @click="logout"
        :href="logoutUrl"
        >注销</a
      >
    </p>
  </div>
</template>

<script>
import {baseUrl} from "@/http/http"
export default {
  data() {
    return {
      baseURL: baseUrl,
      loginUrl: "/link?back=" + location.href,
      isLogin: false,
      logoutUrl:
      baseUrl +
        "/sso/logout?satoken=" +
        localStorage.satoken +
        "&back=" +
        encodeURIComponent(location.href),
    };
  },
  methods: {
    logout() {
      localStorage.removeItem("satoken")
      alert("注销成功")
    },
  },
  created(){
    this.$http('/sso/isLogin',"GET",{}).then(res =>{
      this.isLogin = res.data
    })
  }
};
</script>

<style>
</style>