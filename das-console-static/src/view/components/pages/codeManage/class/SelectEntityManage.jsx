/**
 * Created by liang.wang on 18/9/14.
 */
import React from 'react'
import TablePanle from '../../base/TablePanle'
import {View} from 'ea-react-dm-v14'
import {SelectEntityControl} from '../../../../../controller/Index'
import {Modal} from 'antd'
import ManagePanle from '../../base/ManagePanle'
import Immutable from 'immutable'
import AddSelectEntry from './AddSelectEntry'

@View(SelectEntityControl)
export default class SelectEntityManage extends ManagePanle {

    static defaultProps = {
        tabIndex: 0,
        groupId: 0,
        projectId: 0,
        dbSetlist: []
    }

    constructor(props, context) {
        super(props, context)
        this.modelName = 'SelectEntityModel'
        this.editorTitlte = ' 查询实体'
        this.searchInfo = this.modelName + '.searchInfo'
        this.objName = this.modelName + '.task_sql'
        this.states = this.modelName + '.states'
        this.loadList = props.loadList
        this.addItem = props.addSelectEntity
        this.updateItem = props.updateSelectEntity
        this.deleteItem = props.deleteSelectEntity
        this.groupId = props.groupId
        this.dbSetlist = props.dbSetlist
        this.projectId = props.projectId
        this.cleanExceptKeys = ['project_id']
        this.initValueLink()
        this.state = {
            tabIndex: 0,
            editerVisible: false,
            submitDisabled: false,
            submitLoading: false,
            submitTitle: '点击提交',
            title: '编辑',
            suggestionItem: {}
        }
    }

    /** @Override **/
    addValidate = () => {
        if (this.projectId === 0) {
            return {
                state: false,
                msg: '请选择项目！！！'
            }
        }
        return {
            state: true,
            msg: 'success'
        }
    }

    componentWillReceiveProps(nextProps) {
        const {groupId, projectId, dbSetlist, tabIndex} = nextProps
        this.groupId = groupId
        this.dbSetlist = dbSetlist
        if (tabIndex == 3) {
            if (this.projectId != projectId || (this.state.tabIndex != tabIndex)) {
                this.projectId = projectId
                let searchInfo = this.getValueByReducers(this.searchInfo).toJS()
                let objName = this.getValueByReducers(this.objName).toJS()
                searchInfo.data['project_id'] = projectId
                objName['project_id'] = projectId
                this.setState({projectId, tabIndex}, () => {
                    this.loadList(searchInfo, this, (_this, rs) => {
                        if (rs.code === 200) {
                            _this.setValueByReducers(_this.searchInfo, Immutable.fromJS(searchInfo))
                            _this.setValueByReducers(_this.objName, Immutable.fromJS(objName))
                        } else {
                            _this.showErrorsNotification(rs.msg)
                        }
                    })
                })
            }
        } else {
            if (this.projectId != projectId || this.state.tabIndex != tabIndex) {
                this.setState({projectId, tabIndex})
            }
        }
    }

    visibleCallback = visible => {
        this.setState({visible})
    }

    createModel = () => {
        const {confirmLoading, title} = this.state
        const {setValueByReducers, selectentitymodel, addSelectEntity, updateSelectEntity} = this.props
        const states = this.getValueToJson(this.states)
        return <Modal title={title}
                      width={1000}
                      maskClosable={false}
                      visible={states.editeVisible}
                      confirmLoading={confirmLoading}
                      onOk={::this.handleOk}
                      onCancel={::this.handleCancel} footer={null}>
            <AddSelectEntry dbSetlist={this.dbSetlist} groupId={this.groupId} projectId={this.projectId}
                            addSelectEntity={addSelectEntity} reload={this.reload}
                            valueLink={this.states + '.editeVisible'}
                            selectentitymodel={selectentitymodel} updateSelectEntity={updateSelectEntity}
                            setValueByReducers={setValueByReducers}
                            visibleCallback={::this.visibleCallback}/>
        </Modal>
    }

    render() {
        const {selectentitymodel, setValueByReducers} = this.props
        const states = this.getValueToJson(this.states)
        return (<div>
            {states.editeVisible ? ::this.createModel() : null}
            <TablePanle title='查询实体' zDepth={0}
                        pageStyle={{padding: 0}}
                        lineTop={0} type={3}
                        modelName={this.modelName}
                        add={::this.add}
                        delete={::this.delete}
                        editor={::this.editor}
                        isloadList={false}
                        selectentitymodel={selectentitymodel}
                        setValueByReducers={setValueByReducers}
                        cleanExceptSearchKeys={this.cleanExceptKeys}
                        loadList={::this.loadList}/>
        </div>)
    }
}
