angular.module('admin', ['ngRoute', 'ui.bootstrap']);


angular.module('admin').config(
		function($routeProvider) {
			$routeProvider
			.when('/', {controller:'AdminHomeCtrl', templateUrl: '/signin/partials/admin/homepartial.html'})
			.when('/timecard', {controller: 'TimecardCtrl', templateUrl: '/signin/partials/admin/timecardpartial.html'})
			.when('/employeemgmt', {controller: 'EmployeeMgmtCtrl', templateUrl: '/signin/partials/admin/employeemgmtpartial.html'})
			.otherwise({redirectTo:'/'});
		});
