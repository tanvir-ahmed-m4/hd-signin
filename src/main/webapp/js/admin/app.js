angular.module('admin', ['ngRoute']);


angular.module('admin').config(
		function($routeProvider) {
			$routeProvider
			.when('/', {controller:'AdminHomeCtrl', templateUrl: '/signin/partials/admin/homepartial.html'})
			.when('/timecard', {controller: null, templateUrl: '/signin/partials/admin/timecardpartial.html'})
			.otherwise({redirectTo:'/'});
		});
