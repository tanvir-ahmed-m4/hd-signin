var processError = function(response){
	console.log('Got error: ' + JSON.stringify(response));
	var msg = '';
	
	var payload = getPayload(response);
	if(payload.error){
		msg = payload.error;
	}else{
		msg = response.statusText;
	}
	
	return {'error': msg, 'code': response.status};
}
var getPayload = function(response){
	return response.data;
}

angular.module('signin').factory('swipeServices', ['$http', function($http){
	/* TODO add real error handling */
	return {
		swipe: swipe,
		getSignedInEmployees: getSignedInEmployees
	}
	
	function swipe(sid){
		
		return $http({
			url: '/signin/rest/signin/swipe',
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
}]);