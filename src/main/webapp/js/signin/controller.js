angular.module('signin').controller('SigninCtrl', ['$scope', 'swipeServices',  function($scope, swipeServices){

	$scope.swiper = '';
	$scope.message = '';
	
	$scope.doSwipe = function(){
		function handleResponse(data){
			processMessage('');
			
			// error communicating with server
			if(data.error){
				handleServerError(data);
			}
			
			// error doing swipe, probably bad data
			else if(data.hasError){
				handleBadSwipeError(data);
			}
			// now signed in
			else if(data.isNowSignedIn){
				handleSigin(data);
			}
			// now signed out
			else{
				handleSignout(data);
			}
		}
		processMessage('Working...');
		swipeServices.swipe($scope.swiper).then(handleResponse);
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
		msg = '';
		msg += 'Goodbye ' + data.name + '. You worked for ' + (data.timeWorkedShift / 1000 / 3600.0) + ' hours that shift.\n';
		msg += 'You\'ve worked for ' + (data.timeWorkedDay / 1000 / 3600.0) + ' hours today.\n';
		msg += data.snark;
		processMessage(msg);
	}
	
	function handleServerError(data){
		processMessage('Error swiping in: ' + data.error);
		console.log('Server error swiping in: ' + JSON.stringify(data));
	}
	
	function handleBadSwipeError(data){
		processMessage('Error swiping in: ' + data.errorMessage);
	}
	
	function processMessage(msg){
		msg = msg.replace('/n', '<br />');
		msg = msg.replace('/r/n', '<br />');
		msg = msg.replace('/r', '<br />');
		$scope.message = msg;
	}
}]);