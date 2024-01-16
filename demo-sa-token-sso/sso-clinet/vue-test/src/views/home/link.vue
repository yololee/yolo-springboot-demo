<template>
  <div>
    跳转中。。。。
    <!-- <router-link :to="redirectURL">点击</router-link> -->
  </div>
</template>

<script>
import { getParam } from "@/http/http";
export default {
  data() {
    return {
      back: getParam("back") || this.$route.query.back,
      ticket: getParam("ticket") || this.$route.query.ticket,
      redirectURL: "",
    };
  },
  created() {
    console.log("获取 back 参数：", this.back);
    console.log("获取 ticket 参数：", this.ticket);
    // debugger;
    if (this.ticket) {
      this.doLoginByTicket(this.ticket);
    } else {
      this.goSsoAuthUrl();
      // console.log("ssssss");
      // this.$router.push('/sa-login')
    }
  },

  methods: {
    // 重定向至认证中心
    goSsoAuthUrl: function () {
      this.$http("/sso/getSsoAuthUrl", "POST", {
        clientLoginUrl: location.href,
      }).then((res) => {
        this.redirectURL = res.data;
        
        // var url =
          // "http://127.0.0.1:8080/login?redirect=http%3A%2F%2Flocalhost%3A8080%2Flink%3Fback%3Dhttp%3A%2F%2Flocalhost%3A8080%2F";

        // 解析URL
        // var parsedUrl = new URL(url);

        // 获取redirect参数值
        // var redirectParam = parsedUrl.searchParams.get("redirect");

        // 对redirect参数值进行解码
        // var decodedRedirect = decodeURIComponent(redirectParam);
        // console.log(decodedRedirect);
        location.href = res.data;
      });
    },
    // 根据ticket值登录
    doLoginByTicket: function (ticket) {
      console.log("进入ticket");
      this.$http("/sso/doLoginByTicket", "POST", { ticket: ticket }).then(
        (res) => {
          console.log("/sso/doLoginByTicket 返回数据", res);
          if (res.code === 200) {
            localStorage.setItem("satoken", res.data);
            location.href = decodeURIComponent(this.back);
          } else {
            alert(res.msg);
          }
        }
      );
    },
  },
};
</script>

<style>
</style>