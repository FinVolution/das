/**
 * Created by liang.wang on 19/6/14.
 */
import React from 'react'
import ManagePanle from '../base/ManagePanle'
import {View} from 'ea-react-dm-v14'
import {DataSearchControl} from '../../../../controller/Index'
import {Button, Col, Row, Form, Alert} from 'antd'
import QueueAnim from 'rc-queue-anim'
import {SplitEditor, RsuiteTable, SelectPlus} from '../../utils'
import {FrwkUtil, DataUtil} from '../../utils/util/Index'
import _ from 'underscore'

@View(DataSearchControl)
export default class DataSearchManage extends ManagePanle {

    constructor(props, context) {
        super(props, context)
        this.modelName = 'DataSearchModel'
        this.objName = this.modelName + '.dataSearch'
        this.updateItem = props.updateAppGroup
        this.appGroupId = 0
        this.state = {
            limit: 100,
            loading: false,
            dbsetName: '',
            logList: [],
            dataList: [],
            dbSetlist: [],
            dbSetEntrylist: []
        }
        this.loadDbSetList()
    }

    loadDbSetList = () => {
        FrwkUtil.fetch.fetchGet('/groupdbset/list', {projectId: null}, this, (data, _this) => {
                if (data.code === 200) {
                    if (!DataUtil.ObjUtils.isEqual(_this.state.dbSetlist, data.msg)) {
                        _this.setState({dbSetlist: data.msg})
                    }
                } else if (data.code === 500) {
                    _this.showErrorsNotification(data.msg)
                }
            }
        )
    }

    onChangeDbSetCallbBack = (id, dbsetName) => {
        this.dbSetId = id
        this.dbsetName = dbsetName
        FrwkUtil.fetch.fetchPost('/groupdbSetEntry/list', {
                page: 1,
                pageSize: 1000,
                sort: 'name',
                data: {dbset_id: id}
            }, this, (data, _this) => {
                if (data.code === 200 && data.msg.list) {
                    if (!DataUtil.ObjUtils.isEqual(_this.state.dbSetEntrylist, data.msg.list)) {
                        _this.setState({dbSetEntrylist: data.msg.list})
                    }
                } else if (data.code === 500) {
                    _this.showErrorsNotification(data.msg)
                }
            }
        )
    }

    searchData = () => {
        this.setState({loading: true}, () => {
            const obj = this.getValueToJson(this.objName)
            FrwkUtil.fetch.fetchPost('/dataSearch/select', {
                    dbSetId: this.dbSetId,
                    dbsetName: this.dbsetName,
                    dbSetEntryIds: DataUtil.ObjUtils.filter(obj.dbSetEntryList, 'id'),
                    sql: this.sql,
                    limit: this.state.limit
                }, this, (data, _this) => {
                    _this.setState({loading: false})
                    if (data.code === 200 && data.msg) {
                        if (!DataUtil.ObjUtils.isEqual(_this.state.dataList, data.msg)) {
                            _this.setState({dataList: data.msg})
                        }
                    } else if (data.code === 500) {
                        this.setState({dataList: []}, () => {
                            if (DataUtil.is.Object(data.msg)) {
                                _this.showErrorsNotification(JSON.stringify(DataUtil.ObjUtils.filterObjByKeys(data.msg, ['exception'])))
                            } else {
                                _this.showErrorsNotification(data.msg)
                            }
                        })
                    }
                }
            )
        })
    }

    searchLog = () => {
        this.setState({loading: true}, () => {
            FrwkUtil.fetch.fetchPost('/dataSearch/searchlog', {limit: this.state.limit}, this, (data, _this) => {
                    _this.setState({loading: false})
                    if (data.code === 200 && data.msg) {
                        if (!DataUtil.ObjUtils.isEqual(_this.state.dataList, data.msg.result)) {
                            _this.setState({logList: data.msg.result})
                        }
                    } else if (data.code === 500) {
                        _this.showErrorsNotification(JSON.stringify(DataUtil.ObjUtils.filterObjByKeys(data.msg, ['appId', 'request', 'shard', 'exception'])))
                    }
                }
            )
        })
    }

    download = () => {
        const obj = this.getValueToJson(this.objName)
        const dbSetEntryIds = DataUtil.ObjUtils.filter(obj.dbSetEntryList, 'id')
        window.location.href = '/dataSearch/download/' + this.dbsetName + '/' + this.dbSetId + '?sql=' + this.sql + '&limit=' + this.state.limit + '&dbSetEntryIds=' + dbSetEntryIds
    }

    onChangeCallback = sql => {
        this.sql = sql[0]
    }

    isShardingEmpty = dbSetEntrylist => {
        const arr = []
        dbSetEntrylist.forEach(i => {
            if (i.sharding != '') {
                arr.push(i)
            }
        })
        return arr.length > 0
    }

    onChange = limit => {
        if (limit > 10000) {
            this.setState({limit: 10000})
        } else {
            this.setState({limit})
        }
    }

    render() {
        const {datasearchmodel, setValueByReducers} = this.props
        const _props = {datasearchmodel, setValueByReducers}
        const {dbSetlist, dbSetEntrylist, dataList, loading} = this.state
        const showDbSetEntry = this.isShardingEmpty(dbSetEntrylist)
        const FormItem = Form.Item
        const formItemLayout = {
            labelCol: {
                xs: {span: 24},
                sm: {span: 4},
            },
            wrapperCol: {
                xs: {span: 24},
                sm: {span: 20},
            },
        }
        return (
            <Col sm={24} key='1'>
                <QueueAnim type={['left', 'right']} delay={400}>
                    <Row key='b'>
                        {/*<Col sm={4}>
                            <FormItem label='Limit' {...formItemLayout} style={{marginBottom: '5px'}}>
                                <InputNumber min={1} max={500} defaultValue={limit} value={limit}
                                             onChange={::this.onChange}/>
                            </FormItem>
                        </Col>*/}
                        <Col sm={9}>
                            <FormItem label='逻辑库' {...formItemLayout} style={{marginBottom: '5px'}}>
                                <SelectPlus {..._props} placeholder='请选择逻辑库'
                                            items={dbSetlist} valueLink={this.objName + '.dbset'}
                                            onChangeCallbBack={::this.onChangeDbSetCallbBack}/>
                            </FormItem>
                        </Col>
                        <Col sm={10}>
                            <div style={{display: showDbSetEntry ? 'block' : 'none'}}>
                                <FormItem label='sharding' {...formItemLayout}
                                          style={{marginBottom: '5px'}}>
                                    <SelectPlus {..._props} placeholder='请选择逻辑库映射'
                                                format={{id: 'id', name: 'sharding'}}
                                                selectedIds={[]} items={dbSetEntrylist}
                                                isSearch={true} mode='multiple'
                                                valueLink={this.objName + '.dbSetEntryList'}/>
                                </FormItem>
                            </div>
                        </Col>
                        <Col sm={4} style={{float: 'right'}}>
                            <Col sm={11}>
                                <Button icon='search' onClick={::this.searchData}>点击查询</Button>
                            </Col>
                            <Col sm={11}>
                                <Button icon='download' onClick={::this.download}>点击下载</Button>
                            </Col>
                        </Col>
                    </Row>
                    <Row key='d'>
                        <SplitEditor onChangeCallback={::this.onChangeCallback}/>
                    </Row>
                </QueueAnim>
                <QueueAnim type={['left', 'right']} delay={400}>
                    <Row key='e' style={{display: _.isEmpty(dataList) ? 'none' : 'block'}}>
                        <RsuiteTable dataList={dataList} loading={loading}/>
                    </Row>
                    <Row key='f' style={{paddingTop: '8px'}}>
                        <Alert type='info' message='查询结果每个shard最大显示500条'/>
                    </Row>
                </QueueAnim>
            </Col>)
    }
}
