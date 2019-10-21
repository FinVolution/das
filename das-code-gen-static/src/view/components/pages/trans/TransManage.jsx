/**
 * Created by liang.wang on 18/9/14.
 */
import React from 'react'
import ManagePanle from '../base/ManagePanle'
import {View} from 'ea-react-dm-v14'
import {TransControl} from '../../../../controller/Index'
import PageBase from '../../page/PageBase'
import QueueAnim from 'rc-queue-anim'
import {Button, Col, Row} from 'antd'
import {CodeEditor} from '../../utils'
import globalStyles from '../../page/styles'
//import FrwkUtil from '../../utils/util/FrwkUtil'
//import DataUtil from '../../utils/util/DataUtil'
//import _ from 'underscore'

@View(TransControl)
export default class TransManage extends ManagePanle {

    constructor(props, context) {
        super(props, context)
        this.modelName = 'TransModel'
        this.xmlValueLink = this.modelName + 'TransModel.xmlValue'
        this.dasValueLink = this.modelName + 'TransModel.dasValue'
        this.objName = this.modelName + '.dbinfo'
        this.trans = this.props.trans
        this.state = {
            xmlContent: '',
            dasContent: ''
        }
    }

    onChangeCallbackXml = xmlContent => {
        this.setState({xmlContent})
    }

    onChangeCallbackDas = dasContent => {
        this.setState({dasContent})
    }

    onClick = () => {
        const {xmlContent} = this.state
        this.trans({xmlContent}, this, (_this, data) => {
            if (data.code === 200) {
                _this.setState({dasContent: data.msg})
            } else {
                _this.showErrorsNotification(data.msg)
            }
        })
    }

    render() {
        const {xmlContent, dasContent} = this.state
        return (<div>
                <PageBase title='' navigation='数据访问平台 / 代码转换' addButtonShow={false} showDivider={false} zDepth={1}>
                    <div style={{paddingTop: '0px'}}>
                        <Row>
                            <QueueAnim type={['right', 'right']} delay={600}>
                                <Col sm={11} key='1'>
                                    <QueueAnim type={['left', 'right']} delay={400}>
                                        <Row key='a'>
                                            <h3 style={globalStyles.title}>mybatis mapper</h3>
                                        </Row>
                                        <Row key='b'>
                                            <CodeEditor style={{width: '100%', height: '1000px'}} mode='java'
                                                        theme='monokai'
                                                        value={xmlContent} valueLink={this.xmlValueLink}
                                                        onChangeCallback={::this.onChangeCallbackXml}/>
                                        </Row>
                                    </QueueAnim>
                                </Col>
                                <Col sm={1} key='2' style={{width: '60px'}}>
                                    <Button type='primary' icon='double-right' size='large' onClick={::this.onClick}
                                            style={{width: '40px', margin: '520px 9px 9px 9px'}}/>
                                </Col>
                                <Col sm={12} key='3'>
                                    <QueueAnim type={['right', 'right']} delay={500}>
                                        <Row key='a'>
                                            <h3 style={globalStyles.title}>das code</h3>
                                        </Row>
                                        <Row key='b'>
                                            <CodeEditor style={{width: '100%', height: '1000px'}} mode='java'
                                                        theme='monokai'
                                                        value={dasContent} valueLink={this.dasValueLink}
                                                        onChangeCallback={::this.onChangeCallbackDas}/>
                                        </Row>
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
