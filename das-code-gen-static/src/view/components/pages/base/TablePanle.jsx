/**
 * Created by liang.wang on 18/9/14.
 */
import React from 'react'
import Component from '../../utils/base/Component'
import {TheadList} from '../../utils/index'
import {column, display} from '../../../../model/base/BaseModel'
import Immutable from 'immutable'
import _ from 'underscore'
import PageBase from '../../page/PageBase'
import {Col, Row} from 'antd'
import QueueAnim from 'rc-queue-anim'
import {DataUtil} from '../../utils/util/Index'

export default class TablePanle extends Component {

    static defaultProps = {
        tyep: 1, //1: 裸表，2:单表, 3: 多表, 4:左侧树+多表
        title: 'title',
        //addButtonShow: true,    //是否显示添加按钮
        checkButtonShow: false,
        pageStyle: null,
        lineTop: 20,
        zDepth: 1,
        navigation: '',
        dangerText: '确认删除吗？',
        isLoadTheadList: true,
        isloadList: true,
        defaultCheckedId: 0,
        isSearchAble: true,
        cleanExceptSearchKeys: [],
        clearSearchCallback: () => {
        },
        onChangeCheckbox: () => {
        },
        clickCheckBack: () => {
        },
        loadListFiler: () => {
        },
        customButton: () => {
            return null
        },
        delete: () => {
        },
        editor: () => {
        },
        check: () => {
        },
        sync: () => {
        },
        add: () => {
        }
    }

    constructor(props, context) {
        super(props, context)
        const {modelName, loadListFiler, isLoadTheadList, isloadList} = props
        this.initValueLink(modelName)
        isloadList && this.loadList(this.getValueToJson(this.searchInfo), this, null, loadListFiler)
        this.state = {
            addButtonShow: true,
            checkButtonShow: props.checkButtonShow,
            isLoadTheadList: isLoadTheadList
        }
        setTimeout(() => {
            this.initColumn()
        }, 500)

    }

    initValueLink = modelName => {
        this.searchResultList = modelName + '.list'
        this.columnInfoBack = modelName + '.columnInfoBack'
        this.columnInfo = modelName + '.columnInfo'
        this.searchInfo = modelName + '.searchInfo'
    }

    loadList = searchInfo => {
        DataUtil.ObjUtils.trim(searchInfo.data)
        this.props.loadList(searchInfo, this, null, this.props.loadListFiler)
    }

    componentWillReceiveProps(nextProps) {
        const {isLoadTheadList, checkButtonShow} = nextProps
        if (isLoadTheadList != this.state.isLoadTheadList || checkButtonShow != this.state.checkButtonShow) {
            this.setState({isLoadTheadList, checkButtonShow})
        }
    }

    initColumn = () => {
        const _this = this
        const columnInfo = this.getValueToJson(this.columnInfo)
        for (const i in columnInfo.column) {
            _.extend(columnInfo.column[i], column)
        }

        if (columnInfo.column[0]['checkbox']) {
            columnInfo['onChange'] = (e, item) => {
                _this.props.onChangeCheckbox(e, item)
            }
        }

        const isShow = button => {
            const customDisplay = this.props.customButton()
            if (_.isObject(button) && button.display && customDisplay.displaybuttons[display.buttons[button.key]]) {
                return true
            }
            return false
        }

        const filter = (customDisplay, _buttons) => {
            let buttons = []
            if (customDisplay === null) {
                buttons = _buttons
            } else {
                _buttons = _buttons.concat(customDisplay.customButtons)
                _buttons.forEach(e => {
                    const key = display.buttons[e.type]
                    if (customDisplay.displaybuttons[key]) {
                        buttons.push(e)
                    }
                })
            }
            return buttons
        }

        const button = columnInfo.column[columnInfo.column.length - 1]['button']
        if (button) {
            let buttons = []
            if (button.add) {
                const addButtonShow = isShow(button.add)
                const checkButtonShow = isShow(button.checkAll)
                this.setState({addButtonShow, checkButtonShow})
            }
            if (button.editor) {
                buttons.push({
                    type: 'editor',
                    title: '编辑',
                    class: '',
                    icon: true,
                    onClick: ele => {
                        ::this.props.editor(ele.toJS())
                    }
                })
            }
            if (button.delete) {
                buttons.push({
                    type: 'delete',
                    title: '删除',
                    text: _this.props.dangerText,
                    icon: true,
                    class: '',
                    onClick: (e, ele) => {
                        _this.props.delete(ele.toJS())
                    }
                })
            }
            if (button.sync) {
                buttons.push(
                    {
                        type: 'sync',
                        title: button.sync.title ? button.sync.title : '同步',
                        text: _this.props.dangerText,
                        icon: true,
                        class: '',
                        onClick: ele => {
                            _this.props.sync(ele.toJS())
                        }
                    }
                )
            }
            if (button.check) {
                buttons.push(
                    {
                        type: 'check',
                        title: button.check.title ? button.check.title : '校验',
                        text: _this.props.dangerText,
                        icon: true,
                        class: '',
                        onClick: ele => {
                            ::this.props.check(ele.toJS())
                        }
                    }
                )
            }
            if (buttons.length > 0) {
                columnInfo.column[columnInfo.column.length - 1]['buttons'] = filter(this.props.customButton(), buttons)
            }
        }
        setTimeout(() => {
            const columnInfoBack = Immutable.fromJS(Object.assign({}, columnInfo))
            this.setValueByReducers(this.columnInfo, Immutable.fromJS(columnInfo))
            this.setValueByReducers(this.columnInfoBack, columnInfoBack)
        }, 500)
    }

    sortBack(key, sort, item, _this) {
        let searchInfo = _this.getValueByReducers(this.searchInfo).toJS()
        searchInfo.sort = item.sortKey ? item.sortKey : key
        searchInfo.ascending = sort == 'asc'
        this.loadList(searchInfo)
        this.props.setValueByReducers(this.searchInfo, Immutable.fromJS(searchInfo))
    }

    loadPageCallback = (ps, _this) => {
        let searchInfo = _this.getValueByReducers(this.searchInfo).toJS()
        searchInfo.page = 1
        searchInfo.pageSize = ps
        this.setValueByReducers(this.searchInfo, Immutable.fromJS(searchInfo))
        this.loadList(searchInfo)
    }

    pageCallback(page, _this) {
        let searchInfo = _this.getValueToJson(this.searchInfo)
        searchInfo.page = page
        this.setValueByReducers(this.searchInfo, Immutable.fromJS(searchInfo))
        this.loadList(searchInfo)
    }

    clearSearchCallback = () => {
        let searchInfo = this.getValueToJson(this.searchInfo)
        searchInfo.data = DataUtil.ObjUtils.clearJson(searchInfo.data, this.props.cleanExceptSearchKeys)
        this.setValueToImmutable(this.searchInfo, searchInfo)
        this.loadList(searchInfo)
    }

    searchOnChangeCallback = (type, v, item, checkeds) => {
        let searchInfo = this.getValueToJson(this.searchInfo)
        if (!searchInfo.data) {
            searchInfo.data = {}
        }
        if (type === 1) {
            searchInfo.data[item.key] = v
        } else {
            searchInfo.data[v] = {checkAll: item, checkeds: checkeds}

        }
        this.setValueToImmutable(this.searchInfo, searchInfo)
        clearTimeout(window.searchOnChangeTimer)
        window.searchOnChangeTimer = setTimeout(() => {
            this.loadList(searchInfo)
        }, 500)
    }

    onChangeRangePickerCallback = (date, item) => {
        let searchInfo = this.getValueToJson(this.searchInfo)
        const key = item.key + 's'
        if (searchInfo.data) {
            searchInfo.data[key] = date
        }
        this.setValueToImmutable(this.searchInfo, searchInfo)
        this.loadList(searchInfo)
    }

    render() {
        const {type, children, tree, zDepth, pageStyle, lineTop, isSearchAble} = this.props
        const {isLoadTheadList, addButtonShow, checkButtonShow} = this.state
        const theadList = this.getValueByReducers(this.searchResultList)
        const columnInfo = this.getValueByReducers(this.columnInfo)
        const table = <TheadList {...this.props}
                                 isSearchAble={isSearchAble}
                                 zDepth={1}
                                 theadList={theadList}
                                 columnInfo={columnInfo}
                                 isLoadTheadList={isLoadTheadList}
                                 sortBack={::this.sortBack}
                                 pageCallback={::this.pageCallback}
                                 loadPageCallback={::this.loadPageCallback}
                                 clearSearchCallback={::this.clearSearchCallback}
                                 searchOnChangeCallback={::this.searchOnChangeCallback}
                                 onChangeRangePickerCallback={::this.onChangeRangePickerCallback}/>
        if (type === 2) {
            if (this.props.title || this.props.navigation) {
                return <div>
                    <PageBase title={this.props.title} navigation={this.props.navigation} clickBack={::this.props.add}
                              clickCheckBack={::this.props.clickCheckBack} checkButtonShow={checkButtonShow}
                              addButtonShow={addButtonShow} style={pageStyle} zDepth={zDepth}>
                        <QueueAnim type={['bottom', 'right']} delay={600}>
                            <div key='a' style={{paddingTop: lineTop + 'px'}}>
                                {table}
                            </div>
                        </QueueAnim>
                    </PageBase>
                </div>
            } else {
                return <div>
                    {table}
                </div>
            }
        } else if (type === 3) {
            return <div>
                <PageBase title={this.props.title} navigation={this.props.navigation} clickBack={::this.props.add}
                          clickCheckBack={::this.props.clickCheckBack} checkButtonShow={checkButtonShow}
                          addButtonShow={addButtonShow} style={pageStyle} zDepth={zDepth}>
                    <QueueAnim type={['bottom', 'right']} delay={600}>
                        <Row key='a'>
                            <Col sm={24}>
                                <div style={{paddingTop: lineTop + 'px'}}>
                                    {table}
                                </div>
                            </Col>
                        </Row>
                        <Row key='b'>
                            <Col sm={24}>
                                {
                                    React.Children.map(children, (child) => {
                                        return <Row>{child}</Row>
                                    })
                                }
                            </Col>
                        </Row>
                    </QueueAnim>
                </PageBase>
            </div>
        } else if (type === 4) {
            return <div>
                <PageBase title={this.props.title} navigation={this.props.navigation} clickBack={::this.props.add}
                          clickCheckBack={::this.props.clickCheckBack} checkButtonShow={checkButtonShow}
                          addButtonShow={addButtonShow} style={pageStyle} zDepth={zDepth}>
                    <Row>
                        <QueueAnim type={['left', 'right']} delay={600} interval={300}>
                            <Col sm={3} key='a'>
                                {tree}
                            </Col>
                        </QueueAnim>
                        <QueueAnim type={['bottom', 'right']} delay={600} interval={300}>
                            <Col sm={21} key='b'>
                                <Row>
                                    <Col sm={24}>
                                        <div style={{paddingTop: lineTop + 'px'}}>
                                            {table}
                                        </div>
                                    </Col>
                                </Row>
                                {
                                    React.Children.map(children, (child) => {
                                        return <Row> <Col sm={24}>{child}</Col></Row>
                                    })
                                }
                            </Col>
                        </QueueAnim>
                    </Row>
                </PageBase>
            </div>
        }
        return {table}
    }
}
