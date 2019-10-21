/**
 * Created by liang.wang on 18/9/14.
 */
import React from 'react'
import ManagePanle from '../base/ManagePanle'
import TablePanle from '../base/TablePanle'
import {View} from 'ea-react-dm-v14'
import {AppGroupControl} from '../../../../controller/Index'
import {Modal, Row, Col, Button} from 'antd'
import {Inputlabel, InputPlus, SelectPlus, RadioPlus, TextArea} from '../../utils/index'
import DataUtil from '../../utils/util/DataUtil'
import FrwkUtil from '../../utils/util/FrwkUtil'
import {das_msg, serverEnabled} from '../../../../model/base/BaseModel'

@View(AppGroupControl)
export default class AppGroupManage extends ManagePanle {

    constructor(props, context) {
        super(props, context)
        this.editorTitlte = '应用组'
        this.modelName = 'AppGroupModel'
        this.objName = this.modelName + '.appGroup'
        this.syncLink = '/appGroup/sync'
        this.checkLink = '/appGroup/check'
        this.serverList = this.modelName + '.serverList'
        this.projectList = this.modelName + '.projectList'
        this.loadList = this.props.loadList
        this.addItem = this.props.addAppGroup
        this.deleteItem = this.props.deleteAppGroup
        this.updateItem = this.props.updateAppGroup
        this.appGroupId = 0
        this.state = {
            title: '添加' + this.editorTitlte,
            confirmLoading: false,
            currentCheckedId: 0
        }
        this.initValueLink()
    }

    /** @Override **/
    addCallBack = () => {
        this.loadProjects(true)
        this.loadServers(true)
        this.cleanObjName()
    }

    /** @Override **/
    reloadCallBack = () => {
        //this.loadProjects(this.appGroupId, null, null, this.loadListFiler)
    }

    /** @Override **/
    editorCallBack = item => {
        this.appGroupId = item.id
        this.loadProjects(false)
        this.loadServers(false)
        /* FrwkUtil.fetch.fetchGet('/project/projectsByAppGroupId', {appGroupId: this.appGroupId}, this, (data, _this) => {
             _this.listFiler(_this, _this.objName + '.projects', data)
         })*/
    }

    /** @Override **/
    /*editorFiler = item => {
        if (!item.projects) {
            item.projects = []
        }
        return Immutable.fromJS(item)
    }*/

    loadProjects = isAll => {
        FrwkUtil.fetch.fetchGet('/project/projectsNoGroup', isAll ? null : {appGroupId: this.appGroupId}, this, (data, _this) => {
            if (data.code === 200) {
                const projectList = _this.getValueToJson(_this.projectList)
                if (!DataUtil.ObjUtils.isEqual(projectList, data.msg)) {
                    _this.setValueByReducers(_this.projectList, data.msg)
                }
            } else if (data.code === 500) {
                _this.showErrorsNotification(data.msg)
            }

            //_this.listFiler(_this, _this.modelName + '.projectList', data)
        })
    }

    loadServers = isAll => {
        FrwkUtil.fetch.fetchGet('/serverGroup/serversNoGroup', isAll ? null : {appGroupId: this.appGroupId}, this, (data, _this) => {
            if (data.code === 200) {
                //const serverList = _this.getValueToJson(_this.serverList)
                //if (!DataUtil.ObjUtils.isEqual(serverList, data.msg)) {
                _this.setValueByReducers(_this.serverList, data.msg)
                //}
            } else if (data.code === 500) {
                _this.showErrorsNotification(data.msg)
            }
        })
    }

    /*listFiler = (_this, valueLink, data) => {
           if (data.code === 200 && data.msg.length > 0) {
               let rs = DataUtil.ObjUtils.transformJson(data.msg, {id: 'id', name: 'name'})
               _this.setValueByReducers(valueLink, rs)
           }
     }*/

    onClickDelete = () => {
        this.setValueByReducers(this.objName + '.serverGroupId', 0)
    }

    createModel = () => {
        const {confirmLoading} = this.state
        const {setValueByReducers, appgroupmodel} = this.props
        const _props = {setValueByReducers, appgroupmodel}
        const projectList = this.getValueToJson(this.projectList)
        const serverList = this.getValueToJson(this.serverList)
        const appGroup = this.getValueToJson(this.objName)
        const states = this.getValueToJson(this.states)
        return <Modal title={this.state.title} width={800} visible={states.editeVisible} confirmLoading={confirmLoading}
                      onOk={::this.handleOk} onCancel={::this.handleCancel}>
            <Inputlabel title='应用组名称'>
                <InputPlus {..._props}
                           valueLink={this.objName + '.name'} validRules={{isDbName: true, maxLength: 24}}
                           defaultValue={appGroup.name} placeholder={das_msg.apollo_namespace}
                           disabled={states.editerType == 1}/>
            </Inputlabel>
            <Inputlabel title='是否是远程连接Das Server'>
                <RadioPlus {..._props}
                           items={serverEnabled} selectedId={appGroup.serverEnabled}
                           valueLink={this.objName + '.serverEnabled'}/>
            </Inputlabel>
            <Inputlabel title='服务器组'>
                <Row>
                    <Col sm={20}>
                        <SelectPlus {..._props} selectedId={appGroup.serverGroupId} items={serverList}
                                    selectedIds={appGroup.projectIds} valueLink={this.objName + '.serverGroupId'}/>
                    </Col>
                    <Col sm={4} style={{paddingLeft: '3px'}}>
                        <Button type='primary' size='large' disabled={appGroup.serverGroupId == 0} ghost
                                style={{width: '100%'}} onClick={::this.onClickDelete}>取消</Button>
                    </Col>
                </Row>
            </Inputlabel>
            <Inputlabel title='应用名称(项目名)'>
                <SelectPlus {..._props} mode='multiple'
                            selectedIds={appGroup.projectIds} items={projectList} valueLink={this.objName + '.items'}/>
            </Inputlabel>
            <Inputlabel title='备注'>
                <TextArea {..._props}
                          valueLink={this.objName + '.comment'} defaultValue={appGroup.comment}/>
            </Inputlabel>
        </Modal>
    }

    render() {
        const states = this.getValueToJson(this.states)
        return (<div>
            {states.editeVisible ? ::this.createModel() : null}
            {states.checkVisible ? ::this.createCheckModel() : null}
            <TablePanle title='应用组管理' navigation='数据访问平台 / 应用组管理'
                        type={2}
                        lineTop={0}
                        modelName={this.modelName}
                        appgroupmodel={this.props.appgroupmodel}
                        add={::this.add}
                        delete={::this.delete}
                        editor={::this.editor}
                        sync={::this.sync}
                        check={::this.check}
                        setValueByReducers={this.props.setValueByReducers}
                        loadList={::this.loadList}/>
        </div>)
    }
}
