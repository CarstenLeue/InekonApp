angular.module('InekonApp.services', []).factory('inekonApi', function($http) {

	var api = {};

	api.getShoppingCarts = function() {
		return $http.get("/shoppingCarts");
	}

	api.createShoppingCart = function(aTitle) {
		return $http.put("/createShoppingCart", aTitle);
	}

	api.calculate = function(aId, aLeft, aRight) {
		return $http.post("/calc", {
			"id" : aId,
			"left" : aLeft,
			"right" : aRight
		});
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
							$scope.cartId = result.data.id;
						});
			}

			$scope.onDeleteShoppingCart = function(aId) {
				console.log("delete", aId);
			}

			$scope.onSelectShoppingCart = function(aId) {
				console.log("select", aId);
				// select
				$scope.cartId = aId;
				delete $scope.versionId;
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
				console.log("calc", $scope.cartId, $scope.left, $scope.right);

				inekonApi.calculate($scope.cartId, $scope.left, $scope.right).then(function(result) {
					var data = result.data;
					$scope.versionId = data.versionId;
					$scope.cartId = data.cartId;
				});
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