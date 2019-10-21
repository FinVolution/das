import FrwkUtil from './FrwkUtil'

/**
 * 用户登录和权限等信息
 */
let UserEnv = UserEnv || {}

UserEnv.getDasEnv = () => {
    return window.DASENV
}

UserEnv.getUser = () => {
    return window.DASENV.user
}

UserEnv.isAdmin = () => {
    return window.DASENV.isAdmin || window.DASENV.admin
}

UserEnv.isDasLogin = () => {
    return window.DASENV.isDasLogin || window.DASENV.dasLogin
}

UserEnv.dasLoginSuccess = () => {
    window.DASENV.isDasLogin = false
}

UserEnv.dasSyncTarget = () => {
    return window.DASENV.dasSyncTarget
}

UserEnv.getConfigCenterName = () => {
    return window.DASENV.configName ? window.DASENV.configName : '配置中心'
}

UserEnv.getSecurityCenterName = () => {
    return window.DASENV.securityName ? window.DASENV.securityName : '安全服务'
}

UserEnv.refresh = callBack => {
    FrwkUtil.fetch.fetchGet('/config/env', null, UserEnv, data => {
        if (data.code === 200) {
            window.DASENV = data.msg
            callBack && callBack(data)
        }
    })
}

export default UserEnv
