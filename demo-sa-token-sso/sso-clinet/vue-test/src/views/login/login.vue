<template>
  <div>
    <head>
      <title>Sa-SSO-Server 认证中心-登录</title>
      <meta charset="utf-8" />
      <base th:href="@{/}" />
      <meta
        name="viewport"
        content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no"
      />
    </head>
    <body>
      <div class="view-box">
        <div class="bg-1"></div>
        <div class="content-box">
          <div class="login-box">
            <div class="from-box">
              <h2 class="from-title">Sa-SSO-Server 认证中心（前后端分离版）</h2>
              <div class="from-item">
                <input
                  class="s-input"
                  v-model="name"
                  name="name"
                  placeholder="请输入账号"
                />
              </div>
              <div class="from-item">
                <input
                  class="s-input"
                  name="pwd"
                  v-model="pwd"
                  type="password"
                  placeholder="请输入密码"
                />
              </div>
              <div class="from-item">
                <button class="s-input s-btn login-btn" @click="login">
                  登录
                </button>
              </div>
              <div class="from-item reset-box">
                <a href="javascript: location.reload();">刷新</a>
              </div>
            </div>
          </div>
        </div>
        <!-- 底部 版权 -->
        <div
          style="
            position: absolute;
            bottom: 40px;
            width: 100%;
            text-align: center;
            color: #666;
          "
        >
          This page is provided by Sa-Token-SSO
        </div>
      </div>
    </body>
  </div>
</template>

<script src="https://unpkg.zhimg.com/jquery@3.4.1/dist/jquery.min.js"></script>
<script>
import { getParam } from "@/http/http";
export default {
  data() {
    return {
      name: "",
      pwd: "",
    };
  },
  methods: {
    login() {
      console.log("123123123", getParam("redirect", ""));
      this.$http("/sso/doLogin", "POST1", {
        name: this.name,
        pwd: this.pwd,
      }).then((res) => {
        console.log("doLogin",res)
        localStorage.setItem("satoken",res.data);
        this.isLogin();
      });
    },
    isLogin() {
      // 解析URL
      var parsedUrl = new URL(location.href);

      // 获取redirect参数值
      var redirectParam = parsedUrl.searchParams.get("redirect");

      // 对redirect参数值进行解码
      var decodedRedirect = decodeURIComponent(redirectParam);
      var pData = {
        client: getParam("client", ""),
        redirect: decodedRedirect,
        mode: getParam("mode", ""),
      };
      this.$http("/sso/getRedirectUrl", "POST1", pData).then((res) => {
        if (res.code == 200) {
          // 已登录，并且redirect地址有效，开始跳转 location.href = decodeURIComponent(res.data);
          location.href = res.data;
        } else if (res.code == 401) {
          console.log(res);
        } else {
          alert("错误");
        }
      });
    },

  },
  created() {
    this.isLogin();
  },
};
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
}
body {
  font-family: Helvetica Neue, Helvetica, PingFang SC, Tahoma, Arial, sans-serif;
}
::-webkit-input-placeholder {
  color: #ccc;
}

/* 视图盒子 */
.view-box {
  position: relative;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
}
/* 背景 EAEFF3 */
.bg-1 {
  height: 100%;
  background: #c0ccf4;
}

/* 内容盒子 */
.content-box {
  position: absolute;
  width: 100vw;
  height: 100vh;
  top: 0px;
}

/* 登录盒子 */
/* .login-box{width: 400px; height: 400px; position: absolute; left: calc(50% - 200px); top: calc(50% - 200px); max-width: 90%; } */
.login-box {
  width: 400px;
  margin: auto;
  max-width: 90%;
  height: 100%;
}
.login-box {
  display: flex;
  align-items: center;
  text-align: center;
}

/* 表单 */
.from-box {
  flex: 1;
  padding: 20px 50px;
  background-color: #fff;
}
.from-box {
  border-radius: 1px;
  box-shadow: 1px 1px 20px #666;
}
.from-title {
  margin-top: 20px;
  margin-bottom: 30px;
  text-align: center;
}

/* 输入框 */
.from-item {
  border: 0px #000 solid;
  margin-bottom: 15px;
}
.s-input {
  width: 100%;
  line-height: 32px;
  height: 32px;
  text-indent: 1em;
  outline: 0;
  border: 1px #ccc solid;
  border-radius: 3px;
  transition: all 0.2s;
}
.s-input {
  font-size: 12px;
}
.s-input:focus {
  border-color: #409eff;
}

/* 登录按钮 */
.s-btn {
  text-indent: 0;
  cursor: pointer;
  background-color: #409eff;
  border-color: #409eff;
  color: #fff;
}
.s-btn:hover {
  background-color: #50aeff;
}

/* 重置按钮 */
.reset-box {
  text-align: left;
  font-size: 12px;
}
.reset-box a {
  text-decoration: none;
}
.reset-box a:hover {
  text-decoration: underline;
}

/* loading框样式 */
.ajax-layer-load.layui-layer-dialog {
  min-width: 0px !important;
  background-color: rgba(0, 0, 0, 0.85);
}
.ajax-layer-load.layui-layer-dialog .layui-layer-content {
  padding: 10px 20px 10px 40px;
  color: #fff;
}
.ajax-layer-load.layui-layer-dialog .layui-layer-content .layui-layer-ico {
  width: 20px;
  height: 20px;
  background-size: 20px 20px;
  top: 12px;
}
</style>
