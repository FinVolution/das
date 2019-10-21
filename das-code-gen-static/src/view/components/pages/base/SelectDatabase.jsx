/**
 * Created by liang.wang on 18/9/14.
 */
import React from 'react'
import Component from '../../utils/base/Component'
import {Select} from 'antd'
import {DataUtil} from '../../utils/util/Index'

export default class SelectDatabase extends Component {

    static defaultProps = {
        width: '100%',
        disabled: false,
        size: 'large',
        defaultId: null,
        defaultValue: 'mySql',
        valueLink: '',
        items: [{id: 1, name: 'mySql'}, {id: 2, name: 'sqlServer'}],
        onSelectCallback: () => {
        }
    }

    constructor(props, context) {
        super(props, context)
        const {defaultId, defaultValue, items} = this.props
        this.state = {
            defaultValue: defaultId == null ? defaultValue : DataUtil.ObjUtils.findWhereValById(items, {id: defaultId}, 'name')
        }
    }

    selectChange = e => {
        this.props.setValueByReducers(this.props.valueLink, e)
        this.props.onSelectCallback && this.props.onSelectCallback(e, this)
    }

    render() {
        const {items, width, disabled, size} = this.props
        const {defaultValue} = this.state
        const Option = Select.Option
        return (
            <Select defaultValue={defaultValue} size={size} disabled={disabled} style={{width: width}}
                    onChange={e => {
                        this.selectChange(e)
                    }}>
                {
                    items && items.map((v, i) => {
                        return <Option value={String(v.id)}
                                       key={i}>{v.name}</Option>
                    })
                }
            </Select>
        )
    }
}