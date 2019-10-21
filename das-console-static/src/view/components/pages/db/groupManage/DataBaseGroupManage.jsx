/**
 * Created by liang.wang on 18/9/14.
 */
import React from 'react'
import ManagePanle from '../../base/ManagePanle'
import TablePanle from '../../base/TablePanle'
import {View} from 'ea-react-dm-v14'
import {DataBaseGroupControl} from '../../../../../controller/Index'
import TreePanle from '../../base/TreePanle'
import {Modal} from 'antd'
import {DropDownSuggestion, Inputlabel, InputPlus} from '../../../utils/index'
import Immutable from 'immutable'

@View(DataBaseGroupControl)
export default class DataBaseGroupManage extends ManagePanle {

    constructor(props, context) {
        super(props, context)
        this.modelName = 'DataBaseGroupModel'
        this.editorTitlte = '分组'
        this.searchInfo = this.modelName + '.searchInfo'
        this.objName = this.modelName + '.dbinfo'
        this.searchInfoSelectId = this.searchInfo + '.data.dal_group_id'
        this.loadList = this.props.loadList
        this.addItem = this.props.addGroupDb
        this.deleteItem = this.props.deleteGroupDb
        this.updateItem = this.props.updateGroupDb
        this.props.loadTree()
        this.initValueLink()
        this.state = {
            dbname: '',
            suggestInitData: null,
            suggestDisabled: false,
            title: '添加' + this.editorTitlte,
            confirmLoading: false,
            currentCheckedId: 0
        }
    }

    /** @Override **/
    addValidate = () => {
        if (this.getValueByReducers(this.objName + '.dal_group_id')) {
            return {
                state: true
            }
        } else {
            return {
                state: false,
                msg: '请选择组!!!'
            }
        }
    }

    /** @Override **/
    cleanObjName = () => {
        let item = this.getValueByReducers(this.objName).toJS()
        item.comment = ''
        item.id = ''
        this.setValueByReducers(this.objName, Immutable.fromJS(item))
    }

    /** @Override **/
    editorCallBack = item => {
        this.setState({dbname: item.dbname})
    }

    onSelect(selectedKeys, info) {
        const groupId = info.node.props.dataRef ? info.node.props.dataRef.id : null
        let searchInfo = this.getValueByReducers(this.searchInfo).toJS()
        let objName = this.getValueByReducers(this.objName).toJS()
        searchInfo.data['dal_group_id'] = groupId
        objName['dal_group_id'] = groupId
        this.loadList(searchInfo, this, (_this, rs) => {
            if (rs.code === 200) {
                _this.setValueByReducers(_this.searchInfo, Immutable.fromJS(searchInfo))
                _this.setValueByReducers(_this.objName, Immutable.fromJS(objName))
            } else {
                _this.showErrorsNotification(rs.msg)
            }
        })
    }

    selectedCallback = ele => {
        this.replaceValueToItem(this.objName, 'id', ele.id)
    }

    cancelCallback = () => {
        this.replaceValueToItem(this.objName, 'id', null)
    }

    createModel = () => {
        const {confirmLoading, dbname} = this.state
        const {setValueByReducers, databasegroupmodel} = this.props
        const item = this.getValueToJson(this.objName)
        const states = this.getValueToJson(this.states)
        return <Modal title={this.state.title}
                      width={800}
                      visible={states.editeVisible}
                      onOk={::this.handleOk}
                      confirmLoading={confirmLoading}
                      onCancel={::this.handleCancel}>
            <Inputlabel title='请查下输入数据库名称(das db name 模糊查询)'>
                {states.editerType === 0 && <DropDownSuggestion
                    url={'/db/dbs'} {...this.props}
                    keyword='name'
                    style={{divWidth: '100%', inputWidth: '94%', showWidth: '100%'}}
                    format={{
                        leng: 400,
                        title: {
                            'dbname': ' 物理库名  - ',
                            'db_catalog': ' 备注 - ',
                            'comment': ''
                        }
                    }}
                    valueLink={this.modelName + '.suggestion'}
                    defaultVal={null}
                    initDataCallback={::this.selectedCallback}
                    selectedCallback={::this.selectedCallback}
                    cancelCallback={::this.cancelCallback}
                    placeholder="请输入数据库名称"/>}
                {states.editerType === 1 && <InputPlus disabled={true} defaultValue={dbname}/>}
            </Inputlabel>
            <Inputlabel title='备注'>
                <InputPlus setValueByReducers={setValueByReducers} databasegroupmodel={databasegroupmodel}
                           validRules={{maxLength: 50}} valueLink={this.objName + '.comment'}
                           defaultValue={item.comment}/>
            </Inputlabel>
        </Modal>
    }

    render() {
        const {databasegroupmodel, setValueByReducers} = this.props
        const states = this.getValueToJson(this.states)
        return (<div>
            {states.editeVisible ? ::this.createModel() : null}
            <TablePanle title='数据库分组管理' navigation='数据访问平台 / 数据库分组管理'
                        type={4}
                        lineTop={0}
                        modelName={this.modelName}
                        databasegroupmodel={databasegroupmodel}
                        add={::this.add}
                        delete={::this.delete}
                        editor={::this.editor}
                        setValueByReducers={setValueByReducers}
                        loadList={::this.loadList}
                        tree={<TreePanle
                            rootShow={false}
                            showLine={true}
                            format={{tree: {title: 'group_name', key: 'id', tooltip: 'group_comment', isLeaf: true}}}
                            onSelect={::this.onSelect}
                            getDefaultSelected={::this.getDefaultSelected}/>}/>
        </div>)
    }
}
