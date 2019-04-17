app.controller("indexController",function ($scope, loginService) {
    $scope.showloginName=function () {
        loginService.loginname().success(function (response) {
            $scope.loginname=response.loginName;
        })
    }
})