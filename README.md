# mall

## 说明

> 基于SSM的电商系统，包括前台商城系统及后台管理系统。

> 如果该项目对您有帮助，您可以点右上角 "Star" 支持一下,谢谢！

## 前言

`pyg`项目致力于打造一个完整的电商系统，采用现阶段流行技术实现。

## 项目介绍

品优购是一个互联网电商类型网站,模式为时下流行的B2B2C，平台既有自营商品,并且允许第三方网站商家入驻，针对第三方商家信息和商品进行管理审核，通过平台和第三方商家的合作形式，提高网站的整体竞争力。

#### 架构图

##### 系统架构图(配套)

![系统架构图](IMG\品优购架构图(配套).png)

##### 系统架构图(优化)

![系统架构图](IMG\品优购架构图(优化).png)
##### 系统架构关系图

![系统架构关系图](IMG\品优购系统架构关系图.png)

### 技术选型

AngularJS+SSM+Dubbo+Zokeeper+Solr+Redis+Mysql+MongoDB+IDE+JDK8

#### 开发工具

工具 | 说明 
----|----
IDEA | 开发IDE
RedisDesktop | redis客户端连接工具 
SwitchHosts| 本地host管理 
Axure | 原型设计工具
MindMaster | 思维导图设计工具
ScreenToGif | gif录制工具
ProcessOn | 流程图绘制工具
PicPick | 屏幕取色工具 

#### 开发环境

工具 | 版本号 
----|----
JDK | 1.8 
Mysql | 5.7
Redis | 3.2 
RabbitMq | 3.7.14
nginx | 1.10 

### 技术描述

#### Maven

使用Maven工具完成项目的管理和打包等,并对项目依赖的进行了规范化的管理。
#### MavenProfile

使用MavenProfile实现开发/生产环境参数的动态切换。
#### Dubbo+zookeeper

使用dubbo搭建分布式系统,使用zokeeper作为服务的注册中心，实现了项目的分离和服务远程调用。

#### AngularJS

使用AngularJS的实现异步请求,使用数据双向绑定机制完成页面数据的刷新。

#### SpringSecurity

基于RBAC(Role-Base Access Controller)进行了权限模块的设计,在DB中存储了用户-角色-权限的基本数据,通过SpringSecurity实现系统的权限控制。

#### Fastdfs

使用FastDFS分布式文件系统,实现系统海量文件的分布式存储，提高了系统文件管理的独立性。

#### Redis

使用Redis缓存系统的高频数据（广告信息/商品分类/模板/规格等信息/秒杀商品和订单）,提高了数据的加载速度,并缓解了数据库访问的压力。

#### Solr

在Solr搜索服务器建立商品信息的索引库,实现商品的多样化搜索,并有效提高了商品搜索的效率和准确性。

#### FreeMarker

使用FreeMarker技术进行商品详情页静态化处理,提高了页面的访问速度,减少了数据库的访问压力。

#### ActiveMQ

使用ActiveMQ实现系统间的解耦,提高了系统的扩展性和服务器的响应速度。

#### SpringBoot

使用SpringBoot搭建独立的微服务短信系统,并集成阿里大于的云短信接口实现系统短信发送功能。

#### CAS

使用CAS技术实现分布式系统之间单点登录功能，并且与SpringSecurity框架整合实现单点登录和权限控制。

#### SpringMVC跨域

基于SpringMVC实现CORS式的跨域请求,解决了分布式系统间JS的跨域请求问题。

#### 微信扫码支付

集成微信扫码支付SDK,实现订单支付功能。

#### SpringTask

使用SpringTask实现定时任务(缓存数据的同步,solr服务器的数据更新和秒杀商品的增量更新和过期商品的移除)的处理。

#### MongoDB

使用MongoDB数据库缓存商品评论信息和收藏商品信息,提高了大批量数据的快速存储和访问。




