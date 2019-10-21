import React from 'react'
import Component from '../../../utils/base/ComponentAlert'
import {Table} from 'eagle-ui'
import './DbSetEntryTab.less'
import {Inputlabel, InputPlus, SelectPlus, RadioPlus} from '../../../utils/index'
import {das_msg, databaseShardingTypes} from '../../../../../model/base/BaseModel'
import {Col, Row, Spin} from 'antd'


export default class DbSetEntryTab extends Component {

    constructor(props, context) {
        super(props, context)
        this.modelName = 'DatabaseSetEntryModel'
        this.objName = this.modelName + '.dbSetEntryInfo'
        this.tablink = this.modelName + '.dbSetEntryList'
        this.searchInfo = this.modelName + '.searchInfo'
        this.state = {
            submitTitle: '提交中，请稍后...',
            submitLoading: false
        }
    }

    onDeselectCallback = (name, item) => {
        this.deleteItemToList(this.modelName + '.dbSetEntryList', {'db_Id': item.id}, 'db_Id')
    }

    onSelectCallback = (id, item) => {
        const dbSetEntryInfo = this.getValueToJson(this.objName)
        this.addItemToList(this.modelName + '.dbSetEntryList', {
            'db_Id': id,
            'name': item.name,
            'databaseType': 1,
            'sharding': '',
            'dbset_id': dbSetEntryInfo.dbset_id,
            'groupId': dbSetEntryInfo.groupId
        })
    }

    render() {
        const {databasesetentrymodel, setValueByReducers, dblist} = this.props
        const dbSetEntryList = this.getValueByReducers(this.tablink)
        const {submitTitle, submitLoading} = this.state
        const _props = {setValueByReducers, databasesetentrymodel}
        return (
            <Spin spinning={submitLoading} tip={submitTitle} size='large'>
                <Row style={{height: '300px'}}>
                    <Col sm={24}>
                        <Inputlabel title='选择物理库'>
                            <SelectPlus {..._props} format={{id: 'id', name: 'dbname'}}
                                        selectedIds={[]} items={dblist} mode='multiple' isSearch={true}
                                        valueLink={this.objName + '.items'} onSelectCallback={::this.onSelectCallback}
                                        onDeselectCallback={::this.onDeselectCallback}/>
                        </Inputlabel>
                    </Col>
                    <Col sm={24}>
                        <div className='templateTableDiv'>
                            <div className='divContent'>
                                <Table>
                                    <thead>
                                    <tr style={{backgroundColor: 'whitesmoke'}}>
                                        <th className='paramName'>逻辑库映射名称</th>
                                        <th className='paramName'><p>类型 <span
                                            style={{color: 'red'}}>(映射必须有一个Master)</span>
                                        </p></th>
                                        <th className='comment'>sharding</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    {
                                        dbSetEntryList && dbSetEntryList.map((_item, index) => {
                                            const item = _item.toJS()
                                            return (<tr key={index}>
                                                    <td className='paramName'>
                                                        <InputPlus {..._props}
                                                                   defaultValue={item.name}
                                                                   valueLink={`DatabaseSetEntryModel.dbSetEntryList.${index}.name`}
                                                                   validRules={{isDbName: true, maxLength: 50}}
                                                                   placeholder={das_msg.apollo_namespace}/>
                                                    </td>
                                                    <td className='addteam'>
                                                        <RadioPlus {..._props}
                                                                   items={databaseShardingTypes}
                                                                   selectedId={item.databaseType}
                                                                   valueLink={`DatabaseSetEntryModel.dbSetEntryList.${index}.databaseType`}/>
                                                    </td>
                                                    <td className='comment'>
                                                        <InputPlus {..._props}
                                                                   valueLink={`DatabaseSetEntryModel.dbSetEntryList.${index}.sharding`}/>
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