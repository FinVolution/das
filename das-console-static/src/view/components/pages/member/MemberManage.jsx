/**
 * Created by liang.wang on 18/9/14.
 */
import React from 'react'
import ManagePanle from '../base/ManagePanle'
import TablePanle from '../base/TablePanle'
import {View} from 'ea-react-dm-v14'
import {MemberControl} from '../../../../controller/Index'
import TreePanle from '../base/TreePanle'
import {Modal} from 'antd'
import {DropDownSuggestion, Inputlabel, RadioPlus} from '../../utils/index'
import Immutable from 'immutable'
import {roleTypes} from '../../../../model/base/BaseModel'
import {DataUtil} from '../../utils/util/Index'

@View(MemberControl)
export default class MemberManage extends ManagePanle {

    constructor(props, context) {
        super(props, context)
        this.modelName = 'MemberModel'
        this.editorTitlte = ' 组员'
        this.searchInfo = this.modelName + '.searchInfo'
        this.objName = this.modelName + '.member'
        this.searchInfoSelectId = this.searchInfo + '.data.id'
        this.cleanExceptKeys = ['group_id', 'role']
        this.loadList = this.props.loadMemberList
        this.addItem = this.props.addMember
        this.deleteItem = this.props.deleteMember
        this.updateItem = this.props.updateMember
        this.props.loadTree()
        this.initValueLink()
        this.state = {
            suggestInitData: null,
            suggestDisabled: false
        }
    }

    /** @Override **/
    ittemCheck = item => {
        if (DataUtil.StringUtils.isEmpty(item.user_id)) {
            this.showErrorsNotification('请选择组员，然后添加到组！！')
            return false
        }
        return true
    }

    addValidate = () => {
        if (this.getValueByReducers(this.searchInfoSelectId) != null) {
            return {
                state: true
            }
        }
        return {
            state: false,
            msg: '请选择组!!!'
        }
    }

    editorCallBack = item => {
        this.setState({
            suggestInitData: item,
            suggestDisabled: true
        })
    }

    addCallBack = () => {
        this.setState({
            suggestInitData: {},
            suggestDisabled: false
        })
    }

    onSelect(selectedKeys, info) {
        const groupId = info.node.props.dataRef ? info.node.props.dataRef.id : null
        let searchInfo = this.getValueByReducers(this.searchInfo).toJS()
        let objName = this.getValueByReducers(this.objName).toJS()
        searchInfo.data['id'] = groupId
        objName['group_id'] = groupId
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
        this.replaceValueToItem(this.objName, 'user_id', ele.user_id)
    }

    cancelCallback = () => {
        this.replaceValueToItem(this.objName, 'user_id', null)
    }

    onSelectCallback = e => {
        this.setValueByReducers(this.objName + '.databaseType', e)
    }

    createModel = () => {
        const {confirmLoading, suggestInitData, suggestDisabled} = this.state
        const {setValueByReducers, membermodel} = this.props
        const item = this.getValueToJson(this.objName)
        const states = this.getValueToJson(this.states)
        return <Modal title={this.state.title}
                      visible={states.editeVisible}
                      width={800}
                      onOk={::this.handleOk}
                      confirmLoading={confirmLoading}
                      onCancel={::this.handleCancel}>
            <Inputlabel title='组员名称'>
                <DropDownSuggestion url={'/member/users'} {...this.props}
                                    keyword='name'
                                    style={{divWidth: '100%', inputWidth: '94%', showWidth: '100%'}}
                                    format={{
                                        leng: 400,
                                        title: {
                                            'userRealName': '  -  工号 : ',
                                            'userNo': ' -  域账号 : ',
                                            'userName': '  -  邮箱 : ',
                                            'userEmail': ''
                                        }
                                    }}
                                    valueLink={this.objName + '.suggestionMember'}
                                    defaultVal={null}
                                    disabled={suggestDisabled}
                                    initData={suggestInitData ? suggestInitData : null}
                                    initDataCallback={::this.selectedCallback}
                                    selectedCallback={::this.selectedCallback}
                                    cancelCallback={::this.cancelCallback}
                                    placeholder="请输入成员名称"/>
            </Inputlabel>
            <Inputlabel title='组员权限'>
                <RadioPlus setValueByReducers={setValueByReducers} membermodel={membermodel}
                           items={roleTypes} valueLink={this.objName + '.role'} selectedId={item.role}/>
            </Inputlabel>
        </Modal>
    }

    getDefaultSelectedCallBack = id => {
        this.setValueByReducers(this.objName + '.group_id', id)
    }

    render() {
        const states = this.getValueToJson(this.states)
        return (<div>
            {states.editeVisible ? ::this.createModel() : null}
            <TablePanle title='组员管理' navigation='数据访问平台 / 组员管理'
                        type={4}
                        lineTop={0}
                        modelName={this.modelName}
                        membermodel={this.props.membermodel}
                        add={::this.add}
                        delete={::this.delete}
                        editor={::this.editor}
                        setValueByReducers={this.props.setValueByReducers}
                        loadList={::this.loadList}
                        isloadList={false}
                        tree={<TreePanle treeUrl='group/member/tree'
                                         rootShow={false} showLine={true}
                                         format={{
                                             tree: {
                                                 title: 'group_name',
                                                 key: 'id',
                                                 tooltip: 'group_comment',
                                                 isLeaf: true
                                             }
                                         }}
                                         onSelect={::this.onSelect}
                                         getDefaultSelected={::this.getDefaultSelected}/>}
            />
        </div>)
    }
}
