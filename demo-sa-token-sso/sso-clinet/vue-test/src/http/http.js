import axios from "axios";
const baseUrl = "http://sa-sso-client.com:9001"
const base1Url = "http://sa-sso-server.com:9000"


const request = (url, method, data) => {
    return new Promise((resolve, reject) => {

        if (method === 'GET') {
            axios({
                    method: method,
                    url: baseUrl + url,
                    params: data || {},
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded",
                        "satoken": localStorage.getItem("satoken")
                    }
                }).then(res => {
                
                    if (res.data.code === 200) {
                        resolve(res.data);
                    } else {
                        resolve(res.data.message);
                    }
                })
                .catch(err => {
                    reject(err.data.message);
                   alert("异常：" + JSON.stringify(err)); //失败
                });
        } else if (method === 'POST') {
            axios({
                    method: method,
                    url: baseUrl + url,
                    data: data || {},
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded",
                        // "satoken": localStorage.getItem("satoken")
                    }
                }).then(res => {
                    console.log(res.data.code)
                    if (res.data.code === 200) {
                        console.log(res)
                        resolve(res.data);
                    }
                })
                // .catch(err => {
                //     reject(err.data.message); //失败
                // });
        } else if (method === 'POST1') {
            axios({
                    method: "POST",
                    url: base1Url + url,
                    data: data || {},
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded",
                        'satoken': localStorage.getItem('satoken')
                    //     // "satoken": localStorage.getItem("satoken")
                    }
                }).then(res => {
                    console.log(res)
                    // setTimeout(function() {
                        // location.reload();
                    // }, 800);
                    resolve(res.data);
                //     if (res.data.code === 200) {
                //         console.log(res)
                //         resolve(res.data);
                //     } else {
                //         resolve(res.data.message);
                //     }
                // })
                // .catch(err => {
                //     reject(err.data.message); //失败
                });
        }
    })
}
// 从url中查询到指定名称的参数值
const getParam = function(name, defaultValue){
    var query = window.location.search.substring(1);
    var vars = query.split("&");
    for (var i=0;i<vars.length;i++) {
        var pair = vars[i].split("=");
        if(pair[0] == name){return pair[1];}
    }
    return(defaultValue == undefined ? null : defaultValue);
}
export {request, getParam, baseUrl}