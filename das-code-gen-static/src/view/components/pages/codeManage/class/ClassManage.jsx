/**
 * Created by liang.wang on 18/9/14.
 */
import React from 'react'
import ManagePanle from '../../base/ManagePanle'
import TableEntityManage from './TableEntityManage'
import SelectEntityManage from './SelectEntityManage'
import DataUtil from '../../../utils/util/DataUtil'

export default class ClassManage extends ManagePanle {

    static defaultProps = {
        type: 1,
        groupId: 0,
        projectId: 0,
        dbSetlist: []
    }

    constructor(props, context) {
        super(props, context)
        this.state = {
            dbSetlist: this.props.dbSetlist,
            groupId: this.props.groupId,
            projectId: this.props.projectId
        }
    }

    componentWillReceiveProps(nextProps) {
        const {groupId, projectId, dbSetlist} = nextProps
        if (groupId === this.state.groupId) {
            if (projectId != this.state.projectId && DataUtil.ObjUtils.isEqual(dbSetlist, this.state.dbSetlist)) {
                this.setState({projectId})
            } else if (projectId != this.state.projectId && !DataUtil.ObjUtils.isEqual(dbSetlist, this.state.dbSetlist)) {
                this.setState({projectId, dbSetlist})
            }
        } else if (groupId != this.state.groupId) {
            if (projectId === this.state.projectId && DataUtil.ObjUtils.isEqual(dbSetlist, this.state.dbSetlist)) {
                this.setState({groupId})
            } else if (projectId != this.state.projectId && DataUtil.ObjUtils.isEqual(dbSetlist, this.state.dbSetlist)) {
                this.setState({groupId, projectId})
            } else if (projectId != this.state.projectId && !DataUtil.ObjUtils.isEqual(dbSetlist, this.state.dbSetlist)) {
                this.setState({groupId, projectId, dbSetlist})
            }
        }
    }

    render() {
        const {groupId, projectId, dbSetlist} = this.state
        const _props = {dbSetlist, groupId, projectId}
        if (this.props.type === 1) {
            return <TableEntityManage {..._props}/>
        }
        return <SelectEntityManage {..._props}/>
    }
}
