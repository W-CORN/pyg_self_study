app.controller('itemController',function($scope,$http){
    //定义SKU变量
    $scope.sku={};
	$scope.addNum=function(x){
		$scope.num=$scope.num+x;
		if($scope.num<1){
			$scope.num=1;
		}
	}
	$scope.specificationItems={};//记录用户所选择的规格
	
	$scope.selectSpecification=function(name,value){
		$scope.specificationItems[name]=value;
	}
	//判断是否被选中
	$scope.isSelect=function(name,value){
		if($scope.specificationItems[name]==value){
			return true;
		}else{
			return false;
		}
	}
	$scope.loadSku=function () {
		$scope.sku=skuList[0];
		$scope.specificationItems=JSON.parse(JSON.stringify($scope.sku.spec))
    }

    $scope.selectSpecification=function (key, value) {
        $scope.specificationItems[key]=value;
        searchSku();	//查询规格
    }
    searchSku=function () {
		for (var i=0;i<skuList.length;i++){
            if(matchObject(skuList[i].spec ,$scope.specificationItems)) {
                $scope.sku = skuList[i];
                return;
            }
        }
        $scope.sku={id:0,title:'-----',price:0};
    }
    matchObject=function (map1,map2) {
        for(var k in map1){
            if(map1[k]!=map2[k]){
                return false;
            }
        }
        for(var k in map2){
            if(map2[k]!=map1[k]){
                return false;
            }
        }
        return true;
    }

    $scope.addToCart=function () {
		$http.get("http://localhost:9107/seckill/addGoodsToCartList.do?itemId="+$scope.sku.id+"&num="
            +$scope.num,{'withCredentials':true}).success(function (response) {
            if(response.success){
                location.href='http://localhost:9107/seckill.html';
            }else {
                alert(response.message);
            }
        })
    }
})