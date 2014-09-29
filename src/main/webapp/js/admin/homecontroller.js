angular.module('admin').controller('AdminHomeCtrl', ['$scope', 'miscServices', 'correctionRequestServices', 'TimeUtils','employeeServices',  function($scope, miscServices, correctionRequestServices, TimeUtils, employeeServices){
	$scope.user = null;
	
	$scope.corrections = [];
	$scope.signedInEmployees = [];
	
	$scope.myCorrections = [];
	
	miscServices.getSignedInUser().then(function(response){
		$scope.user = response;
	});
	
	correctionRequestServices.getPendingCorrectionRequests().then(function(response){
		$scope.corrections = response;
	});
	
	correctionRequestServices.getOwnCorrectionRequests().then(function(response){
		$scope.myCorrections = response;
	});
	
	$scope.cancelRequest = function(request){
		correctionRequestServices.cancelCorrectionRequest(request.id);
	}
	
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
			if(response.error){
				//TODO something better than an "alert"
				alert('Error updating correction request: ' + response.error);
				return;
			}
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
				break;
			}
		}
		
		for(var i = 0; i < $scope.myCorrections.length; i++){
			if($scope.myCorrections[i].id == request.id){
				$scope.myCorrections.splice(i, 1);
				break;
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