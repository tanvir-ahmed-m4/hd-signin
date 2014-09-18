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

angular.module('signin').factory('TimeUtils', [function(){
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