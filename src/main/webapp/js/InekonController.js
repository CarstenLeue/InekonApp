angular.module('InekonApp.services', []).factory('inekonApi', function($http) {

	var api = {};

	api.getShoppingCarts = function() {
		return $http.get("/shoppingCarts");
	}

	api.createShoppingCart = function(aTitle) {
		return $http.put("/createShoppingCart", aTitle);
	}

	return api;
});

angular.module('InekonApp.controllers', []).controller(
		'InekonController',
		function($scope, inekonApi) {

			console.log("this", this, "scope", $scope);

			// initial state
			$scope.shoppingCarts = [];

			$scope.versions = [];

			$scope.onCreateShoppingCart = function() {
				console.log("name", $scope.shoppingCartTitle);

				// dispatch to the REST service
				inekonApi.createShoppingCart($scope.shoppingCartTitle).then(
						function(result) {
							console.log(result);
						});
			}

			$scope.onDeleteShoppingCart = function(aId) {
				console.log("delete", aId);
			}

			$scope.onSelectShoppingCart = function(aId) {
				console.log("select", aId);
				// select
				$scope.cartId = aId;
			}

			$scope.onSelectVersion = function(aId) {
				$scope.versionId = aId;
			}

			$scope.onClearVersion = function() {
				delete $scope.versionId;
			}

			$scope.onClearShoppingCart = function() {
				delete $scope.cartId;
				delete $scope.versionId;
			}

			$scope.onCalculate = function() {
				console.log("calc", $scope.left, $scope.right);
			}

			$scope.onSelectShoppingCartList = function() {
				delete $scope.cartId;
				delete $scope.versionId;
				// update the list
				updateShoppingCarts();
			}

			function updateShoppingCarts() {

				console.log("updateShoppingCarts");

				inekonApi.getShoppingCarts().then(function(result) {
					$scope.shoppingCarts = result.data.shoppingCarts;
					console.log($scope.shoppingCarts);
				});
			}

			// initial update
			updateShoppingCarts();
		});

angular.module('InekonApp', [ 'InekonApp.controllers', 'InekonApp.services' ]);