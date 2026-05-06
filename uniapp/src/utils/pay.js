/**
 * 微信支付工具
 * 注意：实际使用需要申请微信支付商户号并完成配置
 */

// 支付类型
export const PayType = {
  WECHAT: 1,    // 微信支付
  BALANCE: 2,   // 余额支付
  CASH: 3,      // 现金支付
  CARD: 4       // 卡券支付
}

// 唤起微信支付
export function requestWxPay(orderId, totalFee) {
  return new Promise((resolve, reject) => {
    // 实际调用 uni.requestPayment
    uni.requestPayment({
      provider: 'wxpay',
      timeStamp: String(Date.now()),
      nonceStr: 'random_' + Math.random().toString(36).substr(2),
      package: `prepay_id=mock_prepay_${orderId}`,
      signType: 'HMAC-SHA256',
      paySign: 'mock_sign',
      success: (res) => {
        resolve(res)
      },
      fail: (err) => {
        reject(err)
      }
    })
  })
}

// 模拟支付（用于测试）
export function mockPay(orderId, totalFee) {
  return new Promise((resolve) => {
    uni.showModal({
      title: '模拟支付',
      content: `订单金额: ¥${totalFee}\n是否确认支付？`,
      success: (res) => {
        if (res.confirm) {
          resolve({ code: 200, message: '支付成功' })
        } else {
          resolve({ code: 500, message: '用户取消' })
        }
      }
    })
  })
}

// 支付结果查询
export function queryPayResult(orderId) {
  return new Promise((resolve) => {
    // 模拟查询
    setTimeout(() => {
      resolve({ paid: true, status: 'success' })
    }, 500)
  })
}
