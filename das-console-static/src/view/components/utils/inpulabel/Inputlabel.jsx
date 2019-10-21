/**
 * Created by liang.wang on 18/9/14.
 */
import React from 'react'
import Component from '../base/Component'
import {Row, Col} from 'antd'
import {Row as ERow, Col as ECol} from 'eagle-ui'
import _ from 'underscore'
import './Inputlabel.less'

export default class Inputlabel extends Component {

    static defaultProps = {
        type: 0, //1、横排、其他，横排
        star: false,
        title: '',
        style: {},
        display: true
    }

    constructor(props, context) {
        super(props, context)
        this.state = {
            title: props.title,
            display: props.display
        }
    }

    componentWillReceiveProps(nextProps) {
        const {display, title} = nextProps
        if (display != this.state.display || title != this.state.title) {
            this.setState({display, title})
        }
    }

    render() {
        const {type, star, children} = this.props
        const {display, title} = this.state
        const style = _.extend({padding: '10px'}, this.props.style)
        if (!children || !display) {
            return null
        }
        if (type === 1) {
            return <div className='inputlabel'>
                <ERow className='pad-row'>
                    <ECol sm={3} className="base-col">
                        <span className='spanInline'>{title}:</span>
                        {star ? <span className="redFont spanInline">*</span> : null}
                    </ECol>
                    <ECol sm={7} end>
                        {children}
                    </ECol>
                </ERow>
            </div>
        }
        return <Row style={style}>
            <Col>{star ? <span className="redFont spanInline">*</span> : null}{title}</Col>
            <Col sm={24}>
                {children}
            </Col>
        </Row>
    }
}
