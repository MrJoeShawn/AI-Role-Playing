// src/utils/request.js
import axios from 'axios'

// 创建axios实例，配置后端基础地址
const request = axios.create({
  baseURL: 'http://localhost:8082', // 后端接口前缀
  timeout: 60000, // 请求超时时间
})

export default request
