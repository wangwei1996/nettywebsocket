window.onload = function (ev) {
    app = new Vue({
        el: '#app',
        data: {
            //websocket端点
            socket: createWebSocket(),
            //需要发送的消息
            mess: "",
            //websocket是否可以关闭
            closeAble: false,
            isOpen: false
        },
        methods: {
            createMess: function () {
                var container = document.getElementById("mess-show");
                var temp = '<div class="message">' +
                    '<h3>发送人:' + "Me" + '</h3><div>' + this.mess + '</div>' +
                    '<div>发送时间:' + (new Date().toLocaleDateString()) + '</div></div>';
                container.innerHTML = container.innerHTML + temp;
                console.log("=======>>>>>  " + this.mess);
                this.socket.send(this.mess);
                this.mess = '';
            },
            uploadFile: function () {
                var file = document.getElementById("fileUpload").files[0];
                var formData = new FormData();
                formData.append("uploadFile", file);
                var socket = this.socket;
                axios.post('/uploadfile/nolimit', formData).then(function (response) {
                    console.log(response);
                    if (response.data.success) {
                        createMessImg("Me", response.data.url);
                        var mess = {type: 1, url: response.data.url};
                        socket.send(str2ab(JSON.stringify(mess)));
                    }
                })
                    .catch(function (reason) {
                        console.log(reason)
                    });
            }
        }
    });
};

function createWebSocket() {
    var socket = null;
    try {
        socket = new WebSocket("ws://127.0.0.1:9999/websocket");
    } catch (e) {
        console.error("建立WebSocket连接异常" + e.message());
        throw  e;
    }
    socket.onopen = function (ev1) {
        console.log("连接成功");
        app.isOpen = true;
        heartBeatReset();
    };

    socket.onclose = function (ev1) {
        console.log("close");
        app.isOpen = false;
    };

    socket.onerror = function (ev1) {
        app.isOpen = false;
        console.log("error");
    };
    socket.onmessage = function (ev1) {
        console.log(typeof ev1.data);
        if (typeof (ev1.data) == 'string') {
            if (ev1.data.match(/欢迎.*/ig)) {
                app.$notify({
                    message: ev1.data
                });
                return;
            }
            var messs = ev1.data.split(",");
            var container = document.getElementById("mess-show");
            var temp = '<div class="message">' +
                '<h3>发送人:' + messs[0] + '</h3><div>' + messs[1] + '</div>' +
                '<div>发送时间:' + (new Date().toLocaleDateString()) + '</div></div>';
            container.innerHTML = container.innerHTML + temp;
        } else if (typeof (ev1.data) == 'object' && ev1.data instanceof Blob) {
            var reader = new FileReader();
            reader.readAsText(ev1.data, "utf-8");
            reader.onload = function (ev) {
                console.log(reader.result);
                mess = JSON.parse(reader.result);
                console.log(ev1.data.toString());
                //心跳消息,不予处理
                if (mess.type == '3') {
                    console.log("心跳消息======================");
                    return;
                }
                createMessImg(mess.user, mess.url);
            };

        }
    };

    return socket;
}


function createMessImg(name, url) {
    var container = document.getElementById("mess-show");
    var temp = '<div class="message">' +
        '<h5>发送人:' + name + '</h5><div><img src=" ' + url + '"/></div>' +
        '<div>发送时间:' + (new Date().toLocaleDateString()) + '</div></div>';
    container.innerHTML = container.innerHTML + temp;
}


// ArrayBuffer转为字符串，参数为ArrayBuffer对象
function ab2str(buf) {
    return String.fromCharCode.apply(null, new Uint16Array(buf));
}

// 字符串转为ArrayBuffer对象，参数为字符串
function str2ab(str) {
    var buf = new ArrayBuffer(str.length * 2); // 每个字符占用2个字节
    var bufView = new Uint16Array(buf);
    for (var i = 0, strLen = str.length; i < strLen; i++) {
        bufView[i] = str.charCodeAt(i);
    }
    return buf;
}

//心跳
function heartBeatStart() {
    //定时发送消息
    interval = window.setInterval(function () {
        if (app.isOpen) {
            app.socket.send(str2ab(JSON.stringify({type: 3, url: "alive"})));
            return;
        }

        if (!app.isOpen && !app.closeAble) {
            reConnected();
        }
    }, 10000);
}

function heartBeatReset() {
    if (typeof interval != 'undefined') {
        window.clearInterval(interval);
    }

    heartBeatStart();
}

//断点重连
function reConnected() {
    var reConnectedIntrval = window.setTimeout(function () {
        try {
            var socket = createWebSocket();
            app.socket = socket;
            //没连接成功，那么我就要让他一直阻塞在这里
            console.log(socket.readyState + "----" + WebSocket.OPEN);
            if (socket.readyState == WebSocket.OPEN) {
                app.isOpen = true;
                //关闭掉重连操作
                window.clearTimeout(reConnectedIntrval);
            }
        } catch (e) {
            console.log("重连发生错误: " + e)
        } finally {
        }
    }, 2000);
}



