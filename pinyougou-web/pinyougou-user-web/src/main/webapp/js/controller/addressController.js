/** 定义控制器层 */
app.controller('addressController', function ($scope, baseService) {

    $scope.address = {"userId": ''};
    // 获取登录用户名
    $scope.showName = function () {
        baseService.sendGet("/user/showName").then(function (response) {
            // 获取响应数据
            $scope.loginName = response.data.loginName;
            $scope.address.userId = $scope.loginName;
            //获取用户的地址集合
            $scope.showAddress(response.data.loginName);
        });
    };

    //初始化$scope.adderss
    $scope.removeAdderss=function () {
        $scope.address={};
        $scope.address.userId = $scope.loginName;
    }
    //地址别名
    $scope.updataAlias = function (alias) {
        $scope.address.alias = alias;
    };

    //初始化获取用户地址
    $scope.showAddress=function (loginName) {
        baseService.sendGet("/address/showAddress?loginName="+loginName).then(function (response) {
            $scope.addressList = response.data;
        })
    }

    $scope.reload=function () {
        $scope.showAddress($scope.address.userId);
    };

    //一级分类地址-1
    $scope.findAllProvinces = function () {
        baseService.sendGet("/address/findAllProvinces").then(function (response) {
            $scope.itemCatList1 = response.data;
        })
    };


    //监控ProvincesId变量,调动二级分类
    $scope.$watch("address.provinceId", function (newValue) {
        if (newValue) {
            $scope.findCitiesByParentId(newValue);
        } else {
            $scope.itemCatList2 = [];
        }
    });


    //二级分类
    $scope.findCitiesByParentId = function (newValue) {
        baseService.sendGet("/address/findCitiesByParentId?parentId=" + newValue).then(function (response) {
            $scope.itemCatList2 = response.data;
        });
    };

    //监控cityId变量,调用三级分类
    $scope.$watch("address.cityId", function (newValue) {
        if (newValue) {
            $scope.findAreaByParentId(newValue);
        } else {
            $scope.itemCatList3 = [];
        }
    });

    //三级分类
    $scope.findAreaByParentId = function (newValue) {
        baseService.sendGet("/address/findAreaByParentId?parentId=" + newValue).then(function (response) {
            $scope.itemCatList3 = response.data;
        });
    };
    //添加新地址
    $scope.insertAddress = function (address) {
        var url = 'insertAddress';
        if(address.id){
            url = 'updateAddress';
        }
        baseService.sendPost("/address/"+url,address).then(function (response) {
            if(response.data){
                alert("修改成功!");
                $scope.reload();
            }else {
                alert("修改失败!");
            }
        })
    };
    //删除地址
    $scope.deleteAddress = function (id) {
        baseService.sendGet("/address/deleteAddress?id="+id).then(function (response) {
            if(response.data){
                alert("删除成功!");
                $scope.reload();
            }else {
                alert("删除失败!");
            }
        })
    };

    //编辑买家地址-获取地址
    $scope.updateAddress=function (id) {
        baseService.sendGet("/address/getAddress?id="+id).then(function (response) {
            if(response.data){
                $scope.address = response.data;
                $scope.address = JSON.parse(JSON.stringify($scope.address));
            }else {
                alert("修改失败!");
            }
        })
    };
    //修改地址默认状态
    $scope.selectDefault=function (id) {
        baseService.sendGet("/address/selectDefault?id="+id).then(function (response) {
            if(response.data){
                alert("修改成功!");
                $scope.reload();
            }else {
                alert("修改失败!");
            }
        })
    }
});