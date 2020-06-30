<template>
  <transition name="slide-fade">
    <div class="wrap">
      <div class="limheader">
        <span>{{ mobile }}<i class="hna" v-show="isHna === '1'">HNA</i></span>
        <i class="menu" @click.stop="toShowBtn"></i>
      </div>
      <div class="menuBtn" v-show="showBtn">
        <span @click="logout">退出账号</span>
        <span @click="toJump('/increase')">转赠卡券</span>
        <span @click="toJump('/increaseDetail')">转赠明细</span>
        <span @click="toJump('/about')">关于我们</span>
      </div>
      <div class="navbar">
        <div class="nav" v-for="(item, index) in navData" :key="index" @click='navEvent(index)' :class="{'on': item.pick}">{{ item.name }}</div>
      </div>
      <div class="mark">当前有<span>{{ total }}</span>张券{{ navName }}</div>
      <div class="h-list" :style="listStyle">
         <scroller delegate-id="myScroller" :on-infinite="loadMore" :on-refresh="refresh" ref="scroller" class="scroll">
          <div class="liWrap" v-for="(item, index) in listData" :key="index">
            <div class="li" v-for="(item2, index2) in item.arr" :key="index2" v-show="item2.expand">
              <div class="about">
                <img :src="item2.imageUrl" />
                <div class="info">
                  <p>{{ item2.name }}</p>
                  <div class="op">
                    <span class="num"><span>￥</span>{{ item2.money }}</span>
                    <div class="detailBtn" @click="showDetail(index, index2)"><span>详细规则</span><i class="arrow" :style="item2.rotate"></i></div>
                    <div class="btn" :class="{'liBtn': paramObj.status === 'A', 'disBtn': paramObj.status !== 'A'}" @click="toUse(item2)">{{ btnObj[paramObj.status]}}</div>
                  </div>
                </div>
              </div>
              <ul class="detail" v-show="item2.showDetail">
                <li>{{ item2.useRule}}</li>
              </ul>
            </div>
            <div class="expand" v-if='item.lastTotal !== 0 && item.showExpand' @click="expand(index)">
              <span>...</span>
              <div><span>同类券还有{{ item.lastTotal }}张,</span><div class="eBtn">展开全部<i>></i></div></div>
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
        name: '未使用',
        value: 'A'
      }, {
        pick: false,
        name: '已使用',
        value: 'B'
      }, {
        pick: false,
        name: '已过期',
        value: 'C'
      }],
      btnObj: {
        'A': '立即使用',
        'B': '已使用',
        'C': '已过期'
      },
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
        status: 'A' // A-未使用;B-已使用;C-已过期
      },
      totalPage: 0,
      interval: null
    }
  },
  created () {
    document.title = '我的卡券 - FuInt卡券管家'
  },
  mounted () {
    let self = this
    this.toJudgeHna()
    this.getList()
    self.$refs.scroller.resize()
    this.listStyle = {
      'height': (screen.availHeight - 130) + 'px'
    }
    // console.log(document.body.clientHeight)
    document.onclick = function () {
      self.showBtn = false
    }
  },
  destroyed () {
    this.interval && window.clearInterval(this.interval)
    // this.getList()
  },
  components: { qrDialog },
  methods: {
    toJump (url) {
      this.$router.push({path: url})
    },
    logout () {
      MessageBox.confirm('您确定要退出么?', '温馨提示').then(action => {
        service.post('/rest/sign/doSign', {}).then(
          result => {
            if (result.data.code === 200) {
              this.$MessageBox({
                title: '温馨提示',
                message: '退出成功', // 提示的内容，作为参数，传进来
                closeOnClickModal: true	// 表示不只是点击确定按钮才能关闭弹窗，点击页面的任何地方都可以关闭弹窗
              })
              this.$cookies.remove('storeToken')
              this.$cookies.remove('mobile')
              this.$router.push({path: '/login'})
            }
          }
        ).catch(error => {
          this.$MessageBox({
            title: '温馨提示',
            message: '系统繁忙，请稍后重试', // 提示的内容，作为参数，传进来
            closeOnClickModal: true	// 表示不只是点击确定按钮才能关闭弹窗，点击页面的任何地方都可以关闭弹窗
          })
        })
      })
    },
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
    toUse (data) {
      let self = this
      if (this.paramObj.status !== 'A') {
        return false
      }
      this.interval && window.clearInterval(self.interval)
      Indicator.open()
      service.get(`/rest/qrCode/doGet?id=${data.id}`, {}).then(
        result => {
          Indicator.close()
          if (result.data.code === 200) {
            let res = result.data.data
            res['title'] = data.name
            res['id'] = data.id
            this.$refs.qrDialog.show(res)
            this.interval = setInterval(() => {
              self.toUse(data)
            }, 60000)
          } else {
            this.$MessageBox({
              title: '温馨提示',
              message: result.data.message, // 提示的内容，作为参数，传进来
              closeOnClickModal: true	// 表示不只是点击确定按钮才能关闭弹窗，点击页面的任何地方都可以关闭弹窗
            })
          }
        }
      ).catch(error => {
        Indicator.close()
        // MessageBox.alert('系统繁忙，请稍后再试', '温馨提示')
      })
    },
    refresh(){
      this.paramObj.pageNumber = 1
      this.listData = []
      this.getList("refresh")
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
      this.paramObj.status = this.navData[index].value
      console.log(this.paramObj)
      Indicator.open()
      this.listData = []
      this.getList()
    },
    getList (type) {
      let self = this
      let param = []
      for (let p in this.paramObj) {
        param.push([p, this.paramObj[p]].join('='))
      }
      service.get(`/rest/myCoupon/doQuery?${param.join('&')}`, {}).then(
        result => {
          console.log(result)
          Indicator.close()
          if (result.data.code === 200) {
            let res = result.data.data.dataList
            if (type === 'refresh') {
              self.$refs.scroller.finishPullToRefresh()
            }
            const arr = []
            let couponIdArr = []
            res.map((el, index) => {
              el['imageUrl'] = el.image
              el['rotate'] = {
                'transform': 'rotate(0)',
                'transition': 'all .3s linear'
              }
              el['showDetail'] = false
              el['expand'] = false
              if (couponIdArr.indexOf(el.couponId) === -1) {
                couponIdArr.push(el.couponId)
              }
            })
            couponIdArr.map((k, v) => {
              arr.push({
                couponId: k,
                arr: []
              })
            })
            res.map((el, index) => {
              arr.map((k, v) => {
                if (k.couponId === el.couponId) {
                  k.arr.push(el)
                }
              })
            })
            arr.map((el, index) => {
              el['showExpand'] = el.arr.length - 5 > 0 ? true : false,
              el['lastTotal'] = el.arr.length - 2
              el.arr.map((k, v) => {
                if (el.showExpand) {
                  if (v < 2) {
                    k.expand = true
                  }
                } else {
                  if (v < 5) {
                    k.expand = true
                  }
                }
              })
            })
            self.total = result.data.data.totalRow
            self.listData = self.listData.concat(arr)
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
    min-height: 100vh;
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
      z-index: 1000;
      span{
        display: block;
        width: 100%;
        height: .8rem;
        line-height: .8rem;
        text-align: center;
        background: #333;
        color: #fff;
        font-size: .3rem;
        border-bottom: 1px solid #dddddd;
        cursor: pointer;
        z-index: 1000;
        &:last-child{
          border-bottom: 0px;
        }
      }
    }
    .limheader{
      width: 100%;
      display: flex;
      justify-content: space-between;
      padding: 0 .3rem;
      align-items: center;
      height: 40px;
      background: #f1f1f1;
      box-sizing: border-box;
      z-index: 200;
      span{
        font-size: .24rem;
        color: #333;
        .hna{
          // display: inline-block;
          padding: 0 2px;
          height: .25rem;
          line-height: .25rem;
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
      justify-content: space-between;
      background-color: #fff;
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
    .mark{
      background: #f1f1f1;
      height: 40px;
      line-height: 40px;
      width: 100%;
      text-align: left;
      padding: 0 .3rem;
      box-sizing: border-box;
      font-size: .22rem;
      z-index: 200;
      span{
        color: #ff713f;
        font-size:.3rem;
        font-weight: bold;
      }
    }
    .h-list{
      display: flex;
      flex-direction: column;
      align-items: flex-start;
      width: 100%;
      height: 6rem;
      position: relative;
      left: 0;
      right: 0;
      box-sizing: border-box;
      background: #f1f1f1;
      .scroll{
        box-sizing: border-box;
        padding: 0 .3rem;
      }
      .liWrap{
        width: 100%;
        box-sizing: border-box;
        padding-top: .2rem;
        background: #ffffff;
        border-radius: 3px;
        margin-bottom: .2rem;
        .li{
          display: inline-block;
          width: 100%;
          padding: 0 .2rem;
          box-sizing: border-box;
          .about{
            display: flex;
            align-items: center;
            img{
              width: 1.5rem;
              height: 1.15rem;
              margin-right: .2rem;
              flex-shrink: 0;
            }
            .info{
              box-sizing: border-box;
              width: 100%;
              padding-left: 0 .4rem;
              p{
                font-size: .28rem;
                color: #333;
                text-align: left;
                line-height: .36rem;
                margin: 0;
                padding: 0;
              }
              .op{
                width: 100%;
                display: flex;
                justify-content: space-between;
                align-items: flex-end;
                .num{
                  display: flex;
                  align-items: flex-end;
                  font-size: .3rem;
                  color: #ff713f;
                  span{
                    font-size: .16rem;
                    position: relative;
                    bottom: 2px;
                  }
                }
                .detailBtn{
                  display: flex;
                  align-items: center;
                  font-size: .2rem;
                  .arrow{
                    display: inline-block;
                    height: .15rem;
                    width: .25rem;
                    background: url('../assets/images/arrow.png') no-repeat center center;
                    background-size: contain;
                  }
                }
                .liBtn{
                  display: flex;
                  align-items: center;
                  width: 1.45rem;
                  height: .45rem;
                  font-size: .22rem;
                  border-radius: 4px !important;
                  color: #fff;
                  background-color: #00acac;
                }
                .disBtn{
                  display: flex;
                  align-items: center;
                  width: 1.45rem;
                  height: .45rem;
                  font-size: .22rem;
                  border-radius: 4px !important;
                  color: #ababab;
                  border: 1px solid #dcdcdc;
                }
              }
            }
          }
          .detail{
            width: 100%;
            padding: 0;
            margin: 0;
            margin-top: .2rem;
            list-style: none;
            li{
              font-size: .2rem;
              text-align: left;
              margin-top: .05rem;
              color: #787878;
            }
          }
        }
        .expand{
          border-top: 1px solid #dddddd;
          width: 100%;
          padding: .1rem 0 .2rem;
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          span{
            font-size: .18rem;
            color: #787878;
          }
          >div{
            display: flex;
            justify-content: center;
            align-items: center;
            text-align: center;
            height: .25rem;
            width: 100%;
            .eBtn{
              display: flex;
              align-items: center;
              justify-content: center;
              font-size: .2rem;
              color: #ff713f;
              i{
                display: inline-block;
                font-size: .24rem;
              }
            }
          }
        }
      }
    }
  }
</style>
