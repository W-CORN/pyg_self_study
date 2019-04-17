app.controller("contentController",function ($scope,contentService) {

    $scope.contentList=[];
    $scope.findByCategoryId=function(categoryId){
        contentService.findByCategoryId(categoryId).success(
            function(response){
                $scope.contentList[categoryId]=response;
            }
        );
    }
    $scope.search=function () {
        location.href="http://localhost:9104#?keywords="+$scope.keywords;
    }
})