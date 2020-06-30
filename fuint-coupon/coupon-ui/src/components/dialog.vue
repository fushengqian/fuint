<template>
  <transition name="slide-fade">
    <div class="dialogWrap" v-if="showDialog">
      <div class="mask"></div>
      <div class="dContent">
        <i class="delete" @click="close"></i>
        <p class="dTitle">{{ dialogData.title }}</p>
        <span class="price"><span>￥</span>{{ dialogData.money }}</span>
        <span class="mark">{{ dialogData.tips }}</span>
        <img :src="dialogData.img" />
      </div>
    </div>
  </transition>
</template>
<script>
import service from '@/service/serviceApi.js'
export default {
  data () {
    return {
      showDialog: false,
      dialogData: {
        title: '',
        money: '',
        tips: '',
        img: ''
      },
      timesOfShow: 0,
      intervalMark: null
    }
  },
  destroyed () {
    this.intervalMark && window.clearInterval(this.intervalMark)
    this.timesOfShow = 0
  },
  methods: {
    close () {
      for (let p in this.dialogData) {
        this.dialogData[p] = ''
      }
      this.intervalMark && window.clearInterval(this.intervalMark)
      this.$emit('listenDialog', {})
      this.timesOfShow = 0
      this.showDialog = false
    },
    getState (data) {
      let self = this
      service.get(`/rest/myCoupon/isUsed?id=${data.id}`, {}).then(
        result => {
          if (result.data.code === 200) {
            if (result.data.data) {
              self.intervalMark && window.clearInterval(self.intervalMark)
              self.$router.push({path: '/result', 'query': {'type': 'used', 'info': JSON.stringify(data)}})
            } else {
              this.intervalMark = setTimeout(() => {
                self.getState(data)
              }, 2000)
            }
          }
        }
      )
    },
    show (data) {
      let self = this
      this.dialogData = data
      this.dialogData['img'] = `data:image/jpg;base64,${data.img}`
      this.showDialog = true
      this.timesOfShow++
      // 当循环调用二维码刷新接口调用一次后，执行循环刷新状态操作
      if (this.timesOfShow === 1) {
        this.getState(data)
      }
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
      position: fixed;
      margin: 0 auto;
      top: 30%;
      left: 0;
      right: 0;
      background: #fff;
      border-radius: .1rem;
      z-index: 2000;
      box-sizing: border-box;
      padding: .6rem .3rem;
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
        color: #ff713f;
        span{
          font-size: .2rem;
        }
      }
      .mark{
        display: block;
        margin-top: .15rem;
        width: 100%;
        text-align: center;
        color: #333;
        font-size: .24rem;
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
