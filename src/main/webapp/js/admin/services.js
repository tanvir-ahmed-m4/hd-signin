angular.module('admin').factory('swipeServices', ['$http', function($http){
	return {
		swipe: swipe
	}
	
	function swipe(sid){
		
		return $http({
			url: 'rest/signin/swipe',
			method: 'POST',
			data: sid
		}).then(function(response){
			return response.data;
		}, function(error){
			console.log('got error: ' + JSON.stringify(error));
			return '';
		});
	}
	
	
}]);