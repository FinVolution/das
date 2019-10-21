import React from 'react'
import {Row, Col, Grid, Panel, PanelContent, PanelHeader} from 'eagle-ui'
import {Inputlabel, InputPlus} from '../utils/index'
import {Button} from 'antd'
import {LogRegControl} from '../../../controller/Index'
import {View} from 'ea-react-dm-v14'
import $ from 'jquery'
import Alert from 'react-s-alert'
import {DataUtil} from '../utils/util/Index'
import {actionType} from '../../../constants/action-type'
import UserEnv from '../utils/util/UserEnv'
import Common from '../app/Common'

@View(LogRegControl)
export default class Login extends Common {

    constructor(props, context) {
        super(props, context)
        this.state = {
            lrwid: 3,
            contwid: 6,
            accountshow: false,
            pwderrshow: false
        }
        this.initeChekck()
    }

    componentDidMount() {
        const _this = this
        const innerPanelWidth = () => {
            const screenWidth = window.innerWidth
            if (screenWidth > 1024) {
                _this.setState({
                    lrwid: 4,
                    contwid: 4
                })
            }
        }
        innerPanelWidth()
        $(window).resize(function () {
            innerPanelWidth()
        })
    }

    checkaAcount(_this, val) {
        this.setState({
            accountshow: val == 0
        })
    }

    loginSubmit = () => {
        const loginfo = this.getValueToJson('LogRegModel.loginfo')
        if (loginfo.userName.length < 4 || loginfo.password.length < 5) {
            this.showSuccessMsg('请填写正确的账号和密码！！')
            return
        }
        this.props.login(loginfo, this, (_this, data) => {
            if (data.code == 200) {
                DataUtil.setLocalStorageData(actionType.LOGIN_ACCOUNT, {
                    id: data.msg.id,
                    email: data.msg.userEmail,
                    displayName: data.msg.userRealName
                })
                UserEnv.dasLoginSuccess()
                UserEnv.refresh(() => {
                    window.location.href = '/#/app'
                })
            } else {
                this.showSuccessMsg('请填写正确的账号和密码！！！或检查数据库连接！！！')
            }
        })
    }

    toRegister() {
        window.location.href = '#/register'
    }

    render() {
        return (
            <div style={{backgroundColor: '#fff', width: '100%', height: '100%', position: 'fixed'}}>
                <Alert stack={true} timeout={4000}/>
                <Grid style={{paddingTop: '100px'}}>
                    <Row>
                        <Col sm={this.state.lrwid}/>
                        <Col sm={this.state.contwid}>
                            <Panel egType='normal'>
                                <PanelHeader leftFlag={false} style={{textAlign: 'center'}}>
                                    <h1>DAS数据访问平台</h1>
                                </PanelHeader>
                                <PanelContent>
                                    <Row>
                                        <Col sm={12} style={{paddingBottom: 0}}>
                                            <Inputlabel title='域账号'>
                                                <InputPlus {...this.props} placeholder=''
                                                           valueLink='LogRegModel.loginfo.userName'
                                                           validRules={{maxLength: 20}}
                                                           onBlurCallback={::this.checkaAcount}/>
                                            </Inputlabel>
                                        </Col>
                                        <Col sm={12} style={{
                                            paddingTop: 0,
                                            display: this.state.accountshow ? 'block' : 'none'
                                        }}>
                                            <span style={{fontSize: '12px', color: 'red'}}>账号不能为空！</span>
                                        </Col>
                                    </Row>
                                    <Row>
                                        <Col sm={12} style={{paddingBottom: 0}}>
                                            <Inputlabel title='密码'>
                                                <InputPlus {...this.props} placeholder='密码' type='password'
                                                           validRules={{maxLength: 20}}
                                                           valueLink='LogRegModel.loginfo.password'/>
                                            </Inputlabel>
                                        </Col>
                                        <Col sm={12}
                                             style={{paddingTop: 0, display: this.state.pwderrshow ? 'block' : 'none'}}>
                                            <span style={{fontSize: '12px', color: 'red'}}>账户或密码错误！</span>
                                        </Col>
                                    </Row>
                                    <Row>
                                        <Col sm={12}>
                                            <Button type='primary' style={{width: '100%', height: '40px'}}
                                                    onClick={::this.loginSubmit}>登&nbsp;&nbsp;&nbsp;录</Button>
                                        </Col>
                                    </Row>
                                    <Row>
                                        <Col sm={12}>
                                            <a style={{width: '100%', height: '40px'}}
                                               onClick={::this.toRegister}>用户注册</a>
                                        </Col>
                                    </Row>
                                </PanelContent>
                            </Panel>
                        </Col>
                        <Col sm={this.state.lrwid}/>
                    </Row>
                </Grid>
            </div>
        )
    }
}