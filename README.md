# fuint会员营销系统介绍

#### 介绍
fuint会员营销系统是一套开源的实体店铺会员管理和营销系统。系统基于前后端分离的架构，后端采用<b>Java SpringBoot</b> + <b>Mysql</b>，前端基于当前流行的<b>Uniapp</b>，<b>Element UI</b>，支持小程序、h5。主要功能包含电子优惠券、储值卡、集次卡（计次卡）、短信发送、储值卡、会员积分、会员等级权益体系，支付收款等会员日常营销工具。本系统适用于各类实体店铺，如酒吧、酒店、汽车4S店、鲜花店、甜品店、餐饮店等，是实体店铺会员营销必备的一款利器。
以下是前台的页面展示：
<p><img src="https://fuint-cn.oss-cn-shenzhen.aliyuncs.com/screenshots/g1.png?v=1" alt="前台页面1"></p>
<p><img src="https://fuint-cn.oss-cn-shenzhen.aliyuncs.com/screenshots/g2.png?v=1" alt="前台页面2"></p>
<p><img src="https://fuint-cn.oss-cn-shenzhen.aliyuncs.com/screenshots/g3.png?v=1" alt="前台页面3"></p>
<p><img src="https://fuint-cn.oss-cn-shenzhen.aliyuncs.com/screenshots/g4.png?v=1" alt="前台页面4"></p>
<p><img src="https://fuint-cn.oss-cn-shenzhen.aliyuncs.com/screenshots/g5.png?v=1" alt="前台页面4"></p>

fuint侧重于线下实体店的私域流量的运营，帮助商户降低获客成本。顾客通过扫码支付成为私域流量，支付即可成为会员。积分和卡券功能建立起会员等级体系，通过消息推送和短信营销方便触达用户。
<p>1、会员运营自动化：商家通过日常活动设置，如开卡礼设置，沉睡唤醒等，成为会员后自动给顾客送优惠券，让顾客更有黏性，提升会员运营效率。</p>
<p>2、打通收银系统和会员营销的壁垒，代客下单收银，支付即成为会员。</p>
<p>3、会员体系完整化：积分兑换、积分转赠、会员等级权益、积分加速、买单折扣。</p>
<p>4、会员卡券齐全：储值卡、电子券、优惠券、集次卡、计次卡、会员余额支付。</p>
<p>5、线上代客下单收银系统，后台管理员可帮助临柜的会员下单、扫码支付。</p>
<p>6、支持手机短信、站内弹框消息、微信订阅消息：支持包括发货消息、卡券到期提醒、活动提醒、会员到期提醒、积分余额变动提醒等消息。</p>
<p>小程序前端仓库：https://gitee.com/fuint/fuint-uniapp</p>
<b>扫码小程序演示：</b><br>
<p><img src="https://fuint-cn.oss-cn-shenzhen.aliyuncs.com/screenshots/miniapp.png" alt="小程序演示"></p>
<br>
<b>官网演示地址：</b><br>
<p>
   1、官网：<a target="_blank" href="https://www.fuint.cn">https://www.fuint.cn</a> 点击 -> 系统演示，演示账号：fuint / 123456<br>
   2、swagger接口文档：<a target="_blank" href="https://www.fuint.cn/fuint-application/swagger-ui.html">https://www.fuint.cn/fuint-application/swagger-ui.html</a>
</p>

#### 软件架构
后端：JAVA SpringBoot + MYSQL Mybatis Plus + Redis
前端：采用基于Vue的Uniapp、Element UI，前后端分离，支持微信小程序、h5等
<p>后台截图：</p>
<p><img src="https://fuint-cn.oss-cn-shenzhen.aliyuncs.com/screenshots/login.png?v=fuint" alt="登录界面"></p>
<p><img src="https://fuint-cn.oss-cn-shenzhen.aliyuncs.com/screenshots/homeV2.png?v=fuint" alt="首页"></p>

前端使用技术<br>
2.1 Vue2<br>
2.2 Uniapp<br>
2.3 Element UI

后端使用技术<br>
1.1 SpringBoot 2.5<br>
1.2 Mybatis Plus<br>
1.3 Maven<br>
1.4 SpringSecurity<br>
1.5 Druid<br>
1.6 Slf4j<br>
1.7 Fastjson<br>
1.8 JWT<br>
1.9 Redis<br>
1.10 Quartz<br>
1.11 Mysql 5.8<br>
1.12 Swagger UI<br>


#### 安装步骤
推荐软件环境版本：jdk 1.8、mysql 5.8
1. 导入db目录下的数据库文件。
2. 修改config目录下的配置文件。
3. 将工程打包，把jar包上传并执行。
<p>提示：无后端和linux基础的朋友，可以使用<b>宝塔</b>部署，非常方便简单。</p>


#### 前台使用说明

1.  会员登录，登录成功后可看到会员的卡券列表。
2.  卡券领取和购买，预存券的充值等。
3.  核销卡券，会员在前台出示二维码，管理员用微信扫一扫即可核销。
4.  卡券转赠，会员可将自己的卡券转赠给其他用户，输入对方的手机号即可完成转赠，获赠的好友会收到卡券赠送的短信。

<p><img src="https://fuint-cn.oss-cn-shenzhen.aliyuncs.com/screenshots/create.png?v=fuint" alt="卡券创建界面"></p>
<p><img src="https://fuint-cn.oss-cn-shenzhen.aliyuncs.com/screenshots/member.png?v=fuint" alt="卡券创建界面"></p>

#### 后台使用
1.  会员管理：会员新增、导入、禁用等。
2.  卡券管理：电子券管理为2层结构，即电子券组和电子券。
3.  会员积分：会员积分管理，会员积分的操作，会员积分明细查看。
4.  转赠管理：卡券转赠记录。
5.  短信管理：短信营销功能，已发送的短信列表。
6.  系统配置：配置系统管理员权限等。
7.  店铺管理：支持多店铺模式。
8.  核销管理员:核销人员管理主要包含3个功能：核销人员列表、核销人员审核、核销人员信息编辑。
9.  短信模板管理：可配置不同场景和业务的短信内容。
10. 卡券发放：单独发放、批量发放，发放成功后给会员发送短信通知
11. 操作日志主要针对电子券系统后台的一些关键操作进行日志记录，方便排查相关操作人的行为等问题。
12. 发券记录主要根据发券的实际操作情况来记录，分为单用户发券和批量发券，同时可针对该次发券记录进行作废操作。
13.代客下单收银功能。
<p>卡券营销：</p>
<p><img src="https://fuint-cn.oss-cn-shenzhen.aliyuncs.com/screenshots/coupon-list.png?v=fuint" alt="卡券列表"></p>

<p>收银代客下单功能：店员角色登录后台，从首页的“下单首页”菜单可进入代客收银下单界面，完成代客下单收银的流程。</p>
<p><img src="https://fuint-cn.oss-cn-shenzhen.aliyuncs.com/screenshots/cashier.png?v=fuint3.0.2" alt="收银界面"></p>
<p>发起结算：</p>
<p><img src="https://fuint-cn.oss-cn-shenzhen.aliyuncs.com/screenshots/cashier-1.png?v=fuint3.0.2" alt="收银结算"></p>

#### 开发计划
1.  完善的报表统计
2.  接入支付宝支付
3.  分享助力、分享领券、分享获得积分
4.  更多营销工具...


#### 允许使用范围：
1.  允许个人学习使用
2.  允许用于毕业设计、论文参考代码
3.  推荐Watch、Star项目，获取项目第一时间更新，同时也是对项目最好的支持
4.  希望大家多多支持原创软件
5.  请勿去除版权标签，要商用请购买源码授权（非常便宜），感谢理解！

不足和待完善之处请谅解！源码仅供学习交流，更多功能欢迎进群咨询讨论，或需安装帮助请联系我们（<b>麻烦先点star！！！！！！</b>）。<br>
官方网站：https://www.fuint.cn <br>
开源不易，感谢支持！<br>
<b>作者wx：fsq_better：</b><br>
<p><img src="https://fuint-cn.oss-cn-shenzhen.aliyuncs.com/screenshots/qr.png" alt="公众号二维码"></p>


特别鸣谢：<br>
Mybaits Plus: https://github.com/baomidou/mybatis-plus<br>
Vue: https://github.com/vuejs/vue<br>
Element UI: https://element.eleme.cn