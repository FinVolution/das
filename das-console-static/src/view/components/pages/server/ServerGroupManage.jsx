/**
 * Created by liang.wang on 18/9/14.
 */
import React from 'react'
import ManagePanle from '../base/ManagePanle'
import TablePanle from '../base/TablePanle'
import {View} from 'ea-react-dm-v14'
import {ServerGroupControl} from '../../../../controller/Index'
import {Modal} from 'antd'
import {Inputlabel, InputPlus, TextArea} from '../../utils/index'
import ServerManage from './ServerManage'
import {das_msg} from '../../../../model/base/BaseModel'

@View(ServerGroupControl)
export default class ServerGroupManage extends ManagePanle {

    constructor(props, context) {
        super(props, context)
        this.editorTitlte = ' Server Group'
        this.modelName = 'ServerGroupModel'
        this.searchInfo = this.modelName + '.searchInfo'
        this.objName = this.modelName + '.serverGroup'
        this.syncLink = '/serverGroup/sync'
        this.checkLink = '/serverGroup/check'
        this.loadList = this.props.loadList
        this.addItem = this.props.addServerGroup
        this.deleteItem = this.props.deleteServerGroup
        this.updateItem = this.props.updateServerGroup
        this.initValueLink()
        this.state = {
            dbname: '',
            suggestInitData: null,
            suggestDisabled: false,
            currentCheckedId: 0 //初始化第选中的id
        }
    }

    /** @Override **/
    editorCallBack = item => {
        this.setState({dbname: item.dbname})
    }

    createModel = () => {
        const {confirmLoading} = this.state
        const {setValueByReducers, servergroupmodel} = this.props
        const _props = {setValueByReducers, servergroupmodel}
        const item = this.getValueToJson(this.objName)
        const states = this.getValueToJson(this.states)
        return <Modal title={this.state.title}
                      width={800}
                      visible={states.editeVisible}
                      onOk={::this.handleOk}
                      confirmLoading={confirmLoading}
                      onCancel={::this.handleCancel}>
            <Inputlabel title='Server Group 名称'>
                <InputPlus {..._props} validRules={{isDbName: true, maxLength: 24}}
                           placeholder={das_msg.apollo_namespace}
                           defaultValue={item.name} valueLink={this.objName + '.name'}
                           disabled={states.editerType == 1}/>
            </Inputlabel>
            <Inputlabel title='备注'>
                <TextArea {..._props}
                          valueLink={this.objName + '.comment'} defaultValue={item.comment}/>
            </Inputlabel>
        </Modal>
    }

    onChangeCheckbox = item => {
        item = item.toJS()
        const link = this.modelName + '.list'
        let items = this.getValueByReducers(link).toJS()
        items.list && items.list.forEach(e => {
            if (item.id == e.id) {
                e.checkbox = true
            } else {
                e.checkbox = false
            }
        })
        this.setState({currentCheckedId: item.id}, () => {
            this.setValueByReducers(link, items)
        })
    }

    render() {
        const {servergroupmodel, setValueByReducers} = this.props
        const {currentCheckedId} = this.state
        const states = this.getValueToJson(this.states)
        return (<div>
            {states.editeVisible ? ::this.createModel() : null}
            {states.checkVisible ? ::this.createCheckModel() : null}
            <TablePanle title='Server Group 管理' navigation='数据访问平台 / Server Group 管理'
                        type={3}
                        lineTop={0}
                        modelName={this.modelName}
                        servergroupmodel={servergroupmodel}
                        onChangeCheckbox={::this.onChangeCheckbox}
                        add={::this.add}
                        delete={::this.delete}
                        editor={::this.editor}
                        sync={::this.sync}
                        check={::this.check}
                        loadListFiler={this.loadListFiler}
                        setValueByReducers={setValueByReducers}
                        loadList={::this.loadList}>
                <ServerManage fatherCheckedId={currentCheckedId}/>
            </TablePanle>
        </div>)
    }
}
