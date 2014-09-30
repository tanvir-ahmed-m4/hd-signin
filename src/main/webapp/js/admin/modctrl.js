angular.module('admin').controller('ModCtrl', ['$scope', 'PayPeriodServices', 'EventLogServices',  function($scope, PayPeriodServices, EventLogServices){
	$scope.periodEnds = [];
	$scope.eventLog = [];
	
	$scope.formatPeriodEnd = function(time){
		return new Date(time).toDateString();
	}
	
	$scope.formatEventDate = function(time){
		return new Date(time).toLocaleString();
	}
	
	
	PayPeriodServices.getPayPeriodEnds(true, true).then(function(response){
		$scope.periodEnds = response;
		$scope.periodEnds.sort(function(a, b){
			return b.end - a.end;
		});
	});
	
	EventLogServices.getLog().then(function(response){
		if(response.error){
			console.log('Error gettting log: ' + response.error);
			return;
		}
		
		$scope.eventLog = response.reverse();
	});
}]);
