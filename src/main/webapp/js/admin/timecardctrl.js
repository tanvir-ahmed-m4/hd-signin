angular.module('admin').controller('TimecardCtrl', ['$scope', 'timecardServices', 'miscServices', 'correctionRequestServices', 'PayPeriodServices', 'employeeServices', 'TimeUtils',  function($scope, timecardServices, miscServices, correctionRequestServices, PayPeriodServices, employeeServices, TimeUtils){
	$scope.user = null;
	
	$scope.periodStart = 0;
	$scope.periodEnd = 0;
	$scope.periodEnds = [];
	
	$scope.employees = [];
	$scope.employee = null;
	
	$scope.workSessions = [];
	$scope.corrections = [];
	
	$scope.timecard = null;
	
	$scope.editedRow = -1;
	$scope.workSessionBeingEdited = null;
	
	// work session: {"id":6,"employeeId":3,"signinTime":1410031651000,"signoutTime":1410031661000}
	$scope.getStartTime = function(workSession){
		return new Date(workSession.signinTime).toLocaleString();
	}
	
	$scope.formatWorkTime = function(time){
		return $scope.formatTime(time);
	}
	
	$scope.formatWorkDate = function(time){
		var days = {0: 'Sun', 1: 'Mon', 2: 'Tue', 3: 'Wed', 4: 'Thu', 5: 'Fri', 6: 'Sat'};
		var months = {0: 'Jan', 1:'Feb', 2:'Mar', 3:'Apr', 4:'May', 5:'Jun', 6:'Jul', 7:'Aug', 8:'Sep', 9:'Oct', 10:'Nov', 11:'Dec'};
		var d = new Date(time);
		return days[d.getDay()] + ' ' + months[d.getMonth()] + ' ' + d.getDate();
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
		loadTimecardData();
	}
	
	$scope.selectedPeriodChanged = function(newPeriod){
		loadTimecardData();
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
		if($scope.workSessions == null || $scope.workSessions.length == 0)
			return;
		if($scope.corrections == null || $scope.corrections.length == 0)
			return;
		var ids = {};
		
		for(var i = 0; i < $scope.corrections.length; i++){
			ids[$scope.corrections[i].signinId] = $scope.corrections[i];
		}
		var workSessions = $scope.workSessions.workSessions;
		for(var i = 0; i < workSessions.length; i++){
			if(workSessions[i].id in ids){
				workSessions[i].hasCorrectionRequest = true;
				workSessions[i].correctionRequest = ids[workSessions[i].id];
			}
		}
	}
	
	function updateDisplayedEmployee(){
		if($scope.user != null){
			if($scope.employees != null){
				for(var i = 0; i < $scope.employees.length; i++){
					if($scope.employees[i].id == $scope.user.id){
						$scope.employee = $scope.employees[i];
						return;
					}
				}
			}
			else{
				$scope.employee = $scope.user;
				return;
			}
		}
	}
	
	function loadTimecardData(){
		if($scope.employee == null){
			return;
		}
		
		$scope.workSessions = [];
		$scope.corrections = [];
		
		var eid = $scope.employee.id;
		var twoWeeks = 1000 * 60 * 60 * 24 * 14;
		
		$scope.periodStart = $scope.periodEnd - twoWeeks; 
		timecardServices.getTimecard(eid, $scope.periodStart, $scope.periodEnd).then(function(response){
			$scope.workSessions = response;
			$scope.timecard = generateTimecard(response.workSessions, response.period.startOfPeriod, response.period.endOfPeriod);
			updateDisplayedCorrectionRequests();
		});
		
		correctionRequestServices.getCorrectionRequestsForEmployee(eid).then(function(response){
			$scope.corrections = response;
			updateDisplayedCorrectionRequests();
		});
	}
	
	function generateTimecard(workSessions, startDate, endDate){
		var millisPerDay = 1000 * 60 * 60 * 24;
		function getIdx(sessionStart){
			return Math.floor((sessionStart - startDate) / millisPerDay)
		}
		
		var hours = [];
		var days = Math.ceil((endDate - startDate) / millisPerDay);
		
		for(var i = 0; i < days; i++){
			hours.push({time: 0, idx: i, day: startDate + (millisPerDay * i)});
		}
		
		for(var i = 0; i < workSessions.length; i++){
			var signoutTime = workSessions[i].signoutTime > 0 ? workSessions[i].signoutTime : new Date().getTime();
			var duration = signoutTime - workSessions[i].signinTime;
			var idx = getIdx(workSessions[i].signinTime);
			hours[idx].time += duration;
		}
		var weeks = [];
		for(var i = 0; i < Math.floor(hours.length / 7); i++){
			weeks.push(hours.slice(i * 7, (i + 1) * 7));
		}
		return {
			'startDate': startDate,
			'times': hours,
			'weeks': weeks
		};
	}
	
	/**
	 * Called when an ajax call that loads requisite data is completed
	 */
	function ajaxCallCompleted(){
		
		// update the employee if we haven't and we have the missing data
		if($scope.employee == null){
			if($scope.employees != null && $scope.employees.length > 0){
				updateDisplayedEmployee();
			}
		}
		
		if($scope.workSessions == null || $scope.workSessions.length == 0){
			if($scope.employee != null && $scope.periodEnds != null && $scope.periodEnds.length > 0){
				loadTimecardData();
			}
		}
	}
	
	
	miscServices.getSignedInUser().then(function(response){
		$scope.user = response;
		ajaxCallCompleted();
	});
	
	PayPeriodServices.getPayPeriodEnds().then(function(response){
		$scope.periodEnds = response.reverse();
		$scope.periodEnd = $scope.periodEnds[0];
		ajaxCallCompleted();
	});
	
	employeeServices.getAllEmployees().then(function(response){
		if(response.error){
			console.log("Error getting all employees: " + response.error);
			return;
		}
		$scope.employees = response;
		ajaxCallCompleted();
	});
}]);