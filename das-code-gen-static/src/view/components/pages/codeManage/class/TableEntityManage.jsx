/**
 * Created by liang.wang on 18/9/14.
 */
import React from 'react'
import TablePanle from '../../base/TablePanle'
import {View} from 'ea-react-dm-v14'
import {TableEntityControl} from '../../../../../controller/Index'
import {Col, Modal, Row, Select} from 'antd'
import ManagePanle from '../../base/ManagePanle'
import Immutable from 'immutable'
import FrwkUtil from '../../../utils/util/FrwkUtil'
import {InputPlus, Inputlabel, RadioPlus} from '../../../utils'
import DataUtil from '../../../utils/util/DataUtil'
import {das_msg, dataFieldTypeEnum, fieldTypes} from '../../../../../model/base/BaseModel'
import TableEntityTab from './TableEntityTab'

@View(TableEntityControl)
export default class TableEntityManage extends ManagePanle {

    static defaultProps = {
        tabIndex: 0,
        groupId: 0,
        projectId: 0,
        dbSetlist: []
    }

    constructor(props, context) {
        super(props, context)
        this.modelName = 'TableEntityModel'
        this.editorTitlte = ' 表实体'
        this.searchInfo = this.modelName + '.searchInfo'
        this.objName = this.modelName + '.task_table'
        this.loadList = this.props.loadList
        this.addItem = this.props.addTableEntity
        this.updateItem = this.props.updateTableEntity
        this.deleteItem = this.props.deleteTableEntity
        this.cleanExceptKeys = ['project_id']
        this.initValueLink()
        this.state = {
            tabIndex: 0,
            dbset_id: 0,
            catalogs: [],
            groupId: props.groupId,
            projectId: props.projectId,
            dbSetlist: props.dbSetlist,
            submitDisabled: false,
            submitLoading: false,
            submitTitle: '点击提交',
            title: '编辑',
            suggestionItem: {}
        }
    }

    /** @Override **/
    addValidate = () => {
        if (this.state.projectId === 0) {
            return {
                state: false,
                msg: '请选择项目！！'
            }
        }
        return {
            state: true,
            msg: 'success'
        }
    }

    /** @Override **/
    editorCallBack = item => {
        return item
        //const task_table = this.getValueByReducers(this.objName).toJS()
        //this.loadCatalogs(item.dbset_id, item)
    }

    /** @Override **/
    addCancel = () => {
        this.setState({
            visible: false,
        })
        this.setValueByReducers(this.editeVisible, false)
        this.setValueByReducers(this.modelName + '.tableEntityList', [])
        this.cleanSubmitDbs()
    }

    componentWillReceiveProps(nextProps) {
        const {groupId, projectId, dbSetlist, tabIndex} = nextProps
        this.groupId = groupId
        this.dbSetlist = dbSetlist
        if (tabIndex == 2) {
            if (this.state.projectId != projectId || this.state.tabIndex != tabIndex) {
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
            if (this.state.projectId != projectId || this.state.tabIndex != tabIndex) {
                this.setState({projectId, tabIndex})
            }
        }
    }

    visibleCallback = visible => {
        this.setState({visible})
    }

    loadCatalogs = (dbset_id, item) => {
        FrwkUtil.fetch.fetchGet('/db/catalogs', {dbset_id: dbset_id}, this, (data, _this) => {
                if (data.code === 200) {
                    _this.setState({catalogs: data.msg})
                    _this.replaceValuesToItem(_this.objName, {dbset_id: dbset_id, db_name: item.props.dbname})
                } else if (data.code === 500) {
                    _this.showErrorsNotification(data.msg)
                }
            }
        )
    }

    selectDbSet = (dbset_id, item) => {
        if (this.state.dbset_id != dbset_id && null != dbset_id) {
            this.loadCatalogs(dbset_id, item)
        }
    }

    cleanSubmitDbs() {
        this.setValueByReducers(this.modelName + '.tableEntryList', Immutable.fromJS([]))
    }

    selectCatlog = e => {
        const table_names = e
        e = DataUtil.StringUtils.toHump(e)
        e = DataUtil.StringUtils.upperCase(e)
        this.replaceValuesToItem(this.objName, {
            table_names: table_names,
            view_names: e,
            custom_table_name: table_names
        })
    }

    addTablelist = () => {
        this.setState({
                confirmLoading: true
            }, () => {
                try {
                    const tableEntryList = this.getValueToJson(this.modelName + '.tableEntryList')
                    setTimeout(() => {
                        this.props.addTablelist(tableEntryList, this, (_this, data) => {
                            if (data.code == 200) {
                                this.reload()
                                this.cleanSubmitDbs()
                                this.updateState(false)
                                this.showSuccessMsg('添加成功')
                            } else {
                                this.showErrorsNotification(data.msg)
                                this.setState({confirmLoading: false})

                            }
                        })
                    }, 2000)
                } catch (e) {
                    this.setState({confirmLoading: false})
                }
            }
        )
    }

    createModel = () => {
        const Option = Select.Option
        const {confirmLoading, title, catalogs} = this.state
        const {setValueByReducers, tableentitymodel} = this.props
        const states = this.getValueToJson(this.states)
        const item = this.getValueToJson(this.objName)
        const _props = {setValueByReducers, tableentitymodel}
        let field_type = item.field_type
        if (field_type == null || (field_type != dataFieldTypeEnum.sql_date && field_type != dataFieldTypeEnum.util_date)) {
            field_type = dataFieldTypeEnum.sql_date
        }
        const tip = <Row>
            <Col sm={3}>{title}</Col>
            <Col sm={21}><p style={{color: 'red'}}>表名+数字 自定义表名和自定义表实体名，请去掉后缀，例如：table_01 请使用table</p></Col>
        </Row>
        if (states.editerType === 0) {
            return <Modal title={tip}
                          mask={true}
                          width={1200}
                          maskClosable={false}
                          visible={states.editeVisible}
                          onOk={::this.addTablelist}
                          confirmLoading={confirmLoading}
                          onCancel={::this.addCancel}>
                <TableEntityTab {..._props} dbSetlist={this.dbSetlist}/>
            </Modal>
        } else if (states.editerType === 1) {
            return <Modal title={tip}
                          width={1000}
                          visible={states.editeVisible}
                          onOk={::this.handleOk}
                          confirmLoading={confirmLoading}
                          onCancel={::this.handleCancel}>
                <Inputlabel title={'逻辑数据库'}>
                    <Select defaultValue={item.dbsetName} style={{width: '100%'}} size='large'
                            onSelect={(e, item) => {
                                this.selectDbSet(e, item)
                            }}>
                        {
                            this.dbSetlist && this.dbSetlist.length > 0 && this.dbSetlist.map((v, i) => {
                                return <Option value={String(v.id)} dbname={v.name} key={i}>{v.name}</Option>
                            })
                        }
                    </Select>
                </Inputlabel>
                <Inputlabel title={'选择表'}>
                    <Select defaultValue={item.table_names} style={{width: '100%'}} size='large'
                            onChange={e => {
                                this.selectCatlog(e)
                            }}>
                        {
                            catalogs && catalogs.length > 0 && catalogs.map(v => {
                                return <Option value={v} key={v}>{v}</Option>
                            })
                        }
                    </Select>
                </Inputlabel>
                <Inputlabel title='自定义表名'>
                    <InputPlus {..._props}
                               defaultValue={item.custom_table_name} placeholder={das_msg.ordinary_name}
                               validRules={{maxLength: 150, isEnglishnderline: true}}
                               valueLink={this.objName + '.custom_table_name'}/>
                </Inputlabel>
                <Inputlabel title='自定义表实体名'>
                    <InputPlus {..._props}
                               defaultValue={item.view_names} placeholder={das_msg.ordinary_name}
                               validRules={{maxLength: 150, isEnglishnderline: true}}
                               valueLink={this.objName + '.view_names'}/>
                </Inputlabel>
                <Inputlabel title='DATE类型'>
                    <RadioPlus {..._props}
                               items={fieldTypes}
                               selectedId={field_type}
                               valueLink={this.objName + '.field_type'}/>
                </Inputlabel>
                <Inputlabel title='描述'>
                    <InputPlus {..._props}
                               defaultValue={item.comment} validRules={{maxLength: 200}}
                               valueLink={this.objName + '.comment'}/>
                </Inputlabel>
            </Modal>
        }
    }

    render() {
        const {tableentitymodel, setValueByReducers} = this.props
        const states = this.getValueToJson(this.states)
        return (<div>
            {states.editeVisible ? ::this.createModel() : null}
            <TablePanle title='表实体'
                        pageStyle={{padding: 0}}
                        zDepth={0}
                        lineTop={0}
                        type={3}
                        modelName={this.modelName}
                        tableentitymodel={tableentitymodel}
                        setValueByReducers={setValueByReducers}
                        add={::this.add}
                        delete={::this.delete}
                        editor={::this.editor}
                        cleanExceptSearchKeys={this.cleanExceptKeys}
                        isloadList={false}
                        loadList={::this.loadList}/>
        </div>)
    }
}
