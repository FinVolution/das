/**
 * Created by liang.wang on 18/9/14.
 */
import React from 'react'
import {View} from 'ea-react-dm-v14'
import {UserControl} from '../../../../controller/Index'
import TablePanle from '../base/TablePanle'
import ManagePanle from '../base/ManagePanle'
import {Modal, Input, Popconfirm, Tooltip, Button} from 'antd'
import {Inputlabel, InputPlus} from '../../utils'
import FrwkUtil from '../../utils/util/FrwkUtil'
import {DataUtil} from '../../utils/util/Index'
import {actionType} from '../../../../constants/action-type'
import {display} from '../../../../model/base/BaseModel'

@View(UserControl)
export default class UserManage extends ManagePanle {

    constructor(props, context) {
        super(props, context)
        this.modelName = 'UserModel'
        this.editorTitlte = '用户'
        this.searchInfo = this.modelName + '.searchInfo'
        this.objName = this.modelName + '.loginUser'
        this.loadList = this.props.loadUserList
        this.addItem = this.props.addUser
        this.deleteItem = this.props.deleteUser
        this.updateItem = this.props.updateUser
        this.dispalyManage.initButtons('/user/buttons')
        this.initValueLink()
        this.state = {
            title: '添加' + this.editorTitlte,
            confirmLoading: false
        }
    }

    onBlurCallback = (_this, val) => {
        this.props.getWorkInfo({name: val}, this, (_this, rs) => {
            if (rs.code === 200) {
                if (rs.msg.active) {
                    _this.setState({'user': rs.msg})
                } else if (rs.msg.active === false) {
                    this.showSuccessMsg('该员工已注销')
                }
            } else if (rs.code === 500) {
                _this.showErrorsMsg(rs.msg)
            }
        })
    }

    onSearch = val => {
        this.props.getWorkInfo({name: val}, this, (_this, rs) => {
            if (rs.code === 200) {
                if (rs.msg.active) {
                    _this.setState({'user': rs.msg})
                } else if (rs.msg.active === false) {
                    this.showSuccessMsg('该员工已注销')
                }
            } else if (rs.code === 500) {
                _this.showErrorsMsg(rs.msg)
            }
        })
    }

    createModel = () => {
        const Search = Input.Search
        const {setValueByReducers, usermodel} = this.props
        const _props = {setValueByReducers, usermodel}
        const {confirmLoading} = this.state
        const item = this.getValueToJson(this.objName)
        const states = this.getValueToJson(this.states)
        return <Modal title={this.state.title}
                      width={800}
                      visible={states.editeVisible}
                      onOk={::this.handleOk}
                      confirmLoading={confirmLoading}
                      onCancel={::this.handleCancel}>
            <Inputlabel title='域账号' display={states.editerType === 0}>
                <Search placeholder='请输入域账号查询' onSearch={value => this.onSearch(value)} size='large'/>
            </Inputlabel>
            <Inputlabel title='域账号' display={states.editerType === 1}>
                <InputPlus {..._props} disabled={true}
                           defaultValue={item.userName} valueLink={this.objName + '.userName'}/>
            </Inputlabel>
            <Inputlabel title='工号'>
                <InputPlus {..._props} disabled={true}
                           defaultValue={item.userNo} valueLink={this.objName + '.userNo'}/>
            </Inputlabel>
            <Inputlabel title='电子邮箱'>
                <InputPlus {..._props}
                           defaultValue={item.userEmail} valueLink={this.objName + '.userEmail'}/>
            </Inputlabel>
        </Modal>
    }

    simulateLogin = row => {
        FrwkUtil.fetch.fetchPost('/logReg/simulateLogin', row.toJS(), this, (data) => {
                if (data.code === 200) {
                    DataUtil.setLocalStorageData(actionType.LOGIN_ACCOUNT, {
                        id: data.msg.id,
                        email: data.msg.userEmail,
                        displayName: data.msg.userRealName
                    })
                    setTimeout(() => {
                        window.location.href = window.location.origin
                    }, 500)
                } else {
                    this.showErrorsNotification(data.msg)
                }
            }
        )
    }

    customButton = () => {
        const displaybuttons = this.getValueToJson(this.modelName + display.buttons_path)
        const button = (row) => {
            return <span style={{paddingRight: '8px'}}>
             <Popconfirm placement='topLeft' title='确定模拟登录?' okText='是' cancelText='否'
                         onConfirm={() => ::this.simulateLogin(row)}>
                            <Tooltip placement='left' title='连接串同步'>
                                <Button icon='user' size='small'/>
                            </Tooltip>
                 </Popconfirm>
                            </span>
        }

        const customButtons = [
            {
                type: 'simLogin',
                button: button
            }
        ]

        return {
            displaybuttons: displaybuttons,
            customButtons: customButtons
        }
    }

    render() {
        const states = this.getValueToJson(this.states)
        return (<div>
            {states.editeVisible ? ::this.createModel() : null}
            <TablePanle title='用户管理' navigation='数据访问平台 / 用户管理'
                        type={2}
                        lineTop={0}
                        modelName={this.modelName}
                        add={::this.add}
                        delete={::this.delete}
                        editor={::this.editor}
                        usermodel={this.props.usermodel}
                        customButton={::this.customButton}
                        setValueByReducers={this.props.setValueByReducers}
                        loadList={::this.loadList}/>
        </div>)
    }
}
