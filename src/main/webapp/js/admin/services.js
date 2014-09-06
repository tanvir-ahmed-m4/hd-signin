var processError = function(response){
	console.log('Got error: ' + JSON.stringify(response));
	var msg = '';
	
	var payload = getPayload(response);
	if(payload.data){
		msg = payload.data;
	}else{
		msg = response.statusText;
	}
	
	return {'error': msg, 'code': response.status};
}
var getPayload = function(response){
	return response.data;
}
angular.module('admin').factory('miscServices', ['$http', function($http){
	
	return {
		toggleSignin: toggleSignin,
		getSignedInEmployees: getSignedInEmployees,
		getSignedInUser: getSignedInUser
	}
	
	function toggleSignin(sid){
		
		return $http({
			url: '/signin/rest/admin/scclead/togglesignin',
			method: 'POST',
			data: sid
		}).then(getPayload, processError);
	}
	
	function getSignedInEmployees(){
		return $http({
			url: '/signin/rest/signin/currentemployees',
			method: 'GET',
		}).then(getPayload, processError);
	}
	
	function getSignedInUser(){
		return $http({
			url: '/signin/rest/admin/scc/currentuser',
			method: 'GET',
		}).then(getPayload, processError);
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
		}).then(getPayload, processError);
	}
	
	function deleteEmployee(id){
		
	}
	
	function getAllEmployees(filterInactive){
		if(typeof(filterInactive) === 'undefined' || filterInactive === null){
			filterInactive = true;
		}
		
		return $http({
			method: 'GET',
			url: '/signin/rest/admin/scclead/employee',
			params: {'filterinactive': filterInactive}
		}).then(getPayload, processError);
		
		
	}
	
}]);
