angular.module('admin').controller('TimecardCtrl', ['$scope', 'timecardServices', 'miscServices', 'correctionRequestServices', 'PayPeriodServices', 'employeeServices', 'TimeUtils',  function($scope, timecardServices, miscServices, correctionRequestServices, PayPeriodServices, employeeServices, TimeUtils){
	$scope.user = null;
	
	$scope.periodStart = 0;
	$scope.periodEnd = 0;
	$scope.periodEnds = [];
	
	$scope.employees = [];
	$scope.employee = null;
	
	$scope.timecard = [];
	$scope.corrections = [];
	
	$scope.editedRow = -1;
	$scope.workSessionBeingEdited = null;
	
	// work session: {"id":6,"employeeId":3,"signinTime":1410031651000,"signoutTime":1410031661000}
	$scope.getStartTime = function(workSession){
		return new Date(workSession.signinTime).toLocaleString();
	}
	
	$scope.formatDate = function(d){
		return new Date(d).toDateString();
	}
	
	$scope.formatTime = function(time){
		return TimeUtils.formatTime(time);
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
		var request = {
				signinId: $scope.workSessionBeingEdited.id,
				submitter: $scope.user,
				newSigninTime: $scope.workSessionBeingEdited.signinTime,
				newSignoutTime: $scope.workSessionBeingEdited.signoutTime
		};
		
		correctionRequestServices.createCorrectionRequest(request).then(function(response){
			$scope.editedRow = -1;
			addCorrectionRequest(response);
		});
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
		return TimeUtils.formatTime(signoutTime - workSession.signinTime);
	}
	
	$scope.selectedEmployeeChanged = function(newEmployee){
		$scope.employee = newEmployee;
		updateDisplayedTimecard();
	}
	
	$scope.selectedPeriodChanged = function(newPeriod){
		updateDisplayedTimecard();
	}
	
	$scope.isEmployeeSccLeadOrAbove = function(){
		if($scope.user == null){
			return false;
		}
		var type = $scope.user.employeeType;
		return type == 'SYSADMIN' || type == 'SCC_LEAD' || type == 'SUPERVISOR';
	}
	
	
	function clone(obj) {
	    if (null == obj || "object" != typeof obj) return obj;
	    var copy = obj.constructor();
	    for (var attr in obj) {
	        if (obj.hasOwnProperty(attr)) copy[attr] = obj[attr];
	    }
	    return copy;
	}
	
	function addCorrectionRequest(request){
		for(var i = 0; i < $scope.corrections.length; i++){
			if($scope.corrections[i].id == request.id){
				$scope.corrections[i] = request;
				updateDisplayedCorrectionRequests();
				return;
			}
		}
		$scope.corrections.push(request);
		updateDisplayedCorrectionRequests()
		
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
	
	function updateDisplayedEmployee(){
		if($scope.employees != null && $scope.user != null){
			for(var i = 0; i < $scope.employees.length; i++){
				if($scope.employees[i].id == $scope.user.id){
					$scope.employee = $scope.employees[i];
					return;
				}
			}
		}
	}
	
	function updateDisplayedTimecard(){
		$scope.timecard = [];
		$scope.corrections = [];
		
		var eid = $scope.employee.id;
		var twoWeeks = 1000 * 60 * 60 * 24 * 14;
		$scope.periodStart = $scope.periodEnd - twoWeeks; 
		timecardServices.getTimecard(eid, $scope.periodStart, $scope.periodEnd).then(function(response){
			$scope.timecard = response;
			updateDisplayedCorrectionRequests();
		});
		correctionRequestServices.getCorrectionRequestsForEmployee(eid).then(function(response){
			$scope.corrections = response;
			updateDisplayedCorrectionRequests();
		});
	}
	
	
	miscServices.getSignedInUser().then(function(response){
		$scope.user = response;
		updateDisplayedEmployee();
	});
	
	PayPeriodServices.getPayPeriodEnds().then(function(response){
		$scope.periodEnds = response.reverse();
		$scope.periodEnd = $scope.periodEnds[0];
		updateDisplayedTimecard();
	});
	
	employeeServices.getAllEmployees().then(function(response){
		if(response.error){
			console.log("Error getting all employees: " + response.error);
			return;
		}
		$scope.employees = response;
		updateDisplayedEmployee();
	});
	
	
	
	
	
	
}]);