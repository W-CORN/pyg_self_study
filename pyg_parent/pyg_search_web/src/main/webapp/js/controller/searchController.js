app.controller("searchController",function ($scope,$location, searchService) {

    $scope.searchMap={'keywords':'','category':'','brand':'','spec':{},'price':'','pageNo':1,'pageSize':40,'sort':'','sortField':''};
    $scope.search=function () {
        $scope.searchMap.pageNo=parseInt($scope.searchMap.pageNo);
        searchService.search($scope.searchMap).success(function (response) {
            $scope.resultMap=response;
            searchPageList();
        })
    };
    $scope.searchMapdele=function () {
        $scope.removeSearchItem("category");
        $scope.removeSearchItem("brand");
        $scope.removeSearchItem("price");
        $scope.removeSearchItem("spec");
        $scope.search();
    }
    //向searchMap中添加数据
    $scope.addsearchMap=function (key,value) {
        if (key=='category'||key=='brand'||key=='price'){
            $scope.searchMap[key]=value;
        }else {
            $scope.searchMap.spec[key]=value;
        }
        $scope.search();
    };
    //删除searchMap中的数据
    $scope.removeSearchItem=function (key) {
        if (key=='category'||key=='brand'||key=='price'){
            $scope.searchMap[key]='';
        }else if(key=='spec') {
            $scope.searchMap[key]={};
        }else {
                delete $scope.searchMap.spec[key];
        }
        $scope.search();
    };
    //页面页码
    searchPageList=function () {
        //构建分页栏
        $scope.pageLabel=[]
        //开始页
        var firstpage=1;
        //截止页数
        var lastpage=$scope.resultMap.totalPages;
        if($scope.resultMap.totalPages>5){
            if($scope.searchMap.pageNo<=3){
                lastpage=5;

            }else if ($scope.searchMap.pageNo>=$scope.resultMap.totalPages-2){
                firstpage=$scope.resultMap.totalPages-4;
            }else{
                firstpage=$scope.searchMap.pageNo-2;
                lastpage=$scope.searchMap.pageNo+2;
            }
        }
        for (var x=firstpage; x<=lastpage;x++){
            $scope.pageLabel.push(x);
        }
    }
    //页码查询
    $scope.queryPage=function (pageNo) {
        $scope.searchMap.pageNo=pageNo;
        $scope.search();//查询
    }
    //判断页码是否大于三
    $scope.ifx=function (pageNo) {
        if (pageNo<=3){
            return false;
        }else {
            return true;
        }
    }
    //判断页码是都小于最大页数-2
    $scope.ify=function (pageNo) {
        if (pageNo>=$scope.resultMap.totalPages-2){
            return false;
        }else {
            return true;
        }
    }
    //排序查询
    $scope.sortSearch=function (sort,sortField) {
        $scope.searchMap.sort=sort;
        $scope.searchMap.sortField=sortField;
        $scope.search();
    }
    //价钱排序查询
    $scope.pricefont=1;
    $scope.pricesortSearch=function () {
        if($scope.pricefont==1){
            $scope.sortSearch('ASC','price');
            $scope.pricefont=2;
        }else if($scope.pricefont==2){
            $scope.sortSearch('DESC','price');
            $scope.pricefont=1;
        }
    }
    //判断查询的索引是否是一个品牌
    $scope.keywordsSearch=function () {
        for (var i=0;i<$scope.resultMap.brandList.length;i++){
            if($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)>=0){
                return true;
            }
        }
        return false;
    }
    //首页链接
    $scope.loadkeywords=function () {
        $scope.searchMap.keywords=$location.search()['keywords']
        $scope.search();
    }
});