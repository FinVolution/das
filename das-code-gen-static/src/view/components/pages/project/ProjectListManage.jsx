/**
 * Created by liang.wang on 18/12/28.
 */
import React from 'react'
import {View} from 'ea-react-dm-v14'
import Component from '../../utils/base/ComponentAlert'
import {PojectListControl} from '../../../../controller/Index'
import {TableList} from '../../utils/index'
import {column} from '../../../../model/base/BaseModel'
import Immutable from 'immutable'
import _ from 'underscore'
import {DataUtil} from '../../utils/util/Index'

@View(PojectListControl)
export default class ProjectListManage extends Component {

    static defaultProps = {
        tyep: 1, //1: 裸表，2:单表, 3: 多表, 4:左侧树+多表
        title: 'title',
        modelName: 'ProjectListModel',
        addButtonShow: true,    //是否显示添加按钮
        checkButtonShow: false,
        pageStyle: null,
        lineTop: 20,
        zDepth: 1,
        navigation: '',
        dangerText: '确认删除吗？',
        isLoadTheadList: true,
        isloadList: true,
        defaultCheckedId: 0,
        onChangeCheckbox: () => {
        },
        clickCheckBack: () => {
        },
        loadListFiler: (_this, data) => {
            if (data.code === 500) {
                _this.showErrorsNotification(data.msg)
            }
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
        const {modelName, isLoadTheadList, isloadList} = props
        this.initValueLink(modelName)
        isloadList && this.loadList(this.getValueToJson(this.searchInfo))
        this.initColumn()
        this.state = {
            addButtonShow: props.addButtonShow,
            checkButtonShow: props.checkButtonShow,
            isLoadTheadList: isLoadTheadList
        }
    }

    initValueLink = modelName => {
        this.searchResultList = modelName + '.list'
        this.columnInfoBack = modelName + '.columnInfoBack'
        this.columnInfo = modelName + '.columnInfo'
        this.searchInfo = modelName + '.searchInfo'
    }

    loadList = searchInfo => {
        DataUtil.ObjUtils.trim(searchInfo.data)
        this.props.loadList(searchInfo, this, this.props.loadListFiler)
    }

    sortBack(key, sort, item, _this) {
        let searchInfo = _this.getValueByReducers(this.searchInfo).toJS()
        searchInfo.sort = item.sortKey ? item.sortKey : key
        searchInfo.ascending = sort == 'asc'
        this.loadList(searchInfo)
        this.props.setValueByReducers(this.searchInfo, Immutable.fromJS(searchInfo))
    }

    loadPageCallback(ps) {
        let searchInfo = this.getValueToJson(this.searchInfo)
        searchInfo.page = 1
        searchInfo.pageSize = ps
        this.setValueByReducers(this.searchInfo, Immutable.fromJS(searchInfo))
        this.loadList(searchInfo)
    }

    pageCallback(page) {
        let searchInfo = this.getValueToJson(this.searchInfo)
        searchInfo.page = page
        this.setValueByReducers(this.searchInfo, Immutable.fromJS(searchInfo))
        this.loadList(searchInfo)
    }

    initColumn() {
        const columnInfo = this.getValueByReducers(this.columnInfo).toJS()
        for (const i in columnInfo.column) {
            _.extend(columnInfo.column[i], column)
        }
        this.setValueByReducers(this.columnInfo, Immutable.fromJS(columnInfo))
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
        const {projectlistmodel, setValueByReducers} = this.props
        const columnInfo = this.getValueByReducers(this.columnInfo)
        const theadList = this.getValueByReducers(this.searchResultList)
        return <TableList projectlistmodel={projectlistmodel}
                          setValueByReducers={setValueByReducers}
                          columnInfo={columnInfo}
                          theadList={theadList}
                          sortBack={::this.sortBack}
                          pageCallback={::this.pageCallback}
                          loadPageCallback={::this.loadPageCallback}
                          clearSearchCallback={::this.clearSearchCallback}
                          searchOnChangeCallback={::this.searchOnChangeCallback}
                          onChangeRangePickerCallback={::this.onChangeRangePickerCallback}/>
    }
}
