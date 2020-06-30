
<template>
  <transition name="slide-fade">
    <div class="wrap">
      <div class="baise"></div>
      <i class="logo"></i>
      <div class="cntCover">
        <div class="row">
          <span>姓名</span>
          <!-- <i class="lock"></i> -->
          <input placeholder="请输入姓名" v-model="paramObj.realName" style="margin-right: .1rem;"/>
          <i class="close" v-if="paramObj.realName !== ''" @click="resetParam('realName')"></i>
        </div>
        <div class="row">
          <span>手机号</span>
          <!-- <i class="lock"></i> -->
          <input placeholder="请输入手机号" v-model="paramObj.mobile" style="margin-right: .1rem;"/>
          <i class="close" v-if="paramObj.mobile !== ''" @click="resetParam('mobile')"></i>
        </div>
        <div class="row">
          <span>所属店铺</span>
          <!-- <i class="phone"></i> -->
          <select v-model="paramObj.storeID" placeholder="请选择所属店铺">
            <option v-for="(item, index) in storeList" :key="index" :value='item.id'>{{ item.name }}</option>
          </select>
        </div>
        <div class="row" style="margin-top: 1.2rem;">
          <div class="btn loginBtn" @click="submit" :class="{'loginDisBtn': paramObj.mobile === '' || paramObj.verifyCode === '', 'actBtn': paramObj.mobile !== '' && paramObj.verifyCode !== ''}">提&nbsp;&nbsp;交</div>
        </div>
      </div>
    </div>
  </transition>
</template>
<script>
import service from '@/service/serviceApi.js'
import qs from 'qs'
import { MessageBox } from 'mint-ui'
export default {
  data () {
    return {
      times: 60,
      showButton: true,
      storeList: [],
      paramObj: {
        mobile: '',
        realName: '',
        storeID: ''
      }
    }
  },
  mounted () {
    this.getStoreList()
  },
  created () {
    document.title = '注册 - FuInt卡券管家'
  },
  methods: {
    // 输入框重置
    resetParam (objName) {
      this.paramObj[objName] = ''
    },
    // 获取店铺列表
    getStoreList () {
      let self = this
      service.postWithNoToken('/rest/confirmer/getStoreList', {}).then(
        result => {
          console.log(result)
          if (result.data.code === 200) {
            self.storeList = result.data.data
          }
        }
      ).catch(error => {
        console.log(error)
      })
    },
    // 提交
    submit () {
      if (this.paramObj.storeID === '' || this.paramObj.mobile === '' || this.paramObj.realName === '') {
        return false
      }
      service.postWithNoToken('/rest/confirmer/doAdd', this.paramObj).then(
        result => {
          if (result.data.code === 200) {
            this.$router.push({'path': '/result', 'query': {'type': 'submit'}})
          } else {
            MessageBox.alert(result.data.message, '温馨提示')
          }
        }
      ).catch(error => {
        console.log(error)
        MessageBox.alert('系统繁忙，请稍后重试', '温馨提示')
      })
    }
  }
}
</script>
<style lang="less" scoped>
  .wrap{
    height: 100%;
    width: 100%;
    .baise{
      background: #fff;
      position: fixed;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      z-index: -1;
    }
    .logo{
      z-index: 2;
      display: block;
      margin: 0 auto;
      margin-top: .8rem;
      height: 1.8rem;
      width: 2.45rem;
      background: url('../assets/images/logo.png') no-repeat center center;
      background-size: contain;
    }
    .cntCover{
      z-index: 2;
      width: 100%;
      box-sizing: border-box;
      padding: 0 1rem;
      margin-top: 1rem;
      .row{
        margin-top: .2rem;
        width: 100%;
        display: inline-flex;
        align-items: center;
        padding-bottom: .2rem;
        border-bottom: 1px solid #dcdcdc;
        &:last-child{
          border-bottom: none;
        }
        input{
          width: 100%;
          outline: 0px;
          border: 0px;
          height: 40px;
          line-height: 40px;
          font-size: .3rem;
        }
        >span{
          display: inline-block;
          min-width: 1.4rem;
          text-align: left;
          font-size: .3rem;
        }
        select{
          width: 100%;
          border: 1px solid #fff;
          outline: 0px;
          font-size: .3rem;
          background: #fff;
        }
        i{
          display: inline-block;
          height: .4rem;
          width: .4rem;
          flex-shrink: 0;
          margin-right: .4rem;
          &.phone{
            background: url('../assets/images/phone.png') no-repeat center center;
            background-size: contain;
          }
          &.lock{
            background: url('../assets/images/lock.png') no-repeat center center;
            background-size: contain;
          }
          &.close{
            width: .3rem;
            height: .3rem;
            margin-right: 0px;
            background: url('../assets/images/close.png') no-repeat center center;
            background-size: contain;
          }
        }
        .loginBtn{
          font-size: .34rem;
          // font-weight: 600;
          width: 100%;
          height: .76rem;
        }
        .validBtn{
          flex-shrink: 0;
          padding: 0 .1rem;
          width: 2.28rem;
          height: .76rem;
          font-size: .3rem;
        }
        .disBtn{
          color: #ababab;
          border: 1px solid #dcdcdc;
        }
        .loginDisBtn{
          background: #ababac;
          color: #fff;
        }
        .actBtn{
          color: #fff;
          background: -webkit-linear-gradient(left, #00acac , #00acac); /* Safari 5.1 to 6.0 */
          background: -o-linear-gradient(right, #00acac ,#00acac); /* Opera 11.1 to 12.0 */
          background: -moz-linear-gradient(right, #00acac ,#00acac); /* Firefox 3.6 to 15 */
          background: linear-gradient(to right, #00acac ,#00acac); /* 标准语法 */
        }
      }

    }
  }
</style>
