window.onload = function (ev) {
    app = new Vue({
        el: '#app',
        data: {
            socket: createWebSocket(),
            mess: "",
            fileList: []
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
                axios.post('/uploadfile/nolimit', formData).then(function (response) {
                    console.log(response)
                })
                    .catch(function (reason) {
                        console.log(reason)
                    });
            }
        }
    });
};

function createWebSocket() {
    var ss = new WebSocket("ws://127.0.0.1:9999/websocket");
    ss.onopen = function (ev1) {
        console.log("连接成功");
    };

    ss.onclose = function (ev1) {
        console.log("close");
    };

    ss.onerror = function (ev1) {
        console.log("error");
    };
    ss.onmessage = function (ev1) {
        console.log(ev1.data);
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
        }
    };

    return ss;
}


