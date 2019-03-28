/** 定义控制器层 */
app.controller('settingController', function ($scope, $controller, baseService) {
    /** 指定继承baseController */
    $controller('indexController1', {$scope: $scope});

    // 定义获取登录用户名的方法
    $scope.loadUsername = function () {

        // 对请求URL进行unicode编码
        $scope.redirectUrl = window.encodeURIComponent(location.href);

        // 发送异步请求
        baseService.sendGet("/user/showName").then(function(response){
            // 获取响应数据
            $scope.loginName = response.data.loginName;
        });
    };


    $scope.user = {};

    $scope.jobArr= ['程序员','产品经理','UI设计师']

    /** 保存用户信息 */
    $scope.updateMessage = function () {
        baseService.sendPost("/user/updateMessage?", $scope.user).then(function (response) {
            if (response.data) {
                alert("保存成功")
            } else {
                alert("保存失败")
            }
        })
    }

    // 图片上传
    $scope.upload = function () {
        baseService.uploadFile().then(function (response) {
            // 获取响应数据: {status : 200|500, url : ''}
            if (response.data.status == 200){
                $scope.user.headPic = response.data.url;
            }
        });
    };

    /** 查询省份 */
    $scope.findProvince = function () {
        baseService.sendGet("/user/findProvinceid").then(function (response) {
            $scope.entity = response.data;
        });
    }

    /** 查询城市 */
    $scope.findCityById = function (provinceId) {
        baseService.sendGet("/user/findCityById?provinceId=" + provinceId)
            .then(function (response) {
                $scope.city = response.data;
            });
    }

    /** 查询城区 */
    $scope.findAreaById = function (cityId) {
        baseService.sendGet("/user/findAreaById?cityId=" + cityId)
            .then(function (response) {
                $scope.areas = response.data;
            });
    }

    /** 监控 user.address.provinceId 变量,查询城市 */
    $scope.$watch('user.address.provinceId', function (newValue, oldValue) {
        $scope.findCityById(newValue);
    })

    /** 监控 user.address.provinceId 变量,查询城市 */
    $scope.$watch('user.address.cityId', function (newValue, oldValue) {
        $scope.findAreaById(newValue);
    })
});