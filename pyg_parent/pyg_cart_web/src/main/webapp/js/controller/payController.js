app.controller('payController',function($scope,$location,payService){
    
    $scope.createNative=function () {
        payService.createNative().success(function (response) {
            $scope.money= (response.total_fee/100).toFixed(2) ;  //金额
            $scope.out_trade_no= response.out_trade_no;//订单号
            //二维码
            $scope.erwei=0;
            var qr = new QRious({
                element:document.getElementById('qrious'),
                size:250,
                level:'H',
                value:response.code_url
            });
            queryPayStatus(response.out_trade_no);
        })
    }
    $scope.erwei=0;
    queryPayStatus=function (out_trade_no) {
        payService.queryPayStatus(out_trade_no).success(function (response) {
            if(response.success){
                location.href="paysuccess.html#?money="+$scope.money;
            }else{
                if(response.message=='二维码超时'){
                    $scope.erwei=1;
                }else{
                location.href="payfail.html";
                }
            }
        })
    }

    $scope.getMoney=function () {
        return $location.search()['money'];
    }
});