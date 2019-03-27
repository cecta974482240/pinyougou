/** 定义控制器层 */
app.controller('userController', function($scope, $timeout, $controller,$location,baseService){
    // 继承baseController
    $controller('baseController', {$scope:$scope});
    // 定义json对象
    $scope.user = {};

    // 用户注册
    $scope.save = function () {

        // 判断密码是否一致
        if ($scope.okPassword && $scope.user.password == $scope.okPassword){
            // 发送异步请求
            baseService.sendPost("/user/save?code=" + $scope.code,
                $scope.user).then(function(response){
                // 获取响应数据
                if (response.data){
                    // 跳转到登录页面
                    // 清空表单数据
                    $scope.user = {};
                    $scope.okPassword = "";
                    $scope.code = "";
                }else{
                    alert("注册失败！");
                }
            });
        }else{
            alert("两次密码不一致！");
        }
    };




    // 定义显示文本
    $scope.tipMsg = "获取短信验证码";
    $scope.flag = false;

    // 发送短信验证码
    $scope.sendSmsCode = function () {

        // 判断手机号码的有效性
        if ($scope.user.phone && /^1[3|4|5|7|8|9]\d{9}$/.test($scope.user.phone)){
            // 发送异步请求
            baseService.sendGet("/user/sendSmsCode?phone="
                + $scope.user.phone).then(function(response){
                    // 获取响应数据
                    if (response.data){
                        // 倒计时 (扩展)
                        $scope.flag = true;
                        // 调用倒计时方法
                        $scope.downcount(90);
                    }else{
                        alert("获取短信验证码失败！");
                    }
            });
        }else{
            alert("手机号码不正确！");
        }
    };


    // 倒计时方法
    $scope.downcount = function (seconds) {
        if (seconds > 0) {
            seconds--;
            $scope.tipMsg = seconds + "秒，后重新获取";

            // 开启定时器
            $timeout(function () {
                $scope.downcount(seconds);
            }, 1000);
        }else{
            $scope.tipMsg = "获取短信验证码";
            $scope.flag = false;
        }
    };
    
//----------------------------------------------------------------------m-

    // 查询当前用户信息
    $scope.findUser = function () {
        baseService.sendGet("/user/findUser").then(function (response) {
            $scope.userList = response.data;
            /** 手机号码转换 */
            var phone = $scope.userList.phone;
            var reg = /^(\d{3})\d*(\d{4})$/;
            $scope.starPhone = phone.replace(reg,'$1****$2')
        });
    };

    // 定义显示文本
    $scope.msge = "发送";
    $scope.stop = false;
    // 安全管理 发送短信验证码 第一步
    $scope.sendCodeOne = function () {
        // 判断手机号码的有效性
        if ($scope.userList.phone && /^1[3|4|5|7|8|9]\d{9}$/.test($scope.userList.phone)){
            // 发送异步请求
            baseService.sendGet("/user/sendSmsCode?phone="
                + $scope.userList.phone).then(function(response){
                // 获取响应数据
                if (response.data){
                    // 倒计时 
                    $scope.stop = true;
                    // 调用倒计时方法
                    $scope.count(90);
                }else{
                    alert("获取短信验证码失败！");
                }
            });
        }else{
            alert("手机号码不正确！");
        }
    };

    // 安全管理 发送短信验证码 第二步
    $scope.sendCodeTwo = function () {
        // 判断手机号码的有效性
        if ($scope.user.phone && /^1[3|4|5|7|8|9]\d{9}$/.test($scope.user.phone)){
            // 发送异步请求
            baseService.sendGet("/user/sendSmsCode?phone="
                + $scope.user.phone).then(function(response){
                // 获取响应数据 
                if (response.data){
                    // 倒计时 
                    $scope.stop = true;
                    // 调用倒计时方法
                    $scope.count(90);
                }else{
                    alert("获取短信验证码失败！");
                }
            });
        }else{
            alert("手机号码不正确！");
        }
    };

    // 倒计时方法
    $scope.count = function (seconds) {
        if (seconds > 0){
            seconds --;
            $scope.msge = seconds + "秒后重新获取";
            // 开启定时器
            $timeout(function () {
                $scope.count(seconds)
            },1000)
        }else {
            $scope.msge = "发送";
            $scope.flag = false;
        }
    };

    // 安全管理 检验短信验证码 第一步
    $scope.oneCheckSmsCode = function(){
        baseService.sendPost("/user/oneCheckSmsCode?smsCode="
            + $scope.smsCode, $scope.userList)
            .then(function(response){
                // 判断是否输入验证码
                if ($scope.smsCode){
                    if (response.data){
                        $scope.smsCode = "";
                        alert("验证成功！");
                        location.href = "home-setting-address-phone.html";
                    }else{
                        $scope.smsCode = "";
                        alert("请输入有效的验证码！");
                    }
                }else {
                    alert("请输入验证码!")
                }
            });
    };

    // 安全管理 检验短信验证码 第二步, 修改绑定的号码
    $scope.twoCheckSmsCode = function(){
        // 判断手机号码的有效性
        if ($scope.user.phone && /^1[3|4|5|7|8|9]\d{9}$/.test($scope.user.phone)) {
            baseService.sendPost("/user/twoCheckSmsCode?smsCode="
                + $scope.smsCode, $scope.user)
                .then(function (response) {
                    // 判断验证码是否输入
                    if ($scope.smsCode ) {
                        if (response.data) {
                            $scope.smsCode = "";
                            $scope.user.phone = "";
                            alert("绑定成功！");
                            // 跳转页面
                            location.href = "home-setting-address-complete.html";
                        } else {
                            $scope.smsCode = "";
                            alert("请输入有效的验证码！");
                        }
                    } else {
                        alert("请输入验证码!")
                    }
                });
        }else {
            $scope.user.phone = "";
            $scope.smsCode = "";
            alert("请输入有效的手机号码！");
        }
    };



    // 密码设置
    $scope.savePasswordOrNickname = function () {
        // 判断密码是否一致
        if ($scope.password && $scope.user.password == $scope.password){
            // 发送异步请求
            baseService.sendPost("/user/savePasswordOrNickname", $scope.user).then(function(response){
                // 获取响应数据
                if (response.data){
                    // 清空表单数据
                    $scope.user.password = "";
                    $scope.password = "";
                    alert("更改成功!");
                    // 跳转到登录页面
                    location.href = "http://sso.pinyougou.com/logout?service=" + $scope.redirectUrl;
                }else{
                    alert("更改失败！");
                }
            });
        }else{
            alert("两次密码不一致！");
        }
    };

});