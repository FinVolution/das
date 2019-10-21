/**
 * Created by liang.wang on 18/9/14.
 */
import React from 'react'
import ManagePanle from '../base/ManagePanle'
import {View} from 'ea-react-dm-v14'
import {CodeControl} from '../../../../controller/Index'
import TreePanle from '../base/TreePanle'
import {Row, Col, Tabs, Icon} from 'antd'
import PageBase from '../../page/PageBase'
import FrwkUtil from '../../utils/util/FrwkUtil'
import DataUtil from '../../utils/util/DataUtil'
import GenerateCodeManage from './generate/GenerateCodeManage'
import _ from 'underscore'
import TableEntityManage from './class/TableEntityManage'
import SelectEntityManage from './class/SelectEntityManage'
import QueueAnim from 'rc-queue-anim'

@View(CodeControl)
export default class CodeManage extends ManagePanle {

    constructor(props, context) {
        super(props, context)
        this.modelName = 'CodeModel'
        this.searchInfo = this.modelName + '.searchInfo'
        this.objName = this.modelName + '.dbinfo'
        this.initRemindItem()
        this.state = {
            remindItem: '',
            tabIndex: '1',
            groupId: 0,
            projectId: 0,
            dbSetlist: []
        }
    }

    initRemindItem = () => {
        FrwkUtil.fetch.fetchGet('/api/item', null, this, data => {
            if (data.code === 200) {
                this.setState({remindItem: data.msg})
            } else {
                this.showErrorsNotification(data.msg)
            }
        })
    }

    loadDbSetList = item => {
        FrwkUtil.fetch.fetchGet('/groupdbset/list', {projectId: item.id}, this, (data, _this) => {
                if (data.code === 200) {
                    if (!DataUtil.ObjUtils.isEqual(_this.state.dbSetlist, data.msg)) {
                        _this.setState({projectId: item.id, dbSetlist: data.msg})
                    } else {
                        _this.setState({projectId: item.id})
                    }
                } else if (data.code === 500) {
                    _this.showErrorsNotification(data.msg)
                }
            }
        )
    }

    onSelect = (selectedKeys, node) => {
        const item = node.node.props.dataRef
        if (item.isLeaf) {
            if (this.state.projectId != item.id) {
                this.loadDbSetList(item)
            }
        } else {
            if (this.state.groupId != item.id) {
                this.setState({groupId: item.id, projectId: 0})
            } else {
                if (this.state.projectId != 0) {
                    this.setState({projectId: 0})
                }
            }
        }
    }

    getDefaultSelected = item => {
        if (!_.isEmpty(item)) {
            if (item.length === 1) {
                this.setState({groupId: item[0].id})
            }
            if (item.length === 2) {
                this.setState({groupId: item[0].id, projectId: item[1].id}, () => {
                    this.loadDbSetList(item[1])
                })
            }
        }
    }

    onTabClick = i => {
        this.setState({tabIndex: i})
    }

    render() {
        const TabPane = Tabs.TabPane
        const {groupId, projectId, dbSetlist, tabIndex, remindItem} = this.state
        return (<div>
                <PageBase title='实体类与代码生成管理' navigation='数据访问平台 / 实体类与代码生成管理' addButtonShow={false}
                          zDepth={1}>
                    <div style={{paddingTop: '0px'}}>
                        <Row>
                            <QueueAnim type={['left', 'right']} delay={600}>
                                <Col sm={3} key='a'>
                                    <TreePanle showLine={true} rootShow={false} onSelect={::this.onSelect}
                                               getDefaultSelected={::this.getDefaultSelected}
                                               remindItem={remindItem}
                                               format={{
                                                   tree: {
                                                       title: 'group_name',
                                                       key: 'id',
                                                       tooltip: 'group_comment',
                                                       isLeaf: false
                                                   },
                                                   leaf: {title: 'name', key: 'id', tooltip: 'app_scene', isLeaf: true}
                                               }}/>
                                </Col>
                            </QueueAnim>
                            <QueueAnim type={['bottom', 'right']} delay={600}>
                                <Col sm={21} key='b'>
                                    <QueueAnim type={['right', 'right']} delay={600}>
                                        <Tabs activeKey={tabIndex} onTabClick={::this.onTabClick}>
                                            <TabPane tab={<span><Icon type="code-o"/>代码生成器</span>} key='1'>
                                                <GenerateCodeManage groupId={groupId} projectId={projectId}
                                                                    tabIndex={tabIndex}
                                                                    changeActiveKey={::this.onTabClick}/>
                                            </TabPane>
                                            <TabPane tab={<span><Icon type="file-text"/>表实体管理</span>} key='2'>
                                                <TableEntityManage dbSetlist={dbSetlist} groupId={groupId}
                                                                   projectId={projectId} tabIndex={tabIndex}/>
                                            </TabPane>
                                            <TabPane tab={<span><Icon type="file-text"/>查询实体管理</span>} key='3'>
                                                <SelectEntityManage dbSetlist={dbSetlist} groupId={groupId}
                                                                    projectId={projectId} tabIndex={tabIndex}/>
                                            </TabPane>
                                        </Tabs>
                                    </QueueAnim>
                                </Col>
                            </QueueAnim>
                        </Row>
                    </div>
                </PageBase>
            </div>
        )
    }
}
