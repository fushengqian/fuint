<template>
  <transition name="slide-fade">
    <div class="dialogWrap" v-if="showDialog">
      <div class="mask"></div>
      <div class="dContent">
        <i class="delete" @click="close"></i>
        <p class="dTitle">卡券转赠</p>
        <div class="remark">
          <div>已选择<span>{{ dialogData.num }}</span>张优惠券</div>
          <div>总计面额<span>￥{{ dialogData.money }}</span></div>
        </div>
        <div class="context" v-if="state === 1">
          <div class="row">
            <span>请输入赠予对象手机号（必填）</span>
            <div>
              <input placeholder="请输入手机号码" v-model="param.mobile"/>
              <i class="close" v-show="param.mobile !== ''" @click="resetParam('mobile')"></i>
            </div>
            <span class="attention">提醒：转赠后无法撤回，优惠券将进入被赠送人账户内，请反复核对确认赠予对象手机号及所赠卡券内容。若赠送错误，损失由赠送人自行承担。</span>
          </div>
          <div class="row">
            <span>给对方留言（选填）</span>
            <div>
              <input type="text" v-model="param.message"/>
              <i class="close" v-show="param.message !== ''" @click="resetParam('message')"></i>
            </div>
          </div>
          <div class="row">
            <span>备注（选填）</span>
            <div>
              <input type="text" v-model="param.note"/>
              <i class="close" v-show="param.note !== ''" @click="resetParam('note')"></i>
            </div>
          </div>
        </div>
        <div class="context" v-if="state === 2">
          <div class="row flxcenter">
            <span>赠予对象手机号</span>
            <span>{{ param.mobile }}</span>
          </div>
          <div class="row">
            <span>短信验证码（尾号：{{ lastMobile }}）</span>
            <div class="validCol">
              <input placeholder="短信验证码" style="margin-right: .1rem;" v-model="param.vcode"/>
              <i class="close" v-show="param.vcode !== ''" @click="resetParam('vcode')" style="right: 2.2rem;"></i>
              <div class="btn validBtn actBtn" v-if="showButton" @click="sendCode">获取验证码</div>
              <div class="btn validBtn disBtn" v-if="!showButton">{{ times }}s</div>
            </div>
          </div>
        </div>
        <div class="context" v-if="state === 3">
          <div class="row flxcenter">
            <span class="largeText">转赠成功!</span>
          </div>
        </div>
        <div class="btn dBtn" v-if="state === 1" @click="next(2)">下一步</div>
        <div class="btn dBtn inline"  v-if="state === 2">
          <span @click="next(1)">返回</span>
          <span @click="next(3)">确认赠送</span>
        </div>
        <div class="btn dBtn" v-if="state === 3" @click="next(4)">关闭</div>
      </div>
    </div>
  </transition>
</template>
<script>
import service from '@/service/serviceApi.js'
import { MessageBox } from 'mint-ui'
export default {
  data () {
    return {
      showButton: true,
      showDialog: false,
      dialogData: {
        num: 0,
        money: '0.00',
        mobile: '',
        couponId: ''
      },
      param: {
        mobile: '',
        note: '',
        vcode: '',
        couponId: '',
        message: ''
      },
      times: 60,
      // 提交步骤状态位标记
      state: 1,
      lastMobile: '',
      timesOfShow: 0,
      intervalMark: null
    }
  },
  destroyed () {
    this.intervalMark && window.clearInterval(this.intervalMark)
    this.timesOfShow = 0
  },
  methods: {
    resetParam (type) {
      this.param[type] = ''
    },
    close () {
      for (let p in this.dialogData) {
        this.dialogData[p] = ''
      }
      this.state = 1
      this.showDialog = false
    },
    next (state) {
      if (state === 1) {
        this.state = state
        return false
      } else if (state === 2) {
        console.log(/^[0-9]*$/.test(this.param.mobile))
        if (this.param.mobile === '' || !/^[0-9]*$/.test(this.param.mobile)) {
          this.$MessageBox({
            title: '温馨提示',
            message: '请填写手机号码', // 提示的内容，作为参数，传进来
            closeOnClickModal: true	// 表示不只是点击确定按钮才能关闭弹窗，点击页面的任何地方都可以关闭弹窗
          })
          return false
        }
        this.state = state
      } else if (state === 3) {
        if (this.param.vcode === '') {
          this.$MessageBox({
            title: '温馨提示',
            message: '请输入验证码', // 提示的内容，作为参数，传进来
            closeOnClickModal: true	// 表示不只是点击确定按钮才能关闭弹窗，点击页面的任何地方都可以关闭弹窗
          })
          return false
        }
        this.param.couponId = this.dialogData.couponId
        service.post(`/rest/give/doGive`, this.param).then(
          result => {
            if (result.data.code === 200) {
              this.state = state
            } else {
              this.$MessageBox({
                title: '温馨提示',
                message: result.data.message, // 提示的内容，作为参数，传进来
                closeOnClickModal: true	// 表示不只是点击确定按钮才能关闭弹窗，点击页面的任何地方都可以关闭弹窗
              })
            }
          }
        )
      } else if (state === 4) {
        this.state = 1
        for(let p in this.param) {
          this.param[p] = ''
        }
        this.showDialog = false
        this.$emit('listenDialog', null)
      }
    },
    // 发送短信验证码
    sendCode () {
      let self = this
      if (!this.showButton) {
        return false
      }
      service.postWithNoToken('/rest/sms/doSendVeryfiCode', {'mobile': this.dialogData.mobile}).then(
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
        this.$MessageBox({
          title: '温馨提示',
          message: '系统繁忙，请稍后重试', // 提示的内容，作为参数，传进来
          closeOnClickModal: true	// 表示不只是点击确定按钮才能关闭弹窗，点击页面的任何地方都可以关闭弹窗
        })
      })
    },
    show (data) {
      let self = this
      // console.log(data)
      this.dialogData = data
      this.lastMobile = data.mobile.slice(data.mobile.length - 4, data.mobile.length)
      this.showDialog = true
    }
  }
}
</script>
<style lang="less" scoped>
  .dialogWrap{
    width: 100%;
    height: 100%;
    .mask{
      position: fixed;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: rgba(0, 0, 0, .5);
      z-index: 500;
    }
    .dContent{
      width: 75%;
      position: absolute;
      margin: 0 auto;
      top: 15%;
      left: 0;
      right: 0;
      background: #fff;
      border-radius: .1rem;
      z-index: 2000;
      box-sizing: border-box;
      padding-top: .6rem;
      text-align: center;
      .delete{
        display: inline-block;
        position: absolute;
        top: .2rem;
        right: .2rem;
        width: .4rem;
        height: .4rem;
        background: url('../assets/images/delete.png') no-repeat center center;
        background-size: contain;
      }
      .dTitle{
        width: 100%;
        text-align: center;
        color: #333;
        font-size: .3rem;
      }
      .price{
        margin-top: .15rem;
        display: flex;
        width: 100%;
        justify-content: center;
        align-items: flex-end;
        font-size: .3rem;
        color: #00acac;
        span{
          font-size: .2rem;
        }
      }
      .remark{
        display: flex;
        margin-top: .15rem;
        width: 100%;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        color: #333;
        font-size: .24rem;
        >div{
          span{
            font-size: .28rem;
            font-weight: 600;
          }
        }
      }
      .context{
        width: 100%;
        box-sizing: border-box;
        padding: .2rem .4rem;
        .row{
          width: 100%;
          display: flex;
          flex-direction: column;
          align-items: flex-start;
          justify-content: center;
          font-size: .28rem;
          margin-top: .2rem;
          &.flxcenter{
            align-items: center !important;
          }
          >div{
            position: relative;
            width: 100%;
            margin-top: .1rem;
          }
          .validCol{
            width: 100%;
            display: flex;
          }
          span{
            text-align: left;
          }
          .attention{
            color: red;
            margin-top: .1rem;
          }
          input{
            width: 100%;
            outline: 0px;
            border: 1px solid #ddd;
            height: .6rem;
            line-height: .6rem;
            font-size: .28rem;
            padding: .1rem;
            box-sizing: border-box;
          }
          input::-webkit-input-placeholder {
            opacity: 1;
            color: #ababab;
            font-size: .22rem;
            height: .6rem;
            line-height: .6rem;
          }
          i{
            position: absolute;
            display: inline-block;
            right: .1rem;
            top: .17rem;
            flex-shrink: 0;
            &.close{
              height: .3rem;
              width: .3rem;
              background: url('../assets/images/close.png') no-repeat center center;
              background-size: contain;
            }
          }
          .largeText{
            font-size: .5rem;
          }
          .validBtn{
            flex-shrink: 0;
            padding: 0 .1rem;
            width: 2rem;
            height: .6rem;
            font-size: .28rem;
          }
          .disBtn{
            color: #ababab;
            border: 1px solid #dcdcdc;
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
      .dBtn{
        position: relative;
        left: 0;
        bottom: -.15rem;
        width: 100%;
        height: .8rem;
        border-radius: .1rem;
        font-size: .3rem;
        color: #fff;
        background: -webkit-linear-gradient(left, #00acac , #00acac); /* Safari 5.1 to 6.0 */
        background: -o-linear-gradient(right, #00acac ,#00acac); /* Opera 11.1 to 12.0 */
        background: -moz-linear-gradient(right, #00acac ,#00acac); /* Firefox 3.6 to 15 */
        background: linear-gradient(to right, #00acac ,#00acac); /* 标准语法 */
        &.inline{
          background: transparent !important;
          >span{
            background: -webkit-linear-gradient(left, #00acac , #00acac); /* Safari 5.1 to 6.0 */
            background: -o-linear-gradient(right, #00acac ,#00acac); /* Opera 11.1 to 12.0 */
            background: -moz-linear-gradient(right, #00acac ,#00acac); /* Firefox 3.6 to 15 */
            background: linear-gradient(to right, #00acac ,#00acac); /* 标准语法 */
          }
        }
        >span{
          display: flex;
          justify-content: center;
          align-items: center;
          border-radius: .1rem;
          height: 100%;
          &:first-child{
            width: 30%;
            text-align: center;
          }
          &:last-child{
            width: 70%;
            text-align: center;
          }
        }
      }
      img{
        width: 1.84rem;
        height: 1.84rem;
        margin: 0 auto;
        margin-top: .4rem;
      }
    }
  }
</style>
