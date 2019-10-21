import React from 'react'
import Component from '../utils/base/ComponentAlert'
import {FrwkUtil, UserEnv} from '../utils/util/Index'

export default class Common extends Component {

    constructor(props, context) {
        super(props, context)
    }

    initeChekckCallBack = data => {
        if (data.code === 500) {
            window.location.href = '#/app'
        }
    }

    loginCkeckCallBack = () => {
    }

    initeChekck() {
        FrwkUtil.fetch.fetchGet('/config/datasourceValid', '', this, data => {
                this.initeChekckCallBack(data)
            }
        )
    }

    loginCkeck() {
        if (UserEnv.getDasEnv().isDasLogin()) {
            window.location.href = '#/login'
        }
        if (window.DASENV && window.DASENV.user) {
            this.loginCkeckCallBack()
        }
    }

    render() {
        return <div/>
    }
}