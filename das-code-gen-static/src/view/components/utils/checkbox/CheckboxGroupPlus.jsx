/**
 * Created by liang.wang on 19/01/16.
 */
import React from 'react'
import Component from '../base/Component'
import {Checkbox} from 'antd'
import {DataUtil} from '../../utils/util/Index'
import _ from 'underscore'

export default class CheckboxGroupPlus extends Component {

    static defaultProps = {
        keyType: '',
        options: {1: 'MySql', 2: 'SQLServer'},
        defaultCheckedList: [],      //['mySql']
        checkboxCallBack: function () {
        }
    }

    constructor(props, context) {
        super(props, context)
        this.defaultCheckedList = this.initPlainOptions(props.options)
        this.state = {
            plainOptions: this.defaultCheckedList,
            checkedList: _.isEmpty(props.defaultCheckedList) ? this.defaultCheckedList : props.defaultCheckedList,
            indeterminate: true,
            checkAll: false
        }
    }

    componentWillReceiveProps(nextProps) {
        const {selectedId} = nextProps
        if (selectedId != this.state.selectedId) {
            this.setState({selectedId})
        }
    }

    initPlainOptions = options => {
        const _options = DataUtil.ObjUtils.objToArr(options)
        const plainOptions = DataUtil.ObjUtils.filter(_options, 'name')
        return plainOptions
    }

    checkboxOnChangge = (checkAll, checkedList) => {
        const key = this.props.keyType + 's'
        const options = DataUtil.ObjUtils.objToArr(this.props.options)
        const checkeds = DataUtil.ObjUtils.filterArrToItems(options, checkedList)
        this.props.checkboxCallBack && this.props.checkboxCallBack(key, checkAll, checkeds, checkedList)
    }

    onChange = checkedList => {
        const {plainOptions} = this.state
        const checkAll = checkedList.length === plainOptions.length
        this.setState({
            checkedList,
            indeterminate: !!checkedList.length && (checkedList.length < plainOptions.length),
            checkAll
        }, this.checkboxOnChangge(checkAll, checkedList))
    }

    onCheckAllChange = e => {
        const {plainOptions} = this.state
        const checkedList = e.target.checked ? plainOptions : []
        const checkAll = e.target.checked
        this.setState({
            checkedList,
            indeterminate: false,
            checkAll
        }, this.checkboxOnChangge(checkAll, checkedList))
    }

    render() {
        const CheckboxGroup = Checkbox.Group
        const {checkedList, indeterminate, checkAll, plainOptions} = this.state
        return (
            <div>
                <div style={{borderBottom: '1px solid #E9E9E9'}}>
                    <Checkbox indeterminate={indeterminate}
                              onChange={::this.onCheckAllChange}
                              checked={checkAll}>
                        全选
                    </Checkbox>
                </div>
                <br/>
                <CheckboxGroup options={plainOptions} value={checkedList} onChange={::this.onChange}/>
            </div>
        )
    }
}