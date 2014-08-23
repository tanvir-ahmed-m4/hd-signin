angular.module('admin').controller('AdminNavCtrl', ['$scope', '$location',  function($scope, $location){
	$scope.msg = 'Testing';
	
	$scope.isActive = function(name){
		return '/' + name == $location.path();
	}
}]);