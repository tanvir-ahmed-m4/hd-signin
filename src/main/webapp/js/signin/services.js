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
}]);