/**
 * Created by liang.wang on 18/9/14.
 */
import React from 'react'
import ManagePanle from '../../base/ManagePanle'
import TablePanle from '../../base/TablePanle'
import {View} from 'ea-react-dm-v14'
import {DatabaseSetEntrySyncControl} from '../../../../../controller/Index'
import Immutable from 'immutable'
import DataUtil from '../../../utils/util/DataUtil'

@View(DatabaseSetEntrySyncControl)
export default class DatabaseSetEntrySync extends ManagePanle {

    static defaultProps = {
        dblist: null,
        groupId: 0,
        dbset_id: 0
    }

    constructor(props, context) {
        super(props, context)
        this.modelName = 'DatabaseSetEntrySyncModel'
        this.editorTitlte = '逻辑数据库映射'
        this.searchInfo = this.modelName + '.searchInfo'
        this.objName = this.modelName + '.dbSetEntryInfo'
        this.loadList = this.props.loadDbSetEntryList
        this.addItem = this.props.addDbSetEntry
        this.syncLink = '/groupdbSetEntry/sync'
        this.checkLink = '/groupdbSetEntry/check'
        this.cleanExceptKeys = ['groupId', 'dbset_id', 'databaseType']
        this.initValueLink()
        this.state = {
            title: 'dbSet Entry',
            dbname: '',
            visible: false,
            checkVisible: false,
            suggestInitData: null,
            suggestDisabled: false,
            groupId: this.props.groupId,
            dbset_id: this.props.dbset_id,
            confirmLoading: false,
            currentCheckedId: 0
        }
    }

    componentWillReceiveProps(nextProps) {
        const {dbset_id, groupId, dblist} = nextProps
        if (this.state.dbset_id != dbset_id) {
            this.setState({dbset_id}, () => {
                let searchInfo = this.getValueByReducers(this.searchInfo).toJS()
                searchInfo.data.dbset_id = dbset_id
                this.loadList(searchInfo)
                this.setValueByReducers(this.searchInfo, Immutable.fromJS(searchInfo))
                this.setValueByReducers(this.objName + '.dbset_id', dbset_id)
            })
        }
        if (this.state.groupId != groupId) {
            this.setState({groupId: groupId, dblist: dblist})
            this.setValueByReducers(this.objName + '.groupId', groupId)
        }
    }

    /** @Override **/
    addValidate = () => {
        const {dbset_id, groupId} = this.state
        if (groupId > 0 && dbset_id > 0) {
            return {
                state: true
            }
        } else {
            return {
                state: false,
                msg: '请选组和dbset!!!'
            }
        }
    }

    /** @Override **/
    editorCallBack = item => {
        this.setState({dbname: item.dbname})
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
                _this.reload()
                _this.showErrorsNotification(rs.msg)
            }
        })
    }

    onChangeCallbBack = (id, value) => {
        this.replaceValuesToItem(this.objName, {'name': value, db_Id: id})
    }

    sync = item => {
        item = DataUtil.ObjUtils.toNnull(item, ['id', 'update_time', 'app_group_id', 'create_time', 'insert_time', 'dal_config_name', 'projectUsers', 'userRealName', 'dbsetIds', 'userIds', 'update_user_no'])
        this.addItem(item, this, (_this, rs) => {
            if (rs.code === 200) {
                this.reload()
                this.showSuccessMsg('同步成功')
            } else {
                this.showErrorsNotification(rs.msg)
            }
        })
    }

    render() {
        const {databasesetentrysyncmodel, setValueByReducers} = this.props
        return (<div>
            <TablePanle title='逻辑数据库映射同步' navigation=''
                        pageStyle={{padding: 0}}
                        type={2}
                        zDepth={0}
                        lineTop={0}
                        sync={::this.sync}
                        addButtonShow={false}
                        modelName={this.modelName}
                        setValueByReducers={setValueByReducers}
                        databasesetentrysyncmodel={databasesetentrysyncmodel}
                        loadList={::this.loadList}/>
        </div>)
    }
}
