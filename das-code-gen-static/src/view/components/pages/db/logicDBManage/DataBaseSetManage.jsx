/**
 * Created by liang.wang on 18/9/14.
 */
import React from 'react'
import ManagePanle from '../../base/ManagePanle'
import TablePanle from '../../base/TablePanle'
import {View} from 'ea-react-dm-v14'
import {DatabaseSetControl} from '../../../../../controller/Index'
import TreePanle from '../../base/TreePanle'
import {Modal} from 'antd'
import {Inputlabel, InputPlus, RadioPlus, SelectPlus} from '../../../utils/index'
import Immutable from 'immutable'
import DatabaseSetEntry from './DatabaseSetEntry'
import ApiParamsTab from '../paramsTab/ApiParamsTab'
import {DasUtil} from '../../../utils/util/Index'
import {strategyDbsetType, databaseTypes, das_msg, display} from '../../../../../model/base/BaseModel'
import FrwkUtil from '../../../utils/util/FrwkUtil'
import DataUtil from '../../../utils/util/DataUtil'

@View(DatabaseSetControl)
export default class DataBaseSetManage extends ManagePanle {

    constructor(props, context) {
        super(props, context)
        this.modelName = 'DatabaseSetModel'
        this.editorTitlte = '逻辑数据库'
        this.searchInfo = this.modelName + '.searchInfo'
        this.objName = this.modelName + '.dbSetInfo'
        this.syncLink = '/groupdbset/sync'
        this.checkLink = '/groupdbset/check'
        this.searchInfoSelectId = this.searchInfo + '.data.groupId'
        this.loadList = this.props.loadList
        this.addItem = this.props.addDbSet
        this.deleteItem = this.props.deleteDbSet
        this.updateItem = this.props.updateDbSet
        this.props.loadTree()
        this.cleanExceptKeys = ['dbType', 'groupId']
        this.initValueLink()
        this.dispalyManage.initButtons('/groupdbset/buttons')
        this.state = {
            title: this.editorTitlte,
            groupId: 0,
            dbSetEntryVisible: true,
            visible: false,
            checkVisible: false,
            suggestInitData: null,
            suggestDisabled: false,
            publicStrategyList: [],
            publicStrategyApiParams: [],
            confirmLoading: false,
            currentCheckedId: 0
        }
        this.loadPublicStrategy()
    }

    /** @Override **/
    addValidate = () => {
        if (this.getValueByReducers(this.searchInfoSelectId)) {
            return {
                state: true
            }
        } else {
            return {
                state: false,
                msg: '请选择组!!!'
            }
        }
    }

    /** @Override **/
    beforeDefaultSelected = item => {
        if (item.length > 0) {
            this.setState({dbSetEntryVisible: true})
        }
    }

    /** @Override **/
    getDefaultSelectedCallBack = id => {
        this.onSelectByGroupData(id)
    }

    /** @Override **/
    /*    cleanObjName = () => {
            let item = this.getValueByReducers(this.objName).toJS()
            item.comment = ''
            item.id = ''
            item.dbType = 1
            item.dynamicStrategyId = 0
            item.name = ''

            this.setValueByReducers(this.objName, Immutable.fromJS(item))


        }*/

    /** @Override **/
    editorCallBack = item => {
        this.setState({publicStrategyApiParams: item.apiParams})
    }

    /** @Override **/
    loadListFilerBefore = data => {
        DasUtil.transformStrategyStrToList(data.list, 'strategySource')
        return data
    }

    onSelect(selectedKeys, info, appid) {
        if (!info) {
            this.setState({dbSetEntryVisible: false}, () => {
                this.setValueToImmutable(this.modelName + '.list', [])
                window.setTimeout(() => {
                    this.setValueToImmutable(this.modelName + '.list', [])
                }, 1000)
            })
            return
        }
        this.setState({dbSetEntryVisible: true}, () => {
            const groupId = info.node.props.dataRef ? info.node.props.dataRef.id : null
            this.onSelectByGroupData(groupId, appid)
        })
    }

    onSelectByGroupData = (groupId, appid) => {
        let searchInfo = this.getValueToJson(this.searchInfo)
        let objName = this.getValueToJson(this.objName)
        searchInfo.data['groupId'] = groupId
        searchInfo.data['app_id'] = appid
        objName['groupId'] = groupId
        this.setState({groupId}, () => {
            this.loadList(searchInfo, this, (_this, rs) => {
                if (rs.code === 200) {
                    _this.setValueByReducers(_this.searchInfo, Immutable.fromJS(searchInfo))
                    _this.setValueByReducers(_this.objName, Immutable.fromJS(objName))
                } else {
                    _this.showErrorsNotification(rs.msg)
                }
            }, this.loadListFiler)
            this.props.loadDBList(groupId)
        })
    }


    loadPublicStrategy = () => {
        FrwkUtil.fetch.fetchGet('/publicStrategy/list', null, this, (data, _this) => {
                if (data.code === 200) {
                    DasUtil.transformStrategyStrToList(data.msg, 'strategyParams', 'placeholder')
                    _this.setState({publicStrategyList: data.msg})
                } else if (data.code === 500) {
                    _this.showErrorsMsg(data.msg)
                }
            }
        )
    }

    onChangeCallback = id => {
        if (id == 0) {//无策略
            //TODO
        }
        if (id == 1) {//私有
            //TODO
        }
        if (id == 2) {//共有
            //this.loadPublicStrategy()
        }
    }

    onChangeCallbBack = id => {
        if (id === 0) {
            return
        }
        const {publicStrategyList} = this.state
        const publicStrategy = DataUtil.ObjUtils.findWhere(publicStrategyList, {id: id})
        const publicStrategyApiParams = publicStrategy.apiParams
        this.setState({publicStrategyApiParams})
        this.replaceValuesToItem(this.objName, {
            className: publicStrategy.className,
            apiParams: publicStrategy.apiParams
        })
    }

    createModel = () => {
        const {confirmLoading, publicStrategyList, publicStrategyApiParams} = this.state
        const {setValueByReducers, databasesetmodel} = this.props
        const _props = {setValueByReducers, databasesetmodel}
        const item = this.getValueToJson(this.objName)
        const states = this.getValueToJson(this.states)
        return <Modal title={this.state.title}
                      width={800}
                      maskClosable={false}
                      visible={states.editeVisible}
                      onOk={::this.handleOk}
                      confirmLoading={confirmLoading}
                      onCancel={::this.handleCancel}>
            <Inputlabel title='数据库类型'>
                <RadioPlus {..._props}
                           items={databaseTypes} valueLink={this.objName + '.dbType'} selectedId={item.dbType}
                           disabled={states.editerType === 1}/>
            </Inputlabel>
            <Inputlabel title='逻辑数据库名'>
                <InputPlus {..._props}
                           valueLink={this.objName + '.name'} placeholder={das_msg.ordinary_name}
                           defaultValue={item.name} validRules={{maxLength: 150, isEnglishnderline: true}}/>
            </Inputlabel>
            <Inputlabel title='策略类型'>
                <RadioPlus {..._props}
                           items={strategyDbsetType} valueLink={this.objName + '.strategyType'}
                           onChangeCallback={::this.onChangeCallback} selectedId={item.strategyType}
                           disabled={states.editerType === 1}/>
            </Inputlabel>
            <Inputlabel title='共有策略名' display={item.strategyType === 2}>
                <SelectPlus {..._props}
                            selectedId={item.dynamicStrategyId} items={publicStrategyList}
                            onChangeCallbBack={::this.onChangeCallbBack}
                            valueLink={this.objName + '.dynamicStrategyId'}/>
            </Inputlabel>
            <Inputlabel title={item.strategyType == 2 ? '共有策略类名(全名)' : '私有策略类名(全名)'}
                        display={item.strategyType != 0}>
                <InputPlus {..._props}
                           disabled={item.strategyType == 2} valueLink={this.objName + '.className'}
                           validRules={{maxLength: 150}} defaultValue={item.className}
                           placeholder='例如:com.ppdai.invest.dplan.account.service.dao.HelloStrategy'/>
            </Inputlabel>
            <Inputlabel title='共有策略参数' display={item.strategyType === 2}>
                <ApiParamsTab  {..._props} apiParams={publicStrategyApiParams}
                               valueLink={this.objName + '.apiParams'}
                               limited={{add: false, delete: false, keyEditor: false}}/>
            </Inputlabel>
            <Inputlabel title='私有策略参数' display={item.strategyType === 1}>
                <ApiParamsTab {..._props} apiParams={item.apiParams}
                              valueLink={this.objName + '.apiParams'}/>
            </Inputlabel>
        </Modal>
    }

    onChangeCheckbox = item => {
        item = item.toJS()
        const link = this.modelName + '.list'
        let items = this.getValueByReducers(link).toJS()
        items.list && items.list.forEach(e => {
            if (item.id == e.id) {
                e.checkbox = true
            } else {
                e.checkbox = false
            }
        })
        this.setState({currentCheckedId: item.id}, () => {
            this.setValueByReducers(link, items)
        })
    }

    clickCheckBack = item => {
        const objName = this.getValueToJson(this.objName)
        FrwkUtil.fetch.fetchGet('/groupdbset/groupCheck', {dbSetId: item.id, gourpId: objName.groupId}, this, data => {
            if (data.code == 500 && DataUtil.is.String(data.msg) && !data.item) {
                this.showErrorsNotification(data.msg)
            } else if (DataUtil.is.Object(data.item)) {
                const states = {
                    checkVisible: true,
                    checkData: data
                }
                this.replaceValuesToItem(this.states, states)
            }
        })
    }

    customButton = () => {
        const displaybuttons = this.getValueToJson(this.modelName + display.buttons_path)
        return {
            displaybuttons: displaybuttons,
            customButtons: []
        }
    }

    render() {
        const displaybuttons = this.getValueToJson(this.modelName + display.buttons_path)
        const dblist = this.getValueToJson(this.modelName + '.dblist')
        const {databasesetmodel, setValueByReducers} = this.props
        const dbSetInfo = this.getValueToJson(this.objName)
        const {currentCheckedId, dbSetEntryVisible} = this.state
        const states = this.getValueToJson(this.states)
        return (<div>
            {states.editeVisible ? ::this.createModel() : null}
            {states.checkVisible ? ::this.createCheckModel() : null}
            <TablePanle title='逻辑数据库管理' navigation='数据访问平台 / 逻辑数据库管理'
                        type={4}
                        lineTop={0}
                        modelName={this.modelName}
                        databasesetmodel={databasesetmodel}
                        isloadList={false}
                        checkButtonShow={displaybuttons.showCkeckAllButton}
                        onChangeCheckbox={::this.onChangeCheckbox}
                        add={::this.add}
                        delete={::this.delete}
                        editor={::this.editor}
                        sync={::this.sync}
                        check={::this.check}
                        customButton={::this.customButton}
                        setValueByReducers={setValueByReducers}
                        loadListFiler={::this.loadListFiler}
                        loadList={::this.loadList}
                        clickCheckBack={::this.clickCheckBack}
                        tree={<TreePanle rootShow={false}
                                         showLine={true}
                                         getDefaultSelected={::this.getDefaultSelected}
                                         format={{
                                             tree: {
                                                 title: 'group_name',
                                                 key: 'id',
                                                 tooltip: 'group_comment',
                                                 isLeaf: true
                                             }
                                         }}
                                         onSelect={::this.onSelect}/>}>
                {dbSetEntryVisible ?
                    <DatabaseSetEntry dbset_id={currentCheckedId} dblist={dblist} groupId={dbSetInfo.groupId}/> : null}
            </TablePanle>
        </div>)
    }
}
