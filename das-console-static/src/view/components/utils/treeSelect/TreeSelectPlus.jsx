import React from 'react'
import Component from '../base/ComponentAlert'
import {Col, Row, TreeSelect} from 'antd'
import {DataUtil} from '../util/Index'
import _ from 'underscore'

export default class TreeSelectPlus extends Component {

    static defaultProps = {
        treeData: [{
            label: 'Node2',
            value: '0-1',
            key: '0-1',
            children: [{
                label: 'Child Node3',
                value: '0-1-0',
                key: '0-1-0',
            }]
        }],
        sortType: false,
        onSelect: () => {   //多选
        },
        onDeselect: () => { //多选
        }
    }

    constructor(props, context) {
        super(props, context)
        this.state = {
            isRefresh: true,
            searchkey: '',
            value: [],
            treeData: props.treeData,
            names: []
        }
    }

    componentWillReceiveProps(nextProps) {
        const {treeData} = nextProps
        if (this.state.isRefresh && !DataUtil.ObjUtils.isEqual(this.state.treeData, treeData)) {
            const names = DataUtil.ObjUtils.filter(treeData, 'value')
            this.setState({treeData, names})
        }
    }

    onChange = (value, label, extra) => {
        const {onSelect, onDeselect} = this.props
        let names
        if (extra.clear) {  //全不选
            if (extra.preValue[0].value == -1 && extra.triggerValue == -1) {
                //cleanSelect()
                window.console.log('全不选11 ', value)
            } else {
                onDeselect(extra.triggerValue)
            }
        } else {
            if (extra.triggerNode && extra.triggerNode.props && extra.triggerNode.props.value == -1 && extra.triggerValue == -1) {
                const node = {
                    label: extra.triggerNode.props.title,
                    key: extra.triggerNode.props.eventKey,
                    value: extra.triggerNode.props.value
                }
                if (extra.checked) { //全选
                    if (value.length == 1 && value[0] == -1) {
                        names = DataUtil.ObjUtils.filter(extra.triggerNode.props.children, 'key')
                    } else {
                        names = value
                    }
                    onSelect(names, node, extra, this, true)
                } else {  //全不选
                    names = DataUtil.ObjUtils.filter(extra.triggerNode.props.children, 'key')
                    onDeselect(names, true)
                }
            } else if (_.isArray(value) && _.isEmpty(value) && _.isArray(label) && _.isArray(extra.preValue)) {  //全不选
                names = DataUtil.ObjUtils.filter(extra.preValue, 'value')
                onDeselect(names, true)
            }
        }
        value = DataUtil.ObjUtils.sort(value)
        this.setState({value})
    }

    onSelect = (value, node, extra) => {
        if (node.props.eventKey == -1 && node.props.value == -1) {
            return
        }
        const {onSelect, onDeselect} = this.props
        node = {label: node.props.title, key: node.props.eventKey, value: node.props.value}
        if (extra.checked) {
            onSelect(value, node, extra, this)
        } else {
            onDeselect(value)
        }
    }

    onSearch = searchkey => {
        const {treeData} = this.props
        const {isRefresh, value} = this.state
        if (DataUtil.StringUtils.isEmpty(searchkey)) {
            this.setState({treeData, isRefresh: true})
            return
        }

        const children = treeData[0].children
        let list = DataUtil.ObjUtils.filterWhere(children, {value: searchkey}, 'value')
        const root = {
            label: treeData[0].label,
            value: treeData[0].value,
            key: treeData[0].key,
            children: []
        }

        if (!_.isEmpty(value) && !DataUtil.StringUtils.isEmpty(searchkey)) {
            root.children = list
            this.setState({searchkey, treeData: [root], value: [], isRefresh: false})
            return
        }

        let _children = []
        if (!isRefresh) {
            _children = DataUtil.ObjUtils.filterListByKeys(children, value, 'value')
        }

        list = DataUtil.ObjUtils.extendArr(list, _children)
        root.children = list
        this.setState({searchkey, treeData: [root], isRefresh: false})

    }

    refresh = () => {
        const {treeData} = this.props
        this.setState({treeData, isRefresh: true})
    }

    /*
    addItems = () => {

    }*/

    render() {
        const SHOW_CHILD = TreeSelect.SHOW_CHILD
        const {value, treeData} = this.state
        return (
            <div>
                <Row>
                    <Col sm={24}>
                        <TreeSelect treeData={treeData}
                                    multiple={true}
                                    showSearch={true}
                                    treeDefaultExpandAll={true}
                                    allowClear={true}
                                    treeCheckable={true}
                                    showCheckedStrategy={SHOW_CHILD}
                                    searchPlaceholder='Please select'
                                    onChange={::this.onChange}
                                    onSelect={::this.onSelect}
                                    onSearch={::this.onSearch}
                                    value={value}
                                    style={{width: '100%'}}
                                    dropdownStyle={{maxHeight: 500, overflow: 'auto'}}/>
                    </Col>
                    {/* <Col sm={2}>
                        <Button type='dashed' onClick={::this.addItems}>添加</Button>
                    </Col>*/}
                </Row>
            </div>
        )
    }
}