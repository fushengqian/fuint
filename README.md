# fuint会员营销系统介绍

#### 介绍
fuint会员营销系统是一套开源的实体店铺会员管理和营销系统。系统基于前后端分离的架构，后端采用<b>Java</b> + <b>Mysql</b>，前端基于当前流行的<b>Uniapp</b>，支持小程序、h5。主要功能包含电子优惠券、预存卡、集次卡（计次卡）、短信发送、储值卡、会员积分、会员等级权益体系，支付收款等会员日常营销工具。本系统适用于各类实体店铺，如酒吧、酒店、汽车4S店、鲜花店、甜品店、餐饮店等，是实体店铺会员营销必备的一款利器。
以下是前台的页面展示：
<p><img src="screenshots/g1.png" alt="卡券前台页面1"></p>
<p><img src="screenshots/g2.png" alt="卡券前台页面2"></p>
<p><img src="screenshots/g3.png" alt="卡券前台页面3"></p>
<p><img src="screenshots/g4.png" alt="卡券前台页面4"></p>

fuint侧重于线下实体店的私域流量的运营，帮助商户降低获客成本。顾客通过扫码支付成为私域流量，支付即可成为会员。积分和卡券功能建立起会员等级体系，通过消息推送和短信营销方便触达用户。
<p>1、会员运营自动化：商家通过日常活动设置，如开卡礼设置，沉睡唤醒等，成为会员后自动给顾客送优惠券，让顾客更有黏性，提升会员运营效率。</p>
<p>2、打通收银系统和会员营销的壁垒，代客下单收银，支付即成为会员。</p>
<p>3、会员体系完整化：积分、会员等级权益、积分加速、买单折扣。</p>
<p>小程序前端仓库：https://gitee.com/fuint/fuint-uniapp</p>
<b>扫码小程序演示：</b><br>
<p><img src="screenshots/miniapp.png" alt="小程序演示"></p>
<br>
<b>后台管理演示地址：</b><br>
<p>
   <a target="_blank" href="https://www.fuint.cn">https://www.fuint.cn</a>
   账号:fuint / 123456
</p>

#### 软件架构
后端：JAVA（springBoot + mybatis） + MYSQL + Redis
前端：采用基于Vue的uni-app，前后端分离，支持微信小程序、支付宝小程、h5等
<p>后台截图：</p>
<p><img src="screenshots/login.png" alt="登录界面"></p>
<p><img src="screenshots/homeV2.png" alt="首页"></p>

前端使用技术<br>
2.1 Vue<br>
2.3 uniapp


后端使用技术<br>
1.1 SpringBoot 2.1<br>
1.2 mybatis、MyBatis-Plus<br>
1.3 SpringSecurity<br>
1.5 Druid<br>
1.6 Slf4j<br>
1.7 Fastjson<br>
1.8 JWT<br>
1.9 Redis<br>
1.10 Quartz<br>
1.11 Mysql<br>
1.12 Tomcat


#### 安装步骤
推荐软件环境版本：jdk 1.8、tomcat 8.5、mysql 5.7
1.  导入db目录下的数据库文件。
2.  修改config目录下的配置文件。
3.  将工程打包，把war包放入tomcat中。


#### 前台使用说明

1.  会员登录，登录成功后可看到会员的卡券列表。
2.  卡券领取和购买，预存券的充值等。
3.  核销卡券，会员在前台出示二维码，管理员用微信扫一扫即可核销。
4.  卡券转赠，会员可将自己的卡券转赠给其他用户，输入对方的手机号即可完成转赠，获赠的好友会收到卡券赠送的短信。

<p><img src="screenshots/create.png" alt="卡券创建界面"></p>

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
<p>卡券营销：</p>
<p><img src="screenshots/coupon-list.png" alt="卡券列表"></p>

<p>收银功能：店员角色登录后台，从首页的“下单首页”菜单可进入代客收银下单界面，完成代客下单收银的流程。</p>
<p><img src="screenshots/cashier.png" alt="收银界面"></p>


#### 后续功能迭代

1.  报表统计
2.  微信卡券接入
3.  微信消息
4.  抽奖功能
5.  更多营销工具...

不足和待完善之处请谅解！源码仅供学习交流，更多功能欢迎进群咨询讨论，或需安装帮助请联系我们（<b>麻烦先点star！！！！！！</b>）。<br>
官方网站：https://www.fuint.cn <br>
开源不易，感谢支持！<br>
<b>作者wx：fsq_better：</b><br>
<p><img src="screenshots/qr.png" alt="公众号二维码"></p>


特别鸣谢<br>
mybaitsplus:https://github.com/baomidou/mybatis-plus<br>
vue:https://github.com/vuejs/vue
