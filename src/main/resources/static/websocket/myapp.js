var stompClient = null;

/**
 * 建立websocket连接
 *
 * @param address 服务端向客户端推送消息的地址，例如 '/notice/xxx'
 */
function wb_connect(address) {
    var socket = new SockJS('/endpoint-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log("Connected!");

        var headers = {'id' : address}; //这里指定subscription_id的值，也即回调函数的result.headers.subscription值
        stompClient.subscribe(address, function (result) {
            var resp = JSON.parse(result.body);
            // console.log(result.headers.destination);
            // console.log(result.headers.subscription);

            if(resp.payload.code == 0) {
                bootoast({
                    message: 'Delete success!',
                    type: 'success',
                    position:'right-top',
                    timeout:3
                });
                $('#jstree').jstree('refresh');
            } else {
                bootoast({
                    message: 'Delete failed!',
                    type: 'danger',
                    position:'right-top',
                    timeout:3
                });
            }
        }, headers);
    });
}

/**
 * 取消订阅，并断开websocket连接
 *
 * @param subscription_id 订阅的id
 */
function wb_disconnect(subscription_id) {
    if (stompClient !== null) {
        if (stompClient.subscriptions.hasOwnProperty(subscription_id)) {
            stompClient.unsubscribe(subscription_id);
        }
        stompClient.disconnect();
        console.log("Disconnected!");
    }
}