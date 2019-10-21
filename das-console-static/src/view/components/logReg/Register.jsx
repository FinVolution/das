import React from 'react'
import {Row, Col, Grid, Panel, PanelContent} from 'eagle-ui'
import {Inputlabel, InputPlus} from '../utils/index'
import {Button, Steps, Icon, Alert, Spin} from 'antd'
import {LogRegControl} from '../../../controller/Index'
import {View} from 'ea-react-dm-v14'
import {DataUtil} from '../utils/util/Index'
import {actionType} from '../../../constants/action-type'
import $ from 'jquery'
import Common from '../app/Common'

@View(LogRegControl)
export default class Register extends Common {

    constructor(props, context) {
        super(props, context)
        this.state = {
            regBtn: 'dashed',
            lrwid: 3,
            contwid: 6,
            nameerrshow: false,
            userrealnameerrshow: false,
            usernoerrshow: false,
            emailerrshow: false,
            pwderrshow: false,
            pwderrshow2: false,
            stepStatus: ['process', 'wait', 'wait'],
            stepAudit: 'hourglass',
            spinShow: false
        }
    }

    componentDidMount() {
        const _this = this
        const innerPanelWidth = () => {
            let width = $(window).width()
            if (width > 1024) {
                _this.setState({
                    lrwid: 4,
                    contwid: 4
                })
            }
        }
        this.initeChekck()
        innerPanelWidth()
        $(window).resize(function () {
            innerPanelWidth()
        })
    }

    checkData() {
        const reginfo = this.getValueByReducers('LogRegModel.reginfo').toJS()
        const isCanReg = DataUtil.validate.email(reginfo.userEmail) && (reginfo.userName.length < 20 && reginfo.userName.length > 2) && (reginfo.password.length < 20 && reginfo.password.length > 5) && reginfo.password == reginfo.password2
        if (isCanReg) {
            this.setState({
                regBtn: 'primary'
            })
        } else {
            this.setState({
                regBtn: 'dashed'
            })
        }
        return isCanReg
    }

    checkEmail(val) {
        this.setState({
            emailerrshow: !DataUtil.validate.email(val)
        })
        this.checkData()
    }

    checkUserName(val) {
        this.setState({
            nameerrshow: val.length > 20 || val.length < 3
        })
        this.checkData()
    }

    checkUserRealName(val) {
        this.setState({
            userrealnameerrshow: val.length > 20 || val.length < 3
        })
        this.checkData()
    }

    checkUserNameNo(val) {
        this.setState({
            namenoerrshow: val.length > 20 || val.length < 3
        })
        this.checkData()
    }

    checkPwd1(val) {
        const pwd2 = this.getValueByReducers('LogRegModel.reginfo.password2')
        this.setState({
            pwderrshow: val.length > 20 || val.length < 6,
            pwderrshow2: pwd2 != val
        })
        this.checkData()
    }

    checkPwd2(val) {
        let pwd1 = this.getValueByReducers('LogRegModel.reginfo.password')
        this.setState({
            pwderrshow2: val.length > 20 || val.length < 6 || pwd1 != val
        })
        this.checkData()
    }

    register() {
        let reginfo = this.getValueByReducers('LogRegModel.reginfo').toJS()
        let timer
        if (this.checkData()) {
            this.props.register(reginfo, this, function (_this, data) {
                _this.setState({
                    stepStatus: ['finish', 'process', 'wait'],
                    stepAudit: 'loading',
                    spinShow: true
                }, () => {
                    if (data.code === 200) {
                        timer = window.setTimeout(() => {
                            _this.setState({
                                stepStatus: ['finish', 'finish', 'finish'],
                                stepAudit: 'hourglass',
                                spinShow: false
                            }, () => {
                                _this.setValueByReducers('LogRegModel.loginfo.accountId', data.msg)
                                _this.setValueByReducers('LogRegModel.regStatus', 1)
                                DataUtil.setLocalStorageData(actionType.LOGIN_ACCOUNT, {
                                    userName: data.msg.userName,
                                    password: data.msg.password
                                })
                                window.clearTimeout(timer)
                            })
                        }, 2000)
                    } else {
                        _this.showErrorsMsg(data.msg)
                        _this.setState({
                            stepStatus: ['process', 'wait', 'wait'],
                            stepAudit: 'hourglass',
                            spinShow: false
                        })
                    }
                })
            })
        }
    }

    toLogin() {
        window.location.href = window.location.origin + '#/login'
    }

    render() {
        const regStatus = this.getValueByReducers('LogRegModel.regStatus')
        const reginfo = this.getValueToJson('LogRegModel.reginfo')
        const Step = Steps.Step
        return (
            <div style={{backgroundColor: '#fff', width: '100%', height: '100%', position: 'fixed'}}>
                <Grid style={{paddingTop: '100px'}}>
                    <Row>
                        <Col sm={this.state.lrwid}/>
                        <Col sm={this.state.contwid} style={{textAlign: 'center'}}>
                            <h1 style={{fontSize: '24px', paddingBottom: '24px'}}>DAS数据访问平台</h1>
                        </Col>
                        <Col sm={this.state.lrwid}/>
                    </Row>
                    <Row>
                        <Col sm={this.state.lrwid}/>
                        <Col sm={this.state.contwid}>
                            <Steps>
                                <Step status={this.state.stepStatus[0]} title='申请账号' icon={<Icon type='user'/>}/>
                                <Step status={this.state.stepStatus[1]} title='审核中'
                                      icon={<Icon type={this.state.stepAudit}/>}/>
                                <Step status={this.state.stepStatus[2]} title='审核通过' icon={<Icon type='smile-o'/>}/>
                            </Steps>
                        </Col>
                        <Col sm={this.state.lrwid}/>
                    </Row>
                    <Spin tip='审核中，请稍后...' spinning={this.state.spinShow}>
                        <Row style={{display: regStatus == 0 ? 'block' : 'none'}}>
                            <Col sm={this.state.lrwid}/>
                            <Col sm={this.state.contwid}>
                                <Panel egType='normal'>
                                    <PanelContent>
                                        <Row>
                                            <Col sm={12} style={{paddingBottom: 0}}>
                                                <Inputlabel title='域账号'>
                                                    <InputPlus {...this.props} placeholder='请输入域账号，长度3到20之间'
                                                               valueLink='LogRegModel.reginfo.userName'
                                                               validRules={{maxLength: 20}}
                                                               onBlurCallback={::this.checkUserName}/>
                                                </Inputlabel>
                                            </Col>
                                            <Col sm={12} style={{paddingBottom: 0}}>
                                                <Inputlabel title='姓名'>
                                                    <InputPlus {...this.props} placeholder='请输入姓名，长度3到20之间'
                                                               valueLink='LogRegModel.reginfo.userRealName'
                                                               validRules={{maxLength: 20}}
                                                               onBlurCallback={::this.checkUserRealName}/>
                                                </Inputlabel>
                                            </Col>
                                            <Col sm={12}
                                                 style={{
                                                     paddingTop: 0,
                                                     display: this.state.nameerrshow ? 'block' : 'none'
                                                 }}>
                                                <span style={{fontSize: '12px', color: 'red'}}>用户名长度3到20之间！</span>
                                            </Col>


                                            <Col sm={12} style={{paddingBottom: 0}}>
                                                <Inputlabel title='工号'>
                                                    <InputPlus {...this.props} placeholder='请输入工号，长度3到20之间'
                                                               valueLink='LogRegModel.reginfo.userNo'
                                                               validRules={{maxLength: 20}}
                                                               onBlurCallback={::this.checkUserNameNo}/>
                                                </Inputlabel>
                                            </Col>
                                            <Col sm={12}
                                                 style={{
                                                     paddingTop: 0,
                                                     display: this.state.usernoerrshow ? 'block' : 'none'
                                                 }}>
                                                <span style={{fontSize: '12px', color: 'red'}}>工号长度3到20之间！</span>
                                            </Col>
                                        </Row>
                                        <Row>
                                            <Col sm={12} style={{paddingBottom: 0}}>
                                                <Inputlabel title='邮箱'>
                                                    <InputPlus {...this.props} placeholder='邮箱'
                                                               valueLink='LogRegModel.reginfo.userEmail'
                                                               validRules={{maxLength: 50}}
                                                               onBlurCallback={::this.checkEmail}/>
                                                </Inputlabel>
                                            </Col>
                                            <Col sm={12}
                                                 style={{
                                                     paddingTop: 0,
                                                     display: this.state.emailerrshow ? 'block' : 'none'
                                                 }}>
                                                <span style={{fontSize: '12px', color: 'red'}}>请输入正确的邮箱格式！</span>
                                            </Col>
                                        </Row>

                                        <Row>
                                            <Col sm={12} style={{paddingBottom: 0}}>
                                                <Inputlabel title='密码'>
                                                    <InputPlus {...this.props} placeholder='请输入密码，长度6到20之间'
                                                               type='password'
                                                               validRules={{maxLength: 20}}
                                                               valueLink='LogRegModel.reginfo.password'
                                                               onBlurCallback={::this.checkPwd1}/>
                                                </Inputlabel>
                                            </Col>
                                            <Col sm={12}
                                                 style={{
                                                     paddingTop: 0,
                                                     display: this.state.pwderrshow ? 'block' : 'none'
                                                 }}>
                                                <span style={{fontSize: '12px', color: 'red'}}>密码长度6到20之间！</span>
                                            </Col>
                                        </Row>
                                        <Row>
                                            <Col sm={12} style={{paddingBottom: 0}}>
                                                <Inputlabel title='确认密码'>
                                                    <InputPlus {...this.props} placeholder='确认密码' type='password'
                                                               validRules={{maxLength: 50}}
                                                               valueLink='LogRegModel.reginfo.password2'
                                                               onBlurCallback={::this.checkPwd2}/>
                                                </Inputlabel>
                                            </Col>
                                            <Col sm={12}
                                                 style={{
                                                     paddingTop: 0,
                                                     display: this.state.pwderrshow2 ? 'block' : 'none'
                                                 }}>
                                                <span style={{fontSize: '12px', color: 'red'}}>两次密码不一致！</span>
                                            </Col>
                                        </Row>
                                        <Row>
                                            <Col sm={12}>
                                                <Inputlabel title=''>
                                                    <Button type={this.state.regBtn}
                                                            style={{width: '100%', height: '40px'}}
                                                            onClick={::this.register}>立即申请</Button>
                                                </Inputlabel>
                                            </Col>
                                        </Row>
                                    </PanelContent>
                                </Panel>
                            </Col>
                            <Col sm={this.state.lrwid}/>
                        </Row>
                    </Spin>
                    <div style={{display: regStatus == 1 ? 'block' : 'none'}}>
                        <Row>
                            <Col sm={this.state.lrwid}/>
                            <Col sm={this.state.contwid}>
                                <Alert
                                    message='您的账号申请成功,务必保存！'
                                    description={'您的账号: ' + reginfo.userName}
                                    type='success'
                                    showIcon/>
                            </Col>
                            <Col sm={this.state.lrwid}/>
                        </Row>
                        <Row>
                            <Col sm={this.state.lrwid}/>
                            <Col sm={this.state.contwid}>
                                <Col sm={12}>
                                    <Button type='primary' ghost onClick={::this.toLogin}>返回登录</Button>
                                </Col>
                            </Col>
                            <Col sm={this.state.lrwid}/>
                        </Row>
                    </div>
                </Grid>
            </div>
        )
    }
}