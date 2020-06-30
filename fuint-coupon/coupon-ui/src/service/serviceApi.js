import axiosApi from './axiosApi'
import qs from 'qs'

export default {
  get(pathAlias, data) {
    return new Promise(function(resolve, reject) {
      axiosApi.get(pathAlias, data).then(response => {
        resolve(response)
      }).catch(error => {
        reject(error)
      })
    })
  },
  post(pathAlias, data) {
    return new Promise(function(resolve, reject) {
      axiosApi.post(pathAlias, qs.stringify(data)).then(response => {
        resolve(response)
      }).catch(error => {
        reject(error)
      })
    })
  },
  postWithNoToken(pathAlias, data) {
    return new Promise(function(resolve, reject) {
      axiosApi.postWithNoToken(pathAlias, qs.stringify(data)).then(response => {
        resolve(response)
      }).catch(error => {
        reject(error)
      })
    })
  }
}