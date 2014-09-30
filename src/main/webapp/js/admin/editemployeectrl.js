angular.module('admin').controller('EmployeeMgmtCtrl', ['$scope', 'employeeServices', 'miscServices',  function($scope, employeeServices, miscServices){
	// TODO load employee types via AJAX
	$scope.employeeTypes = 	['SCC', 'SCC_LEAD', 'SUPERVISOR', 'SYSADMIN'];

	var invalidNetidMsg = "Must enter netID to finger user";
	var badFingerMsg = "No finger data found for ";

	$scope.errorMessage = null;

	$scope.fingerDisabled = false;

	$scope.employees = [];
	$scope.newEmployee = {};

	$scope.retiredEmployees = [];

	$scope.activeEmployee = {'firstName': 'Loading...'};

	var invalidNetid = false;

	function addEmployees(employees){
		for(var i = 0; i < employees.length; i++){
			addEmployee(employees[i]);
		}
	}

	function addEmployee(employee){
		if(employee.isEmployeeActive){
			for(var i = 0; i < $scope.employees.length; i++){
				if($scope.employees[i].id == employee.id){
					$scope.employees[i] = employee;
					return;
				}
			}
			$scope.employees.push(employee);
			sortEmployees($scope.employees);
		}
		else{
			for(var i = 0; i < $scope.retiredEmployees.length; i++){
				if($scope.retiredEmployees[i].id == employee.id){
					$scope.retiredEmployees[i] = employee;
					return;
				}
			}
			$scope.retiredEmployees.push(employee);
			sortEmployees($scope.retiredEmployees);
		}
	}

	function refreshNewEmployee(){
		$scope.newEmployee.isEmployeeActive = true;
		$scope.newEmployee.employeeType = $scope.employeeTypes[0];
		$scope.newEmployee.riceId = '';
		$scope.newEmployee.netId = '';
		$scope.newEmployee.lastName = '';
		$scope.newEmployee.firstName = '';
	}
	
	function sortEmployees(list){
		list.sort(function(a, b){
			return a.firstName > b.firstName ? 1 : -1;
		});
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

	$scope.createEmployee = function(){
		employeeServices.createEmployee($scope.newEmployee).then(function(response){
			addEmployee(response);
			refreshNewEmployee();
		});
	};

	$scope.getDisplayName = function(employee){
		return employee.firstName + ' ' + employee.lastName;
	}

	$scope.setEmployeeActive = function(employee, active){
		var action = active ? 'Reactivating' : 'Retiring';
		console.log(action + ' ' + $scope.getDisplayName(employee));
		employee.isEmployeeActive = active ? true : false;

		employeeServices.updateEmployee(employee).then(function(response){
			if(response.error){
				//TODO error handling
				return;
			}
			
			function cleanArray(array1, element){
				for(var i = 0; i < array1.length; i++){
					if(array1[i].id == element.id){
						array1.splice(i, 1);
						return;
					}
				}
			}
			
			if(response.isEmployeeActive){
				cleanArray($scope.retiredEmployees, employee);
			}
			else{
				cleanArray($scope.employees, employee);
			}
			addEmployee(employee);
		});


	}
	
	
	employeeServices.getAllEmployees(false).then(function(response){
		sortEmployees(response);
		
		addEmployees(response);
		$scope.activeEmployee = $scope.employees[0];
	});

	refreshNewEmployee();

}]);
