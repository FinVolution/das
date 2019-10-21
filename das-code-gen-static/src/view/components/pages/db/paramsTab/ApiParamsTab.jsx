import React from 'react'
import Component from '../../../utils/base/ComponentAlert'
import {Table} from 'eagle-ui'
import {Button} from 'antd'
import './ApiParamsTab.less'
import {InputPlus} from '../../../utils/index'

export default class ApiParamsTab extends Component {

    static defaultProps = {
        valueLink: '',
        apiParams: [],
        limited: {add: true, delete: true, keyEditor: true}
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
        const {apiParams, logicdatabasemodel, setValueByReducers, valueLink, limited} = this.props
        const _props = {setValueByReducers, logicdatabasemodel}
        return (
            <div className='apiParamsTab'>
                <div className='divContent'>
                    <Table>
                        <thead>
                        <tr style={{backgroundColor: 'whitesmoke'}}>
                            <th className='th1'>策略键</th>
                            <th className='th2'>策略值</th>
                            {limited.add ? <th className='operator'>
                                <Button type='primary' ghost
                                        onClick={() => ::this.addTemplate(1)}>添加</Button>
                            </th> : null}
                        </tr>
                        </thead>
                        <tbody>
                        {
                            apiParams && apiParams.map((item, index) => {
                                const placeholder = item.placeholder ? item.placeholder : ''
                                const value = item.value ? item.value : ''
                                return (<tr key={index}>
                                        <td className='th1'>
                                            {limited.keyEditor ? <InputPlus {..._props}
                                                                            placeholder={placeholder}
                                                                            defaultValue={item.key}
                                                                            valueLink={valueLink + `.${index}.key`}/> : item.key}
                                        </td>
                                        <td className='th2'>
                                            <InputPlus {..._props}
                                                       placeholder={placeholder}
                                                       defaultValue={value}
                                                       valueLink={valueLink + `.${index}.value`}/>
                                        </td>
                                        {limited.delete ?
                                            <td className='operator'>
                                                <a className='hand' onClick={() => ::this.delete(item)}>删除</a>
                                            </td> : null}
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