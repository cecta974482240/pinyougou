/** 定义控制器层 */
app.controller('indexController', function($scope, $interval,$location,baseService){

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


    // 定义json对象封装搜索条件
    $scope.page = 1;
    $scope.rows = 4;

    $scope.findOrder = function () {
               baseService.sendGet("/user/findByPage?page="+$scope.page+"&rows="+$scope.rows).then(function (response) {
                    $scope.dataList  = response.data;
                      $scope.initPageNums();
               })
    };




    $scope.zt =['未付款','已付款','未发货','已发货','交易成功','交易关闭','待评价'];





    // 定义初始化页码的方法
    $scope.initPageNums = function () {
        // 页码数组
        $scope.pageNums = [];

        // 开始页码
        var firstPage = 1;
        // 结束页码
        var lastPage = $scope.dataList.lastPage;

        // 前面加点
        $scope.firstDot = true;
        // 后面加点
        $scope.lastDot = true;

        // 判断总页数是不是大于5
        if ($scope.dataList.pages > 5){

            // 当前页码靠首页近些
            if ($scope.page <= 3){
                lastPage = 5;
                $scope.firstDot = false;
            }else if($scope.page >= $scope.dataList.pages - 3){
                // 当前页码靠尾页近些
                firstPage = $scope.dataList.pages - 4;
                $scope.lastDot = false;
            }else{
                // 在中间
                firstPage = $scope.page - 2;
                lastPage = $scope.page + 2;
            }
        }else{
            // 前面加点
            $scope.firstDot = false;
            // 后面加点
            $scope.lastDot = false;
        }

        for (var i = firstPage; i <= lastPage; i++){
            $scope.pageNums.push(i);
        }

    };


    // 分页搜索
    $scope.pageSearch = function (page) {

        page = parseInt(page);

        // 判断页码的有效性
        if (page >= 1 && page <= $scope.dataList.pages
            && page != $scope.page){
            $scope.page = page;
            $scope.jumpPage = page;
            // 执行搜索
            $scope.findOrder();
        }
    };



    // 生成微信支付二维码
    $scope.genPayCode = function () {
        // 发送异步请求
        baseService.sendGet("/order/genPayCode?orderId="+$scope.orderId).then(function(response){
            // 获取响应数据 {outTradeNo : '', money : 100, codeUrl : ''}
            // 获取交易订单号
            $scope.outTradeNo = response.data.outTradeNo;
            // 获取支付金额
            $scope.money = (response.data.totalFee / 100).toFixed(2);
            // 获取支付URL
            $scope.codeUrl = response.data.codeUrl;

            // 生成二维码
            document.getElementById("qrious").src = "/barcode?url=" + $scope.codeUrl;


            /**
             * 开启定时器(间隔3秒发送异步请求，获取支付状态)
             * 第一个参数：调用回调的函数
             * 第二个参数：时间毫秒数 3秒
             * 第三个参数：总调用次数 100次
             */
            var timer = $interval(function () {
                // 发送异步请求
                baseService.sendGet("/order/queryPayStatus?outTradeNo="
                    + $scope.outTradeNo+"%orderId="+$scope.orderId).then(function(response){
                    // 获取响应数据 {status : 1|2|3} 1: 支付成功、2:未支付、3:支付失败
                    if (response.data.status == 1){ // 支付成功
                        // 取消定时器
                        $interval.cancel(timer);
                        // 跳转到支付成功页面
                        location.href = "/order/paysuccess.html?money=" + $scope.money;
                    }
                    if (response.data.status == 3){// 支付失败
                        // 取消定时器
                        $interval.cancel(timer);
                        // 跳转到支付失败页面
                        location.href = "/order/payfail.html";
                    }
                });
            }, 3000, 100);

            // 在总次数调用完之后，回调一个指定函数
            timer.then(function(){
                // 提示信息
                $scope.tip = "二维码已过期，刷新页面重新获取二维码。";
            });
        });
    };



     $scope.orderId = "";
     $scope.getOrderId = function () {
        $scope.orderId = $location.search().orderId;
     };



        // 获取支付金额
        $scope.getMoney = function () {
            return $location.search().money;
        };


    $scope.saveOrder = function (orderId) {
          location.href = "/order/pay.html?orderId="+orderId;
    };

});