 //控制层 
app.controller('seckillGoodsController' ,function($scope ,$interval ,$location,seckillGoodsService){

    $scope.findList=function () {
		seckillGoodsService.findList().success(function (response) {
			$scope.list=response;
        })
    }

    $scope.findOneFromRedis=function () {
        seckillGoodsService.findOneFromRedis($location.search()['id']).success(function (response) {
            $scope.entity=response;
            allsecond=Math.floor(new Date($scope.entity.endTime).getTime()-new Date().getTime())/1000;//总秒数
            time=$interval(function () {
                allsecond=allsecond-1;
                $scope.timeString=convertTimeString(allsecond);
                if (allsecond<=0){
                    $interval.cancel(time);
                    alert("秒杀结束")
                }
            },1000);
        });
    }
    convertTimeString=function(allsecond){
        var days=Math.floor(allsecond/(60*60*24));//天数
        var hours=Math.floor((allsecond-days*60*60*24)/(60*60));//小时
        var minutes=Math.floor(((allsecond-days*60*60*24-hours*60*60))/60);//分
        var seconds=Math.floor(allsecond-days*60*60*24-hours*60*60-minutes*60);//秒
        var stringdays="";
        if(days>0){
            stringdays=days+"天";
        }
        var min="";
        if (minutes<10){
            min="0"+minutes;
        }else {
            min=minutes+"";
        }
        var tim="";
        if (seconds<10){
            tim="0"+seconds;
        }else {
            tim=seconds+"";
        }
        return stringdays+hours+":"+min+":"+tim;
    }
    $scope.submitOrder=function () {
        seckillGoodsService.submitOrder($scope.entity.id).success(function (response) {
            if (response.success){
                alert("下单成功，请在5分钟内完成支付");
                location.href="pay.html";
            }else {
                alert(response.message);
            }
        })
    }
});	
