 //控制层 
app.controller('userController' ,function($scope,$controller   ,userService){	

    $scope.reg=function () {
        //两次密码不对
        if($scope.entity.password!=$scope.password){
            alert("两次输入密码不一致，请重新输入");
            $scope.entity.password="";
            $scope.password="";
            return ;
        }
        userService.add($scope.entity,$scope.smscode).success(function (response) {
            alert(response.message);
        })
    }
    $scope.sendCode=function () {
        if($scope.entity.phone==null){
            alert("请输入手机号！");
            return ;
        }
        userService.sendCode($scope.entity.phone).success(function (response) {
            alert(response.message);
        })
    }
	
});	
