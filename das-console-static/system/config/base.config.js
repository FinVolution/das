var package = require('../../package.json')

var ci = require('../../f2eci.json')

var devPort = '3005'

module.exports = {
    //静态页存储目录
    html: "./src/view",
    //本地调试端口
    devPort: devPort,
    //调试默认打开的页面
    defaultStartPage: '/index.html',
    //web or app
    projectType: 'app',
    //生成目录
    "output": ci.dist,
    root: 'src/view/pages',
    env: ci.env,
    //资源对应的不同环境域名
    cdn: ci.urlPrefix
}