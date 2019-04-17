app.controller("baseController",function ($scope) {
    //分页插件
    $scope.reloadList = function () {
        $scope.search($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
    }
    $scope.paginationConf = {
        currentPage: 1,
        totalItems: 10,
        itemsPerPage: 10,
        perPageOptions: [10, 20, 30, 40, 50],
        onChange: function () {
            $scope.reloadList();
        }
    };

    //删除部分数据
    //复选框没法选择
    $scope.selectIds = [];//Id集合
    //复选框更新
    $scope.updateSelection = function ($event, id) {
        if ($event.target.checked) {//如果被选中则添加到数组中
            $scope.selectIds.push(id)
        }else {
            var idx=$scope.selectIds.indexOf(id);
            $scope.selectIds.splice(idx,1);//删除
        }
    }

    $scope.searchObjectByKey=function (list,key,keyValue) {
        //在list集合中根据某key的值查询对象
        for (var i= 0 ;i<list.length;i++ ){
            if (list[i][key]==keyValue){
                return list[i];
            }
        }
        return null;
    }
});