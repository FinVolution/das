/**
 * Created by liang.wang on 18/9/14.
 */
import React from 'react'
import Component from '../../../utils/base/ComponentAlert'
import {Row, Col, Button, Steps, Spin, Alert} from 'antd'
import {InputPlus, Inputlabel, CodeEditor, SelectPlus, RadioPlus} from '../../../utils'
import FrwkUtil from '../../../utils/util/FrwkUtil'
import Immutable from 'immutable'
import {DataUtil} from '../../../utils/util/Index'
/*import './AddSelectEntry.less'
import AceEditor from 'react-ace'
import 'brace/mode/sql'
import 'brace/theme/monokai'*/
import $ from 'jquery'
import {das_msg, dataFieldTypeEnum, fieldTypes} from '../../../../../model/base/BaseModel'
import _ from 'underscore'


export default class AddSelectEntry extends Component {

    static defaultProps = {
        groupId: 0,
        projectId: 0,
        dbSetlist: [],
        addSelectEntity: null
    }

    constructor(props, context) {
        super(props, context)
        this.modelName = 'SelectEntityModel'
        this.objName = this.modelName + '.task_sql'
        this.cleanExceptKeys = ['project_id']
        this.steps = [{
            key: '1',
            title: '编辑查询'
        }, {
            key: '2',
            title: '查询校验'
        }, {
            key: '3',
            title: '提交'
        }]
        this.tabsSelected = 1
        this.state = {
            dbType: 0,
            dbset_id: 0,
            sqlValidateRs: '',
            dbinfoShow: false,
            nextBtndisable: true,
            current: 0,
            submitDisabled: false,
            submitLoading: false,
            submitTitle: '点击提交',
            title: '编辑',
            suggestionItem: {}
        }
    }

    next() {
        const current = this.state.current + 1
        switch (current) {
            case 1:
                return this.sqlValidateConnect(current)
            case 2:
                return this.addSelectEntry(current)
        }
    }

    sqlValidate = current => {
        const task_sql = this.getValueByReducers(this.objName).toJS()
        FrwkUtil.fetch.fetchPost('/db/sqlValidate', {
                dbset_id: task_sql.dbset_id,
                sql_content: task_sql.sql_content
            }, this, (data, _this) => {
                if (data.code === 200) {
                    _this.setState({current, submitLoading: false, sqlValidateRs: data.msg})
                } else if (data.code === 500) {
                    _this.showErrorsNotification('SQL校验失败!' + data.msg)
                    _this.setState({
                        submitLoading: false
                    })
                    window.console.error(data.msg)
                }
            }
        )
    }

    sqlValidateConnect = current => {
        this.setState({
            submitTitle: 'SQL校验中，请稍后...',
            submitLoading: true
        }, () => {
            try {
                setTimeout(() => {
                    this.sqlValidate(current)
                }, 2000)
            } catch (e) {
                this.setState({
                    submitLoading: false
                })
            }
        })
    }

    cleanSubmitDbs = () => {
        let item = this.getValueByReducers(this.objName).toJS()
        this.setValueByReducers(this.objName, Immutable.fromJS(DataUtil.ObjUtils.cleanJson(item, this.cleanExceptKeys)))
    }

    submitSelectEntry() {
        let task_sql = this.getValueToJson(this.objName)
        const states = this.getValueToJson(this.modelName + '.states')
        if (states.editerType == 1) {
            this.props.updateSelectEntity(task_sql, this, (_this, data) => {
                if (data.code == 200) {
                    this.setState({
                        submitTitle: '已提交',
                        submitDisabled: true,
                        submitLoading: false
                    }, () => {
                        this.setValueByReducers(false)
                        this.props.visibleCallback(false)
                        this.props.reload()
                        this.cleanSubmitDbs()
                    })
                } else {
                    this.showErrorsNotification('编辑失败!!! ' + data.msg)
                    this.setState({
                        submitLoading: false
                    })
                }
            })
        } else {
            this.props.addSelectEntity(task_sql, this, (_this, data) => {
                if (data.code == 200) {
                    this.setState({
                        submitTitle: '已提交',
                        submitDisabled: true,
                        submitLoading: false
                    }, () => {
                        this.setValueByReducers(false)
                        this.props.visibleCallback(false)
                        this.props.reload()
                        this.cleanSubmitDbs()
                    })
                } else {
                    this.showErrorsNotification('添加失败!!! ' + data.msg)
                    this.setState({
                        submitLoading: false
                    })
                }
            })
        }
    }

    addSelectEntry = current => {
        this.setState({
            current: current,
            submitTitle: '提交中，请稍后...',
            submitLoading: true
        }, () => {
            try {
                setTimeout(() => {
                    this.submitSelectEntry()
                }, 2000)
            } catch (e) {
                this.setState({
                    submitLoading: false
                })
            }
        })
    }

    onSetValueByReducersCallback = val => {
        const task_sql = this.getValueByReducers(this.objName).toJS()
        let flag = false
        const arr = [task_sql.class_name, task_sql.sql_content]
        if (arr.every(f => {
            return String(f).length > 0
        })) {
            this.setState({nextBtndisable: false})
        } else {
            flag = true
            this.setState({nextBtndisable: flag})
        }
        if (!val && !flag) {
            this.setState({nextBtndisable: true})
        }
    }

    createSqlValidateRs = sqlValidateRs => {
        if (!_.isObject(sqlValidateRs)) {
            return
        }

        if (sqlValidateRs.dbType === 1) {
            let msg, explanJson
            try {
                msg = $.parseJSON(sqlValidateRs.msg)
            } catch (e) {
                window.console.error(e)
            }
            if (sqlValidateRs.rows || !_.isEmpty(msg)) {
                const rows = sqlValidateRs.rows
                explanJson = msg[0]
                return <div style={{padding: '15px'}}>
                    <Alert
                        type='success'
                        message='SQL验证通过，结果如下'
                        showIcon
                        description={<div>
                            <Row>
                                <Col className='row-boder' span={4}>Select_type</Col>
                                <Col className='row-boder' span={4}>Type</Col>
                                <Col className='row-boder' span={4}>Possible_keys</Col>
                                <Col className='row-boder' span={4}>Key</Col>
                                <Col className='row-boder' span={4}>Rows</Col>
                                <Col className='row-boder' span={4}>Extra</Col>
                            </Row>
                            <Row>
                                <Col className='row-boder'
                                     span={4}>{explanJson.select_type ? explanJson.select_type : '-'}</Col>
                                <Col className='row-boder' span={4}>{explanJson.type ? explanJson.type : '-'}</Col>
                                <Col className='row-boder'
                                     span={4}>{explanJson.possible_keys ? explanJson.possible_keys : '-'}</Col>
                                <Col className='row-boder' span={4}>{explanJson.key ? explanJson.key : '-'}</Col>
                                <Col className='row-boder' span={4}>{rows}</Col>
                                <Col className='row-boder' span={4}>{explanJson.extra ? explanJson.extra : '-'}</Col>
                            </Row>
                        </div>}/>
                </div>
            }
        } else if (sqlValidateRs.dbType === 2) {
            return <div style={{padding: '15px'}}>
                <Alert
                    type='success'
                    message='SQL Server 请自行验证，若确认无误请提交！！'
                    showIcon
                    description={<div>
                        <Row>
                            <Col className='row-boder' span={8}>Select_type</Col>
                            <Col className='row-boder' span={8}>Type</Col>
                            <Col className='row-boder' span={8}>Rows</Col>
                        </Row>
                        <Row>
                            <Col className='row-boder' span={8}>SQl select</Col>
                            <Col className='row-boder' span={8}>Sql Server</Col>
                            <Col className='row-boder' span={8}>{sqlValidateRs.rows}</Col>

                        </Row>
                    </div>}/>
            </div>
        }
    }

    onChangeCallback = e => {
        this.onSetValueByReducersCallback(e)
    }

    onChangeCallbBack = (dbset_id, db_name) => {
        this.replaceValuesToItem(this.objName, {dbset_id, db_name})
    }

    render() {
        const rowHeight = '300px'
        const Step = Steps.Step
        const {current, nextBtndisable, submitTitle, submitLoading, sqlValidateRs} = this.state
        const {setValueByReducers, selectentitymodel, dbSetlist} = this.props
        const states = this.getValueToJson(this.modelName + '.states')
        const task_sql = this.getValueToJson(this.objName)
        let field_type = task_sql.field_type
        if (states.editerType == 0) {
            field_type = dataFieldTypeEnum.sql_date
        } else if (field_type == null || (field_type != dataFieldTypeEnum.sql_date && field_type != dataFieldTypeEnum.util_date)) {
            field_type = dataFieldTypeEnum.sql_date
        }
        const _props = {
            selectentitymodel,
            setValueByReducers
        }
        return (
            <div className='addSelectEntry'>
                <Row style={{height: '64px'}}>
                    <Col sm={3}/>
                    <Col sm={18}>
                        <Steps current={current}>
                            {this.steps.map(item => <Step key={item.title} title={item.title}/>)}
                        </Steps>
                    </Col>
                    <Col sm={3}/>
                </Row>
                <div style={{display: current === 0 ? 'block' : 'none'}}>
                    <Spin spinning={submitLoading} tip={submitTitle} size='large'>
                        <Inputlabel title={'逻辑数据库'}>
                            <SelectPlus {..._props}
                                        valueLink={this.objName + '.dbset_id'}
                                        onChangeCallbBack={::this.onChangeCallbBack}
                                        selectedId={task_sql.dbset_id} items={dbSetlist}/>
                        </Inputlabel>
                        <Inputlabel title={'实体类名'}>
                            <InputPlus {..._props}
                                       defaultValue={task_sql.view_names} valueLink={this.objName + '.class_name'}
                                       placeholder={das_msg.ordinary_name}
                                       validRules={{maxLength: 150, isEnglishnderline: true}}
                                       onSetValueByReducersCallback={::this.onSetValueByReducersCallback}/>
                        </Inputlabel>
                        <Inputlabel title='DATE类型'>
                            <RadioPlus {..._props}
                                       items={fieldTypes}
                                       selectedId={field_type}
                                       valueLink={this.objName + '.field_type'}
                                       onChangeCallback={::this.onChangeCallback}/>
                        </Inputlabel>
                        <Inputlabel title={'输入SQL'}>
                            <CodeEditor {..._props}
                                        valueLink={this.objName + '.sql_content'} contStyle={{height: '300px'}}
                                        mode='sql' theme='monokai' value={task_sql.sql_content}
                                        onChangeCallback={::this.onChangeCallback}/>
                        </Inputlabel>
                        <Inputlabel title={'描述'}>
                            <InputPlus {..._props}
                                       defaultValue={task_sql.comment}
                                       validRules={{maxLength: 200}}
                                       valueLink={this.objName + '.comment'}
                                       onSetValueByReducersCallback={::this.onSetValueByReducersCallback}/>
                        </Inputlabel>
                    </Spin>
                </div>
                <div style={{display: current > 0 ? 'block' : 'none', height: rowHeight}}>
                    <Spin
                        spinning={submitLoading}
                        tip={submitTitle}
                        size='large'>
                        <Row>
                            <Col sm={24}>
                                {this.createSqlValidateRs(sqlValidateRs)}
                            </Col>
                        </Row>
                    </Spin>
                </div>
                <Row>
                    <div className='steps-action'>
                        {
                            current === 0
                            &&
                            <Button type="primary" size='large' disabled={nextBtndisable}
                                    onClick={::this.next}>下一步</Button>
                        }
                        {
                            current > 0
                            &&
                            <Button type="primary" onClick={::this.next}>提交</Button>
                        }
                        {
                            /* current > 0
                             &&
                             <Button style={{marginLeft: 8}} onClick={::this.prev}>上一步</Button>*/
                        }
                    </div>
                </Row>
            </div>
        )
    }
}