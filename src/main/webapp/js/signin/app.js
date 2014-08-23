angular.module('signin', ['ngRoute']);


angular.module('signin').config(
		function($routeProvider) {
			$routeProvider
			.when('/', {controller:'SigninCtrl'})
			.otherwise({redirectTo:'/'});
		});


angular.module('signin').directive('ngEnter', function () {
    return function (scope, element, attrs) {
        element.bind("keydown keypress", function (event) {
            if(event.which === 13) {
                scope.$apply(function (){
                    scope.$eval(attrs.ngEnter);
                });
 
                event.preventDefault();
            }
        });
    };
});