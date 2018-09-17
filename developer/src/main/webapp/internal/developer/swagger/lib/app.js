'use strict';

(function() {
  var app = angular.module('toast', [ 'ngRoute' ]);
  app.config([ '$routeProvider', function($routeProvider) {
    $routeProvider.when('/', {
      controller : 'HomeController',
      templateUrl : 'templates/default.html'
    }).otherwise({
      redirectTo : '/'
    });
  } ]);

  app.controller('HomeController', [ '$scope', 'ToastService', function($scope, toastService) {
    $scope.version = angular.version.full;
    $scope.messages = toastService.messages;
  } ]);

  app.factory('ToastService', [ '$rootScope', function($rootScope) {
    var messages = [];
    var websocketSocket = atmosphere;
    var websocketSubSocket;
    var websocketTransport = 'websocket';

    function getUrl() {
      var url = window.location.origin;
      return '/sp/subscribe';
    }

    var websocketRequest = {
      url : getUrl(),
      contentType : "application/json",
      transport : websocketTransport,
      trackMessageLength : true,
      withCredentials : true,
      reconnectInterval : 5000,
      enableXDR : true,
      timeout : 60000
    };//

    websocketRequest.onOpen = function(response) {
      console.log('Trying to use transport: ' + response.transport);
        alert(response.transport);
      websocketTransport = response.transport;
    };

    websocketRequest.onClientTimeout = function(r) {
      setTimeout(function() {
        websocketSubSocket = websocketSocket.subscribe(websocketRequest);
      }, websocketRequest.reconnectInterval);
    };

    websocketRequest.onClose = function(response) {
      console.log('Server closed websocket connection. Changing transport to: '+ response.transport);
    };

    websocketRequest.onMessage = function(message) {
      $rootScope.$apply(function() {

          console.log(message.responseBody);
          var afterParsed = JSON.parse(message.responseBody);
          if('quote.result' === afterParsed.channel ){
              if('success' === afterParsed.status) {
                  messages.push(JSON.stringify(afterParsed.data));
              } else if('fail' === afterParsed.status) {
                  messages.push(afterParsed.data.insuranceCompany.name+"报价失败");
              } else {
                  console.log('unexpected state');
              }

          } else if ('quote.stage' === afterParsed.channel && 'success' === afterParsed.status) {
              messages.push(afterParsed.data.insuranceCompany.name+'当前报价阶段：'+afterParsed.data.stage);
          } else{
              console.log('unexpected channel ');
          }

      });
    };//

    websocketSubSocket = websocketSocket.subscribe(websocketRequest);

    return {
      messages : messages
    };
  } ]);
})();
