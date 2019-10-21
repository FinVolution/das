/**
 * Created by liang.wang on 17/9/7.
 */
import React from 'react'
import Component from '../base/ComponentAlert'
import {Row, Col, Panel, PanelHeader, PanelContent, Paging} from 'eagle-ui'
import TableRowNew from './TableRow'
import Paper from 'material-ui/Paper'
import './TableList.less'
import classNames from 'classnames'
import {imgs} from './img/imgs'
import Immutable from 'immutable'
import QueueAnim from 'rc-queue-anim'
import {DataUtil} from '../util/Index'
import {Button, DatePicker, Input, Popover, Tooltip} from 'antd'
import {CheckboxGroupPlus} from '../index'

/**
 * div table
 */
export default class TableList extends Component {

    static defaultProps = {
        zDepth: 0,
        columnInfo: '',
        pageSize: 20,
        theadList: [],
        isLoadTheadList: true,
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
            searchValueLink: this.props.searchValueLink,
            expand: 'up',
            refreshRow: false,
            toastType: 'success',
            toastMsg: '',
            iconLoading: false,
            columnInfo: props.columnInfo,
            theadList: props.theadList,
            isShowSarch: false,
            isSarchRow: props.isSarchRow,
            search: {
                page: 1,
                pageSize: props.pageSize
            }
        }
    }

    componentWillReceiveProps(nextProps) {
        const {theadList, columnInfo} = nextProps
        if (nextProps.isLoadTheadList) {
            if (!DataUtil.ObjUtils.isEqual(theadList.toJS(), this.state.theadList.toJS())) {
                this.setState({theadList: theadList})
            }
        }
        if (!DataUtil.ObjUtils.isEqual(columnInfo.toJS(), this.props.columnInfo.toJS())) {
            this.setState({columnInfo: columnInfo})
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

    ceatTitle = item => {
        const imgsrc = this.getImageStyles(item.sortArrow)
        if (item.name == '操作' || item.type === 'sequence') {
            const {isShowSarch} = this.state
            const {isSearchAble} = this.props
            return (<p>{isSearchAble ? <Tooltip placement='right' title='展开或隐藏查询条件'>
                <Button size='small' onClick={() => {
                    this.setState({isShowSarch: !isShowSarch})
                }} icon='search'/>
            </Tooltip> : null}</p>)
        } else {
            if (item.sort) {
                return (<p>{item.name}<img src={imgsrc} style={{position: 'absolute', top: '7px'}}/></p>)
            }
        }
        return item.name
    }

    searchOnChange = (v, item) => {
        this.props.searchOnChangeCallback && this.props.searchOnChangeCallback(1, v, item)
    }

    clearSearch = () => {
        this.setState({isSarchRow: false}, () => {
            this.setState({isSarchRow: true})
            this.props.clearSearchCallback && this.props.clearSearchCallback()
        })
    }

    checkboxCallBack = (key, checkAll, checkeds) => {
        this.props.searchOnChangeCallback && this.props.searchOnChangeCallback(2, key, checkAll, checkeds)
    }

    onChangeRangePicker = (date, dateString, item) => {
        this.props.onChangeRangePickerCallback && this.props.onChangeRangePickerCallback(dateString, item)
    }

    createSearch = item => {
        if (!item.button) {
            if (item.checkbox || item.type) {
                return null
            } else if (item.map) {
                return <Popover placement='left' title={item.name} trigger='click'
                                content={<CheckboxGroupPlus checkboxCallBack={::this.checkboxCallBack}
                                                            options={item.map} keyType={item.key}/>}>
                    <Button icon='filter' size='small'/>
                </Popover>
                /*<SelectPlus items={item.map} mode='multiple' valueLink={this.objName + '.items'}/>*/
            } else if (item.timePicker) {
                const {RangePicker} = DatePicker
                return <Popover placement='left' title={item.name} trigger='click'
                                content={<RangePicker
                                    onChange={(date, dateString) => ::this.onChangeRangePicker(date, dateString, item)}/>}>
                    <Button icon='calendar' size='small'/>
                </Popover>
            } else if (item.search != false) {
                return <Input placeholder='查询...'
                              onChange={e => ::this.searchOnChange(e.target.value, item)}
                              onPressEnter={e => ::this.searchOnChange(e.target.value, item)}/>
            }
        }
        else {
            return <Button icon='reload' size='small' onClick={::this.clearSearch}>查询条件重置</Button>
        }
    }

    render() {
        const columnInfo = this.state.columnInfo
        let theadList = this.state.theadList
        const list = theadList.get('list')
        const {isSearchAble} = this.props
        const {isShowSarch, isSarchRow} = this.state
        const search = this.getValueToJson('ProjectListModel.searchInfo')
        let totals = 0
        if (theadList) {
            theadList = theadList.toJS()
            totals = theadList.totalCount

        }
        return (
            <Paper zDepth={1}>
                <div className='tradeList outerPanel marginTopSpace'>
                    <Panel className='marginTopSpace'>
                        <PanelHeader className='marginSpacePanelHeader'>
                            <Row className='panelHeader'>
                                <QueueAnim animConfig={[
                                    {opacity: [2, 0], translateY: [0, 80]},
                                    {opacity: [2, 0], translateY: [0, -80]}
                                ]} duration={240}>
                                    {
                                        columnInfo && columnInfo.get('column').map((item, i) => {
                                            item = item.toJS()
                                            const _className = classNames(item.sortArrow + '_style', item.classNametitle)
                                            //const imgsrc = this.getImageStyles(item.sortArrow)
                                            return <Col style={{width: item.width + '%'}}
                                                        className={_className}
                                                        onClick={() => {
                                                            item.sort ? this.toggle(i, item) : null
                                                        }} key={i}> {::this.ceatTitle(item)}
                                            </Col>
                                        })
                                    }
                                </QueueAnim>
                            </Row>
                            {
                                (isSearchAble && isSarchRow) ?
                                    <Row className='panelHeader' style={{display: isShowSarch ? 'block' : 'none'}}>
                                        {
                                            columnInfo && columnInfo.get('column').map((item, i) => {
                                                item = item.toJS()
                                                const _className = classNames(item.sortArrow + '_style', item.classNametitle)
                                                return <Col style={{width: item.width + '%'}} className={_className}
                                                            key={i}>
                                                    {::this.createSearch(item)}
                                                </Col>
                                            })
                                        }
                                    </Row> : null
                            }
                        </PanelHeader>
                        <PanelContent style={{'padding-top': '0px'}}>
                            {list &&
                            <TableRowNew {...this.props} columnInfo={columnInfo} list={list} search={search}
                                         ref={(e) => this.tradeRow = e}/>}
                            <Row className='paging-margin'>
                                <Col sm={1}/>
                                <Col sm={11}>
                                    <Paging showItemsNumber={true} loadPageCallback={::this.loadPageCallback}
                                            currentPage={this.state.search.page} pageSize={this.state.search.pageSize}
                                            pageCallback={::this.pageCallback}
                                            total={totals && totals > 0 ? totals : 0}/>
                                </Col>
                            </Row>
                        </PanelContent>
                    </Panel>

                </div>
            </Paper>
        )
    }
}