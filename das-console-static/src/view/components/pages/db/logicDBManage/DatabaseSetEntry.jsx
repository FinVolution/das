/**
 * Created by liang.wang on 18/9/14.
 */
import React from 'react'
import ManagePanle from '../../base/ManagePanle'
import TablePanle from '../../base/TablePanle'
import {View} from 'ea-react-dm-v14'
import {DatabaseSetEntryControl} from '../../../../../controller/Index'
import {Modal} from 'antd'
import {Inputlabel, InputPlus, RadioPlus, SelectPlus, TextArea} from '../../../utils/index'
import Immutable from 'immutable'
import {das_msg, databaseShardingTypes, display} from '../../../../../model/base/BaseModel'
import DbSetEntryTab from './DbSetEntryTab'

@View(DatabaseSetEntryControl)
export default class DatabaseSetEntry extends ManagePanle {

    static defaultProps = {
        dblist: null,
        groupId: 0,
        dbset_id: 0
    }

    constructor(props, context) {
        super(props, context)
        this.modelName = 'DatabaseSetEntryModel'
        this.editorTitlte = '逻辑数据库映射'
        this.searchInfo = this.modelName + '.searchInfo'
        this.objName = this.modelName + '.dbSetEntryInfo'
        this.loadList = this.props.loadDbSetEntryList
        this.addItem = this.props.addDbSetEntry
        this.deleteItem = this.props.deletedbSetEntry
        this.updateItem = this.props.updateDbSetEntry
        this.syncLink = '/groupdbSetEntry/sync'
        this.checkLink = '/groupdbSetEntry/check'
        this.cleanExceptKeys = ['groupId', 'dbset_id', 'databaseType']
        this.initValueLink()
        this.dispalyManage.initButtons('/groupdbSetEntry/buttons')
        this.state = {
            title: 'dbSet Entry',
            dbname: '',
            visible: false,
            checkVisible: false,
            suggestInitData: null,
            suggestDisabled: false,
            groupId: this.props.groupId,
            dbset_id: this.props.dbset_id,
            confirmLoading: false,
            currentCheckedId: 0
        }
    }

    componentWillReceiveProps(nextProps) {
        const {dbset_id, groupId, dblist} = nextProps
        if (this.state.dbset_id != dbset_id) {
            this.setState({dbset_id}, () => {
                let searchInfo = this.getValueByReducers(this.searchInfo).toJS()
                searchInfo.data.dbset_id = dbset_id
                this.loadList(searchInfo)
                this.setValueByReducers(this.searchInfo, Immutable.fromJS(searchInfo))
                this.setValueByReducers(this.objName + '.dbset_id', dbset_id)
            })
        }
        if (this.state.groupId != groupId) {
            this.setState({groupId: groupId, dblist: dblist})
            this.setValueByReducers(this.objName + '.groupId', groupId)
        }
    }

    /** @Override **/
    addValidate = () => {
        const {dbset_id, groupId} = this.state
        if (groupId > 0 && dbset_id > 0) {
            return {
                state: true
            }
        } else {
            return {
                state: false,
                msg: '请选组和dbset!!!'
            }
        }
    }

    /** @Override **/
    addCancel = () => {
        this.setState({
            visible: false,
        })
        this.setValueByReducers(this.editeVisible, false)
        this.setValueByReducers(this.modelName + '.dbSetEntryList', [])
    }

    /** @Override **/
    editorCallBack = item => {
        this.setState({dbname: item.dbname})
    }

    onSelect = (selectedKeys, info) => {
        const groupId = info.node.props.dataRef ? info.node.props.dataRef.id : null
        let searchInfo = this.getValueByReducers(this.searchInfo).toJS()
        let objName = this.getValueByReducers(this.objName).toJS()
        searchInfo.data['groupId'] = groupId
        objName['groupId'] = groupId
        this.loadList(searchInfo, this, (_this, rs) => {
            if (rs.code === 200) {
                _this.setValueByReducers(_this.searchInfo, Immutable.fromJS(searchInfo))
                _this.setValueByReducers(_this.objName, Immutable.fromJS(objName))
            } else {
                _this.reload()
                _this.showErrorsNotification(rs.msg)
            }
        })
    }

    onChangeCallbBack = (id, value) => {
        this.replaceValuesToItem(this.objName, {'name': value, db_Id: id})
    }

    cleanSubmitDbs() {
        this.setValueByReducers(this.modelName + '.dbSetEntryList', Immutable.fromJS([]))
    }

    addDbSetlist = () => {
        this.setState({
            confirmLoading: true
        }, () => {
            setTimeout(() => {
                try {
                    const dbSetEntryList = this.getValueToJson(this.modelName + '.dbSetEntryList')
                    this.props.addDbSetEntryList(dbSetEntryList, this, (_this, data) => {
                        if (data.code == 200) {
                            this.reload()
                            this.cleanSubmitDbs()
                            this.updateState(false)
                            this.showSuccessMsg('添加成功')
                        } else {
                            this.showErrorsNotification(data.msg)
                            this.updateState(true)
                            this.setState({confirmLoading: false})
                        }
                    })
                } catch (e) {
                    this.setState({confirmLoading: false})
                }
            }, 2000)
        })
    }

    createModel = () => {
        const {confirmLoading} = this.state
        const {setValueByReducers, databasesetentrymodel, dblist} = this.props
        const _props = {setValueByReducers, databasesetentrymodel}
        const obj = this.getValueToJson(this.objName)
        const list = this.getValueToJson(this.modelName + '.list')
        const states = this.getValueToJson(this.states)
        let disabled = false, selectedId = obj.databaseType
        if (list && list.list) {
            if (states.editerType === 1 && list.list.length <= 1) {
                disabled = true
                selectedId = 1
            }
            if (states.editerType === 0 && list.list.length === 0) {
                disabled = true
                selectedId = 1
            }
        }
        if (states.editerType === 0) {
            return <Modal title='新建逻辑库映射'
                          mask={true}
                          width={1200}
                          maskClosable={false}
                          visible={states.editeVisible}
                          onOk={::this.addDbSetlist}
                          confirmLoading={confirmLoading}
                          onCancel={::this.addCancel}>
                <DbSetEntryTab {..._props} dblist={dblist}/>
            </Modal>
        } else if (states.editerType === 1) {
            return <Modal title={this.state.title} width={800}
                          visible={states.editeVisible}
                          maskClosable={false}
                          confirmLoading={confirmLoading}
                          onOk={::this.handleOk}
                          onCancel={::this.handleCancel}>
                <Inputlabel title='物理库库'>
                    <SelectPlus {..._props} format={{id: 'id', name: 'dbname'}} valueLink={this.objName + '.db_Id'}
                                selectedId={obj.db_Id} items={dblist} onChangeCallbBack={::this.onChangeCallbBack}/>
                </Inputlabel>
                <Inputlabel title='逻辑库映射名称'>
                    <InputPlus {..._props}
                               placeholder={das_msg.ordinary_name}
                               validRules={{maxLength: 150, isEnglishnderline: true}}
                               valueLink={this.objName + '.name'} defaultValue={obj.name}/>
                </Inputlabel>
                <Inputlabel title={(<p>类型 <span style={{color: 'red'}}>(映射必须有一个Master)</span></p>)}>
                    <RadioPlus {..._props}
                               items={databaseShardingTypes} selectedId={selectedId} disabled={disabled}
                               valueLink={this.objName + '.databaseType'}/>
                </Inputlabel>
                <Inputlabel title='策略'>
                <TextArea {..._props}
                          valueLink={this.objName + '.sharding'} defaultValue={obj.sharding}/>
                </Inputlabel>
            </Modal>
        }
    }

    /*
        onChangeCheckbox = item => {
            item = item.toJS()
            const link = this.modelName + '.list'
            let dbsets = this.getValueByReducers(link).toJS()
            dbsets.list && dbsets.list.forEach(e => {
                if (item.id == e.id) {
                    e.checkbox = true
                } else {
                    e.checkbox = false
                }
            })
            this.setValueByReducers(link, dbsets)
        }
    */

    customButton = () => {
        const displaybuttons = this.getValueToJson(this.modelName + display.buttons_path)
        return {
            displaybuttons: displaybuttons,
            customButtons: []
        }
    }

    render() {
        const {databasesetentrymodel, setValueByReducers} = this.props
        const states = this.getValueToJson(this.states)
        return (<div>
            {states.editeVisible ? ::this.createModel() : null}
            {states.checkVisible ? ::this.createCheckModel() : null}
            <TablePanle title='逻辑数据库映射' navigation=''
                        pageStyle={{padding: 0}}
                        type={2}
                        zDepth={0}
                        lineTop={0}
                        modelName={this.modelName}
                        customButton={::this.customButton}
                        databasesetentrymodel={databasesetentrymodel}
                        add={::this.add}
                        delete={::this.delete}
                        editor={::this.editor}
                        sync={::this.sync}
                        check={::this.check}
                        setValueByReducers={setValueByReducers}
                        loadList={::this.loadList}/>
        </div>)
    }
}
