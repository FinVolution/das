import React from 'react'
import Component from '../../../utils/base/ComponentAlert'
import {Table} from 'eagle-ui'
import {Button} from 'antd'
import './ApiParamsTab.less'
import {InputPlus} from '../../../utils/index'

export default class ApiParamsTab extends Component {

    static defaultProps = {
        valueLink: '',
        apiParams: []
    }

    constructor(props, context) {
        super(props, context)
    }

    addTemplate = () => {
        let {valueLink} = this.props
        let apiParams = this.getValueByReducers(valueLink).toJS()
        this.addItemToList(valueLink, {
            id: apiParams.length + 1,
            key: '',
            value: ''
        })
    }

    delete = item => {
        this.deleteItemToList(this.props.valueLink, item, 'id')
    }

    render() {
        const {apiParams, publicstrategymodel, setValueByReducers, valueLink} = this.props
        const _props = {setValueByReducers, publicstrategymodel}
        return (
            <div className='apiParamsTab'>
                <div className='divContent'>
                    <Table>
                        <thead>
                        <tr style={{backgroundColor: 'whitesmoke'}}>
                            <th className='th1'>策略名称</th>
                            <th className='th2'>参数说明</th>
                            <th className='operator'>
                                <Button type='primary' ghost onClick={() => ::this.addTemplate(1)}>添加</Button>
                            </th>
                        </tr>
                        </thead>
                        <tbody>
                        {
                            apiParams && apiParams.map((item, index) => {
                                return (<tr key={index}>
                                        <td className='th1'>
                                            <InputPlus {..._props}
                                                       defaultValue={item.key}
                                                       valueLink={valueLink + `.${index}.key`}/>
                                        </td>
                                        <td className='th2'>
                                            <InputPlus {..._props}
                                                       defaultValue={item.value}
                                                       valueLink={valueLink + `.${index}.value`}/>
                                        </td>

                                        <td className='operator'>
                                            <a className='hand' onClick={() => ::this.delete(item)}>删除</a>
                                        </td>
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