import React from 'react'
import Component from '../../utils/base/ComponentAlert'
import {Row, Col, Select, Alert, Icon} from 'antd'
import {DataUtil, FrwkUtil} from '../../utils/util/Index'
import './DataBaseInfo.less'

export default class DataBaseInfo extends Component {

    static defaultProps = {
        visibleCallback: () => {
        }
    }

    constructor(props, context) {
        super(props, context)
        this.modelName = 'DatabaseModel'
        this.objName = this.modelName + '.item'
        this.dbconectInfoLink = this.modelName + '.dbconectInfo'
        this.states = this.modelName + '.states'
        this.tabsSelected = 1
        this.state = {
            isShow: false,
            tableInfo: null,
            db_cataloss: []
        }
        this.initDataBases()
    }

    initDataBases = () => {
        const dbconectInfo = this.getValueToJson(this.objName)
        FrwkUtil.fetch.fetchGet('db/catalogsByDbId', {dbId: dbconectInfo.id}, this, data => {
            if (data.code == 200) {
                this.setState({db_cataloss: data.msg})
            } else {
                this.showErrorsNotification(data.msg)
            }
        })
    }

    onSelectCallback = name => {
        const item = this.getValueToJson(this.objName)
        FrwkUtil.fetch.fetchGet('db/getTableAttributes', {name, id: item.id}, this, data => {
            if (data.code == 200) {
                this.setState({tableInfo: data.msg, isShow: true})
            } else {
                this.showErrorsNotification(data.msg)
            }
        })
    }

    testConnect = current => {
        const dbconectInfo = this.getValueToJson(this.dbconectInfoLink)
        dbconectInfo && this.props.connectionTest(dbconectInfo, this, (_this, data) => {
            if (data.code == 200) {
                _this.showSuccessMsg('链接成功')
                _this.setState({current})
            } else {
                _this.showSuccessMsg('链接失败！请检查信息重试！')
                window.console.error(data.msg)
            }
        })
    }

    createShow = () => {
        const {tableInfo} = this.state
        return tableInfo && tableInfo.tableAttributes && tableInfo.tableAttributes.map((v, i) => {
            return <Row key={i}>
                <Col sm={6} className='col-border'>
                    <Alert message={v.columnName} type='success'/>
                </Col>
                <Col sm={6} className='col-border'>
                    <Alert message={v.columnType} type='success'/>
                </Col>
                <Col sm={6} className='col-border'>
                    <Alert message={v.datasize} type='success'/>
                </Col>
                <Col sm={6} className='col-border'>
                    <Alert message={v.nullable === 1 ? <Icon type='check'/> : <Icon type='close'/>}
                           type='success'/>
                </Col>
            </Row>
        })
    }

    render() {
        const Option = Select.Option
        const {db_cataloss, isShow, tableInfo} = this.state
        const children = []
        let primaryKeys = '', total = 0
        db_cataloss && !DataUtil.is.String(db_cataloss) && db_cataloss.forEach((item) => {
            children.push(<Option key={item}>{item}</Option>)
        })
        if (tableInfo && tableInfo.primaryKeys && tableInfo.primaryKeys.length > 0) {
            primaryKeys = tableInfo.primaryKeys.join(',')
            total = tableInfo.total
        }
        return (
            <div className='dataBaseInfo'>
                <Row>
                    <Col sm={24}>
                        选择表：
                    </Col>
                    <Col sm={24}>
                        <Select showSearch={true}
                                style={{width: '100%'}}
                                tokenSeparators={[',']}
                                onSelect={::this.onSelectCallback}>
                            {children}
                        </Select>
                    </Col>
                </Row>
                <Row style={{display: isShow && primaryKeys ? 'block' : 'none'}}>
                    <Col sm={24} className='col-border' style={{padding: '8px 0px 8px 0px'}}>
                        <Alert message={'表主键: ' + primaryKeys} type='success'
                               style={{wordBreak: 'break-all'}}/>
                    </Col>
                </Row>
                <Row style={{display: isShow && total ? 'block' : 'none'}}>
                    <Col sm={24} className='col-border' style={{padding: '0px 0px 8px 0px'}}>
                        <Alert message={'字段个数: ' + total} type='success'
                               style={{wordBreak: 'break-all'}}/>
                    </Col>
                </Row>
                <Row style={{display: isShow ? 'block' : 'none'}}>
                    <Col sm={6} className='col-border'>
                        <Alert message='字段名' type='info'/>
                    </Col>
                    <Col sm={6} className='col-border'>
                        <Alert message='字段类型' type='info'/>
                    </Col>
                    <Col sm={6} className='col-border'>
                        <Alert message='字段长度' type='info'/>
                    </Col>
                    <Col sm={6} className='col-border'>
                        <Alert message='允许空值' type='info'/>
                    </Col>
                </Row>
                {this.createShow()}
            </div>
        )
    }
}