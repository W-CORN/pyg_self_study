 //控制层 
app.controller('goodsController' ,function($scope,$controller,itemCatService,typeTemplateService,goodsService,$location){
	$controller('baseController',{$scope:$scope});//继承
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    //查询实体
    $scope.findOne = function () {
        var id = $location.search()['id'];
        if (id == null) {
            return;
        }
        goodsService.findOne(id).success(
            function (response) {
                $scope.entity = response;
                //回显富文本内容
                editor.html($scope.entity.tbGoodsDesc.introduction);
                //回显图片
                $scope.entity.tbGoodsDesc.itemImages = JSON.parse($scope.entity.tbGoodsDesc.itemImages);
                //回显扩展属性
                $scope.entity.tbGoodsDesc.customAttributeItems = JSON.parse($scope.entity.tbGoodsDesc.customAttributeItems);
                //回显规格选择
                $scope.entity.tbGoodsDesc.specificationItems = JSON.parse($scope.entity.tbGoodsDesc.specificationItems);
                //回显列表
                for (var i = 0; i < $scope.entity.itemList.length; i++) {
                    $scope.entity.itemList[i].spec = JSON.parse($scope.entity.itemList[i].spec);
                }
            }
        );
    };
    //查询回显后规格选项
    $scope.checkAttributeValue = function (specName, optionName) {
        var items = $scope.entity.tbGoodsDesc.specificationItems;
        var object = $scope.searchObjectByKey(items, "attributeName", specName);
        if (object != null) {
            if (object.attributeValue.indexOf(optionName) >= 0) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
    //查询一级
    $scope.selectItemCat1List = function () {
        itemCatService.findParentId(0).success(function (response) {
            $scope.itemCat1List = response;
        })
    };
    //查询二级
    $scope.$watch('entity.tbGoods.category1Id', function (newValue, oldValue) {
        itemCatService.findParentId(newValue).success(function (response) {
            $scope.itemCat2List = response;
            $scope.itemCat3List = null;
            $scope.entity.tbGoods.typeTemplateId = null;

        })
    });
    //查询三级
    $scope.$watch('entity.tbGoods.category2Id', function (newValue, oldValue) {
        itemCatService.findParentId(newValue).success(function (response) {
            $scope.itemCat3List = response;
            $scope.entity.tbGoods.typeTemplateId = null;

        })
    });
    //读取模板ID
    $scope.$watch('entity.tbGoods.category3Id', function (newValue, oldValue) {
        itemCatService.findOne(newValue).success(function (response) {
            $scope.entity.tbGoods.typeTemplateId = response.typeId;

        })
    });
    //读取模板ID后，读取品牌列表 扩展属性  规格列表
    $scope.$watch('entity.tbGoods.typeTemplateId', function (newValue, oldValue) {
        typeTemplateService.findOne(newValue).success(function (response) {
            // 模板对象
            $scope.typeTemplate = response;
            //品牌列表类型转换
            $scope.typeTemplate.brandIds = JSON.parse($scope.typeTemplate.brandIds);
            //扩展属性
            if ($scope.entity.tbGoodsDesc.customAttributeItems == null) {
                $scope.entity.tbGoodsDesc.customAttributeItems = JSON.parse($scope.typeTemplate.customAttributeItems);
            } else {
                if ($location.search()['id'] == null) {
                    $scope.entity.tbGoodsDesc.customAttributeItems = JSON.parse($scope.typeTemplate.customAttributeItems);
                }
            }
        });
        //规格的获取
        typeTemplateService.findSpecList(newValue).success(function (response) {
            $scope.specList = response;
        })
    });
    //规格的添加和删除
    $scope.updateSpecAttribute = function ($event, name, value) {
        //查询entity是否存在点击的规格
        //object={"attributeName":"网络制式","attributeValue":["移动3G","移动4G"]}
        var object = $scope.searchObjectByKey($scope.entity.tbGoodsDesc.specificationItems, 'attributeName', name);
        if (object != null) { //规格存在
            if ($event.target.checked) {//勾选状态
                object.attributeValue.push(value);
            } else {//未勾选状态
                //移除选项
                object.attributeValue.splice(object.attributeValue.indexOf(value), 1);
                if (object.attributeValue.length == 0) {//如果选项都取消了，将此规格从entity中移除
                    $scope.entity.tbGoodsDesc.specificationItems.splice(
                        $scope.entity.tbGoodsDesc.specificationItems.indexOf(object), 1);
                }
            }
        } else {//规格不存在
            $scope.entity.tbGoodsDesc.specificationItems.push({"attributeName": name, "attributeValue": [value]});
        }

    }
    //定义搜索对象
	$scope.searchEntity={};
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    //定义审核的集合
    $scope.findAuditStatus = ['未审核', '已审核', '审核未通过', '已关闭'];
    //定义分类的集合
    $scope.findCategroyId = [];
    //查询所有的分类
    $scope.findItemCatList = function () {
        itemCatService.findAll().success(function (response) {
            for (var i = 0; i < response.length; i++) {
                $scope.findCategroyId[response[i].id] = response[i].name;
            }
        })
    }
    //审核修改
    $scope.updateStatus=function (status) {
        goodsService.updateStatus($scope.selectIds,status).success(function (response) {
            if(response.success){
                //重新查询
                $scope.reloadList();//重新加载
                $scope.selectIds=[];//清空集合
            }else{
                alert(response.message);
            }
        })
    }
});	
