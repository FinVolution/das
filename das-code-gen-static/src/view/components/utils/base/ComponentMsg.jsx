/**
 * Created by liang.wang on 18/6/29.
 */
import React from 'react'
import Component from './Component'

export default class BaseComponent extends Component {

    constructor(props, context) {
        super(props, context)
    }

    msgOptions = {
        offset: 14,
        position: 'top right',
        theme: 'light',
        time: 5000,
        transition: 'scale'
    }

    /**
     * @param type : info, success, error
     * @param msg
     */
    showMsg = (type, msg) => {
        this.msg[type](msg)
    }

    render() {
        return (
            <h1>重写父类render()方法</h1>
        )
    }
}