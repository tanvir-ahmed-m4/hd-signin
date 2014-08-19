angular.module('signin').controller('SigninCtrl', ['$scope', 'swipeServices',  function($scope, swipeServices){

	$scope.swiper = '';
	$scope.message = '';
	

	$scope.doSwipe = function(){
		function handleResponse(data){
			processMessage('');
			if(data.error){
				handleError(data);
			}
			else if(data.isNowSignedIn){
				handleSigin(data);
			}
			else{
				handleSignout(data);
			}
		}
		processMessage('Working...');
		swipeServices.swipe($scope.swiper).then(handleResponse);
	}
	
	function handleSigin(data){
		
	}
	
	function handleSignout(data){
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
	
	function handleError(data){
		
	}
	
	function processMessage(msg){
		msg = msg.replace('/n', '<br />');
		msg = msg.replace('/r/n', '<br />');
		msg = msg.replace('/r', '<br />');
		$scope.message = msg;
	}



}]);