/**
 * Created by liang.wang on 18/6/29.
 */
import React from 'react'
import Component from './Component'
import Alert from 'react-s-alert'
import {notification} from 'antd'
import 'react-s-alert/dist/s-alert-default.css'
import 'react-s-alert/dist/s-alert-css-effects/slide.css'
import 'react-s-alert/dist/s-alert-css-effects/scale.css'
import 'react-s-alert/dist/s-alert-css-effects/flip.css'
import 'react-s-alert/dist/s-alert-css-effects/stackslide.css'
import './ComponentAlert.less'

export default class ComponentAlert extends Component {

    constructor(props, context) {
        super(props, context)
    }

    static defaultProps = {
        AlertType: {
            info: 'info',
            success: 'success',
            warning: 'warning',
            error: 'error'
        }
    }

    /**
     * @param type : info, success, warning, error
     * @param msg
     */
    showMsg = function (type, msg) {
        if (arguments.length == 0) {
            type = this.props.AlertType.error
            msg = 'showMsg 参数缺失'
        } else if (arguments.length == 1) {
            type = this.props.AlertType.info
        }
        Alert[type](msg, {
            position: 'top-right',
            effect: 'slide'
        })
    }
    /**
     * @param type : info, success, warning, error
     * @param html
     */
    showHTML = function (type, html) {
        if (arguments.length == 0) {
            type = this.props.AlertType.error
            html = 'showMsg 参数缺失'
        } else if (arguments.length == 1) {
            type = this.props.AlertType.info
        }
        Alert[type](html, {
            position: 'top-right',
            effect: 'slide',
            html: true
        })
    }
    /**
     * @param type : info, success, warning, error
     * @param msg
     */
    showMsgTop = function (type, msg) {
        if (arguments.length == 0) {
            type = this.props.AlertType.error
            msg = 'showMsgTop 参数缺失'
        } else if (arguments.length == 1) {
            type = this.props.AlertType.info
        }
        Alert[type](msg, {
            position: 'top'
        })
    }
    /**
     * @param type : info, success, warning, error
     * @param html
     */
    showHTMLTop = function (type, html) {
        if (arguments.length == 0) {
            type = this.props.AlertType.error
            html = 'showMsg 参数缺失'
        } else if (arguments.length == 1) {
            type = this.props.AlertType.info
        }
        Alert[type](html, {
            position: 'top',
            html: true
        })
    }

    showSuccessMsg(msg) {
        this.showMsg('success', msg)
    }

    showErrorsMsg(msg) {
        this.showMsg('error', msg)
    }

    showErrorsNotification(msg) {
        let style = {
            width: 450,
            marginLeft: -111
        }
        if (msg.length > 150 && msg.length <= 400) {
            style = {
                width: 500,
                marginLeft: -160
            }
        } else if (msg.length > 400) {
            style = {
                width: 600,
                marginLeft: -260
            }
        }
        const args = {
            type: 'error',
            message: '错误信息',
            description: msg,
            duration: 0,
            style: style
        }
        notification.open(args)
    }

    render() {
        return (
            <div>重写父类render()方法,需要把Alert添加到对应的组件
                <Alert stack={true} timeout={4000}/>
            </div>
        )
    }
}