angular.module('admin').controller('AdminNavCtrl', ['$scope', '$location', 'miscServices',  function($scope, $location, miscServices){
	$scope.logoutUrl = '/signin/cas_logout?service=' + escape($location.protocol() + '://' + $location.host() + ':' + $location.port() + '/signin');
	$scope.username = '';
	$scope.usermsg = 'loading user...';
	$scope.type = '';
	$scope.user = null;
	
	$scope.isActive = function(name){
		return '/' + name == $location.path();
	}
	
	$scope.hasLevel = function(level){
		return miscServices.hasLevel($scope.user, level);
	}
	
	
	miscServices.getSignedInUser().then(function(response){
		if(response.error){

		}
		else{
			$scope.user = response;
			$scope.usermsg = 'Currently signed in as ';

			if(response.firstName){
				$scope.username = response.firstName;
			}
			else if(response.netId){
				$scope.username = response.netId;
			}
			else{
				$scope.username = '';
				$scope.usermsg = 'Failed to load user, try reloading page';
			}
			
			if(response.type){
				$scope.type = '(' + response.type + ')';
			}
		}
	});
	


}]);