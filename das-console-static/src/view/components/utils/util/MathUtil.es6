let MathUtil = MathUtil || {}

MathUtil.REGS = {
    /**
     * 截取第一个点号之后的所有内容
     * 127.0.0.1 ==> 0.0.1
     */
    'subStringByFirstPoint': /\.(.+?)$/
}

MathUtil.match = function (reg, str) {
    return str.match(reg)
}

export default MathUtil