<template>
  <transition name="slide-fade">
    <div class="resultWrap">
      <div class="success" v-if="type === 'success'">
        <i></i>
        <p><span>使用成功!</span></p>
        <span class="mark">成功使用1张{{ successData.name }}</span>
        <span class="price"><span>￥</span>{{ successData.money }}</span>
        <span class="mark">{{ successData.tips }}</span>
        <span class="mark" v-if="successData.code">流水号：{{ successData.code}}</span>
      </div>
      <div class="attention" v-if="type === 'fail'">
        <i></i>
        <p><span>使用失败!</span></p>
        <span class="mark">{{ failMessage }}</span>
      </div>
      <div class="success" v-if="type === 'submit'">
        <i></i>
        <p><span>信息已提交!</span></p>
        <span class="mark">后台管理人员正在审核，请耐心等待</span>
      </div>
      <div class="success" v-if="type === 'used'">
        <i></i>
        <p><span>使用成功！</span></p>
        <span class="mark">成功使用1张{{ usedData.title }}</span>
        <span class="price"><span>￥</span>{{ usedData.money }}</span>
        <span class="mark">{{ usedData.tips }}</span>
      </div>
    </div>
  </transition>
</template>
<script>
import service from '@/service/serviceApi.js'
import qrDialog from '@/components/dialog'
import qs from 'qs'
import { MessageBox, Indicator, Toast } from 'mint-ui'
export default {
  data () {
    return {
      type: '',
      failMessage: '',
      successData: {
        name: '',
        tips: '',
        money: '',
        code: ''
      },
      usedData: {
        title: '',
        tips: '',
        money: ''
      }
    }
  },
  created () {
    document.title = '结果 - FuInt卡券管家'
  },
  destroyed () {
    Indicator.close()
  },
  mounted () {
    this.type = this.$route.query.type
    if (this.type) {
      Indicator.close()
      if (this.type === 'used') {
        this.usedData = JSON.parse(this.$route.query.info)
      }
      return false
    } else {
      let _url = this.getUrlparam()
      this.doConfirm(_url.code)
    }
  },
  methods: {
    // 核销
    doConfirm (code) {
      Indicator.open()
      service.post(`/rest/confirm/doConfirm`, {code: code}).then(
        result => {
          Indicator.close()
          if (result.data.code === 200) {
            this.type = 'success'
            this.successData = result.data.data
          } else {
            this.type = 'fail'
            this.failMessage = result.data.message
          }
        }
      ).catch(error => {
        Indicator.close()
        console.log(error)
        // MessageBox.alert('系统繁忙，请稍后再试', '温馨提示')
      })
    },
    // 获取url的参数
    getUrlparam () {
      let _url = window.location.href.split('?')[1]
      let _obj = {}
      if (_url !== '' && _url !== null) {
        if (_url.indexOf('=') > -1) {
          if (_url.indexOf('&') > -1) {
            let a = _url.split('&')
            for (let i = 0; i < a.length; i++) {
              let b = a[i].split('=')
              _obj[b[0]] = b[1]
            }
            return _obj
          } else {
            let a = _url.split('=')
            _obj[a[0]] = a[1]
            return _obj
          }
        } else {
          _obj[_url] = _url
          return _obj
        }
      } else {
        return _obj
      }
    }
  }
}
</script>
<style lang="less" scoped>
  .resultWrap{
    width: 100%;
    height: 100%;
    p{
      width: 100%;
      text-align: center;
      color: #333;
      font-size: .3rem;
      margin-top: .2rem;
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
        position: relative;
        bottom: 1px;
      }
    }
    .mark{
      display: block;
      margin-top: .15rem;
      width: 100%;
      text-align: center;
      color: #666;
      font-size: .24rem;
    }
    .success{
      width: 100%;
      text-align: center;
      margin-top: 1.4rem;
      i{
        display: block;
        width: 1.74rem;
        height: 1.74rem;
        margin: 0 auto;
        background: url('../assets/images/success.png') no-repeat center center;
        background-size: contain;
      }
    }
    .attention{
      width: 100%;
      text-align: center;
      margin-top: 1.4rem;
      i{
        display: block;
        width: 1.74rem;
        height: 1.74rem;
        margin: 0 auto;
        background: url('../assets/images/attention.png') no-repeat center center;
        background-size: contain;
      }
    }
  }
</style>
