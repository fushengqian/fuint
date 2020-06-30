<template>
  <transition name="slide-fade">
    <div class="wrap">
      <div class="baise"></div>
      <i class="logo"></i>
      <div class="cntCover">
        <div class="row">
          <i class="phone"></i>
          <input placeholder="请输入手机号码" v-model="paramObj.mobile" />
          <i v-if="paramObj.mobile !== ''" class="close" @click="resetParam('mobile')"></i>
        </div>
        <div class="row">
          <i class="lock"></i>
          <input placeholder="短信验证码" v-model="paramObj.verifyCode" style="margin-right: .1rem;" />
          <i v-if="paramObj.verifyCode !== ''" class="close" @click="resetParam('verifyCode')"></i>
          <div class="btn validBtn" @click="sendCode" :class="{'disBtn': paramObj.mobile === '', 'actBtn': paramObj.mobile !== ''}" v-if="showButton">获取验证码</div>
          <div class="btn validBtn actBtn" v-if="!showButton">{{ times }}s</div>
        </div>
        <div class="row" style="margin-top: 1.2rem;">
          <div class="btn loginBtn" @click="login" :class="{'loginDisBtn': paramObj.mobile === '' || paramObj.verifyCode === '', 'actBtn': paramObj.mobile !== '' && paramObj.verifyCode !== ''}">登&nbsp;&nbsp;录</div>
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
      paramObj: {
        mobile: '',
        verifyCode: ''
      },
      jumpUrl: null,
      jumpQuery: null
    }
  },
  mounted () {
    this.jumpUrl = this.$route.query.frompath
    // console.log(this.$route.query)
    if (this.$route.query.param) {
      this.jumpQuery = JSON.parse(this.$route.query.param)
    }
    console.log(this.jumpQuery)
  },
  created () {
    document.title = '登录 - FuInt卡券管家'
  },
  methods: {
    // 输入框重置
    resetParam (objName) {
      this.paramObj[objName] = ''
    },
    // 发送短信验证码
    sendCode () {
      let self = this
      if (this.paramObj.mobile === '') {
        // MessageBox.alert('请填写手机号码', '温馨提示')
        return false
      }
      if (!this.showButton) {
        return false
      }
      service.postWithNoToken('/rest/sms/doSendVeryfiCode', {'mobile': this.paramObj.mobile}).then(
        result => {
          // console.log(result)
          if (result.data.code === 200) {
            self.showButton = false
            let a = setInterval(() => {
              if (self.times === 0) {
                self.showButton = true
                window.clearInterval(a)
                self.times = 60
                return false
              }
              self.times--
            }, 1000);
          } else {
            this.$MessageBox({
              title: '温馨提示',
              message: result.data.message, // 提示的内容，作为参数，传进来
              closeOnClickModal: true	// 表示不只是点击确定按钮才能关闭弹窗，点击页面的任何地方都可以关闭弹窗
            })
          }
        }
      ).catch(error => {
        if (error.response.status === 555) {
          this.$MessageBox({
            title: '温馨提示',
            message: '系统繁忙，请稍后重试', // 提示的内容，作为参数，传进来
            closeOnClickModal: true	// 表示不只是点击确定按钮才能关闭弹窗，点击页面的任何地方都可以关闭弹窗
          })
        }
      })
    },
    // 登录
    login () {
      // if (this.jumpUrl) {
      //         this.$router.push({'path': this.jumpUrl, query: this.jumpQuery})
      //       } else {
      //         this.$router.push({'path': '/home'})
      //       }
      if (this.paramObj.verifyCode === '') {
        // MessageBox.alert('请填写短信验证码', '温馨提示')
        this.$MessageBox({
          title: '温馨提示',
          message: '请填写短信验证码', // 提示的内容，作为参数，传进来
          closeOnClickModal: true	// 表示不只是点击确定按钮才能关闭弹窗，点击页面的任何地方都可以关闭弹窗
        })
        return false
      }

      service.postWithNoToken('/rest/sign/doSign', this.paramObj).then(
        result => {
          // console.log(result)
          if (result.data.code === 200) {
            let res = result.data.data
            console.log(res)
            this.$cookies.set('mobile', this.paramObj.mobile)
            this.$cookies.set('storeToken', res.token)
            if (this.jumpUrl) {
              this.$router.push({'path': '/' + this.jumpUrl, query: this.jumpQuery})
            } else {
              this.$router.push({'path': '/home'})
            }
          } else {
            this.$MessageBox({
              title: '温馨提示',
              message: result.data.message, // 提示的内容，作为参数，传进来
              closeOnClickModal: true	// 表示不只是点击确定按钮才能关闭弹窗，点击页面的任何地方都可以关闭弹窗
            })
          }
        }
      ).catch(error => {
        this.$MessageBox({
          title: '温馨提示',
          message: '系统繁忙，请稍后重试', // 提示的内容，作为参数，传进来
          closeOnClickModal: true	// 表示不只是点击确定按钮才能关闭弹窗，点击页面的任何地方都可以关闭弹窗
        })
      })
    }
  }
}
</script>
<style lang="less" scoped>
  .wrap{
    height: 100%;
    width: 100%;
    background: #fff;
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
      height: 2.45rem;
      width: 2.45rem;
      border: #02c7c7 .1rem solid;
      background: url('../assets/images/logo.png') no-repeat center center;
      border-radius: 2.5rem;
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
            height: .3rem;
            width: .3rem;
            background: url('../assets/images/close.png') no-repeat center center;
            background-size: contain;
          }
        }
        input{
          width: 100%;
          outline: 0px;
          border: 0px;
          height: 40px;
          line-height: 40px;
          font-size: .3rem;
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
