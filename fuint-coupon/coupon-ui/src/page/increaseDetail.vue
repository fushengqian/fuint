<template>
  <transition name="slide-fade">
    <div class="wrap">
      <!-- <div class="header">
        <span>{{ mobile }}<i class="hna" v-show="isHna === '1'">HNA</i></span>
        <i class="menu" @click.stop="toShowBtn"></i>
      </div> -->
      <div class="navbar">
        <div class="nav" v-for="(item, index) in navData" :key="index" @click='navEvent(index)' :class="{'on': item.pick}">{{ item.name }}</div>
      </div>
      <div class="searchBar">
        <input type="text" placeholder="输入赠与对象手机号查询" v-if="paramObj.type === 'give'" v-model="paramObj.mobile"/>
        <input type="text" placeholder="输入获赠来源手机号查询" v-else v-model="paramObj.mobile"/>
        <div class="btn searchBtn" @click="search">查询</div>
      </div>
      <div class="listHead">
        <div class="hd">日期</div>
        <div class="hd" v-if="paramObj.type === 'give'">赠予对象手机号</div>
        <div class="hd" v-else>获赠来源手机号</div>
        <div class="hd">赠送优惠券情况</div>
        <div class="hd" v-if="paramObj.type === 'give'">备注</div>
        <div class="hd" v-else>留言</div>
      </div>
      <div class="ii-list" :style="listStyle">
         <scroller delegate-id="myScroller" :on-infinite="loadMore" :on-refresh="refresh" ref="scroller" class="scroll">
          <div class="liWrap">
            <div class="li" v-for="(item, index) in listData" :key="index">
              <div class="ld">{{ item.formatTime }}</div>
              <div class="ld" v-if="paramObj.type === 'give'">{{ item.mobile }}</div>
              <div class="ld" v-else>{{ item.userMobile }}</div>
              <div class="ld">共计{{ item.num }}张<br/>共计{{ item.money }}元</div>
              <div class="ld" v-if="paramObj.type === 'give'">{{ item.note }}</div>
              <div class="ld" v-else>{{ item.message }}</div>
            </div>
          </div>
         </scroller>
      </div>
      <qr-dialog ref='qrDialog' @listenDialog='listenDialog'></qr-dialog>
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
      navName: '未使用',
      navData: [{
        pick: true,
        name: '赠送记录',
        value: 'give'
      }, {
        pick: false,
        name: '获赠记录',
        value: 'gived'
      }],
      listStyle: {},
      isHna: '', // 1-是，0-否
      showBtn: false,
      allLoaded: true,
      mobile: '',
      total: 0,
      listData: [],
      paramObj: {
        pageNumber: 1,
        pageSize: 1000,
        mobile: '',
        type: 'give' // give-赠送;gived-获赠
      },
      totalPage: 0,
      interval: null
    }
  },
  created () {
    document.title = '转赠明细 - FuInt卡券管家'
  },
  mounted () {
    let self = this
    this.toJudgeHna()
    this.getList()
    self.$refs.scroller.resize()
    this.listStyle = {
      'height': (screen.availHeight - 130) + 'px'
    }
    document.onclick = function () {
      self.showBtn = false
    }
  },
  destroyed () {
    this.interval && window.clearInterval(this.interval)
    this.getList()
  },
  components: { qrDialog },
  methods: {
    // logout () {
    //   MessageBox.confirm('您确定要退出么?', '温馨提示').then(action => {
    //     service.post('/rest/sign/doSign', {}).then(
    //       result => {
    //         if (result.data.code === 200) {
    //           MessageBox.alert('退出成功', '温馨提示')
    //           this.$cookies.remove('storeToken')
    //           this.$cookies.remove('mobile')
    //           this.$router.push({path: '/login'})
    //         }
    //       }
    //     ).catch(error => {
    //       console.log(error)
    //       MessageBox.alert('系统繁忙，请稍后重试', '温馨提示')
    //     })
    //   })
    // },
    // 判断是否是hna用户
    toJudgeHna () {
      service.post('/rest/sign/doGetUserInfo', {}).then(
        result => {
          if (result.data.code === 200) {
            this.mobile = result.data.data.mobile
            this.isHna = result.data.data.isHna
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
    toShowBtn () {
      this.showBtn = !this.showBtn
    },
    listenDialog (data) {
      this.interval && window.clearInterval(this.interval)
    },
    refresh(){
      this.paramObj.pageNumber = 1
      this.listData = []
      this.getList("refresh")
    },
    search () {
      this.listData = []
      this.getList()
    },
    loadMore(done){
      let self = this
      console.log('loadmore')
      setTimeout( () => {
        if (self.paramObj.pageNumber === 0) {
          return false
        }
        if (self.total === 0) {
          done(true)
          return false
        }
        if (self.paramObj.pageNumber === self.totalPage){
          done(true)
        } else {
          done(false)
          this.paramObj.pageNumber++
          self.getList()
        }
      },1000)
    },
    showDetail (index, index2) {
      let deg = !this.listData[index].arr[index2].showDetail ? 180 : 0
      this.listData[index].arr[index2].rotate['transform'] = `rotate(${deg}deg)`
      this.listData[index].arr[index2].showDetail = !this.listData[index].arr[index2].showDetail
    },
    expand (index) {
      this.listData[index].arr.map((el, index) => {
        el.expand = true
      })
      this.listData[index].showExpand = false
    },
    navEvent (index) {
      this.navData.map((k, v) => {
        k.pick = false
      })
      this.navData[index].pick = true
      this.navName = this.navData[index].name
      this.paramObj.type = this.navData[index].value
      this.listData = []
      Indicator.open()
      this.getList()
    },
    getList (type) {
      let self = this
      let param = []
      for (let p in this.paramObj) {
        param.push([p, this.paramObj[p]].join('='))
      }
      service.get(`/rest/give/giveLog?${param.join('&')}`, {}).then(
        result => {
          Indicator.close()
          if (result.data.code === 200) {
            let res = result.data.data.dataList
            if (type === 'refresh') {
              self.$refs.scroller.finishPullToRefresh()
            }
            res.map((el, index) => {
              if (el.createTime) {
                let dateArr = el.createTime.split(' ')
                let timeArr = dateArr[1].split(':')
                el['formatTime'] = dateArr[0] + ' ' + timeArr[0] + ':' + timeArr[1]
              } else {
                el['formatTime'] = '--'
              }
            })
            self.total = result.data.data.totalRow
            self.listData = self.listData.concat(res)
            self.totalPage = result.data.data.totalPage
          }
        }
      ).catch(error => {
        Indicator.close()
        console.log(error)
        // MessageBox.alert('系统繁忙，请稍后再试', '温馨提示')
      })
    }
  }
}
</script>
<style lang="less" scoped>
  .wrap{
    height: 100%;
    width: 100%;
    box-sizing: border-box;
    // padding: 0 .3rem;
    background: #f1f1f1;
    position: fixed;
    top: 0;
    bottom: 0;
    left: 0;
    right: 0;
    overflow: hidden;
    .menuBtn{
      position: fixed;
      right: .1rem;
      top: 1rem;
      width: 100px;
      z-index: 500;
      span{
        display: block;
        width: 100%;
        height: .8rem;
        line-height: .8rem;
        text-align: center;
        background: #333;
        color: #fff;
        font-size: .3rem;
        &:first-child{
          border-bottom: 1px solid #dddddd;
        }
      }
    }
    .header{
      width: 100%;
      display: flex;
      justify-content: space-between;
      padding: .2rem .3rem;
      align-items: center;
      height: 1rem;
      background: #f1f1f1;
      box-sizing: border-box;
      z-index: 200;
      span{
        font-size: .24rem;
        color: #333;
        .hna{
          display: inline-block;
          height: .25rem;
          width: .6rem;
          font-size: .24rem;
          color: #ff0000;
          border: 1px solid #ff0000;
          margin-left: .1rem;
        }
      }
      .menu{
        display: inline-block;
        height: .4rem;
        width: .4rem;
        background: url('../assets/images/menu.png') no-repeat center center;
        background-size: contain;
      }
    }
    .navbar{
      width: 100%;
      box-sizing: border-box;
      padding: 0 .75rem;
      display: flex;
      justify-content: space-around;
      background-color: #ffffff;
      border-radius: 3px;
      height: 40px;
      z-index: 200;
      .nav{
        height: 40px;
        width: 1.1rem;
        text-align: center;
        line-height: 40px;
        font-size: .22rem;
        color: #787878;
        box-sizing: border-box;
        border-bottom: .04rem solid #ffffff;
        &.on{
          color: #333333 !important;
          border-bottom: .06rem solid #ff713f !important;
        }
      }
    }
    .searchBar{
      // position: fixed;
      background: #f1f1f1;
      // left: 0;
      // top: 40px;
      display: flex;
      justify-content: center;
      align-items: center;
      height: 50px;
      width: 100%;
      padding: 0 .3rem;
      box-sizing: border-box;
      font-size: .22rem;
      z-index: 200;
      input{
        height: 30px;
        line-height: 30px;
        width: 100%;
        border-radius: 30px;
        border: 1px solid #ddd;
        text-align: center;
        outline: none;
        background: #ffffff;
        font-size: .22rem;
      }
      input::-webkit-input-placeholder {
        opacity: 1;
        color: #ababab;
        font-size: .22rem;
        height: 30px;
        line-height: 30px;
      }
      .searchBtn{
        flex-shrink: 0;
        color: #fff;
        width: 1.2rem;
        margin-left: .1rem;
        height: 30px;
        background: -webkit-linear-gradient(left, #00acac , #00acac); /* Safari 5.1 to 6.0 */
        background: -o-linear-gradient(right, #00acac ,#00acac); /* Opera 11.1 to 12.0 */
        background: -moz-linear-gradient(right, #00acac ,#00acac); /* Firefox 3.6 to 15 */
        background: linear-gradient(to right, #00acac ,#00acac); /* 标准语法 */
      }
    }
    .listHead{
      // top: 90px;
      // position: fixed;
      background: #f1f1f1;
      // left: 0;
      // right: 0;
      box-sizing: border-box;
      width: 100%;
      display: flex;
      padding:  0 .4rem;
      height: 30px;
      .hd{
        height: 100%;
        line-height: 30px;
        text-align: left;
        font-size: .22rem;
        color: #333;
        &:nth-child(1){
          width: 1.5rem;
          flex-shrink: 0;
        }
        &:nth-child(2),&:nth-child(3){
          width: 30%;
        }
        &:nth-child(4){
          width: 15%;
        }
      }
    }
    .ii-list{
      width: 100%;
      height: 20rem;
      position: relative;
      // top: 120px;
      // left: .3rem;
      // right: .3rem;
      // bottom: 0px;
      background: #f1f1f1;
      .scroll{
        box-sizing: border-box;
        padding: 0 .3rem;
      }
      .liWrap{
        width: 100%;
        margin-bottom: .2rem;
        box-sizing: border-box;
        background: #f1f1f1;
        .li{
          display: flex;
          background: #fff;
          width: 100%;
          border: 1px solid #ddd;
          box-sizing: border-box;
          padding: .1rem;
          margin-bottom: .1rem;
          .ld{
            text-align: left;
            font-size: .22rem;
            color: #333;
            text-overflow: ellipsis;
            overflow: hidden;
            &:nth-child(1){
              width: 1.5rem;
              flex-shrink: 0;
            }
            &:nth-child(2),&:nth-child(3){
              width: 30%;
            }
            &:nth-child(4){
              width: 15%;
            }
          }
        }
      }
    }
  }
</style>
