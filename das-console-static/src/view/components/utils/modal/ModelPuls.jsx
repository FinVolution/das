/**
 * Created by liang.wang on 18/9/14.
 */
import React from 'react'
import Component from '../base/Component'
import {Modal} from 'antd'

export default class ModelPuls extends Component {

    static defaultProps = {
        title: '',
        width: 800,
        confirmLoading: false,
        display: true
    }

    constructor(props, context) {
        super(props, context)
        this.state = {
            title: props.title,
            visible: props.visible,
            confirmLoading: props.confirmLoading
        }
    }

    componentWillReceiveProps(nextProps) {
        const {visible, title, confirmLoading} = nextProps
        if (title != this.state.title || visible != this.state.visible || confirmLoading != this.state.confirmLoading) {
            this.setState({title, visible, confirmLoading})
        }
    }

    render() {
        const {children} = this.props
        const {title, width, visible, confirmLoading} = this.state
        return <Modal title={title}
                      width={width}
                      visible={visible}
                      confirmLoading={confirmLoading}
                      onOk={::this.props.handleOk}
                      onCancel={::this.props.handleCancel}>
            {children}
        </Modal>
    }
}
