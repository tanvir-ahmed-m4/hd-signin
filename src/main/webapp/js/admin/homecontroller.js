angular.module('admin').controller('AdminHomeCtrl', ['$scope', 'miscServices', 'correctionRequestServices', 'TimeUtils','employeeServices',  function($scope, miscServices, correctionRequestServices, TimeUtils, employeeServices){
	$scope.user = null;
	$scope.corrections = [];
	$scope.signedInEmployees = [];
	
	
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
	
	$scope.getEmployeeDisplayName = function(employee){
		return employee.firstName + ' ' + employee.lastName;
	}
	
	$scope.toggleSigninState = function(employee){
		employeeServices.toggleSigninState(employee).then(function(response){
			updateSignedInEmployees();
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
	
	function updateSignedInEmployees(){
		employeeServices.getSignedInEmployees().then(function(response){
			$scope.signedInEmployees = response;
		});
	}
	
	
	
	updateSignedInEmployees();
}]);