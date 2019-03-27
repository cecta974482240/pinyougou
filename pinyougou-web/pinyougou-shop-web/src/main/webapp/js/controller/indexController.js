// 定义运营商后台首页控制器
app.controller('indexController', function ($scope, baseService) {

    // 获取登录用户名
    $scope.showLoginName = function () {
        baseService.sendGet("/showLoginName").then(function (response) {
            // 获取响应数据
            $scope.loginName = response.data.loginName;
            $scope.sellerInfo(response.data.loginName);

        });
    };
//初始化商家信息
    $scope.sellerInfo = {};
    $scope.sellerInfo = function (loginName) {
        baseService.sendGet("/seller/sellerInfo?sellerId=" + loginName).then(function (response) {
            $scope.sellerInfo = response.data;
        })
    };
    //修改商家信息可编辑按钮
    var selected = true;
    $scope.isDisabled = true;
    $scope.isDisable = function () {
        if ($scope.newPassword != $scope.okPassword) {
            alert("密码不一致!");
            return;
        }
        if (selected) {
            $scope.isDisabled = false;
            selected = !selected;
            $scope.isSubmit = selected;
        } else {
            $scope.isDisabled = true;
            selected = !selected;
        }
    };
//修改商家修改信息
    $scope.submitInfo = function () {
        baseService.sendPost("/seller/savesellerInfo", $scope.sellerInfo)
            .then(function (response) {
                if (response.data) {
                    alert("修改成功！");
                    $scope.isDisable();
                } else {
                    alert("修改失败！");
                }
            });
    };
    $scope.newPassword = '';
    $scope.okPassword = '';
    $scope.oldPassword = '';
    /** 修改商家密码*/
    $scope.alterPassword = function () {
        if ($scope.newPassword == $scope.okPassword && $scope.okPassword != null) {
            $scope.sellerInfo.password = $scope.newPassword;
            baseService.sendPost("/seller/updatePassword?oldPassword=" + $scope.oldPassword, $scope.sellerInfo).then(function (response) {
                if (response.data) {
                    alert("修改成功!");
                    $scope.newPassword = '';
                    $scope.okPassword = '';
                    $scope.oldPassword = '';
                    location.href="/logout";
                } else {
                    alert("修改失败!");
                }
            })
        } else {
            alert("密码不一致!");
        }
    };
    /**鼠标失去焦点事件 */
    $scope.blur = function (pw) {
        if (!pw) {
            alert("密码不为空");
        }
    }
});