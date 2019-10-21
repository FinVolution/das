/**
 * Created by liang.wang on 18/9/14.
 */
import React from 'react'
import Component from '../../../utils/base/Component'
import {Row, Col, Alert, Button, Tooltip} from 'antd'
import {DataUtil} from '../../../utils/util/Index'
import './ApolloCkeckResult.less'

export default class ApolloCkeckResult extends Component {

    static defaultProps = {
        showDetail: true,
        checkData: {}
    }

    constructor(props, context) {
        super(props, context)
        this.state = {
            showDetail: props.showDetail,
            checkData: props.checkData
        }
    }

    componentWillReceiveProps(nextProps) {
        const {checkData} = nextProps
        if (!DataUtil.ObjUtils.isEqual(this.state.checkData, checkData)) {
            this.setState({checkData})
        }
    }

    createShow = list => {
        if (list) {
            return list && list.map((v, i) => {
                const config = v.config, das = v.das
                return <Row key={i}>
                    <Col sm={6} className='col-border'>
                        <Alert message={config.key} showIcon type={config.type}/>
                    </Col>
                    <Col sm={9} className='col-border'>
                        <Alert message={das.value} showIcon type={das.type}/>
                    </Col>
                    <Col sm={9} className='col-border'>
                        <Alert message={config.value} showIcon type={config.type}/>
                    </Col>
                </Row>
            })
        }
    }

    getNotice = checkData => {
        const {showDetail} = this.state
        const button = <Tooltip placement='top' title='展开或隐藏明细'>
            <Button icon={this.state.showDetail ? 'up' : 'down'} onClick={() => {
                this.setState({showDetail: !showDetail})
            }}/>
        </Tooltip>
        const description = <Row>
            <Col sm={23}>
                {checkData.msg}
            </Col>
            <Col sm={1}>
                {button}
            </Col>
        </Row>

        switch (checkData.code) {
            case 200:
                return <Alert message='数据校验通过！！！' type='success' description={description} showIcon/>
            case 300:
                return <Alert message={'数据校验通过，但' + name + '数据与DAS出现多余属性值！！！'} type='warning'
                              showIcon
                              description={description}/>
            case 500:
                return <Alert message={'数据校验不通过！请尝试同步' + name + '数据'} type='error' description={description} showIcon/>
        }
    }

    getTitle(item) {
        const content = item && item.titles && item.titles.map(i => {
            return <Row><span>
                    <p style={{display: 'inherit'}}>{i.key}:</p>
                    <p style={{display: 'inherit', fontSize: '14px', fontWeight: 'bold'}}>{i.value}</p>
                </span>
            </Row>
        })
        return <Alert message={'项目关键字'} description={content} type='info' style={{wordBreak: 'break-all'}}/>
    }

    render() {
        const {checkData, showDetail} = this.state
        const item = checkData.item ? checkData.item : null
        if (!item) {
            return null
        }
        const columnTitle = checkData.item.columnTitle ? checkData.item.columnTitle : ['das', '配置中心']
        return <div className='apolloCkeckResult'>
            <Row>
                <Col sm={24} className='col-border' style={{paddingBottom: '15px'}}>
                    {this.getNotice(checkData)}
                </Col>
            </Row>
            <div style={{display: showDetail ? 'block' : 'none'}}>
                <Row>
                    <Col sm={24} className='col-border' style={{paddingBottom: '15px'}}>
                        {this.getTitle(item)}
                    </Col>
                </Row>
                <Row>
                    <Col sm={6} className='col-border'>
                        <Alert message='属性KEY值' type='info'/>
                    </Col>
                    <Col sm={9} className='col-border'>
                        <Alert message={columnTitle[0]} type='info'/>
                    </Col>
                    <Col sm={9} className='col-border'>
                        <Alert message={columnTitle[1]} type='info'/>
                    </Col>
                </Row>
                {this.createShow(item.list)}
            </div>
        </div>
    }
}
