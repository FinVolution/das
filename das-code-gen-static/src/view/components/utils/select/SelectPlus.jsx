/**
 * Created by liang.wang on 18/10/29.
 */
import React from 'react'
import Component from '../base/Component'
import {Select} from 'antd'
import {DataUtil} from '../../utils/util/Index'
import _ from 'underscore'

export default class SelectPlus extends Component {

    static defaultProps = {
        mode: null,  // null: 单选 'multiple' | 'tags' | 'combobox'
        isSearch: false,
        valueLink: '',
        selectedIds: null,      //优先, 可以是集合 [1,2,3,4]
        items: [],              //[{id: 1, name: 'mySql'}, {id: 2, name: 'sqlServer'}]
        format: {id: 'id', name: 'name'},
        selectedId: null,       //单选用
        idIsNumber: true,       //id是否是数字
        placeholder: '',
        disabled: false,
        width: '100%',
        size: 'large',
        onChangeCallbBack: () => { //单选
        },
        onSelectCallback: () => {   //多选
        },
        onDeselectCallback: () => { //多选
        }
    }

    constructor(props, context) {
        super(props, context)
        const {items, format, selectedIds, selectedId} = props
        const _items = DataUtil.ObjUtils.transformJson(items, format)
        this.items = _items
        this.defaultValue = this.getDefaultValue(props, _items)
        this.refreshFalg = true
        this.state = {
            isSearch: props.isSearch,
            items: _items,
            //多选
            selectedIds: selectedIds,
            selectedValues: this.defaultValue,
            //单选
            selectedId: selectedId,
            selectedValue: this.defaultValue
        }
        setTimeout(() => {
            this.initModelItems(this.state.selectedValues)
        }, 500)
    }

    componentWillReceiveProps(nextProps) {
        const {selectedId, items, format, mode, selectedIds} = nextProps
        const {isSearch} = this.state
        const _items = DataUtil.ObjUtils.transformJson(items, format)
        //多选
        if (mode != null && !DataUtil.ObjUtils.isEqual(this.state.items, _items) || !DataUtil.ObjUtils.isEqual(this.state.selectedIds, selectedIds)) {
            if (this.refreshFalg || !isSearch) {
                const selectedValues = this.getDefaultValue(nextProps, _items)
                this.setState({selectedIds, items: _items, selectedValues}, () => {
                    this.initModelItems(selectedValues)
                })
            }
            if (!DataUtil.ObjUtils.isEqual(this.state.items, _items)) {
                //this.items = DataUtil.ObjUtils.transformJson(_items, format)
                this.items = _items
                this.setState({items: this.items})
            }
        }
        //单选
        if (mode == null && (!DataUtil.ObjUtils.isEqual(this.state.items, _items) || this.state.selectedId != selectedId)) {
            const selectedValue = this.getDefaultValue(nextProps, _items)
            this.setState({items: _items, selectedId, selectedValue})
        }
    }

    initModelItems = selectedValues => {
        const {selectedIds, items} = this.state
        if (_.isEmpty(items)) {
            this.props.setValueByReducers && this.setValueByReducers([])
            return
        }
        if (_.isEmpty(selectedValues)) {
            this.props.setValueByReducers && this.setValueByReducers([])
            return
        }
        const {mode, idIsNumber} = this.props
        if (mode != null && _.isArray(selectedIds) && selectedIds.length > 0) {
            const _items = []
            selectedIds.forEach(id => {
                _items.push({
                    id: idIsNumber ? parseInt(id) : String(id),
                    name: this.getNameById(id)
                })
            })
            this.props.setValueByReducers && this.setValueByReducers(_items)
        }
    }

    getDefaultValue = (props, items) => {
        let {mode, selectedIds, selectedId, idIsNumber} = props
        if (mode === null) {//单选
            if (!_.isEmpty(items)) {
                selectedId = idIsNumber ? parseInt(selectedId) : String(selectedId)
                return DataUtil.ObjUtils.findWhereValById(items, {id: selectedId}, 'name')
            }
        } else { //多选
            if (mode != null && DataUtil.is.Array(selectedIds)) {
                return DataUtil.ObjUtils.filterObjListToArr(items, selectedIds)
            } else {
                window.console.error('SelectPlus.getDefaultValue 参数错误！！！')
            }
        }
        return null
    }

    getIdByName = name => {
        const {items} = this.state
        return DataUtil.ObjUtils.findWhereIdByVal(items, {name}, 'id')
    }

    getNameById = id => {
        const {items} = this.state
        id = this.props.idIsNumber ? parseInt(id) : String(id)
        return DataUtil.ObjUtils.findWhereIdByVal(items, {id}, 'name')
    }

    onChange = id => {
        const {valueLink, onChangeCallbBack, idIsNumber, mode} = this.props
        id = idIsNumber ? parseInt(id) : String(id)
        if (!mode) {
            const selectedValue = this.getNameById(id)
            valueLink && this.setValueByReducers(id)
            this.setState({selectedValue}, () => {
                onChangeCallbBack && onChangeCallbBack(id, selectedValue, this)
            })
        }
    }

    createOptions = items => {
        let options = []
        if (DataUtil.is.Array(items)) {
            _.each(items, item => {
                options.push(<Select.Option title={String(item.id)} value={String(item.id)}
                                            key={item.id}>{item.name}</Select.Option>)
            })
        }
        return options
    }

    onDeselect = val => {
        let id, name
        let {selectedValues} = this.state
        const {onDeselectCallback, valueLink, idIsNumber} = this.props
        if (DataUtil.TypeUtils.isInt(val)) {
            id = val
            name = this.getNameById(id)
            if (!name) {
                name = val
                id = this.getIdByName(name)
            }
        } else {
            name = val
            id = this.getIdByName(name)
        }
        id = idIsNumber ? parseInt(id) : String(id)
        const item = {'id': id, name}
        DataUtil.array.remove(selectedValues, name)
        this.setState({selectedValues}, () => {
            this.deleteItemToList(valueLink, item, 'id')
            onDeselectCallback(name, item, this)
        })
    }

    onSelect = (id, option) => {
        let {selectedValues} = this.state
        const {onSelectCallback, valueLink, idIsNumber} = this.props
        const name = this.getNameById(id)
        const item = {
            id: idIsNumber ? parseInt(id) : String(id),
            name: name
        }
        DataUtil.array.push(selectedValues, name)
        this.setState({selectedValues, items: this.items}, () => {
            this.addItemToList(valueLink, item)
            onSelectCallback(id, item, option, this)
        })
    }

    filerItems = value => {
        const {isSearch} = this.state
        if (!isSearch) {
            return
        }
        this.refreshFalg = false
        if (DataUtil.StringUtils.isEmpty(value)) {
            this.setState({items: this.items})
            return
        }
        const item = {name: value}
        const _items = DataUtil.ObjUtils.filterTransform(this.items, item, 'name')
        this.setState({items: _items})
    }

    render() {
        const {width, disabled, size, mode, placeholder} = this.props
        const {items, selectedValues, selectedValue} = this.state
        if (null == this.defaultValue) {
            this.defaultValue = []
        }
        if (mode != null) {
            return (<Select defaultValue={this.defaultValue} size={size} disabled={disabled}
                            value={selectedValues} style={{width: width}} mode={mode} filterOption={false}
                            onDeselect={e => this.onDeselect(e)} placeholder={placeholder}
                            onSelect={(e, o) => this.onSelect(e, o)}
                            onSearch={e => this.filerItems(e)}
                            tokenSeparators={[',']}>
                {this.createOptions(items)}
            </Select>)
        }
        return (
            <Select defaultValue={this.defaultValue} value={selectedValue} size={size} disabled={disabled}
                    style={{width: width}} showSearch={true} placeholder={placeholder}
                    onChange={e => this.onChange(e)}
                    filterOption={(input, option) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0}>
                {this.createOptions(items)}
            </Select>
        )
    }
}