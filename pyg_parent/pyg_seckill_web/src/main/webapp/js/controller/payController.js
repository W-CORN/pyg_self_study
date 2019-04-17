app.controller('payController',function($scope,$interval,$location,payService){
    
    $scope.createNative=function () {
        payService.createNative().success(function (response) {
            $scope.money= (response.total_fee/100).toFixed(2) ;  //金额
            $scope.out_trade_no= response.out_trade_no;//订单号
            var qr = new QRious({
                element:document.getElementById('qrious'),
                size:250,
                level:'H',
                value:response.code_url
            });
            $scope.time=response.timeOut;
            queryPayStatus(response.out_trade_no);
            allsecond=Math.floor(new Date($scope.time).getTime()+60*5*1000-new Date().getTime())/1000;//总秒数
            time=$interval(function () {
                allsecond=allsecond-1;
                $scope.timeString=convertTimeString(allsecond);
                if (allsecond<=0){
                    $interval.cancel(time);
                }
            },1000);
        })
    }
    queryPayStatus=function (out_trade_no) {
        payService.queryPayStatus(out_trade_no).success(function (response) {
            if(response.success){
                location.href="paysuccess.html#?money="+$scope.money;
            }else{
                if(response.message=='二维码超时'){
                    location.href="payTimeOut.html";
                }else{
                    location.href="payfail.html";
                }
            }
        })
    }

    $scope.getMoney=function () {
        return $location.search()['money'];
    }

    convertTimeString=function(allsecond){
        var minutes=Math.floor(allsecond/60);//分
        var seconds=Math.floor(allsecond-minutes*60);//秒
        var tim="";
        if (seconds<10){
            tim="0"+seconds;
        }else {
            tim=seconds+"";
        }
        return minutes+":"+tim;
    }
});