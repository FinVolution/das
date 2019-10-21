/**
 * Created by liang.wang on 18/9/14.
 */
import React from 'react'
import ManagePanle from '../base/ManagePanle'
import TablePanle from '../base/TablePanle'
import {View} from 'ea-react-dm-v14'
import {PublicStrategyControl} from '../../../../controller/Index'
import {Modal} from 'antd'
import {Inputlabel, InputPlus, RadioPlus, CodeEditor} from '../../utils/index'
import {das_msg, strategyType} from '../../../../model/base/BaseModel'
import ApiParamsTab from './paramsTab/ApiParamsTab'
import {DasUtil} from '../../utils/util/Index'

@View(PublicStrategyControl)
export default class PublicStrategyManage extends ManagePanle {

    constructor(props, context) {
        super(props, context)
        this.modelName = 'PublicStrategyModel'
        this.editorTitlte = '公共策略'
        this.searchInfo = this.modelName + '.searchInfo'
        this.objName = this.modelName + '.publicStrategy'
        this.syncLink = '/publicStrategy/sync'
        this.checkLink = '/publicStrategy/check'
        this.loadList = this.props.loadList
        this.addItem = this.props.addPublicStrategy
        this.deleteItem = this.props.deletePublicStrategy
        this.updateItem = this.props.updatePublicStrategy
        this.cleanExceptKeys = [{k: 'strategyLoadingType', v: 1}]
        this.initValueLink()
        this.state = {
            title: '添加' + this.editorTitlte,
            confirmLoading: false
        }
    }

    /** @Override **/
    addCallBack = () => {
        const states = this.getValueToJson(this.states)
        const item = this.getValueToJson(this.objName)
        this.cleanObjName()
        this.setValueToImmutable(this.objName + '.strategyLoadingType', states.editerType == 0 ? 1 : item.strategyLoadingType)
    }

    /** @Override **/
    loadListFiler = data => {
        DasUtil.transformStrategyStrToList(data.list, 'strategyParams')
    }

    onChange = e => {
        this.setValueByReducers(this.objName + '.strategySource', e)
    }

    createModel = () => {
        const {confirmLoading} = this.state
        const {setValueByReducers, publicstrategymodel} = this.props
        const _props = {setValueByReducers, publicstrategymodel}
        const item = this.getValueToJson(this.objName)
        const states = this.getValueToJson(this.states)
        return <Modal title={this.state.title}
                      mask={true}
                      width={1000}
                      maskClosable={false}
                      visible={states.editeVisible}
                      onOk={::this.handleOk}
                      confirmLoading={confirmLoading}
                      onCancel={::this.handleCancel}>
            <Inputlabel title='策略名'>
                <InputPlus {..._props}
                           placeholder={das_msg.ordinary_name} validRules={{maxLength: 50, isEnglishnderline: true}}
                           valueLink={this.objName + '.name'} defaultValue={item.name}
                           disabled={states.editerType == 1}/>
            </Inputlabel>
            <Inputlabel title='类名 (class name全名)'>
                <InputPlus {..._props}
                           valueLink={this.objName + '.className'} defaultValue={item.className}
                           placeholder={das_msg.class_name} validRules={{maxLength: 150}}/>
            </Inputlabel>
            <Inputlabel title='策略类型'>
                <RadioPlus {..._props}
                           items={strategyType} selectedId={item.strategyLoadingType}
                           valueLink={this.objName + '.strategyLoadingType'}/>
            </Inputlabel>
            <Inputlabel title='策略参数'>
                <ApiParamsTab {..._props}
                              valueLink={this.objName + '.apiParams'} apiParams={item.apiParams}/>
            </Inputlabel>
            <Inputlabel title='动态策略' display={item.strategyLoadingType == 2}>
                <CodeEditor {..._props}
                            contStyle={{width: '100%', height: '300px'}} mode='java' theme='monokai'
                            valueLink={this.objName + '.sql_content'}
                            value={item.strategySource}
                            onChangeCallback={::this.onChange}/>
            </Inputlabel>
        </Modal>
    }

    render() {
        const {setValueByReducers, publicstrategymodel} = this.props
        const states = this.getValueToJson(this.states)
        return (<div>
            {states.editeVisible ? ::this.createModel() : null}
            {states.checkVisible ? ::this.createCheckModel() : null}
            <TablePanle title='公共策略管理' navigation='数据访问平台 / 公共策略管理'
                        type={2}
                        lineTop={0}
                        modelName={this.modelName}
                        setValueByReducers={setValueByReducers}
                        publicstrategymodel={publicstrategymodel}
                        add={::this.add}
                        delete={::this.delete}
                        editor={::this.editor}
                        sync={::this.sync}
                        check={::this.check}
                        loadListFiler={::this.loadListFiler}
                        loadList={::this.loadList}/>
        </div>)
    }
}
