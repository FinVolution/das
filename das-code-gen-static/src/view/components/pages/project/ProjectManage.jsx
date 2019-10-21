/**
 * Created by liang.wang on 18/9/14.
 */
import React from 'react'
import ManagePanle from '../base/ManagePanle'
import TablePanle from '../base/TablePanle'
import {View} from 'ea-react-dm-v14'
import {PojectControl} from '../../../../controller/Index'
import TreePanle from '../base/TreePanle'
import {Button, Modal, Popconfirm, Tooltip, Progress, Alert, Icon} from 'antd'
import {InputPlus, Inputlabel, SelectPlus, DatePickerPuls, CodeEditor} from '../../utils/index'
import Immutable from 'immutable'
import {das_msg, display} from '../../../../model/base/BaseModel'
import {FrwkUtil, DataUtil, UserEnv} from '../../utils/util/Index'
import CopyToClipboard from 'react-copy-to-clipboard'
import './ProjectManage.less'

@View(PojectControl)
export default class ProjectManage extends ManagePanle {

    constructor(props, context) {
        super(props, context)
        this.modelName = 'ProjectModel'
        this.editorTitlte = '项目'
        this.searchInfo = this.modelName + '.searchInfo'
        this.columnInfo = this.modelName + '.columnInfo'
        this.objName = this.modelName + '.projectinfo'
        this.searchInfoSelectId = this.searchInfo + '.data.dal_group_id'
        this.syncLink = '/project/sync'
        this.checkLink = '/project/check'
        this.addButtonBiden = [0]
        this.cleanExceptKeys = ['namespace', 'dal_group_id', {k: 'app_id', v: ''}]
        this.loadList = this.props.loadList
        this.addItem = this.props.addProject
        this.deleteItem = this.props.deleteProject
        this.updateItem = this.props.updateProject
        this.props.loadTree()
        this.initValueLink()
        this.dispalyManage.initButtons('/project/buttons')
        this.state = {
            percent: 1,
            footer: null,
            showProgress: true,
            title: '添加' + this.editorTitlte,
            confirmLoading: false,
            currentCheckedId: 0,
            dbname: '',
            suggestInitData: null,
            suggestDisabled: false
        }
    }

    /** @Override **/
    addValidate = () => {
        if (this.getValueByReducers(this.objName + '.dal_group_id')) {
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
    editorCallBack = item => {
        this.setState({dbname: item.dbname})
    }

    /** @Override **/
    getDefaultSelectedCallBack = groupId => {
        this.setValueByReducers(this.objName + '.dal_group_id', groupId)
        this.loadDbSetList(groupId)
        this.loadGroupUsrs(groupId)
    }

    loadDbSetList = groupId => {
        FrwkUtil.load.getList('/groupdbset/' + groupId + '/list', null, this, 'dbSetlist')
    }

    loadGroupUsrs = groupId => {
        FrwkUtil.load.getList('/user/group/users', {groupId}, this, 'userlist')
    }

    bidenButton = () => {
        const columnInfo = this.getValueToJson(this.columnInfo)
        const column = columnInfo.column
        column[column.length - 1].visible = false
        this.setValueToImmutable(this.columnInfo, columnInfo)
    }

    showBoton = () => {
        const columnInfo = this.getValueToJson(this.columnInfo)
        const column = columnInfo.column
        column[column.length - 1].visible = true
        this.setValueToImmutable(this.columnInfo, columnInfo)
    }

    onSelectBefore = groupId => {
        groupId = parseInt(groupId)
        if (this.addButtonBiden.includes(groupId)) {
            this.bidenButton()
            this.setValueByReducers(this.states + '.addButtonShow', false)
        } else {
            this.showBoton()
            this.setValueByReducers(this.states + '.addButtonShow', true)
        }
        this.loadDbSetList(groupId)
        this.loadGroupUsrs(groupId)
    }

    onSelect = (selectedKeys, info, appid) => {
        if (!info) {
            this.setValueToImmutable(this.modelName + '.list', [])
            let searchInfo = this.getValueToJson(this.searchInfo)
            if (!DataUtil.StringUtils.isEmpty(searchInfo.data.app_id)) {
                searchInfo.data['app_id'] = null
                this.setValueToImmutable(this.searchInfo, searchInfo)
            }
            window.setTimeout(() => {
                this.setValueToImmutable(this.modelName + '.list', [])
            }, 1000)
            return
        }
        const groupId = info.node.props.dataRef && info.node.props.dataRef ? info.node.props.dataRef.id : selectedKeys[0]
        this.onSelectBefore(groupId)
        let objName = this.getValueByReducers(this.objName).toJS()
        let searchInfo = this.getValueByReducers(this.searchInfo).toJS()
        searchInfo.data['app_id'] = appid
        searchInfo.data['dal_group_id'] = groupId
        objName['dal_group_id'] = groupId
        this.loadList(searchInfo, this, (_this, rs) => {
            if (rs.code === 200) {
                _this.setValueByReducers(_this.searchInfo, Immutable.fromJS(searchInfo))
                _this.setValueByReducers(_this.objName, Immutable.fromJS(objName))
            } else {
                _this.showErrorsNotification(rs.msg)
            }
        })
    }

    selectedCallback = ele => {
        this.replaceValueToItem(this.objName, 'id', ele.id)
    }

    cancelCallback = () => {
        this.replaceValueToItem(this.objName, 'id', null)
    }

    createModel = () => {
        const {confirmLoading, dbSetlist, userlist} = this.state
        const {setValueByReducers, projectmodel} = this.props
        const item = this.getValueToJson(this.objName)
        const states = this.getValueToJson(this.states)
        const _props = {setValueByReducers, projectmodel}
        return <Modal title={this.state.title}
                      width={900}
                      visible={states.editeVisible}
                      onOk={::this.handleOk}
                      confirmLoading={confirmLoading}
                      maskClosable={false}
                      onCancel={::this.handleCancel}>
            <Inputlabel title='项目名称'>
                <InputPlus {..._props}
                           placeholder={das_msg.project_name} star={true}
                           validRules={{maxLength: 32, isProjectName: true}}
                           value={String(item.name)} valueLink={this.objName + '.name'}/>
            </Inputlabel>
            <Inputlabel title='APP ID' star={true}>
                <InputPlus {..._props}
                           value={String(item.app_id)}
                           valueLink={this.objName + '.app_id'}
                           validRules={{maxLength: 50}}/>
            </Inputlabel>
            <Inputlabel title='逻辑数据库'>
                <SelectPlus {..._props}
                            selectedIds={item.dbsetIds} items={dbSetlist} mode='multiple' isSearch={true}
                            valueLink={this.objName + '.items'}/>
            </Inputlabel>
            <Inputlabel title='预计上线时间'>
                <DatePickerPuls {..._props}
                                value={item.pre_release_time} valueLink={this.objName + '.pre_release_time'}/>
            </Inputlabel>
            <Inputlabel title='首次上线时间'>
                <DatePickerPuls {..._props}
                                value={item.first_release_time} valueLink={this.objName + '.first_release_time'}/>
            </Inputlabel>
            <Inputlabel title='项目负责人'>
                <SelectPlus {..._props} mode='multiple'
                            selectedIds={item.userIds} items={userlist} format={{id: 'id', name: 'userRealName'}}
                            valueLink={this.objName + '.users'}/>
            </Inputlabel>
            <Inputlabel title='应用场景'>
                <InputPlus {..._props} projectmodel={projectmodel}
                           value={String(item.app_scene)} valueLink={this.objName + '.app_scene'}/>
            </Inputlabel>
            <Inputlabel title='备注(项目中文名，等其他描述)'>
                <InputPlus {..._props}
                           value={item.comment}
                           valueLink={this.objName + '.comment'}/>
            </Inputlabel>
        </Modal>
    }

    encryption = row => {
        row = row.toJS()
        this.setState({progressVisible: true}, () => {
            window.progressTimer = window.setInterval(() => {
                const percent = this.state.percent + 1
                if (percent < 93) {
                    this.setState({percent})
                }
            }, 30)

            FrwkUtil.fetch.fetchPost('/saec/enconn', {
                    appId: row.app_id
                }, this, data => {
                    window.cleanTimer = window.setInterval(() => {
                        const percent = this.state.percent + 1
                        if (percent <= 100) {
                            this.setState({percent})
                        }
                        if (this.state.percent >= 100) {
                            this.setState({percent: 0, tokenData: data, showProgress: false})
                            window.clearInterval(window.progressTimer)
                            window.clearInterval(window.cleanTimer)
                        }
                    }, 50)
                }
            )
        })
    }

    getInstanceDetail = row => {
        this.setState({dbinfoVisible: true}, () => {
            FrwkUtil.fetch.fetchPost('/saec/getInstanceDetail', {appId: row.toJS().app_id}, this, data => {
                if (data.code === 200) {
                    this.setState({dbinfo: data.msg})
                }
            })
        })
    }

    download = row => {
        window.location.href = '/project/download?projectId=' + row.toJS().id
    }

    createToken = () => {
        const {tokenData} = this.state
        if (tokenData.code === 200) {
            return <div className='projectManage'>
                <Alert message={'获取Token成功，并同步到' + UserEnv.getConfigCenterName() + '，可点击确定复制Token'} type='success'
                       showIcon
                       description={tokenData.msg}/>
                <Tooltip placement='top' title='点击复制'>
                    <CopyToClipboard className='copyToClipboard' text={this.state.tokenData.msg}>
                        <Button style={{top: '-55px', left: '490px'}} type='primary' size='large'>
                            <Icon type='copy'/>
                        </Button>
                    </CopyToClipboard>
                </Tooltip>
            </div>
        } else {
            return <Alert message='获取Token失败' type='error' showIcon description={tokenData.msg}/>
        }
    }

    progressCancel = () => {
        this.setState({progressVisible: false, showProgress: true})
    }

    progressModel = () => {
        const {percent, showProgress} = this.state
        const {confirmLoading, progressVisible} = this.state
        return <Modal title='加密连接串'
                      width={900}
                      visible={progressVisible}
                      confirmLoading={confirmLoading}
                      onOk={::this.handleOk}
                      onCancel={::this.progressCancel}
                      footer={null}>
            {
                showProgress ? <Progress percent={percent}/> : this.createToken()
            }
        </Modal>
    }

    dbinfoCancel = () => {
        this.setState({dbinfoVisible: false})
    }

    dbinfoModel = () => {
        const {dbinfoVisible, dbinfo} = this.state
        return <Modal title='连接串信息'
                      width={1200}
                      visible={dbinfoVisible}
                      onOk={::this.handleOk}
                      onCancel={::this.dbinfoCancel}
                      footer={null}>
            <CodeEditor style={{width: '97%', height: '600px'}}
                        contStyle={{width: '100%', height: '600px'}}
                        theme='monokai' mode='json'
                        value={dbinfo}/>
        </Modal>
    }

    customButton = () => {
        const displaybuttons = this.getValueToJson(this.modelName + display.buttons_path)
        const buttonSysCas = (row) => {
            return <span style={{paddingRight: '8px'}}>
             <Popconfirm placement='top' title='安全加密并获取TOKEN?' okText='是' cancelText='否'
                         onConfirm={() => ::this.encryption(row)}>
                            <Tooltip placement='top' title='安全同步'>
                                <Button icon='key' size='small'/>
                            </Tooltip>
                 </Popconfirm>
                            </span>
        }

        const buttonDbInfo = (row) => {
            return <span style={{paddingRight: '8px'}}>
                <Tooltip placement='top' title='查看安全信息'>
                    <Button icon='book' size='small' onClick={() => ::this.getInstanceDetail(row)}/>
                </Tooltip>
            </span>
        }

        const buttonDownload = (row) => {
            return <span style={{paddingRight: '8px'}}>
                <Tooltip placement='top' title='下载配置'>
                    <Button icon='download' size='small' onClick={() => ::this.download(row)}/>
                </Tooltip>
            </span>
        }
        const customButtons = [
            {
                type: 'saec',
                button: buttonSysCas
            },
            {
                type: 'detail',
                button: buttonDbInfo
            },
            {
                type: 'download',
                button: buttonDownload
            }
        ]
        return {
            displaybuttons: displaybuttons,
            customButtons: customButtons
        }
    }

    render() {
        const {progressVisible, dbinfoVisible} = this.state
        const {projectmodel, setValueByReducers} = this.props
        const states = this.getValueToJson(this.states)
        return (<div>
            {states.editeVisible ? ::this.createModel() : null}
            {states.checkVisible ? ::this.createCheckModel() : null}
            {progressVisible ? ::this.progressModel() : null}
            {dbinfoVisible ? ::this.dbinfoModel() : null}
            <TablePanle title='项目管理' navigation='数据访问平台 / 项目管理'
                        type={4}
                        lineTop={0}
                        add={::this.add}
                        sync={::this.sync}
                        check={::this.check}
                        delete={::this.delete}
                        editor={::this.editor}
                        projectmodel={projectmodel}
                        setValueByReducers={setValueByReducers}
                        modelName={this.modelName}
                        loadList={::this.loadList}
                        addButtonShow={states.addButtonShow}
                        cleanExceptSearchKeys={this.cleanExceptKeys}
                        customButton={::this.customButton}
                        tree={<TreePanle
                            format={{tree: {title: 'group_name', key: 'id', tooltip: 'group_comment', isLeaf: true}}}
                            showLine={true}
                            onSelect={::this.onSelect}
                            getDefaultSelected={::this.getDefaultSelected}/>}
            />
        </div>)
    }
}
