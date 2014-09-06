angular.module('signin').controller('SigninCtrl', ['$scope', '$interval', 'swipeServices',  function($scope, $interval, swipeServices){
	var delay = 1000 * 10;
	var loadingEmployees = false;
	var currentId = 0;
	
	$scope.employees = [];
	$scope.swiper = '';
	$scope.showSuccess = false;
	$scope.showError = false;
	
	$scope.message1 = '';
	$scope.message2 = '';
	$scope.message3 = '';
	$scope.message4 = '';
	
	$scope.doSwipe = function(){
		processMessage('Working...');
		swipeServices.swipe($scope.swiper).then(handleResponse);
	}
	
	$scope.noEmployees = function(){
		return !loadingEmployees && $scope.employees.length == 0;
	}
	
	$scope.isLoadingEmployees = function(){
		return loadingEmployees;
	}
	
	function handleResponse(data){
		currentId++;
		processMessage('');
		$scope.swiper = '';
		var showError = false;
		var showSuccess = false;
		
		
		// error communicating with server
		if(data.error){
			handleServerError(data);
			showError = true
		}
		
		// error doing swipe, probably bad data
		else if(data.hasError){
			handleBadSwipeError(data);
			showError = true;
		}
		// now signed in
		else if(data.isNowSignedIn){
			handleSigin(data);
			showSuccess = true;
		}
		// now signed out
		else{
			handleSignout(data);
			showSuccess = true;
		}
		setDisplayError(showError);
		setDisplaySuccess(showSuccess);
		document.getElementById('sidfield').focus();
		
		var myId = currentId;
		
		// clear the fields after a second
		$interval(function(){
			// only clear the messages if ours is still the one up
			if(myId == currentId){
				setDisplayError(false);
				setDisplaySuccess(false);
				processMessage();
			}
		}, delay, 1);
		updateSignedInEmployeeData();
	}
	
	function setDisplayError(val){
		$scope.showError = val;
	}
	
	function setDisplaySuccess(val){
		$scope.showSuccess = val;
	}
	
	function handleSigin(data){
		/* Example data
		 *  {
		 *    "isNowSignedIn":true,
		 *    "name":"asdf",
		 *    "hasError":false,
		 *    "errorMessage":null
		 *  }
		 */
		processMessage('Welcome to the helpdesk, ' + data.name);
	}
	
	function handleSignout(data){
		// exmaple data
		//{ 
		//  "isNowSignedIn":false,
		//  "name":"asdf",
		//  "hasError":false,
		//  "errorMessage":null,
		//  "snark":"Butts",
		//  "timeWorkedShift":5680875,
		//  "timeWorkedDay":17498889
		//}
		processMessage('Goodbye ' + data.name + '.', 'You worked for ' + (data.timeWorkedShift / 1000 / 3600.0) + ' hours that shift.',
				'You\'ve worked for ' + (data.timeWorkedDay / 1000 / 3600.0) + ' hours today.',
				data.snark
		);
	}
	
	function handleServerError(data){
		processMessage('Error swiping in: ' + data.error);
		console.log('Server error swiping in: ' + JSON.stringify(data));
	}
	
	function handleBadSwipeError(data){
		processMessage('Error swiping in: ' + data.errorMessage);
	}
	
	function processMessage(msg1, msg2, msg3, msg4){
		if(!msg1){ msg1 = ''; }
		if(!msg2){ msg2 = ''; }
		if(!msg3){ msg3 = ''; }
		if(!msg4){ msg4 = ''; }
		
		$scope.message1 = msg1;
		$scope.message2 = msg2;
		$scope.message3 = msg3;
		$scope.message4 = msg4;
	}
	
	function updateSignedInEmployeeData(){
		loadingEmployees = true;
		swipeServices.getSignedInEmployees().then(function(response){
			loadingEmployees = false;
			if(response.error){
				console.log('Got error: ' + JSON.stringify(response.error));
			}
			else{
				$scope.employees = response;
			}
		});
	}
	
	updateSignedInEmployeeData();

	/* Update the signed in employees list once a minute */
	$interval(function(){
		updateSignedInEmployeeData();
	}, 60000);
	
	document.getElementById('sidfield').focus();
}]);