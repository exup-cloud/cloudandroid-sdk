**用户Android端定制指导**

###1.修改applicationId:

![image-20180702175427857](/var/folders/vb/hcm5bqwx3cl9451jrwbt3t6c0000gn/T/abnerworks.Typora/image-20180702175427857.png)

### 2.修改应用Icon：

在app/res/drawable目录中，找到**ic_launcher目录**，替换里面的图片。

尺寸说明：

1. 72*72 ——> hdpi
2. 96*96 ——> xhdpi
3. 144*144——> xxhdpi
4. 192*192 ——>xxxhdpi

![image-20180702180228694](/var/folders/vb/hcm5bqwx3cl9451jrwbt3t6c0000gn/T/abnerworks.Typora/image-20180702180228694.png)

### 3. 修改应用名称

![image-20180702180749298](/var/folders/vb/hcm5bqwx3cl9451jrwbt3t6c0000gn/T/abnerworks.Typora/image-20180702180749298.png)

修改这里()：

![image-20180702190502598](/var/folders/vb/hcm5bqwx3cl9451jrwbt3t6c0000gn/T/abnerworks.Typora/image-20180702190502598.png)

**注意修改：*三个strings文件*

### 4.验证证书

1. 证书所在目录：assets目录，如下图

   ![image-20180702201330324](/var/folders/vb/hcm5bqwx3cl9451jrwbt3t6c0000gn/T/abnerworks.Typora/image-20180702201330324.png)

2. 网络请求验证证书：

   初始化httpclient就在 app/java/net/httpClient文件中的**initOkHttpClient()**方法中。

   ![image-20180702201633237](/var/folders/vb/hcm5bqwx3cl9451jrwbt3t6c0000gn/T/abnerworks.Typora/image-20180702201633237.png)

### 5.修改URL和WebSocket地址

 1. 所在位置：app/java/net/api/ApiConstants;

 2. 修改BASE_URL和SOCKET_ADDRESS即可(PS:注意反斜线)

 3. ![image-20180702202059704](/var/folders/vb/hcm5bqwx3cl9451jrwbt3t6c0000gn/T/abnerworks.Typora/image-20180702202059704.png)

    ​

