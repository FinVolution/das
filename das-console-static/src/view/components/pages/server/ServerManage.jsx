/**
 * Created by liang.wang on 18/9/14.
 */
import React from 'react'
import ManagePanle from '../base/ManagePanle'
import TablePanle from '../base/TablePanle'
import {View} from 'ea-react-dm-v14'
import {ServerControl} from '../../../../controller/Index'
import {Modal} from 'antd'
import {Inputlabel, InputPlus, TextArea} from '../../utils/index'
import Immutable from 'immutable'
import ServerConfigManage from './ServerConfigManage'

@View(ServerControl)
export default class ServerManage extends ManagePanle {

    static defaultProps = {
        dblist: null,
        groupId: 0,
        fatherCheckedId: 0 //fatherCheckedId
    }

    constructor(props, context) {
        super(props, context)
        this.modelName = 'ServerModel'
        this.editorTitlte = ' Server'
        this.searchInfo = this.modelName + '.searchInfo'
        this.objName = this.modelName + '.server'
        this.syncLink = '/server/sync'
        this.checkLink = '/server/check'
        this.loadList = props.loadList
        this.addItem = props.addServer
        this.deleteItem = props.deleteServer
        this.updateItem = props.updateServer
        this.cleanExceptKeys = ['serverGroupId']
        this.initValueLink()
        this.state = {
            title: 'Server',
            dbname: '',
            currentCheckedId: 0,
            fatherCheckedId: 0
        }
    }

    componentWillReceiveProps(nextProps) {
        const {fatherCheckedId} = nextProps
        if (this.state.fatherCheckedId != fatherCheckedId) {
            this.setState({fatherCheckedId}, () => {
                let searchInfo = this.getValueByReducers(this.searchInfo).toJS()
                searchInfo.data.serverGroupId = fatherCheckedId
                this.setValueByReducers(this.searchInfo, Immutable.fromJS(searchInfo))
                this.setValueByReducers(this.objName + '.serverGroupId', fatherCheckedId)
                this.loadList(searchInfo, null, null, this.loadListFiler)
            })
        }
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

    createModel = () => {
        const {confirmLoading} = this.state
        const {setValueByReducers, servermodel} = this.props
        const _props = {setValueByReducers, servermodel}
        const states = this.getValueToJson(this.states)
        const item = this.getValueToJson(this.objName)
        return <Modal title={this.state.title}
                      width={800}
                      visible={states.editeVisible}
                      onOk={::this.handleOk}
                      confirmLoading={confirmLoading}
                      onCancel={::this.handleCancel}>
            <Inputlabel title='IP'>
                <InputPlus {..._props}
                           defaultValue={item.ip} valueLink={this.objName + '.ip'} validRules={{maxLength: 35}}/>
            </Inputlabel>
            <Inputlabel title='端口号'>
                <InputPlus {..._props}
                           valueLink={this.objName + '.port'} defaultValue={item.port}
                           validRules={{maxLength: 10, isInt: true}}/>
            </Inputlabel>
            <Inputlabel title='备注'>
                <TextArea {..._props}
                          valueLink={this.objName + '.comment'} defaultValue={item.comment}/>
            </Inputlabel>
        </Modal>
    }

    render() {
        const {servermodel, setValueByReducers} = this.props
        const {currentCheckedId} = this.state
        const states = this.getValueToJson(this.states)
        return (<div>
            {states.editeVisible ? ::this.createModel() : null}
            {states.checkVisible ? ::this.createCheckModel() : null}
            <TablePanle title='Server' navigation=''
                        pageStyle={{padding: 0}}
                        lineTop={0}
                        type={3}
                        zDepth={0}
                        modelName={this.modelName}
                        add={::this.add}
                        delete={::this.delete}
                        editor={::this.editor}
                        sync={::this.sync}
                        check={::this.check}
                        isSearchAble={false}
                        servermodel={servermodel}
                        setValueByReducers={setValueByReducers}
                        onChangeCheckbox={::this.onChangeCheckbox}
                        loadListFiler={this.loadListFiler}
                        loadList={::this.loadList}>
                {<ServerConfigManage fatherCheckedId={currentCheckedId}/>}
            </TablePanle>
        </div>)
    }
}