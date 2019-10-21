import React from 'react'
import Component from '../../utils/base/ComponentAlert'
import {Grid, Panel, PanelContent, PanelHeader} from 'eagle-ui'
import {Inputlabel, InputPlus} from '../../utils/index'
import {Row, Col, Select, Button, Steps, Alert as AlertMsg, Modal} from 'antd'
import {InitControl} from '../../../../controller/Index'
import {View} from 'ea-react-dm-v14'
import Alert from 'react-s-alert'
import $ from 'jquery'
import {DataUtil} from '../../utils/util/Index'

@View(InitControl)
export default class Init extends Component {

    constructor(props, context) {
        super(props, context)
        this.modelName = 'InitModel'
        this.objName = this.modelName + '.item'
        this.admin = this.modelName + '.admin'
        this.loadList = this.props.loadList
        this.steps = [{
            key: '1',
            title: '数据库链接信息'
        }, {
            key: '2',
            title: '选择DAS数据库'
        }, {
            key: '3',
            title: '设置超级管理员密码'
        }, {
            key: '4',
            title: '提交初始化数据'
        }]
        this.state = {
            db_cataloss: [],
            current: 0,
            btndisable: true,
            lrwid: 4,
            contwid: 16,
            accountshow: false,
            pwderrshow: false
        }
    }

    componentDidMount() {
        const _this = this
        const innerPanelWidth = () => {
            const screenWidth = window.innerWidth
            if (screenWidth > 1024) {
                _this.setState({
                    lrwid: 6,
                    contwid: 12
                })
            }
        }
        innerPanelWidth()
        $(window).resize(function () {
            innerPanelWidth()
        })
    }

    next = () => {
        const current = this.state.current + 1
        switch (current) {
            case 1:
                return this.testConnect(current)
            case 2:
                return this.submitInitData(current)
            case 3:
                return this.initAdminInfo(current)
        }
    }

    testConnect = current => {
        const dbconectInfo = this.getValueToJson(this.objName)
        dbconectInfo && this.props.connectionTest(dbconectInfo, this, (_this, data) => {
            if (data.code == 200) {
                _this.showSuccessMsg('链接成功')
                _this.setState({current, db_cataloss: data.msg})
            } else {
                _this.showErrorsMsg('链接失败！请检查信息重试！')
                window.console.error(data.msg)
            }
        })
    }

    submitInitData(current) {
        const _this = this
        Modal.confirm({
            title: '温馨提示！！',
            content: '将会删除并创建DAS的相关物理库表，是否继续？',
            okText: '继续',
            cancelText: '取消',
            onOk() {
                const dbconectInfo = _this.getValueToJson(_this.objName)
                _this.props.addInit(dbconectInfo, _this, (_this, data) => {
                    if (data.code == 200) {
                        _this.showSuccessMsg('初始化成功')
                        window.setTimeout(() => {
                            _this.setState({current})
                        }, 800)
                    } else {
                        _this.showErrorsNotification(data.msg)
                    }
                })
            }
        })
    }

    initAdminInfo(current) {
        const admin = this.getValueToJson(this.admin)
        this.props.initAdminInfo(admin, this, (_this, data) => {
            if (data.code == 200) {
                _this.showSuccessMsg('admin初始化成功')
                window.setTimeout(() => {
                    _this.setState({current})
                }, 800)
            } else {
                _this.showErrorsNotification(data.msg)
            }
        })
    }

    onSetValueByReducersCallback = val => {
        const dbconectInfo = this.getValueToJson(this.objName)
        let flag = false
        const arr = [dbconectInfo.db_type, dbconectInfo.db_address, dbconectInfo.db_port, dbconectInfo.db_user, dbconectInfo.db_password, dbconectInfo.dbname]
        flag = arr.every(f => {
            return String(f).length > 0
        })
        if (arr.every(f => {
            return String(f).length > 0
        })) {
            this.setState({btndisable: false})
        } else {
            flag = true
            this.setState({btndisable: flag})
        }
        if (!val && !flag) {
            this.setState({btndisable: true})
        }
    }

    onSelectCallback = item => {
        this.replaceValueToItem(this.objName, 'dbname', item)
    }

    toLogin() {
        window.location.href = window.location.origin + '#/login'
    }

    render() {
        const Step = Steps.Step
        const {btndisable, current, db_cataloss} = this.state
        const {initmodel, setValueByReducers} = this.props
        const _props = {setValueByReducers, initmodel}
        const children = []
        db_cataloss && !DataUtil.is.String(db_cataloss) && db_cataloss.forEach(item => {
            children.push(<Select.Option title={String(item)} value={item} key={item}>{item}</Select.Option>)
        })
        return (
            <div style={{backgroundColor: '#fff', width: '100%', height: '100%', position: 'fixed'}}>
                <Alert stack={true} timeout={4000}/>
                <Grid style={{paddingTop: '100px'}}>
                    <Row style={{height: '100px'}}>
                        <Col sm={3}/>
                        <Col sm={18}>
                            <Steps current={current}>
                                {this.steps.map(item => <Step key={item.title} title={item.title}/>)}
                            </Steps>
                        </Col>
                        <Col sm={3}/>
                    </Row>
                    <Row>
                        <Col sm={this.state.lrwid}/>
                        <Col sm={this.state.contwid}>
                            <Panel egType='normal'>
                                <PanelHeader leftFlag={false} style={{textAlign: 'center'}}>
                                    <h1>DAS-Console数据初始化</h1>
                                </PanelHeader>
                                <PanelContent>
                                    <div style={{display: current === 0 ? 'block' : 'none'}}>
                                        <Row>
                                            <Col sm={24} style={{paddingBottom: 0}}>
                                                <Inputlabel type={1} star={true} title='DB Address'>
                                                    <InputPlus {..._props}
                                                               validRules={{maxLength: 50}}
                                                               valueLink={this.objName + '.db_address'}
                                                               onSetValueByReducersCallback={::this.onSetValueByReducersCallback}/>
                                                </Inputlabel>
                                            </Col>
                                        </Row>
                                        <Row>
                                            <Col sm={24} style={{paddingBottom: 0}}>
                                                <Inputlabel type={1} star={true} title='DB Port'>
                                                    <InputPlus {..._props}
                                                               validRules={{isInt: true, maxLength: 10}}
                                                               valueLink={this.objName + '.db_port'}
                                                               onSetValueByReducersCallback={::this.onSetValueByReducersCallback}/>
                                                </Inputlabel>
                                            </Col>
                                        </Row>
                                        <Row>
                                            <Col sm={24} style={{paddingBottom: 0}}>
                                                <Inputlabel type={1} star={true} title='DB User'>
                                                    <InputPlus {..._props}
                                                               validRules={{maxLength: 40}}
                                                               valueLink={this.objName + '.db_user'}
                                                               onSetValueByReducersCallback={::this.onSetValueByReducersCallback}/>
                                                </Inputlabel>
                                            </Col>
                                        </Row>
                                        <Row>
                                            <Col sm={24} style={{paddingBottom: 0}}>
                                                <Inputlabel type={1} star={true} title='DB Password'>
                                                    <InputPlus {..._props}
                                                               type='password'
                                                               valueLink={this.objName + '.db_password'}
                                                               onSetValueByReducersCallback={::this.onSetValueByReducersCallback}/>
                                                </Inputlabel>
                                            </Col>
                                        </Row>
                                    </div>
                                    <div style={{display: current === 1 ? 'block' : 'none'}}>
                                        <Inputlabel title='选择对应的物理库'>
                                            <Select style={{width: '100%'}}
                                                    tokenSeparators={[',']}
                                                    onSelect={::this.onSelectCallback}>
                                                {children}
                                            </Select>
                                        </Inputlabel>
                                    </div>
                                    <div style={{display: current === 2 ? 'block' : 'none'}}>
                                        <p>设置超级管理员密码</p>
                                        <Inputlabel type={1} star={true} title='密码'>
                                            <InputPlus {..._props}
                                                       type='password'
                                                       valueLink={this.admin + '.password'}/>
                                        </Inputlabel>
                                        <Inputlabel type={1} star={true} title='确认密码'>
                                            <InputPlus {..._props}
                                                       type='password'
                                                       valueLink={this.admin + '.password1'}/>
                                        </Inputlabel>
                                    </div>
                                    <Row>
                                        <Col sm={24}>
                                            {
                                                current < this.steps.length - 1
                                                &&
                                                <Button type='primary' style={{width: '100%', height: '40px'}}
                                                        disabled={btndisable}
                                                        onClick={::this.next}>下一步</Button>
                                            }
                                            {
                                                current === this.steps.length - 1 && <div>
                                                    <Row style={{paddingBottom: '10px'}}>
                                                        <AlertMsg message='您的账号申请成功,务必保存！'
                                                                  description={'您的账号: admin'}
                                                                  type="success"
                                                                  showIcon/>
                                                    </Row>
                                                    <Row>
                                                        <Button type="primary" ghost
                                                                onClick={::this.toLogin}>返回登录</Button>
                                                    </Row>
                                                </div>
                                            }
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