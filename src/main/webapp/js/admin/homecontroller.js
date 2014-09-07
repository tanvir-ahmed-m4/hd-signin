angular.module('admin').controller('AdminHomeCtrl', ['$scope', 'miscServices', 'correctionRequestServices', 'TimeUtils',  function($scope, miscServices, correctionRequestServices, TimeUtils){
	$scope.user = null;
	$scope.corrections = [];
	
	miscServices.getSignedInUser().then(function(response){
		$scope.user = response;
	});
	
	correctionRequestServices.getPendingCorrectionRequests().then(function(response){
		$scope.corrections = response;
	});
	
	$scope.formatDate = function(time){
		return new Date(time).toLocaleString();
	}
	$scope.getOriginalDuration = function(correction){
		return TimeUtils.formatTime(correction.originalSignoutTime - correction.originalSigninTime);
	}
	
	$scope.getNewDuration = function(correction){
		return TimeUtils.formatTime(correction.newSignoutTime - correction.newSigninTime);
	}
	
	$scope.resolveCorrectionRequest = function(request, approved){
		correctionRequestServices.resolveCorrectionRequest(request.id, approved).then(function(response){
			removeRequest(request);
		});
	}
	
	function removeRequest(request){
		for(var i = 0; i < $scope.corrections.length; i++){
			if($scope.corrections[i].id == request.id){
				$scope.corrections.splice(i, 1);
				return;
			}
		}
	}
	
	
}]);