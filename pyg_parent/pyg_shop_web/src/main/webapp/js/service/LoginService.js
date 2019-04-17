app.service("loginService",function ($http) {
    this.loginname=function () {
        return $http.get("../login/name.do");
    }
})