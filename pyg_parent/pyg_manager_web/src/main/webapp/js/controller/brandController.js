 //控制层 
app.controller('brandController' ,function($scope,$controller   ,uploadService,brandService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		brandService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		brandService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		brandService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}

    //添加和修改数据
    $scope.save = function () {
        var method = null;
        if ($scope.entity.id != null) {
            method=brandService.saveUpdateOne($scope.entity);
        }else {
            method=brandService.saveAddone($scope.entity);
        }
        method.success(function (response) {
            if (response.success) {
                $scope.reloadList();//重新加载
            } else {
                alert(response.message);
            }
        });
    }
	
	 
	//批量删除 
	$scope.delectByIds=function(){
		//获取选中的复选框			
		brandService.delectByIds( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		brandService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    $scope.entity={url:{}};
    //上传广告图
    $scope.updateImags=function () {
        uploadService.uploadFile().success(function (response) {
            if(response.success){
                alert("上传成功")
                $scope.entity.url=response.message;
            }else {
                alert("上传失败")
            }
        }).error(
            function () {
                alert("上传出错")
            }
        )
    }
    
});	
