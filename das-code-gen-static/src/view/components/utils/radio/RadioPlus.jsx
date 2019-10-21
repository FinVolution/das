/**
 * Created by liang.wang on 18/10/29.
 */
import React from 'react'
import Component from '../base/Component'
import {Radio} from 'antd'
import {DataUtil} from '../../utils/util/Index'
import _ from 'underscore'

export default class RadioPlus extends Component {

    static defaultProps = {
        valueLink: '',
        selectedId: null,
        defaultValue: '',
        items: [],              //[{id: 1, name: 'mySql'}, {id: 2, name: 'sqlServer'}]
        format: {id: 'id', name: 'name'},
        disabled: false,
        onChangeCallback: () => {
        }
    }

    constructor(props, context) {
        super(props, context)
        const {items, format, selectedId} = props
        const _items = DataUtil.ObjUtils.transformJson(items, format)
        this.state = {
            items: _items,
            selectedId: selectedId
        }
        this.initData()
    }

    componentWillReceiveProps(nextProps) {
        const {selectedId} = nextProps
        if (selectedId != this.state.selectedId) {
            this.setState({selectedId})
        }
    }

    initData() {
        const {selectedId, valueLink} = this.props
        if(valueLink){
            this.setValueByReducers(valueLink, selectedId)
        }
    }

    createRadios() {
        const items = this.state.items
        let radios = []
        if (_.isArray(items)) {
            _.each(items, function (item) {
                radios.push(<Radio value={item.id} key={item.id}>{item.name}</Radio>)
            })
        }
        return radios
    }

    onChange = e => {
        const {onChangeCallback, valueLink} = this.props
        const selectedId = e.target.value
        this.setState({selectedId})
        this.setValueByReducers(valueLink, selectedId)
        onChangeCallback && onChangeCallback(selectedId)
    }

    render() {
        const {disabled, size, defaultValue} = this.props
        const {selectedId} = this.state
        const RadioGroup = Radio.Group
        return <RadioGroup defaultValue={defaultValue} value={selectedId} disabled={disabled}
                           size={size} onChange={e => this.onChange(e)}>
            {this.createRadios()}
        </RadioGroup>
    }
}