import React from 'react'
import Component from '../utils/base/ComponentAlert'
import {Row, Col, Grid} from 'eagle-ui'
import {Alert, Button} from 'antd'

export default class Notice extends Component {

    static defaultProps = {
        name: 'admin'
    }

    constructor(props, context) {
        super(props, context)
        this.state = {
            name: props.name,
            lrwid: 3,
            contwid: 6
        }
    }

    toLogin() {
        window.location.href = window.location.origin
    }

    render() {
        const {name} = this.state
        return (
            <div style={{backgroundColor: '#fff', width: '100%', height: '100%', position: 'fixed'}}>
                <Grid>
                    <Row>
                        <Col sm={this.state.lrwid}/>
                        <Col sm={this.state.contwid} style={{textAlign: 'center'}}>
                            <h1 style={{fontSize: '24px', paddingBottom: '24px'}}>数据访问平台</h1>
                        </Col>
                        <Col sm={this.state.lrwid}/>
                    </Row>
                    <div>
                        <Row>
                            <Col sm={this.state.lrwid}/>
                            <Col sm={this.state.contwid}>
                                <Alert
                                    message='您的账号申请成功,务必保存！'
                                    description={'您的账号:' + name}
                                    type='success'
                                    showIcon/>
                            </Col>
                            <Row>
                                <Col sm={this.state.lrwid}/>
                                <Col sm={this.state.contwid}>
                                    <Col sm={12}>
                                        <Button type='primary' ghost onClick={::this.toLogin}>返回登录</Button>
                                    </Col>
                                </Col>
                                <Col sm={this.state.lrwid}/>
                            </Row>
                        </Row>
                    </div>
                </Grid>
            </div>
        )
    }
}