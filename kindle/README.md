# 简介  
阅读APP网页版的kindle适配  
也相当于写了一套旧内核的简易开发框架，看上的可以搬去写点旧设备的简单页面  
为爱发电   
回馈社区   
如有bug   
多多包涵     

# 困难  
kindle浏览器内核老旧，许多新东西用不了  
个人kindle缓存限制大小为2600000字节，约2.48M  
不支持元素滚动条  
不支持Multiple-column多列布局  
不支持calc样式计算  
性能低下，大量数据处理时卡顿  
kindle浏览器自带无法取消的滚动条  
kindle浏览器输入以及双击会强制缩放  
未进行逐字排版，难以计算进度
滚动条分页+自定义行距字号易断字  
github被墙，没有免费的

# 解决的问题  
放弃滚动翻页，尝试点击切屏  
放弃css自动分页，尝试滚动条分页  
骚操作排版防止断字  
骚操作计算进度，与app同步进度  
目录改为虚拟分页，提高页面性能

# 使用方法  

## 在线试用  
试用渠道（资源有限仅供检测设备可用性，不稳定请轻薅）：  
[github pages试用渠道](https://cyx7788414.github.io/kabi-novel.html)  
试用成功后，有能力的朋友，请自行部署kabi-novel.html文件至个人域名或本地内网以免丢失缓存  
如果有大佬开放了免费公众服务，可以与我联系更新文档    
### 优点  
不会丢失缓存   
### 缺点  
初始化时需要联网  

## 静态使用  
将dist目录中的kabi-novel.html拷入kindle根目录  
启动kindle浏览器，地址栏输入file:///mnt/us/kabi-novel.html  
可自行更名，添加书签  
### 优点    
无需外网，可内网使用  
### 缺点  
每次关闭浏览器后缓存丢失，下次使用需要重新配置  

## 使用流程  
初始化时在配置页输入服务端地址，并进行校验  
在配置页检测缓存容量，以获得容量告警  
在配置页调整字号行距（仅对小说正文有效）  
进入书架，初始化时需手动点击刷新才有数据（为了避免自动刷新误删本地缓存）  
点击书籍，开始阅读  
点击目录，可跳章，管理本书缓存  

## 说明  
保持与服务端联网，可以同步进度  
离线时进度保存在本地，再次联网刷新书架时，本地与服务端之间进度根据时间戳匹配，取其新者  
每次翻页/跳章时尝试联网保存进度  

# 仍然存在的问题  
配置页输入服务端地址时仍然会缩放，需要手动恢复  
快速点击多次仍然会触发双击自动缩放，需要手动恢复  
配置页检测缓存容量仍然卡顿  
无法取消的右侧滚动条  
