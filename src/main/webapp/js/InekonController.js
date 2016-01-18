angular.module('InekonApp.services', []).factory('inekonApi', function($http) {

	var api = {};

	api.getShoppingCarts = function() {
		return $http.get("/shoppingCarts");
	}

	api.getVersions = function(aCartId) {
		return $http.get("/versions/" + aCartId);
	}

	api.getVersion = function(aCartId, aVersionId) {
		return $http.get("/version/" + aCartId + "/" + aVersionId);
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

angular
		.module('InekonApp.controllers', [])
		.controller(
				'InekonController',
				function($scope, inekonApi) {

					console.log("this", this, "scope", $scope);

					// load the google chart library
					var chartPromise = new Promise(function(resolve) {
						google.charts.load('current', {
							'packages' : [ 'corechart' ]
						});
						google.charts.setOnLoadCallback(function() {
							var chart = new google.visualization.LineChart(
									document
											.getElementById("resultChart"));
							resolve(chart);
						});
					});

					// initial state
					$scope.shoppingCarts = [];

					$scope.versions = [];

					$scope.onCreateShoppingCart = function() {
						console.log("name", $scope.shoppingCartTitle);

						// dispatch to the REST service
						inekonApi.createShoppingCart($scope.shoppingCartTitle)
								.then(function(result) {
									$scope.cartId = result.data.id;
								});
					}

					$scope.onDeleteShoppingCart = function(aId) {
						console.log("onDeleteShoppingCart", aId);
					}

					$scope.onDeleteVersion = function(aCartId, aVersionId) {
						console.log("onDeleteVersion", aCartId, aVersionId);
					}

					$scope.onSelectShoppingCart = function(aId) {
						console.log("select", aId);
						// select
						$scope.cartId = aId;
						delete $scope.versionId;
						// update the list of versions
						updateVersions();
					}

					$scope.onSelectVersion = function(aId) {
						$scope.versionId = aId;
						// update the result
						updateVersion();
					}

					$scope.onClearVersion = function() {
						delete $scope.versionId;
					}

					$scope.onClearShoppingCart = function() {
						delete $scope.cartId;
						delete $scope.versionId;
					}

					$scope.onCalculate = function() {
						console.log("calc", $scope.cartId, $scope.left,
								$scope.right);

						inekonApi.calculate($scope.cartId, $scope.left,
								$scope.right).then(function(result) {
							var data = result.data;
							$scope.versionId = data.versionId;
							$scope.cartId = data.cartId;
							// update the result
							updateVersion();
						});
					}

					$scope.onSelectShoppingCartList = function() {
						delete $scope.cartId;
						delete $scope.versionId;
						// update the list
						updateShoppingCarts();
					}

					/**
					 * Updates the list of shopping carts
					 */
					function updateShoppingCarts() {

						console.log("updateShoppingCarts");

						inekonApi.getShoppingCarts().then(function(result) {
							$scope.shoppingCarts = result.data.shoppingCarts;
							console.log($scope.shoppingCarts);
						});
					}

					/**
					 * Updates the list of versions
					 */
					function updateVersions() {

						console.log("updateVersions");

						inekonApi.getVersions($scope.cartId)
								.then(
										function(result) {
											var data = result.data;
											$scope.cartId = data.id;
											$scope.versions = data.versions;
											console.log($scope.cartId,
													$scope.versions);
										});
					}

					/**
					 * Updates the result for one version
					 */
					function updateVersion() {

						console.log("updateVersion");

						inekonApi
								.getVersion($scope.cartId, $scope.versionId)
								.then(
										function(result) {
											chartPromise
													.then(function(chart) {
														// data row
														var row = result.data.data.result, len = row.length, i;
														var dataTable = new google.visualization.DataTable();
														dataTable.addColumn(
																'number',
																'index');
														dataTable.addColumn(
																'number',
																'Resultat');
														for (i = 0; i < len; ++i) {
															dataTable
																	.addRow([
																			i,
																			row[i] ]);
														}

														chart.draw(dataTable);

													});
										});
					}

					// initial update
					updateShoppingCarts();
				});

angular.module('InekonApp', [ 'InekonApp.controllers', 'InekonApp.services' ]);