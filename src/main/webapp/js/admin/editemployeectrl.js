angular.module('admin').controller('EmployeeMgmtCtrl', ['$scope', 'employeeServices',  function($scope, employeeServices){
	// TODO load employee types via AJAX
	$scope.employeeTypes = 	['SCC', 'SCC_LEAD', 'SUPERVISOR', 'SYSADMIN'];
	
	$scope.employees = [];
	$scope.newEmployee = {};
	
	$scope.activeEmployee = {'firstName': 'Loading...'};

	function addEmployees(employees){
		for(var i = 0; i < employees.length; i++){
			addEmployee(employees[i]);
		}
	}
	
	function addEmployee(employee){
		for(var i = 0; i < $scope.employees.length; i++){
			if($scope.employees[i].id == employee.id){
				$scope.employees[i] = employee;
				return;
			}
		}
		$scope.employees.push(employee);
	}
	
	$scope.saveChanges = function(){
		console.log('Saving ' + JSON.stringify($scope.activeEmployee));
		employeeServices.updateEmployee($scope.activeEmployee);
		addEmployee($scope.activeEmployee);
	}
	
	employeeServices.getAllEmployees().then(function(response){
		addEmployees(response);
		$scope.activeEmployee = $scope.employees[0];
	});
	
	$scope.createEmployee = function(){
		employeeServices.createEmployee($scope.newEmployee).then(function(response){
			addEmployee(response);
			refreshNewEmployee();
		});
	};
	
	$scope.getDisplayName = function(employee){
		return employee.firstName + ' ' + employee.lastName;
	}
	
	function refreshNewEmployee(){
		$scope.newEmployee.isEmployeeActive = true;
		$scope.newEmployee.employeeType = $scope.employeeTypes[0];
		$scope.newEmployee.riceId = '';
		$scope.newEmployee.netId = '';
		$scope.newEmployee.lastName = '';
		$scope.newEmployee.firstName = '';
	}
	
	refreshNewEmployee();
	
}]);
