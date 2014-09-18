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
		finger: finger,
		toggleSignin: toggleSignin,
		getSignedInEmployees: getSignedInEmployees,
		getSignedInUser: getSignedInUser
	}

	function finger(netid){
		return $http({
			url: '/signin/rest/admin/scc/finger',
			method: 'GET',
			params: {
				'netid': netid
			}
		}).then(getPayload, processError);
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
		toggleSigninState: toggleSigninState,
		createEmployee: createEmployee,
		updateEmployee: updateEmployee,
		deleteEmployee: deleteEmployee,
		getAllEmployees: getAllEmployees,
		getSignedInEmployees: getSignedInEmployees
	}
	
	function toggleSigninState(employee){
		var eid = employee;
		
		if(employee.riceId){
			eid = employee.riceId;
		}
		return $http({
			method: 'POST',
			url: '/signin/rest/admin/scclead/togglesignin',
			data: eid
		}).then(getPayload, processError);
	}
	
	function getSignedInEmployees(){
		return $http({
			method: 'GET',
			url: '/signin/rest/admin/scclead/employee/signedin',
		}).then(getPayload, processError);
	}
	
	function createEmployee(employee){
		return $http({
			method: 'PUT',
			url: '/signin/rest/admin/scclead/employee',
			data: employee
		}).then(getPayload, processError);
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
			params: {'filterInactive': filterInactive}
		}).then(getPayload, processError);


	}

}]);

angular.module('admin').factory('timecardServices', ['$http', function($http){

	return {
		getCurrentTimecardForCurrentEmployee: getCurrentTimecardForCurrentEmployee,
		getTimecard: getTimecard
	}

	function getTimecard(employeeId, startDate, endDate){
		return $http({
			method: 'GET',
			url: '/signin/rest/admin/scc/employee/' + employeeId + '/timecard',
			params: {
				periodStart: startDate,
				periodEnd: endDate
			}
		}).then(getPayload, processError);
	}
	
	function getCurrentTimecardForCurrentEmployee(id){
		return $http({
			method: 'GET',
			url: '/signin/rest/admin/scc/employee/' + id + '/timecard'
		}).then(getPayload, processError);
	}

}]);

angular.module('admin').factory('correctionRequestServices', ['$http', function($http){

	return {
		resolveCorrectionRequest: resolveCorrectionRequest,
		createCorrectionRequest: createCorrectionRequest,
		getCorrectionRequestsForEmployee: getCorrectionRequestsForEmployee,
		getPendingCorrectionRequests: getPendingCorrectionRequests
	}

	function resolveCorrectionRequest(id, approved){
		var end = '';
		if(approved === true){
			end = 'approve';
		}
		else if(approved === false){
			end = 'deny';
		}
		return $http({
			method: 'POST',
			url: '/signin/rest/admin/scclead/correction/' + id + '/' + end
		}).then(getPayload, processError);
	}
	
	function getPendingCorrectionRequests(){
		return $http({
			method: 'GET',
			url: '/signin/rest/admin/scclead/correction'
		}).then(getPayload, processError);
	}


	function getCorrectionRequestsForEmployee(employeeId){
		return $http({
			method: 'GET',
			url: '/signin/rest/admin/scclead/employee/' + employeeId + '/correction'
		}).then(getPayload, processError);

	}

	function createCorrectionRequest(request){
		return $http({
			method: 'PUT',
			url: '/signin/rest/admin/scc/correction',
			data: request
		}).then(getPayload, processError);
	}
}]);


angular.module('admin').factory('PayPeriodServices', ['$http', function($http){

	return {
		getPayPeriodEnds: getPayPeriodEnds,
	}

	function getPayPeriodEnds(includeFuture){
		if(typeof includeFuture == 'undefined'){
			includeFuture = false;
		}
		
		return $http({
			method: 'GET',
			url: '/signin/rest/admin/scc/periods?includeFuture=' + includeFuture
		}).then(getPayload, processError);
	}
	
}]);


angular.module('admin').factory('TimeUtils', [function(){
	return {
		formatTime: formatTime
	}

	function appendsIfPlural(str, num){
		if(num == 1){
			return str;
		}
		return str + 's';
	}

	function formatTime(time){
		if(time == 0){
			return '0';
		}
		
		// calculate values
		var hours = Math.floor(time / (1000 * 60 * 60));
		time %= (1000 * 60 * 60);
		var minutes = Math.floor(time / (1000 * 60));
		time %= (1000 * 60);
		var seconds = Math.floor(time / 1000);

		//build output
		var out = '';
		var includeComma = false;
		if(hours != 0){
			out += hours + ' hour';
			out = appendsIfPlural(out, hours);

			includeComma = true;
		}
		if(minutes != 0){
			if(includeComma){
				out += ', ';
			}

			out += minutes + ' minute';
			out = appendsIfPlural(out, minutes);

			includeComma = true;
		}
		
		if(hours == 0){
			if(seconds > 0){
				if(includeComma){
					out += ', ';
				}
				out += seconds + ' second';
				out = appendsIfPlural(out, seconds);
			}
		}
		return out;
	}

}]);

