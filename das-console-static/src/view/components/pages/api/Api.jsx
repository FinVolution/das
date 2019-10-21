/**
 * Created by liang.wang on 18/9/14.
 */
import React from 'react'
import ManagePanle from '../base/ManagePanle'
import {Card, Col, Row, message} from 'antd'
import ApolloCkeckResult from '../base/apolloCkeckResult/ApolloCkeckResult'
import {FrwkUtil} from '../../utils/util/Index'


export default class Api extends ManagePanle {

    constructor(props, context) {
        super(props, context)
        this.state = {
            projectCheckData: {},
            dbCheckData: {},
            dbsetCheckData: []
        }
        this.initDate()
    }

    initDate = () => {
        const aGET = FrwkUtil.UrlUtils.getUrls()
        if (aGET.appid) {
            FrwkUtil.fetch.fetchGet('apiext/check', aGET, this, data => {
                if (data.code === 200) {
                    const ids = data.msg
                    FrwkUtil.fetch.fetchGet('project/check', {id: ids.projectId}, this, data => {
                        this.setState({projectCheckData: data})
                    })
                    FrwkUtil.fetch.fetchGet('groupdbset/groupCheck', {gourpId: ids.gourpId}, this, data => {
                        this.setState({dbsetCheckData: data})
                    })
                } else {
                    message.error(data.msg, 10)
                }
            })
            FrwkUtil.fetch.fetchGet('apiext/checkDblist', aGET, this, data => {
                if (data.code === 200) {
                    this.setState({dbCheckData: data.msg})
                } else {
                    message.error(data.msg, 10)
                }
            })
        } else {
            message.error('appid 不能为空！！', 10)
        }
    }

    render() {
        const {projectCheckData, dbsetCheckData, dbCheckData} = this.state
        return (
            <div>
                <Row style={{padding: '10px'}}>
                    <Col sm={2}/>
                    <Col sm={20}>
                        <Card title='项目校验' style={{width: '100%'}}>
                            <ApolloCkeckResult checkData={projectCheckData} showDetail={false}/>
                        </Card>
                    </Col>
                    <Col sm={2}/>
                </Row>
                <Row style={{padding: '10px'}}>
                    <Col sm={2}/>
                    <Col sm={20}>
                        <Card title='逻辑库校验' style={{width: '100%'}}>
                            <ApolloCkeckResult checkData={dbsetCheckData} showDetail={false}/>
                        </Card>
                    </Col>
                    <Col sm={2}/>
                </Row>
                {
                    dbCheckData && dbCheckData.map && dbCheckData.map(item => {
                        return <Row style={{padding: '10px'}}>
                            <Col sm={2}/>
                            <Col sm={20}>
                                <Card title='物理库校验' style={{width: '100%'}}>
                                    <ApolloCkeckResult checkData={item} showDetail={false}/>
                                </Card>
                            </Col>
                            <Col sm={2}/>
                        </Row>
                    })
                }
            </div>
        )
    }
}
