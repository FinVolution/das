/**
 * Created by liang.wang on 18/9/14.
 */
import React from 'react'
import ManagePanle from '../base/ManagePanle'
import TablePanle from '../base/TablePanle'
import {View} from 'ea-react-dm-v14'
import {DataBaseControl} from '../../../../controller/Index'
import AddDatabaseList from './AddDatabaseList'
import './DatabaseManage.less'
import {Button, Modal, Tooltip} from 'antd'
import EditorDBInfo from './EditorDBInfo'
import DataBaseInfo from './DataBaseInfo'
import FrwkUtil from '../../utils/util/FrwkUtil'
import Immutable from 'immutable'
import {display} from '../../../../model/base/BaseModel'

@View(DataBaseControl)
export default class DatabaseManage extends ManagePanle {

    constructor(props, context) {
        super(props, context)
        this.modelName = 'DatabaseModel'
        this.editorTitlte = ' Team'
        this.searchInfo = this.modelName + '.searchInfo'
        this.objName = this.modelName + '.item'
        this.tablink = this.modelName + '.dalGroupDBList'
        this.loadList = this.props.loadList
        this.syncLink = '/db/sync'
        this.checkLink = '/db/check'
        this.updateItem = this.props.updateDatabase
        this.deleteItem = this.props.deleteDatabase
        this.initValueLink()
        this.dispalyManage.initButtons('/db/buttons')
        this.state = {
            confirmLoading: false,
            currentCheckedId: 0,
            editerVisible: false,
            submitDisabled: false,
            submitLoading: false,
            submitTitle: '点击提交',
            title: '编辑',
            suggestionItem: {}
        }
        this.props.loadGroupTree()
    }

    visibleCallback = visible => {
        this.setValueByReducers(this.states + '.editeVisible', visible)
    }

    updateDataBase = () => {
        const item = this.getValueToJson(this.objName)
        FrwkUtil.fetch.fetchPost('/setupDb/connectionTest', item, this, data => {
            if (data.code === 200) {
                this.showSuccessMsg('链接成功！！')
                setTimeout(() => {
                    ::this.handleOk()
                }, 1000)
            } else {
                this.showErrorsNotification(data.msg)
            }
        }, {timeout: 2000}, () => {
            this.showErrorsMsg('链接超时或错误，请修改参数重试！！')
        })
    }

    addCancel = () => {
        this.handleCancel()
        this.setValueByReducers(this.tablink, Immutable.fromJS([]))
    }

    createModel = () => {
        const {databasemodel, setValueByReducers, connectionTest, connectionTestNew, addDbs} = this.props
        const _props = {setValueByReducers, databasemodel}
        const {confirmLoading} = this.state
        const states = this.getValueToJson(this.states)
        if (states.editerType === 0) {
            return <Modal title='新建数据库'
                          mask={true}
                          width={1300}
                          maskClosable={false}
                          visible={states.editeVisible}
                          onOk={::this.handleOk}
                          confirmLoading={confirmLoading}
                          onCancel={::this.addCancel}
                          footer={null}>
                <AddDatabaseList {..._props}
                                 addDbs={addDbs}
                                 reload={this.reload}
                                 connectionTest={connectionTest}
                                 connectionTestNew={connectionTestNew}
                                 visibleCallback={::this.visibleCallback}/>
            </Modal>
        } else if (states.editerType === 1) {
            return <Modal title='更新数据库'
                          width={1000}
                          visible={states.editeVisible}
                          onOk={::this.updateDataBase}
                          confirmLoading={confirmLoading}
                          onCancel={::this.handleCancel}>
                <EditorDBInfo {..._props}
                              item='item' editerType={states.editerType}
                              onCheckSetState={(nextBtndisable) => {
                                  this.setState({nextBtndisable})
                              }}/>
            </Modal>
        }
    }

    openDbInfoModel = item => {
        item = item.toJS()
        this.replaceValuesToItem(this.objName, this.editorFiler(item))
        this.setState({dbInfoVisible: true})
    }

    dbInFoCancel = () => {
        this.setState({
            dbInfoVisible: false
        })
    }

    createDbInfoModel = () => {
        const {databasemodel, setValueByReducers, connectionTest, addDbs} = this.props
        const {confirmLoading, dbInfoVisible} = this.state
        return <Modal title='查看数据结构'
                      mask={true}
                      width={1000}
                      maskClosable={false}
                      visible={dbInfoVisible}
                      onOk={::this.handleOk}
                      confirmLoading={confirmLoading}
                      onCancel={::this.dbInFoCancel}
                      footer={null}>
            <DataBaseInfo addDbs={addDbs}
                          reload={this.reload}
                          databasemodel={databasemodel}
                          connectionTest={connectionTest}
                          setValueByReducers={setValueByReducers}
                          visibleCallback={::this.visibleCallback}/>
        </Modal>
    }

    customButton = () => {
        const displaybuttons = this.getValueToJson(this.modelName + display.buttons_path)
        const buttonDbInfo = row => {
            return <span style={{paddingRight: '8px'}}>
                <Tooltip placement='top' title='查看数据结构'>
                    <Button icon='book' size='small' onClick={() => ::this.openDbInfoModel(row)}/>
                </Tooltip>
            </span>
        }

        const customButtons = [
            {
                type: 'catalogs',
                button: buttonDbInfo
            }
        ]

        return {
            displaybuttons: displaybuttons,
            customButtons: customButtons
        }
    }

    render() {
        const {dbInfoVisible} = this.state
        const {databasemodel, setValueByReducers} = this.props
        const states = this.getValueToJson(this.states)
        return (<div>
            {dbInfoVisible ? ::this.createDbInfoModel() : null}
            {states.checkVisible ? ::this.createCheckModel() : null}
            {states.editeVisible ? ::this.createModel() : null}
            <TablePanle title='物理库管理' navigation='数据访问平台 / 物理库管理'
                        type={2}
                        lineTop={0}
                        modelName={this.modelName}
                        databasemodel={databasemodel}
                        add={::this.add}
                        delete={::this.delete}
                        editor={::this.editor}
                        sync={::this.sync}
                        check={::this.check}
                        customButton={::this.customButton}
                        setValueByReducers={setValueByReducers}
                        loadList={::this.loadList}/>
        </div>)
    }
}
