<template>
  <transition name="slide-fade">
    <div class="wrap">
      <p class="title">关于FuInt会员卡券管家</p>
      <p class="subTit">一、包含模块</p>
      <table>
        <tr>
          <th>序号</th>
          <th>模块名称</th>
          <th>说明</th>
        </tr>
        <tr>
          <td>1</td>
          <td>会员端</td>
          <td rowspan="3">免费</td>
        </tr>
        <tr>
          <td>2</td>
          <td>管理员核销端</td>
        </tr>
        <tr>
          <td>3</td>
          <td>后台管理端</td>
        </tr>
      </table>
      <p class="subTit">二、 优惠券使用规定</p>
      <span>1、持优惠券宾客可通过各种渠道预订产品，并按照预订的渠道价格结算房费，结账时出示优惠券即可。</span>
      <span>2、优惠券使用不找零、不兑换现金，优惠券面值等同于等额现金。</span>
      <span>3、客户每次消费可使用优惠券500元面值不超过10张。</span>
      <span>4、商家优惠券能用于购买酒水、饮料、香烟的相关产品。</span>
      <span>5、优惠券不可用于团队或会议客人，商家如发现旅行社或批发商使用房券进行结算，商家有权利拒绝。</span>
      <span>6、开具发票时发票金额为实际订单金额，非优惠券票面金额。</span>
      <span>7、优惠券有效期为自开放受理日起三年。</span>
      <p class="subTit">三、其他说明</p>
      <span>其他未尽事宜由商家负责解释。</span>
    </div>
  </transition>
</template>
<style lang="less" scoped>
  .wrap{
    width: 100%;
    box-sizing: border-box;
    background: #ffffff;
    padding: 0 .3rem;
    padding-top: .4rem;
    .title{
      width: 100%;
      text-align: center;
      color: #333;
      font-size: .4rem;
      font-weight: bold;
      margin-bottom: .4rem;
    }
    .subTit{
      width: 100%;
      text-align: left;
      font-size: .3rem;
      color: #333;
      text-indent: .6rem;
      font-weight: 600;
      margin: .3rem 0 .2rem;
    }
    span{
      display: block;
      text-align: left;
      font-size: .3rem;
      color: #333;
      text-indent: .6rem;
      line-height: .5rem;
    }
    table{
      width: 100%;
      color: #333;
      font-size: .3rem;
      border-collapse: collapse;
      border: 1px solid #000;
      th{
        height: .6rem;
        border: 1px solid #000;
        &:first-child{
          width: 60px;
        }
      }
      td{
        height: .6rem;
        border: 1px solid #000;
        &:nth-child(2){
          padding-left: .1rem;
          text-align: left;
        }
      }
    }
  }
</style>
