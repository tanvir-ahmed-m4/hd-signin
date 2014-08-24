angular.module('admin').controller('AdminHomeCtrl', ['$scope', 'miscServices',  function($scope, miscServices){
	$scope.msg = 'Testing';
	$scope.signedInEmployees = [];
	
	
	miscServices.getSignedInEmployees().then(function(response){
		$scope.signedInEmployees = response;
	});
	
	
}]);