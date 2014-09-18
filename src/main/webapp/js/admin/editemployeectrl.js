angular.module('admin').controller('EmployeeMgmtCtrl', ['$scope', 'employeeServices', 'miscServices',  function($scope, employeeServices, miscServices){
	// TODO load employee types via AJAX
	$scope.employeeTypes = 	['SCC', 'SCC_LEAD', 'SUPERVISOR', 'SYSADMIN'];
	
	var invalidNetidMsg = "Must enter netID to finger user";
	var badFingerMsg = "No finger data found for ";
	
	$scope.errorMessage = null;
	
	$scope.fingerDisabled = false;
	
	$scope.employees = [];
	$scope.newEmployee = {};
	
	$scope.activeEmployee = {'firstName': 'Loading...'};

	var invalidNetid = false;
	
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
	
	function refreshNewEmployee(){
		$scope.newEmployee.isEmployeeActive = true;
		$scope.newEmployee.employeeType = $scope.employeeTypes[0];
		$scope.newEmployee.riceId = '';
		$scope.newEmployee.netId = '';
		$scope.newEmployee.lastName = '';
		$scope.newEmployee.firstName = '';
	}
	
	$scope.finger = function(){
		var netid = $scope.newEmployee.netId;
		if(!netid){
			$scope.errorMessage = invalidNetidMsg;
			return;
		}
		
		$scope.errorMessage = null;
		$scope.fingerDisabled = true;
		
		miscServices.finger(netid).then(function(response){
			$scope.fingerDisabled = false;
			
			if(response.name){
				var names = response.name.split(',');
				$scope.newEmployee.firstName = names[1].trim();
				$scope.newEmployee.lastName = names[0].trim();
			}
			else{
				$scope.errorMessage = badFingerMsg + netid;
			}
		});
	}
	
	$scope.saveChanges = function(){
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
	
	refreshNewEmployee();
	
}]);
