//服务层
app.service('seckillGoodsService',function($http){

	this.findList=function () {
		return $http.get('../seckillGoods/findList.do')
    }
    
    this.findOneFromRedis=function (id) {
        return $http.get('../seckillGoods/findOneFromRedis.do?id='+id);
    }

    this.submitOrder=function (seckillId) {
        return $http.get('../seckillOrder/submitOrder.do?seckillId='+seckillId);
    }
});
