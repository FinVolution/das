/**
 * Created by liang.wang on 18/9/14.
 */
import React from 'react'
import ManagePanle from '../base/ManagePanle'
import TablePanle from '../base/TablePanle'
import {View} from 'ea-react-dm-v14'
import {GroupControl} from '../../../../controller/Index'
import {Modal} from 'antd'
import {Inputlabel, InputPlus, TextArea} from '../../utils/index'
import {das_msg} from '../../../../model/base/BaseModel'

@View(GroupControl)
export default class GroupManage extends ManagePanle {

    constructor(props, context) {
        super(props, context)
        this.modelName = 'GroupModel'
        this.editorTitlte = '组'
        this.searchInfo = this.modelName + '.searchInfo'
        this.objName = this.modelName + '.group'
        this.syncLink = '/group/sync'
        this.loadList = this.props.loadList
        this.addItem = this.props.addGroup
        this.deleteItem = this.props.deleteGroup
        this.updateItem = this.props.updateGroup
        this.initValueLink()
        this.state = {
            title: '添加' + this.editorTitlte,
            confirmLoading: false,
            currentCheckedId: 0,
            dbname: ''
        }

    }

    createModel = () => {
        const {confirmLoading} = this.state
        const {setValueByReducers, groupmodel} = this.props
        const item = this.getValueToJson(this.objName)
        const states = this.getValueToJson(this.states)
        return <Modal title={this.state.title}
                      width={800}
                      visible={states.editeVisible}
                      onOk={::this.handleOk}
                      confirmLoading={confirmLoading}
                      onCancel={::this.handleCancel}>
            <Inputlabel title='组名'>
                <InputPlus groupmodel={groupmodel} disabled={states.editerType == 1}
                           setValueByReducers={setValueByReducers}
                           valueLink={this.objName + '.group_name'} validRules={{isDbName: true, maxLength: 24}}
                           defaultValue={item.group_name} placeholder={das_msg.apollo_namespace}/>
            </Inputlabel>
            <Inputlabel title='项目描述'>
                <TextArea {...this.props} defaultValue={item.group_comment}
                          valueLink={this.objName + '.group_comment'}/>
            </Inputlabel>
        </Modal>
    }

    render() {
        const states = this.getValueToJson(this.states)
        return (<div>
            {states.editeVisible ? ::this.createModel() : null}
            <TablePanle title='组管理' navigation='数据访问平台 / 组管理'
                        type={2}
                        lineTop={0}
                        modelName={this.modelName}
                        groupmodel={this.props.groupmodel}
                        add={::this.add}
                        delete={::this.delete}
                        editor={::this.editor}
                        sync={::this.sync}
                        setValueByReducers={this.props.setValueByReducers}
                        loadList={::this.loadList}/>
        </div>)
    }
}
