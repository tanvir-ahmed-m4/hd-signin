angular.module('admin').controller('EmployeeMgmtCtrl', ['$scope', 'employeeServices',  function($scope, employeeServices){
	// TODO load via AJAX
	$scope.employeeTypes = 	['SCC', 'SCC_LEAD', 'SUPERVISOR', 'SYSADMIN'];
	
	$scope.signedInEmployees = [];

	$scope.employees = [];

	$scope.activeEmployee = {'firstName': 'Loading...'};

	$scope.saveChanges = function(){
		console.log('Saving ' + JSON.stringify($scope.activeEmployee));
		employeeServices.updateEmployee($scope.activeEmployee);
		
	}
	
	employeeServices.getAllEmployees().then(function(response){
		$scope.employees = response;
		$scope.activeEmployee = response[0];
	});
}]);
