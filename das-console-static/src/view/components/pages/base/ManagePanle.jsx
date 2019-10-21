/**
 * Created by liang.wang on 18/9/14.
 */
import React from 'react'
import Component from '../../utils/base/ComponentAlert'
import {DataUtil, UserEnv} from '../../utils/util/Index'
import Immutable from 'immutable'
import FrwkUtil from '../../utils/util/FrwkUtil'
import {Modal} from 'antd'
import {display} from '../../../../model/base/BaseModel'
import ApolloCkeckResult from './apolloCkeckResult/ApolloCkeckResult'
import ApolloSaecCkeckResult from './apolloCkeckResult/ApolloSaecCkeckResult'
import _ from 'underscore'

export default class ManagePanle extends Component {

    constructor(props, context) {
        super(props, context)
        this.syncLink = ''  //同步
        this.checkLink = '' //校验
        this.cleanExceptKeys = []
        this.searchInfoSelectId = ''
        this.initValueLink()
    }

    initValueLink = () => {
        this.configName = UserEnv.getConfigCenterName()
        this.searchResultList = this.modelName + '.list'
        this.columnInfo = this.modelName + '.columnInfo'
        this.searchInfo = this.modelName + '.searchInfo'
        this.editeVisible = this.states + '.editeVisible'
        this.states = this.modelName + '.states'
        this.checkVisible = this.states + '.checkVisible'
        this.displayItemsButtons = this.modelName + '.displayItems.buttons'
    }

    updateState = _visible => {
        setTimeout(() => {
            this.setState({confirmLoading: false})
            this.setValueByReducers(this.editeVisible, _visible)
        }, 1000)
    }
    ittemCheck = () => {
        return true
    }
    handleOk = () => {
        const object = this.getValueToJson(this.objName)
        if (!this.ittemCheck(object)) {
            return
        }
        const states = this.getValueToJson(this.states)
        let visible = false
        this.setState({
            confirmLoading: true
        }, () => {
            try {
                if (states.editerType == 0) {
                    this.addItem(object, this, (_this, rs) => {
                        if (rs.code === 200) {
                            this.reload()
                            this.cleanObjName()
                            this.showSuccessMsg('添加成功')
                        } else {
                            this.showErrorsNotification(rs.msg)
                            visible = true
                        }
                    })
                } else if (states.editerType == 1) {
                    this.updateItem(object, this, (_this, rs) => {
                        if (rs.code === 200) {
                            this.reload()
                            this.cleanObjName()
                            this.showSuccessMsg('修改成功')
                        } else {
                            this.showErrorsNotification(rs.msg)
                            visible = true
                        }
                    })
                }
            } catch (e) {
                this.setState({confirmLoading: false})
            } finally {
                this.updateState(visible)
            }
        })
    }

    reloadCallBack = () => {
    }
    reload = () => {
        setTimeout(() => {
            this.loadList(this.getValueToJson(this.searchInfo), this, null, this.loadListFiler)
            this.reloadCallBack()
        }, 500)
    }

    handleCancel = () => {
        this.setState({
            visible: false,
        })
        this.setValueByReducers(this.editeVisible, false)
    }

    cleanObjName = () => {
        let item = this.getValueByReducers(this.objName).toJS()
        this.setValueByReducers(this.objName, Immutable.fromJS(DataUtil.ObjUtils.cleanJson(item, this.cleanExceptKeys)))
    }

    /**
     * 子类可覆盖
     * @returns {{state: boolean, msg: string}}
     */
    addValidate = () => {
        return {
            state: true,
            msg: 'success'
        }
    }

    addCallBack = () => {
        this.cleanObjName()
    }
    add = () => {
        const rs = this.addValidate()
        if (!rs.state) {
            this.showErrorsMsg(rs.msg)
            return
        }
        this.addCallBack()
        const state = {
            title: '添加' + this.editorTitlte
        }
        this.setState(state)
        const states = {
            editeVisible: true,
            editerType: 0
        }
        this.replaceValuesToItem(this.states, states)
    }

    deleteCallBack = () => {
    }
    delete = obj => {
        this.deleteItem(obj, this, (_this, rs) => {
            if (rs.code === 200) {
                this.reload()
                this.showSuccessMsg('删除成功')
                this.deleteCallBack()
            } else {
                this.reload()
                this.showErrorsNotification(rs.msg)
            }
        })
    }

    editorCallBack = item => {
        return item
    }
    editorFiler = item => {
        return item
    }
    editor = item => {
        this.replaceValuesToItem(this.objName, this.editorFiler(item))
        this.editorCallBack(item)
        const state = {
            title: '编辑' + this.editorTitlte
        }
        this.setState(state)
        const states = {
            editeVisible: true,
            editerType: 1
        }
        this.replaceValuesToItem(this.states, states)
    }

    /**
     * 表格默认第一行选中
     * @param data
     */
    loadListFilerBefore = data => {
        return data
    }
    loadListFiler = data => {
        data = this.loadListFilerBefore(data)
        if (data.list && data.list.length > 0) {
            data.list.map((item, i) => {
                if (i === 0) {
                    item.checkbox = true
                } else {
                    item.checkbox = false
                }
            })
            this.setState({currentCheckedId: data.list[0].id})
        } else if (data.list && data.list.length === 0) {
            this.setState({currentCheckedId: 0})
        }
    }

    sync = item => {
        if (this.syncLink && this.syncLink.length > 5) {
            item = {id: item.id}
            FrwkUtil.fetch.fetchGet(this.syncLink, item, this, data => {
                if (data.code == 200) {
                    this.showSuccessMsg(this.configName + '同步数据成功！！')
                } else {
                    this.showErrorsNotification(this.configName + '同步数据失败！！可重试！！' + data.msg)
                }
            })
        }
    }

    check = item => {
        if (this.checkLink && this.checkLink.length > 5) {
            FrwkUtil.fetch.fetchGet(this.checkLink, {id: item.id}, this, data => {
                if ((data.code == 500 && DataUtil.is.String(data.msg) && ((DataUtil.is.Array(data.item) && data.item.length === 0) || data.item == null)) || (!data.item.appId && !data.item.namespace && data.item.message)) {
                    this.showErrorsNotification(data.msg)
                } else if (DataUtil.is.Object(data.item)) {
                    const states = {
                        checkVisible: true,
                        checkData: data
                    }
                    this.replaceValuesToItem(this.states, states)
                }
            })
        }
    }

    handleCheckCancel = () => {
        this.setValueByReducers(this.checkVisible, false)
    }

    createCheckModel = () => {
        const states = this.getValueToJson(this.states)
        if (states.checkData && states.checkData.item && states.checkData.item.list && states.checkData.item.list.length > 0) {
            const size = DataUtil.ObjUtils.getAtrNumber(states.checkData.item.list[0]).length
            if (size === 2) {
                return <Modal title={this.configName + '数据校验'} width={1400} visible={states.checkVisible}
                              onOk={this.handleCheckCancel}
                              onCancel={this.handleCheckCancel} afterClose={this.handleCheckCancel}>
                    <ApolloCkeckResult checkData={states.checkData}/>
                </Modal>
            } else if (size > 2) {
                return <Modal title={this.configName + '数据校验'} width={1400} visible={states.checkVisible}
                              onOk={this.handleCheckCancel}
                              onCancel={this.handleCheckCancel} afterClose={this.handleCheckCancel}>
                    <ApolloSaecCkeckResult checkData={states.checkData}/>
                </Modal>
            }
        }
    }

    getDefaultSelectedCallBack = id => {
        return id
    }
    beforeDefaultSelected = item => {
        return item
    }
    getDefaultSelected = item => {
        if (_.isEmpty(item) || !this.searchInfoSelectId) {
            return
        }
        this.beforeDefaultSelected(item)
        this.setValueByReducers(this.searchInfoSelectId, item[0].id, this, () => {
            this.reload()
            this.getDefaultSelectedCallBack(item[0].id)
        }, 300)
    }

    dispalyManage = {
        initButtons: url => {
            url && FrwkUtil.fetch.fetchGet(url, null, this, data => {
                if (data.code === 200) {
                    this.setValueToImmutable(this.displayItemsButtons, data.msg)
                }
            })
        },
        filter: (displaybuttons, _buttons, buttons) => {
            _buttons.forEach(e => {
                const key = display.buttons[e.type]
                if (displaybuttons[key]) {
                    buttons.push(e)
                }
            })
        }
    }

    visibleCallback = visible => {
        this.setValueByReducers(this.states + '.editeVisible', visible)
    }
}
