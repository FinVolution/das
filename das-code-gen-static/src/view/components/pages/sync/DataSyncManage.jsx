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
import _ from 'underscore'
import {sysnc} from '../../../../model/base/BaseModel'

@View(CodeControl)
export default class DataSyncManage extends ManagePanle {

    constructor(props, context) {
        super(props, context)
        this.modelName = 'CodeModel'
        this.searchInfo = this.modelName + '.searchInfo'
        this.objName = this.modelName + '.dbinfo'
        this.state = {
            tabIndex: '1',
            groupId: 0,
            projectId: 0,
            dbSetlist: []
        }
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
        const {groupId, projectId, dbSetlist, tabIndex} = this.state
        return (<div>
            <PageBase title='数据同步' navigation='数据访问平台 / 数据同步' addButtonShow={false}
                      zDepth={1}>
                <div style={{paddingTop: '0px'}}>
                    <Row>
                        <Col sm={3}>
                            <TreePanle treeUrl={window.DASENV.dasSyncTarget + '/group/tree?' + sysnc.token}
                                       searchUrl={window.DASENV.dasSyncTarget + '/project/group?' + sysnc.token}
                                       rootShow={false}
                                       showLine={true}
                                       onSelect={::this.onSelect}
                                       getDefaultSelected={::this.getDefaultSelected}/>
                        </Col>
                        <Col sm={21}>
                            <Tabs activeKey={tabIndex} onTabClick={::this.onTabClick}>
                                <TabPane tab={<span><Icon type="code-o"/>项目组同步</span>} key='1'>

                                </TabPane>
                                <TabPane tab={<span><Icon type="file-text"/>物理库同步</span>} key='2'>

                                </TabPane>
                                <TabPane tab={<span><Icon type="file-text"/>逻辑库同步</span>} key='3'>

                                </TabPane>
                            </Tabs>
                        </Col>
                    </Row>
                </div>
            </PageBase>
        </div>)
    }
}
