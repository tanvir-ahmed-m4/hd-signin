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