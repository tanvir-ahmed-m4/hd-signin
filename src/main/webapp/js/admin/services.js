angular.module('admin').factory('miscServices', ['$http', function($http){
	
	return {
		forceSignout: forceSignout,
		getSignedInEmployees: getSignedInEmployees,
		getSignedInUser: getSignedInUser
	}
	
	function forceSignout(sid){
		
		return $http({
			url: 'rest/admin/scclead/forcesignout',
			method: 'POST',
			data: sid
		}).then(function(response){
			return response.data;
		}, function(error){
			console.log('got error: ' + JSON.stringify(error));
			return '';
		});
	}
	
	function getSignedInEmployees(){
		return $http({
			url: '/signin/rest/signin/currentemployees',
			method: 'GET',
		}).then(function(response){
			return response.data;
		}, function(error){
			console.log('got error: ' + JSON.stringify(error));
			return '';
		});
	}
	
	function getSignedInUser(){
		return $http({
			url: '/signin/rest/admin/scc/currentuser',
			method: 'GET',
		}).then(function(response){
			return response.data;
		}, function(error){
			console.log('got error: ' + JSON.stringify(error));
			return '';
		});
	}
}]);

angular.module('admin').factory('employeeServices', ['$http', function($http){
	
	return {
		createEmployee: createEmployee,
		updateEmployee: updateEmployee,
		deleteEmployee: deleteEmployee,
		getAllEmployees: getAllEmployees
	}
	
	function createEmployee(employee){
		
	}
	
	function updateEmployee(employee){
		return $http({
			method: 'POST',
			url: '/signin/rest/admin/scc/employee',
			data: employee
		}).then(function(response){
			return response.data;
		}, function(error){
			console.log('Got an error: ' + JSON.stringify(error));
		});
	}
	
	function deleteEmployee(id){
		
	}
	
	function getAllEmployees(filterInactive){
		if(typeof(filterInactive) === 'undefined' || filterInactive === null){
			filterInactive = true;
		}
		
		return $http({
			method: 'GET',
			url: '/signin/rest/admin/scclead/employees',
			params: {'filterinactive': filterInactive}
		}).then(function(response){
			return response.data;
		}, function(error){
			console.log('Got an error: ' + JSON.stringify(error));
		});
		
		
	}
	
}]);
