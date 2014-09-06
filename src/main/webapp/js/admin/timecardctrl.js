angular.module('admin').controller('TimecardCtrl', ['$scope', 'timecardServices', 'miscServices', 'correctionRequestServices',  function($scope, timecardServices, miscServices, correctionRequestServices){
	$scope.user = null;
	$scope.msg = 'Testing';
	
	$scope.timecard = [];
	$scope.corrections = [];
	
	$scope.editedRow = -1;
	
	$scope.workSessionBeingEdited = null;
	
	// work session: {"id":6,"employeeId":3,"signinTime":1410031651000,"signoutTime":1410031661000}
	$scope.getStartTime = function(workSession){
		return new Date(workSession.signinTime).toLocaleString();
	}
	
	$scope.changed = function(event){
		
	}
	
	$scope.getEditMsg = function(){
		return 'SUPERVISOR' == $scope.user.employeeType || 'SYSADMIN' == $scope.user.employeeType ? 'Submit Correction' : 'Submit Correction Request';
	}
	
	$scope.editRow = function(idx, workSession){
		$scope.workSessionBeingEdited = clone(workSession);
		$scope.editedRow = idx
	};
	
	$scope.cancelCorrectionRequest = function(){
		$scope.editedRow = -1;
		$scope.workSessionBeingEdited = null;
	}
	
	$scope.submitCorrectionRequest = function(){
		var request = {};
		request['signinId'] = $scope.workSessionBeingEdited.id;
		request['submitter'] = $scope.user;
		request['newSigninTime'] = $scope.workSessionBeingEdited.signinTime;
		request['newSignoutTime'] = $scope.workSessionBeingEdited.signoutTime;

		console.log(JSON.stringify($scope.workSessionBeingEdited));
		correctionRequestServices.createCorrectionRequest(request);
	}
	
	$scope.getEndTime = function(workSession){
		if(workSession.signoutTime == null || workSession.signoutTime == 0){
			return 'Still Signed In';
		}
		return new Date(workSession.signoutTime).toLocaleString();
	}
	
	$scope.getDuration = function(workSession){
		var signoutTime = workSession.signoutTime;
		if(signoutTime == null || signoutTime == 0){
			signoutTime = new Date().getTime();
		}
		return ((signoutTime - workSession.signinTime) / 1000.0) + ' Seconds';
	}
	
	function clone(obj) {
	    if (null == obj || "object" != typeof obj) return obj;
	    var copy = obj.constructor();
	    for (var attr in obj) {
	        if (obj.hasOwnProperty(attr)) copy[attr] = obj[attr];
	    }
	    return copy;
	}
	
	function updateDisplayedCorrectionRequests(){
		if($scope.timecard == null || $scope.timecard.length == 0)
			return;
		if($scope.corrections == null || $scope.corrections.length == 0)
			return;
		var ids = {};
		
		for(var i = 0; i < $scope.corrections.length; i++){
			ids[$scope.corrections[i].signinId] = $scope.corrections[i];
		}
		var workSessions = $scope.timecard.workSessions;
		for(var i = 0; i < workSessions.length; i++){
			if(workSessions[i].id in ids){
				workSessions[i].hasCorrectionRequest = true;
				workSessions[i].correctionRequest = ids[workSessions[i].id];
			}
		}
	}
	
	miscServices.getSignedInUser().then(function(response){
		$scope.user = response;
		timecardServices.getCurrentTimecardForCurrentEmployee(response.id).then(function(response){
			$scope.timecard = response;
			updateDisplayedCorrectionRequests();
		});
		
		correctionRequestServices.getCorrectionRequestsForEmployee(response.id).then(function(response){
			$scope.corrections = response;
			updateDisplayedCorrectionRequests();
		});
	})
	
	
	
	
}]);