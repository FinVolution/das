import React from 'react'
import Component from '../../../utils/base/ComponentAlert'
import {Table} from 'eagle-ui'
import './TableEntityTab.less'
import {Inputlabel, InputPlus, SelectPlus, RadioPlus} from '../../../utils/index'
import {das_msg, dataFieldTypeEnum, fieldTypes} from '../../../../../model/base/BaseModel'
import {Col, Row, Select, Spin} from 'antd'
import FrwkUtil from '../../../utils/util/FrwkUtil'
import DataUtil from '../../../utils/util/DataUtil'

export default class TableEntityTab extends Component {

    constructor(props, context) {
        super(props, context)
        this.modelName = 'TableEntityModel'
        this.objName = this.modelName + '.task_table'
        this.tablink = this.modelName + '.tableEntryList'
        this.searchInfo = this.modelName + '.searchInfo'
        this.state = {
            dbset_id: 0,
            catalogs: props.catalogs,
            submitTitle: '提交中，请稍后...',
            submitLoading: false
        }
    }

    loadCatalogs = (dbset_id, db_name) => {
        FrwkUtil.fetch.fetchGet('/db/catalogs', {dbset_id}, this, (data, _this) => {
                if (data.code === 200) {
                    _this.setState({catalogs: data.msg})
                    _this.replaceValuesToItem(_this.objName, {dbset_id, db_name})
                } else if (data.code === 500) {
                    _this.showErrorsNotification(data.msg)
                }
            }
        )
    }

    onDeselectCallback = (name) => {
        this.deleteItemToList(this.modelName + '.tableEntryList', {'table_names': name}, 'table_names')
    }

    selectDbSet = (dbset_id, item) => {
        if (this.state.dbset_id != dbset_id && null != dbset_id) {
            this.loadCatalogs(dbset_id, item)
        }
    }

    onSelectCallback = e => {
        const table_names = e
        e = DataUtil.StringUtils.toHump(e)
        e = DataUtil.StringUtils.upperCase(e)
        const task_table = this.getValueToJson(this.objName)
        this.addItemToList(this.modelName + '.tableEntryList', {
            'dbset_id': task_table.dbset_id,
            'project_id': task_table.project_id,
            'db_name': task_table.db_name,
            'table_names': table_names,
            'view_names': e,
            'custom_table_name': table_names,
            'field_type':dataFieldTypeEnum.sql_date,
            'comment': ''
        })
    }

    render() {
        const Option = Select.Option
        const {databasesetentrymodel, setValueByReducers, dbSetlist} = this.props
        const _props = {databasesetentrymodel, setValueByReducers}
        const tableEntryList = this.getValueByReducers(this.tablink)
        const {submitTitle, submitLoading, catalogs} = this.state
        return (
            <Spin spinning={submitLoading} tip={submitTitle} size='large'>
                <Row style={{height: '300px'}}>
                    <Col sm={24}>
                        <Inputlabel title='逻辑数据库'>
                            <SelectPlus {..._props} items={dbSetlist}
                                        valueLink={this.objName + '.dbset_id'}
                                        onChangeCallbBack={::this.selectDbSet}/>
                        </Inputlabel>
                    </Col>
                    <Col sm={24}>
                        <Inputlabel title='选择表'>
                            <Select style={{width: '100%'}} size='large' mode='multiple'
                                    onDeselect={e => this.onDeselectCallback(e)}
                                    onSelect={(e, o) => this.onSelectCallback(e, o)}>
                                {
                                    catalogs && catalogs.length > 0 && catalogs.map(v => {
                                        return <Option value={v} key={v}>{v}</Option>
                                    })
                                }
                            </Select>
                        </Inputlabel>
                    </Col>
                    <Col sm={24}>
                        <div className='templateTable'>
                            <div className='divContent'>
                                <Table>
                                    <thead>
                                    <tr style={{backgroundColor: 'whitesmoke'}}>
                                        <th className='paramName'>自定义表名</th>
                                        <th className='paramName'>自定义表实体名</th>
                                        <th className='paramName'>自定义DATE类型</th>
                                        <th className='comment'>描述</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    {
                                        tableEntryList && tableEntryList.map((_item, index) => {
                                            const item = _item.toJS()
                                            return (<tr key={index}>
                                                    <td className='paramName'>
                                                        <InputPlus {..._props}
                                                                   defaultValue={item.custom_table_name}
                                                                   valueLink={`DatabaseSetEntryModel.tableEntryList.${index}.custom_table_name`}
                                                                   validRules={{
                                                                       maxLength: 150,
                                                                       isEnglishnderline: true
                                                                   }}
                                                                   placeholder={das_msg.ordinary_name}/>
                                                    </td>
                                                    <td className='paramName'>
                                                        <InputPlus {..._props}
                                                                   defaultValue={item.view_names}
                                                                   valueLink={`DatabaseSetEntryModel.tableEntryList.${index}.view_names`}
                                                                   validRules={{
                                                                       maxLength: 150,
                                                                       isEnglishnderline: true
                                                                   }}
                                                                   placeholder={das_msg.ordinary_name}/>
                                                    </td>
                                                    <td className='paramName'>
                                                        <RadioPlus {..._props}
                                                                   items={fieldTypes}
                                                                   selectedId={item.field_type}
                                                                   valueLink={`DatabaseSetEntryModel.tableEntryList.${index}.field_type`}/>
                                                    </td>
                                                    <td className='comment'>
                                                        <InputPlus {..._props}
                                                                   defaultValue={item.comment}
                                                                   validRules={{maxLength: 200}}
                                                                   valueLink={`DatabaseSetEntryModel.tableEntryList.${index}.comment`}/>
                                                    </td>
                                                </tr>
                                            )
                                        })
                                    }
                                    </tbody>
                                </Table>
                            </div>
                        </div>
                    </Col>
                </Row>
            </Spin>
        )
    }
}