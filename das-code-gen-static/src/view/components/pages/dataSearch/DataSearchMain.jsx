/**
 * Created by liang.wang on 19/6/14.
 */
import React from 'react'
import ManagePanle from '../base/ManagePanle'
import PageBase from '../../page/PageBase'
import {Icon, Tabs} from 'antd'
import QueueAnim from 'rc-queue-anim'
import DataSearchManage from './DataSearchManage'
import DataSearchLog from './DataSearchLog'

export default class DataSearchMain extends ManagePanle {

    constructor(props, context) {
        super(props, context)
        this.state = {
            tabIndex: '1'
        }
    }

    onTabClick = i => {
        this.setState({tabIndex: i})
    }

    render() {
        const TabPane = Tabs.TabPane
        const {tabIndex} = this.state
        return (<div>
            <PageBase title='' navigation='数据访问平台 / 数据查询' addButtonShow={false} showDivider={false} zDepth={1}>
                <div style={{paddingTop: '0px'}}>
                    <QueueAnim type={['right', 'right']} delay={600}>
                        <Tabs activeKey={tabIndex} onTabClick={::this.onTabClick}>
                            <TabPane tab={<span><Icon type="code-o"/>数据查询</span>} key='1'>
                                <DataSearchManage/>
                            </TabPane>
                            <TabPane tab={<span><Icon type="file-text"/>查询日志</span>} key='2'>
                                <DataSearchLog tabIndex={tabIndex}/>
                            </TabPane>
                        </Tabs>
                    </QueueAnim>
                </div>
            </PageBase>
        </div>)
    }
}
