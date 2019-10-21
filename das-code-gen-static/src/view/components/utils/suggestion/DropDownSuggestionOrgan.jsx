/**
 * Created by liang.wang.sh on 16/12/13.
 * 联想下拉组件
 */
import React, {Component, PropTypes} from 'react'
import {findDOMNode} from 'react-dom'
import {fetch} from 'eg-tools'
import Drawing from './Drawing.jsx'//js图形界面
import {trim, getLocalStorageData, setLocalStorageData, union} from '../../../utils/utils.es6'
import {actionType} from '../../../constants/action-type.es6'
import $ from 'jquery'

export default class DropDownSuggestionOrgan extends Component {

    constructor(props, context) {
        super(props, context)
        this.show = 0
        this.localStorageDataKey = 'dropDownSuggestionOrganLocalStorageDataKey'
        this.leng = 35                     //显示长度
        this.maxlength = 10                //最多显示10个
        this.initdata = []
        this.state = {
            formGroup: [],                  //满足联想要求的下拉框
            targetContact: {},              //选中的目标联系人
            customerClassName: '',
            placeHolder: '',
            title: '',                      //展示的数据
            pressToIndex: -1,               //初始化键盘按钮
            status: true                    //点击enter隐藏
        }
        this.formData = this.state.targetContact
        this.hiddenShowTimer()
    }

    initData(formGroup) {
        if (!formGroup) {
            return
        }
        const leng = this.leng
        let finalOutPut = []
        for (var i in formGroup) {
            if (formGroup[i].checked) {
                finalOutPut.push(formGroup[i].organizationName)
            }
        }
        setLocalStorageData(formGroup, this.localStorageDataKey)

        if (finalOutPut.length > 0) {
            finalOutPut = finalOutPut.join(';')
            if (finalOutPut.length > leng) {
                finalOutPut = finalOutPut.replace(/\s/g, '').substring(0, leng) + '.....'
            }
        } else {
            finalOutPut = ''
        }

        //console.log('finalOutPut --->', finalOutPut, 'formGroup --->', formGroup);
        this.setState({
            formGroup: formGroup,
            targetContact: formGroup[0] || '',
            status: false,
            title: finalOutPut
        })
        this.node.value = finalOutPut
        this.formData = formGroup[0] || ''
    }

    componentDidMount() {
        var that = this
        findDOMNode(this.refs['suggestContainer']).addEventListener('mouseover', () => {
            that.judgeStatus()
        })
        this.node = findDOMNode(this.refs['drop-suggestion-input'])
        //控制回调函数执行队列
        this.node.eventArray = []
        this.node.addEventListener('input', (e) => {
            /**
             * 始终保持最后一次输入的回调进行渲染都能够进行网络请求
             */
            that.node.eventArray.push(e.target.value)
            that.handleChange(e.target.value)
        })
        this.node.addEventListener('focus', () => {
            that.setState({
                status: false
            })
        })
        this.setState({
            customerClassName: this.props.customerClassName || 'drop-down-suggestion',
            placeHolder: this.props.placeHolder || '请填写部门'
        })
    }

    handleChange(value) {
        value = trim(value)
        if (!value || value.length == '') {
            return
        }
        let that = this
        that.setState({
            title: value,
            status: false
        })
        this.initData()

        fetch(actionType.BASE_URL + '/user/organization/search?keyword=' + encodeURI(value), {}, function (data) {
                /**
                 * 保证最后输入的结果肯定会被执行，同时优化react渲染时机
                 * @type {T}
                 */
                let lastValue = [].slice.call(that.node.eventArray).pop()
                if (lastValue == value) {
                    let formGroup = that.state.formGroup
                    let _formGroup = data.msg.slice(0, that.maxlength) || data.slice(0, that.maxlength) || []
                    _formGroup.reverse()
                    for (var i in formGroup) {
                        var item = formGroup[i]
                        if (item.checked) {
                            var flag = false
                            for (var j in _formGroup) {
                                if (item.organizationId == _formGroup[j].organizationId) {
                                    _formGroup[j].checked = true
                                    flag = true
                                }
                            }
                            if (!flag) {
                                _formGroup.push(item)
                            }
                        }
                    }
                    _formGroup.reverse()
                    that.setState({
                        formGroup: _formGroup,
                        targetContact: {}
                    })
                }
            }, '', {isLoadingBar: false}
        )
    }

    judgeStatus() {
        this.setState({
            status: false
        })
        if (Object.keys(this.state.targetContact).length < 1) {
            //alert('请选择列表中的联系人数据')
        }
        this.node.eventList = []
        this.formData = this.state.targetContact
    }

    push(item, data) {
        let isIn = false
        for (var i in data) {
            if (data[i].organizationId == item.organizationId) {
                isIn = true
                break
            }
        }
        if (!isIn) {
            data.push(item)
        }
    }

    /**
     * 处理点击操作
     * @param ele
     */
    clickHandler(ele) {
        const leng = this.leng
        let checkedData = getLocalStorageData(this.localStorageDataKey, [])
        let finalOutPut = []
        let formGroup = this.state.formGroup
        for (var i in formGroup) {
            if (formGroup[i].organizationId == ele.organizationId) {
                formGroup[i].checked = !formGroup[i].checked
            }
            if (formGroup[i].checked) {
                finalOutPut.push(formGroup[i].organizationName)
                this.push(formGroup[i], checkedData)
            }
        }
        setLocalStorageData(checkedData, this.localStorageDataKey)

        if (finalOutPut.length > 0) {
            finalOutPut = finalOutPut.join('')
            if (finalOutPut.length > leng) {
                finalOutPut = finalOutPut.replace(/\s/g, '').substring(0, leng) + '.....'
            }
        } else {
            finalOutPut = ''
        }

        //console.log('clickHandler --->', formGroup)
        this.setState({
            formGroup: formGroup,
            targetContact: ele,
            status: false,
            title: finalOutPut
        })
        this.formData = ele
    }

    renderTitleFunc(ele) {
        const leng = this.leng
        let finalOutPut = ele.organizationName
        if (finalOutPut.length > leng) {
            return finalOutPut.replace(/\s/g, '').substring(0, leng) + '...'
        } else {
            return finalOutPut
        }
    }

    createTitle() {
        const leng = this.leng
        let finalOutPut = []
        const formGroup = this.state.formGroup
        for (var i in formGroup) {
            if (formGroup[i].checked) {
                finalOutPut.push(formGroup[i].organizationName)
            }
        }
        if (finalOutPut.length > 0) {
            finalOutPut = finalOutPut.join('')
            if (finalOutPut.length > leng) {
                finalOutPut = finalOutPut.replace(/\s/g, '').substring(0, leng) + '.....'
            }
        } else {
            finalOutPut = ''
        }
        return finalOutPut
    }

    isChecked() {
        const formGroup = this.state.formGroup
        for (var i in formGroup) {
            if (formGroup[i].checked) {
                return true
            }
        }
        return false
    }

    setShow(show) {
        this.show = show
    }

    hiddenShowTimer() {
        const _this = this
        if (this.show == 0) {
            $('#organEditorDialog').on('click', function (e) {
                if (e.target.id == 'input-organization') {
                    //console.log(e.target.id)
                    _this.cancelInput()
                }
                if (e.target.id != 'input-organization' && e.target.className != 'common-input-cancel-button' && e.target.parentNode.className != "select-drop-down-input") {
                    if (_this.isChecked()) {
                        const title = _this.createTitle()
                        _this.setState({
                            status: true,
                            title: title,
                            targetContact: _this.state.formGroup[0] || {}
                        })
                        _this.node.value = title
                    } else {
                        _this.setState({
                            status: true,
                            targetContact: {}
                        })
                    }
                }
            })
            this.show = 1
        }
    }

    cancelInput() {
        this.setState({
            pressToIndex: -1,
            targetContact: {},
            title: ''
        })
        this.formData = {}
        this.node.value = ''
    }

    cancelAll() {
        this.setState({
            formGroup: {},
            pressToIndex: -1,
            targetContact: {},
            title: ''
        })
        this.formData = {}
        this.node.value = ''
        setLocalStorageData([], this.localStorageDataKey)
    }

    renderChildMenu(formGroup) {
        const {pressToIndex, status} = this.state
        const _this = this
        const divStyle = {
            width: '290px',
            position: 'absolute',
            left: '25px',
            top: '7px'
        }
        let XML = formGroup && formGroup.length > 0 ?
            <div id='question-multi-menu-body' className="question-multi-menu-body select-drop-down-list"
                 style={{overflowY: 'auto', height: '330px'}}>
                <ul style={{width: '470px', position: 'relative'}}>
                    {
                        formGroup && formGroup.map((ele, index) => {
                            let value = _this.renderTitleFunc(ele)
                            return <li
                                className={pressToIndex == index ? 'select-drop-down-input on' : 'select-drop-down-input'}
                                onClick={() => {
                                    _this.clickHandler(ele)
                                }}>
                                <input type="checkbox" checked={ele.checked}/>

                                <div style={divStyle}>{value}</div>
                            </li>
                        })
                    }
                </ul>
            </div> : <div></div>
        if (status) {
            XML = null
        }
        return XML
    }

    getOrganIds() {
        let orgids = []
        const formGroup = this.state.formGroup
        for (var i in formGroup) {
            if (formGroup[i].checked) {
                orgids.push(formGroup[i].organizationId)
            }
        }
        orgids = union(orgids)
        return orgids.join(',')
    }

    render() {
        const that = this
        let {formGroup, targetContact} = this.state
        //仅当点击数据的时候才进行样式重新渲染
        let hasDataStyle = Object.keys(targetContact).length > 0 ? {
            width: '450px',
            background: 'rgba(203, 227, 247, 0.06)',
            color: '#000',
            padding: '3px 3px 3px 5px'
        } : {
            padding: '3px 3px 3px 5px',
            border: '1px solid transparent'
        }
        //手动展示
        if (this.node && Object.keys(targetContact).length > 0) {
            this.node.value = this.state.title
        }
        return (
            <div className={this.state.customerClassName} ref='suggestContainer'>
                <div style={{width: 'inherit', padding: '6px 9px'}} className="drop-down-suggestion-head">
                    <div style={hasDataStyle}
                         className={Object.keys(targetContact).length > 0 ? 'drop-down-container-input' : ''}>
                        <input
                            id="input-organization"
                            ref='drop-suggestion-input'
                            style={{
                                outline: 'none',
                                display: 'inline-block',
                                width: '410px',
                                border: 'none',
                                background: 'none',
                                verticalAlign: 'middle'
                            }}
                            placeholder={this.state.placeHolder}/>
                        <Drawing onClick={() => {
                            that.cancelAll()
                        }} show={!!this.state.title}/>
                    </div>
                </div>
                {this.renderChildMenu(formGroup)}
            </div>
        )
    }
}