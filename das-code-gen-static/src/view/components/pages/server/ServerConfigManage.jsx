/**
 * Created by liang.wang on 18/9/14.
 */
import React from 'react'
import ManagePanle from '../base/ManagePanle'
import TablePanle from '../base/TablePanle'
import {View} from 'ea-react-dm-v14'
import {ServerConfigControl} from '../../../../controller/Index'
import {Modal} from 'antd'
import {Inputlabel, InputPlus} from '../../utils/index'
import Immutable from 'immutable'
import {DataUtil} from '../../utils/util/Index'

@View(ServerConfigControl)
export default class ServerConfigManage extends ManagePanle {

    static defaultProps = {
        dblist: null,
        groupId: 0,
        fatherCheckedId: 0 //serverGroupId
    }

    constructor(props, context) {
        super(props, context)
        this.modelName = 'ServerConfigModel'
        this.editorTitlte = ' Server Config'
        this.searchInfo = this.modelName + '.searchInfo'
        this.objName = this.modelName + '.serverConfig'
        this.loadList = this.props.loadList
        this.addItem = this.props.addServerConfig
        this.deleteItem = this.props.deleteServerConfig
        this.updateItem = this.props.updateServerConfig
        this.cleanExceptKeys = ['serverId']
        this.initValueLink()
        this.state = {
            title: 'Server Config',
            dbname: '',
            item_id: this.props.item_id,
            fatherCheckedId: 0
        }
    }

    componentWillReceiveProps(nextProps) {
        const {fatherCheckedId} = nextProps
        if (this.state.fatherCheckedId != fatherCheckedId) {
            this.setState({fatherCheckedId}, () => {
                let searchInfo = this.getValueByReducers(this.searchInfo).toJS()
                searchInfo.data.serverId = fatherCheckedId
                this.loadList(searchInfo)
                this.setValueByReducers(this.searchInfo, Immutable.fromJS(searchInfo))
                this.setValueByReducers(this.objName + '.serverId', fatherCheckedId)
            })
        }
    }

    cleanObjName = () => {
        let item = this.getValueByReducers(this.objName).toJS()
        this.setValueByReducers(this.objName, Immutable.fromJS(DataUtil.ObjUtils.cleanJson(item, this.cleanExceptKeys)))
    }

    /** @Override **/
    addValidate = () => {
        const {fatherCheckedId} = this.state
        if (fatherCheckedId > 0) {
            return {
                state: true
            }
        } else {
            return {
                state: false,
                msg: '请选Server Group!!!'
            }
        }
    }

    onSelect = (selectedKeys, info) => {
        const groupId = info.node.props.dataRef ? info.node.props.dataRef.id : null
        let searchInfo = this.getValueByReducers(this.searchInfo).toJS()
        let objName = this.getValueByReducers(this.objName).toJS()
        searchInfo.data['groupId'] = groupId
        objName['groupId'] = groupId
        this.loadList(searchInfo, this, (_this, rs) => {
            if (rs.code === 200) {
                _this.setValueByReducers(_this.searchInfo, Immutable.fromJS(searchInfo))
                _this.setValueByReducers(_this.objName, Immutable.fromJS(objName))
            } else {
                _this.showErrorsNotification(rs.msg)
            }
        })
    }

    createModel = () => {
        const {confirmLoading} = this.state
        const {setValueByReducers, serverconfigmodel} = this.props
        const _props = {setValueByReducers, serverconfigmodel}
        const states = this.getValueToJson(this.states)
        return <Modal title={this.state.title}
                      width={800}
                      visible={states.editeVisible}
                      onOk={::this.handleOk}
                      confirmLoading={confirmLoading}
                      onCancel={::this.handleCancel}>
            <Inputlabel title='key'>
                <InputPlus {..._props}
                           valueLink={this.objName + '.keya'}
                           validRules={{maxLength: 35}}/>
            </Inputlabel>
            <Inputlabel title='value'>
                <InputPlus {..._props}
                           valueLink={this.objName + '.value'}
                           validRules={{maxLength: 10}}/>
            </Inputlabel>
            <Inputlabel title='备注'>
                <InputPlus {..._props}
                           valueLink={this.objName + '.comment'}/>
            </Inputlabel>
        </Modal>
    }

    render() {
        const {serverconfigmodel, setValueByReducers} = this.props
        const states = this.getValueToJson(this.states)
        return (<div>
            {states.editeVisible ? ::this.createModel() : null}
            <TablePanle title='Server Config' navigation=''
                        pageStyle={{padding: 0}}
                        lineTop={0}
                        type={2}
                        zDepth={0}
                        modelName={this.modelName}
                        add={::this.add}
                        delete={::this.delete}
                        editor={::this.editor}
                        isSearchAble={false}
                        serverconfigmodel={serverconfigmodel}
                        setValueByReducers={setValueByReducers}
                        loadList={::this.loadList}/>
        </div>)
    }
}