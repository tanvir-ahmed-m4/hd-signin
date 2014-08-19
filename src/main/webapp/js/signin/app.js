angular.module('signin', ['ngRoute']);


angular.module('signin').config(
		function($routeProvider) {
			$routeProvider
			.when('/', {controller:'SigninCtrl'})
			.otherwise({redirectTo:'/'});
		});

