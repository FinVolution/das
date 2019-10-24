import React from 'react'
import Component from '../../utils/base/ComponentAlert'
import {Table} from 'eagle-ui'
import './DatabaseTab.less'
import {InputPlus} from '../../utils/index'
import {DataUtil, UserEnv} from '../../utils/util/Index'
import {Select, Checkbox, Tooltip, Button} from 'antd'
import {das_msg} from '../../../../model/base/BaseModel'


export default class DatabaseTab extends Component {

    static defaultProps = {
        deleteItem: () => {
        },
        onChangeCallback: () => {
        }
    }

    constructor(props, context) {
        super(props, context)
        this.modelName = 'DatabaseModel'
        this.tablink = this.modelName + '.dalGroupDBList'
        this.state = {
            sort: true,
            icon: 'down'
        }
    }

    onChangeCheckbox = (item) => {
        item.addToGroup = !item.addToGroup
        if (!item.addToGroup) {
            delete item.dal_group_id
        }
        this.updateItemToList(this.tablink, item, 'dbname')
    }

    selectChange = (e, item) => {
        item.dal_group_id = parseInt(e)
        this.updateItemToList(this.tablink, item, 'dbname')
    }

    createButton = e => {
        const {deleteItem} = this.props
        return <span style={{paddingRight: '8px'}}>
                                <Tooltip placement='top' title='删除'>
                                    <Button icon='delete' size='small' type='danger'
                                            onClick={() => deleteItem(e.db_catalog)}/>
                                </Tooltip>
                            </span>
    }

    createLable = e => {
        if (e.showExist) {
            return <span style={{color: 'red'}}>已存在</span>
        } else {
            return <span style={{color: 'green'}}>可用</span>
        }
    }

    sort = () => {
        let list = this.getValueToJson(this.tablink)
        if (this.state.sort) {
            list = DataUtil.ObjUtils.sortAscByKey(list, 'db_catalog')
        } else {
            list = DataUtil.ObjUtils.sortDesByKey(list, 'db_catalog')
        }
        const sort = !this.state.sort
        let icon = 'up'
        if (sort) {
            icon = 'down'
        }
        this.setState({sort, icon}, () => {
            this.setValueToImmutable(this.tablink, list)
        })
    }

    render() {
        const Option = Select.Option
        const {icon} = this.state
        const {databasemodel, setValueByReducers, onChangeCallback} = this.props
        const dalGroupDBList = this.getValueByReducers(this.tablink)
        const groups = this.getValueByReducers(this.modelName + '.tree')
        const _props = {setValueByReducers, databasemodel}
        return (
            <div className='templateTabledb'>
                <div className='divContent'>
                    <Table>
                        <thead>
                        <tr style={{backgroundColor: 'whitesmoke'}}>
                            <th className='dbRealName'><Button type='dashed' icon={icon}
                                                               onClick={::this.sort}>物理库名</Button></th>
                            <th className='paramName'>
                                <Tooltip placement='top' title={UserEnv.getConfigCenterName() + '限制，长度不得超过24'}>
                                    <a>物理库标识符</a>
                                </Tooltip></th>
                            <th className='msg'/>
                            <th className='comment'>备注</th>
                            <th className='addteam'>添加到Team</th>
                            <th className='option'>操作</th>
                        </tr>
                        </thead>
                        <tbody>
                        {
                            dalGroupDBList && dalGroupDBList.map((_item, index) => {
                                const item = _item.toJS()
                                let name = item.dal_group_id > 0 && groups && groups.toJS().length > 0 ? DataUtil.ObjUtils.findWhereValById(groups.toJS(), {'id': item.dal_group_id}, 'group_name') : null
                                if (!name && groups && groups.toJS().length > 0) {
                                    name = groups.toJS()[0]
                                }
                                const dbname = item.dbname.substring(0, 25)
                                return (<tr key={index}>
                                        <td className='dbRealName'>
                                            {item.db_catalog}
                                        </td>
                                        <td className='paramName'>
                                            <InputPlus {..._props}
                                                       defaultValue={dbname}
                                                       valueLink={`DatabaseModel.dalGroupDBList.${index}.dbname`}
                                                       validRules={{isDbName: true, maxLength: UserEnv.getCons().dataBaseNameMaxLength}}
                                                       onChangeCallBack={onChangeCallback}
                                                       placeholder={das_msg.apollo_namespace}/>
                                        </td>
                                        <th className='msg'>{this.createLable(item)}</th>
                                        <td className='comment'>
                                            <InputPlus {..._props}
                                                       validRules={{maxLength: 200}}
                                                       valueLink={`DatabaseModel.dalGroupDBList.${index}.comment`}
                                                       placeholder='备注'/>
                                        </td>
                                        <td className='addteam'>
                                            <Checkbox checked={item.addToGroup}
                                                      onChange={() => {
                                                          ::this.onChangeCheckbox(item)
                                                      }}>
                                                保存到Team
                                            </Checkbox>
                                            <Select defaultValue={name} disabled={!item.addToGroup} style={{width: 190}}
                                                    onChange={(e) => {
                                                        this.selectChange(e, item)
                                                    }}>
                                                {
                                                    groups && groups.map((v, i) => {
                                                        return <Option value={String(v.get('id'))}
                                                                       key={i}>{v.get('group_name')}</Option>
                                                    })
                                                }
                                            </Select>
                                        </td>
                                        <th className='option'>{this.createButton(item, index)}</th>
                                    </tr>
                                )
                            })
                        }
                        </tbody>
                    </Table>
                </div>
            </div>
        )
    }
}