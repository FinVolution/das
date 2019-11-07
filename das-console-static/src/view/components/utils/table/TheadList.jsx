/**
 * Created by liang.wang on 17/9/7.
 */
import React from 'react'
import Component from '../base/ComponentAlert'
import {Row, Col, Paging} from 'eagle-ui'
import {Checkbox, Input} from 'antd/lib'
import './TbodyRow.less'
import classNames from 'classnames'
import {imgs} from './img/imgs'
import {Button, Popover, Popconfirm, Tooltip, DatePicker} from 'antd'
import {display} from '../../../../model/base/BaseModel'
import Immutable from 'immutable'
import {DataUtil} from '../util/Index'
//import _ from 'underscore'
import {CodeEditor, CheckboxGroupPlus} from '../index'
import FrwkUtil from '../util/FrwkUtil'

/**
 *  table
 */
export default class TheadList extends Component {

    static defaultProps = {
        zDepth: 0,
        columnInfo: '',
        isLoadTheadList: true,
        theadList: [],
        checkBoxMulti: false,
        isSarchRow: true,
        isSearchAble: true,
        onChangeRangePickerCallback: function () {
        },
        searchOnChangeCallback: function () {
        },
        clearSearchCallback: function () {
        },
        loadPageCallback: function () {
        },
        pageCallback: function () {
        },
        sortBack: function () {
        }
    }

    constructor(props, context) {
        super(props, context)
        this.columnInfo = Object.assign({}, props.columnInfo)
        this.state = {
            expand: 'up',
            refreshRow: false,
            toastType: 'success',
            toastMsg: '',
            iconLoading: false,
            columnInfo: props.columnInfo,
            theadList: props.theadList,
            isLoadTheadList: props.isLoadTheadList,
            isShowSarch: false,
            isSarchRow: props.isSarchRow,
            search: {
                page: 1,
                pageSize: 10
            }
        }
    }

    componentWillReceiveProps(nextProps) {
        if (nextProps.isLoadTheadList) {
            if (DataUtil.is.Object(nextProps.theadList) && !DataUtil.ObjUtils.isEqual(nextProps.theadList.toJS(), this.state.theadList.toJS())) {
                this.setState({theadList: nextProps.theadList})
            }
        }
        if (!DataUtil.ObjUtils.isEqual(nextProps.columnInfo.toJS(), this.props.columnInfo.toJS())) {
            this.setState({columnInfo: nextProps.columnInfo})
        }
    }

    getImageStyles(type) {
        switch (type) {
            case 'both':
                return imgs.SORT_BOTH
            case 'asc':
                return imgs.SORT_ASC
            case 'desc':
                return imgs.SORT_DESC
        }
    }

    loadPageCallback(ps) {
        this.setState({
            search: {
                page: 1,
                pageSize: parseInt(ps)
            }
        }, () => {
            this.props.loadPageCallback && this.props.loadPageCallback(parseInt(ps), this)
        })
    }

    pageCallback(page) {
        this.setState({
            search: {
                page: page,
                pageSize: this.state.search.pageSize
            }
        }, () => {
            this.props.pageCallback && this.props.pageCallback(page, this)
        })
    }

    toggle(num, item) {
        const columnInfo = this.state.columnInfo.toJS()
        if (!columnInfo.column[num].key) {
            return
        }
        const addSelected = (classNametitle) => {
            if (classNametitle.indexOf('col-color-select') > -1) {
                return classNametitle
            }
            return classNametitle + 'col-color-select'
        }
        const removeSelected = (classNametitle) => {
            return classNametitle.replace('col-color-select', '')
        }

        for (const i in columnInfo.column) {
            if (num == i) {
                columnInfo.column[i].classNametitle = addSelected(columnInfo.column[i].classNametitle)
                columnInfo.column[i].classNameColumn = addSelected(columnInfo.column[i].classNameColumn)
                columnInfo.column[i].sortArrow = this.toggleSort(columnInfo.column[i].key, columnInfo.column[i].sortArrow, item)
            } else {
                columnInfo.column[i].classNametitle = removeSelected(columnInfo.column[i].classNametitle)
                columnInfo.column[i].classNameColumn = removeSelected(columnInfo.column[i].classNameColumn)
                columnInfo.column[i].sortArrow = 'both'
            }
        }
        this.setState({columnInfo: Immutable.fromJS(columnInfo)})
        //this.setValueByReducers(this.props.columnInfo, Immutable.fromJS(columnInfo))
    }

    toggleSort(key, sort, item) {
        let _sort = 'sort'
        switch (sort) {
            case 'both':
                _sort = 'asc'
                break
            case 'asc':
                _sort = 'desc'
                break
            case 'desc':
                _sort = 'asc'
                break
        }
        this.props.sortBack && this.props.sortBack(key, _sort, item, this)
        return _sort
    }

    //TODO 优化SPAN
    createButton(ele, row) {
        const timetemp = Date.parse(new Date())
        return <div className='button-editor' key={timetemp}>
            {
                ele && ele.buttons && ele.buttons.map && ele.buttons.map((item, i) => {
                    if ('delete' == item.type) {
                        return <span style={{paddingRight: '8px'}} key={i}>
                                <Popconfirm placement='top' title={item.text} okText='是' cancelText='否'
                                            onConfirm={(e) => item.onClick(e, row)}>
                            {item.icon ?
                                <Tooltip placement='top' title={item.title}>
                                    <Button icon='delete' size='small' type='danger'/>
                                </Tooltip> :
                                <Button size='small' type={item.type}>{item.title}</Button>}
                        </Popconfirm>
                            </span>
                    }
                    if ('editor' == item.type) {
                        return item.icon ?
                            <span style={{paddingRight: '8px'}} key={i}>
                            <Tooltip placement='top' title={item.title}>
                                <Button icon='edit' size='small' onClick={() => item.onClick(row)}/>
                            </Tooltip></span> :
                            <span style={{paddingRight: '8px'}} key={i}>
                            <Button size='small' type={item.type}
                                    onClick={() => item.onClick(row)}>{item.title}</Button>
                                </span>

                    }
                    if ('sync' == item.type) {
                        return item.icon ?
                            <span style={{paddingRight: '8px'}} key={i}>
                            <Tooltip placement='top' title={item.title}>
                                <Button icon='sync' size='small' onClick={() => item.onClick(row)}/>
                            </Tooltip>
                            </span> :
                            <span style={{paddingRight: '8px'}} key={i}>
                                <Button size='small' type={item.type}
                                        onClick={() => item.onClick(row)}>{item.title}</Button>
                            </span>
                    }
                    if ('check' == item.type) {
                        return item.icon ?
                            <span style={{paddingRight: '8px'}} key={i}>
                            <Tooltip placement='top' title={item.title}>
                                <Button icon='check-circle-o' size='small' onClick={() => item.onClick(row)}/>
                            </Tooltip>
                            </span> :
                            <span style={{paddingRight: '8px'}} key={i}>
                                <Button size='small' type={item.type}
                                        onClick={() => item.onClick(row)}>{item.title}</Button>
                            </span>
                    }
                    if (display.custom.includes(item.type)) {
                        return item.button(row)
                    }
                })
            }
        </div>
    }

    addColor(list, index, thColor, tdColorOdd, tdColorEve) {
        return list.map((item, i) => {
            if (!item.get('button')) {
                if (i === index) {
                    item = item.set('thBackgroundColor', thColor)
                    item = item.set('tdBackgroundColorOdd', tdColorOdd)
                    item = item.set('tdBackgroundColorEve', tdColorEve)
                } else {
                    item = item.set('thBackgroundColor', null)
                    item = item.set('tdBackgroundColorOdd', null)
                    item = item.set('tdBackgroundColorEve', null)
                }
            }
            return item
        })
    }

    //TODO 竖行变色
    onMouseover = index => {
        return index
        /* let columnInfo = this.state.columnInfo
         const column = this.addColor(this.state.columnInfo.get('column'), index, 'Gainsboro', '#FAFAFA', 'WhiteSmoke')
         columnInfo = columnInfo.set('column', column)
         this.setState({columnInfo: columnInfo})*/
    }

    getWidth = width => {
        if (DataUtil.is.Number(width)) {
            return {width: width + '%'}
        } else {
            return {width: width}
        }
    }

    ceatTitle = item => {
        const imgsrc = this.getImageStyles(item.sortArrow)
        if (item.name == '操作') {
            const {isShowSarch} = this.state
            const {isSearchAble} = this.props
            return (<p>{item.name}&nbsp;&nbsp;&nbsp;&nbsp;
                {isSearchAble ? <Tooltip placement='top' title='展开或隐藏查询条件'>
                    <Button icon='select' size='small' onClick={() => {
                        this.setState({isShowSarch: !isShowSarch})
                    }}/>
                </Tooltip> : null}</p>)
        } else {
            if (item.sort) {
                return (<p>{item.name}<img src={imgsrc} style={{position: 'absolute', top: '7px'}}/></p>)
            }
        }
        return item.name
    }

    /**
     *  popover: {title: '标题', content: '显示内容：可选,有则显示按钮，没有则显示内容', maximum：‘最多显示字数’，placement：‘提示框的位置’}
     */
    createTd = (item, ele) => {
        const createShow = (item, value, type, maximum) => {
            if (!DataUtil.StringUtils.isEmpty(value) && value.length > maximum) {
                value = value.substring(0, maximum) + '...'
            }
            const content = item.popover.content ? item.popover.content : value
            if (type === 1) {
                return <Button type='dashed'>{content}</Button>
            } else if (type === 2) {
                return <a type='dashed'>{content}</a>
            }
        }
        let value = item.key ? (DataUtil.StringUtils.isEmpty(ele.get(item.key)) ? '-' : ele.get(item.key)) : '-'
        if (item.popover) {
            const maximum = item.popover.maximum ? item.popover.maximum : 15
            if (value.length > maximum) {
                if (item.popover.type) {
                    value = <CodeEditor style={{width: '1000px', height: '400px'}}
                                        contStyle={{width: '1000px', height: '400px'}}
                                        mode={item.popover.type} theme='monokai' value={value}/>
                }
                const placement = item.popover.placement ? item.popover.placement : 'bottom'
                const show = item.popover.content ? createShow(item, value, 1, maximum) : createShow(item, value, 2, maximum)
                const content = FrwkUtil.createContent(value)
                return <Popover placement={placement} content={content} title={item.popover.title}>
                    {show}
                </Popover>
            }
        }
        return item.map ? DataUtil.StringUtils.isEmpty(item.map[ele.get(item.key)]) ? '-' : item.map[ele.get(item.key)] : value
    }

    searchOnChange = (v, item) => {
        this.props.searchOnChangeCallback && this.props.searchOnChangeCallback(1, v, item)
    }

    clearSearch = () => {
        this.setState({isSarchRow: false}, () => {
            this.setState({isSarchRow: true}, () => {
                this.props.clearSearchCallback && this.props.clearSearchCallback()
            })
        })
    }

    checkboxCallBack = (key, checkAll, checkeds) => {
        this.props.searchOnChangeCallback && this.props.searchOnChangeCallback(2, key, checkAll, checkeds)
    }

    onChangeRangePicker = (date, item) => {
        this.props.onChangeRangePickerCallback && this.props.onChangeRangePickerCallback(date, item)
    }

    createSearch = item => {
        if (!item.button) {
            if (item.checkbox) {
                return null
            } else if (item.map) {
                return <Popover placement='top' title={item.name} trigger='click'
                                content={<CheckboxGroupPlus checkboxCallBack={::this.checkboxCallBack}
                                                            options={item.map} keyType={item.key}/>}>
                    <Button icon='filter' size='small'/>
                </Popover>
                /*<SelectPlus items={item.map} mode='multiple' valueLink={this.objName + '.items'}/>*/
            } else if (item.timePicker) {
                const {RangePicker} = DatePicker
                return <Popover placement='top' title={item.name} trigger='click'
                                content={<RangePicker
                                    onChange={(date, dateString) => ::this.onChangeRangePicker(dateString, item)}/>}>
                    <Button icon='calendar' size='small'/>
                </Popover>
            } else if (item.search != false) {
                return <Input placeholder='查询...'
                              onChange={e => ::this.searchOnChange(e.target.value, item)}
                              onPressEnter={e => ::this.searchOnChange(e.target.value, item)}/>
            }
        }
        else {
            return <Button icon='reload' size='small' onClick={::this.clearSearch}>查询重置</Button>
        }
        /*<Search onChange={e => ::this.searchOnChange(e.target.value, item)}
                                          placeholder='查询....' style={{
                                      width: '100%',
                                      paddingLeft: '14px'
                                  }}/>*/
    }

    render() {
        const _this = this
        const {isSearchAble} = this.props
        const {isSarchRow, isShowSarch} = this.state
        //const Search = Input.Search
        const columnInfo = this.state.columnInfo
        let theadList = this.state.theadList
        const list = theadList.get('list')
        let totals = 0
        if (theadList) {
            theadList = theadList.toJS()
            totals = theadList.totalCount
        }
        /*if (list && _.isEmpty(list.toJS())) {
            return <div/>
        }*/
        let rowNo = 1
        return (
            <div>
                <table style={{borderSpacing: 0, borderCollapse: 'collapse', tableLayout: 'fixed', width: '100%'}}
                       border='0'>
                    <thead className='marginSpacePanelHeader'>
                    <tr className='panelHeader'>
                        {
                            columnInfo && columnInfo.get('column').map((item, i) => {
                                item = item.toJS()
                                const _className = classNames(item.sortArrow + '_style', item.classNametitle)
                                const width = this.getWidth(item.width)
                                return item.visible &&
                                    <th style={{
                                        position: 'relative',
                                        border: '1px solid #e9e9e9',
                                        width: width.width,
                                        height: item.height ? item.height : '34px',
                                        textAlign: item.align ? item.align : 'center',
                                        cursor: 'pointer',
                                        backgroundColor: item.thBackgroundColor ? item.thBackgroundColor : null
                                    }} className={_className} onClick={() => {
                                        item.sort ? this.toggle(i, item) : null
                                    }} onMouseOver={() => {
                                        this.onMouseover(i)
                                    }} key={'th' + i}>
                                        {::this.ceatTitle(item)}
                                    </th>
                            })
                        }
                    </tr>
                    {
                        (isSearchAble && isSarchRow) ?
                            <tr className='panelHeader' style={{display: isShowSarch ? 'table-row' : 'none'}}>
                                {
                                    columnInfo && columnInfo.get('column').map((item, i) => {
                                        item = item.toJS()
                                        const width = this.getWidth(item.width)
                                        return item.visible &&
                                            <th style={{
                                                position: 'relative',
                                                border: '1px solid #fff',
                                                width: width.width,
                                                height: item.height ? item.height : '34px',
                                                textAlign: item.align ? item.align : 'center',
                                                cursor: 'pointer',
                                                backgroundColor: '#fff'
                                            }} key={i}>
                                                {::this.createSearch(item)}
                                            </th>
                                    })
                                }
                            </tr> : null
                    }
                    </thead>

                    <tbody style={{paddingTop: '0px'}} className='tbodyRow'>
                    {
                        list && list.map((ele) => {
                            const ov = rowNo % 2 == 0
                            let rowColor = ov ? 'row-color-odd' : 'row-color-eve'
                            rowNo++
                            const checkbox = ele.get('checkbox')
                            if (checkbox) {
                                rowColor = 'row-color-checked'
                            }
                            return <tr className={rowColor} key={'tr' + rowNo} onClick={e => {
                                columnInfo.toJS().onChange && columnInfo.toJS().onChange(ele, e)
                            }}>
                                {
                                    columnInfo && columnInfo.get('column').map((item, i) => {
                                        item = item.toJS()
                                        const width = this.getWidth(item.width)
                                        if (item.button) {
                                            return item.visible &&
                                                <td style={width} className={item.classNameColumn} key={'td' + i}>
                                                    {_this.createButton(item, ele)}
                                                </td>
                                        } else if (item['checkbox']) {
                                            return item.visible && <td style={width} className='td-center' key={i}>
                                                {<Checkbox checked={checkbox} onChange={e => {
                                                    columnInfo.toJS().onChange(ele, e)
                                                }}/>}
                                            </td>
                                        }
                                        let backgroundColor = null
                                        if (ov) {
                                            backgroundColor = item.tdBackgroundColorOdd ? item.tdBackgroundColorOdd : null
                                        } else {
                                            backgroundColor = item.tdBackgroundColorEve ? item.tdBackgroundColorEve : null
                                        }
                                        return item.visible &&
                                            <td key={'td' + i} className='td-center' style={{
                                                width: this.getWidth(item.width),
                                                border: '1px solid #e9e9e9',
                                                backgroundColor: backgroundColor
                                            }}>
                                                {::this.createTd(item, ele)}
                                            </td>
                                    })
                                }
                            </tr>
                        })
                    }
                    </tbody>
                </table>
                <Row className='paging-margin'>
                    <Col sm={1}/>
                    <Col sm={11}>
                        <Paging showItemsNumber={true} loadPageCallback={::this.loadPageCallback}
                                currentPage={this.state.search.page}
                                pageSize={this.state.search.pageSize}
                                pageCallback={::this.pageCallback}
                                total={totals && totals > 0 ? totals : 0}/>
                    </Col>
                </Row>
            </div>
        )
    }
}