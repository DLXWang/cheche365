var myEmail;
var myDomain;
var socket;
(function getUserEmail() {
    $.ajax({
        type: "GET",
        url: "/internalId/user",
        data: {},
        dataType: "json",
        success: function (data) {
            myEmail = data.email;
            myDomain = data.domain;
        }
    });
}());


var ip = location.hostname;
var re = /^(\d+)\.(\d+)\.(\d+)\.(\d+)$/;
if (re.test(ip)) {
    socket = io.connect('http://' + location.hostname + ':9092',{transports: ['websocket','xhr-polling'],'force new connection': true});
}
else {
    socket = io.connect('http://' + location.hostname ,{path: '/socket/socket.io',transports: ['websocket','xhr-polling'],'force new connection': true});
}
socket.on('reconnect_attempt', function(){
    socket.io.opts.transports = ['xhr-polling', 'websocket'];
});

socket.on('connect', function () {
   console.log("the socket io connected");
});

socket.on('push', function (data) {
    console.log("the num socketio pushed is "+data.message);
    $("#numtest").text(data.message);
});

socket.on('linkevent', function (data) {
    console.log("get message when socketio link to server");
    setTimeout(function(){
        socket.emit('regId', {
            userName: myEmail,
            message: "abc"
        });
    },1000);

});

socket.on('disconnect', function () {
    console.log("the socket io disconnected");
});



