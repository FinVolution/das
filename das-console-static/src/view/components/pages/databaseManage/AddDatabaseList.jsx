import React from 'react'
import Component from '../../utils/base/ComponentAlert'
import {DropDownSuggestion, TreeSelectPlus} from '../../utils/index'
import {Row, Col, Select, Button, Steps, Icon, Tabs, Alert, Spin} from 'antd'
import DatabaseTab from './DatabaseTab'
import {DasUtil, DataUtil} from '../../utils/util/Index'
import Immutable from 'immutable'
import EditorDBInfo from './EditorDBInfo'
import FrwkUtil from '../../utils/util/FrwkUtil'
import _ from 'underscore'

export default class AddDatabaseList extends Component {

    static defaultProps = {
        visibleCallback: () => {
        }
    }

    constructor(props, context) {
        super(props, context)
        this.modelName = 'DatabaseModel'
        this.dbconectInfoLink = this.modelName + '.dbconectInfo'
        this.states = this.modelName + '.states'
        this.steps = [{
            key: '1',
            title: '数据库链接信息'
        }, {
            key: '2',
            title: '选择数据库'
        }, {
            key: '3',
            title: '提交'
        }]
        this.tabsSelected = 1
        this.state = {
            current: 0,
            dbinfoShow: false,
            nextBtndisable: true,
            submitDisabled: false,
            submitLoading: false,
            submitTitle: '点击提交',
            title: '编辑',
            suggestionItem: {}
        }
    }

    next = () => {
        const current = this.state.current + 1
        if (this.tabsSelected == 1) {
            switch (current) {
                case 1:
                    return this.testConnect(current)
                case 2:
                    return this.addDblist(current)
            }
        } else if (this.tabsSelected == 2) {
            switch (current) {
                case 1:
                    return this.setState({current})
                case 2:
                    return this.addDblist(current)
            }
        }
    }

    selectedCallback = ele => {
        this.setState({suggestionItem: ele}, () => {
            this.props.connectionTest(ele, this, (_this, data) => {
                if (data.code === 200) {
                    _this.setState({nextBtndisable: false, dbinfoShow: true},)
                }
            })

            this.props.connectionTestNew(ele, this, (_this, data) => {
                if (data.code === 200) {
                    _this.setState({nextBtndisable: false, dbinfoShow: true},)
                }
            })
        })
    }

    onDeselectCallback = (item, isBatch = false) => {
        if (isBatch) {
            const list = []
            item.forEach(i => {
                list.push({'db_catalog': i})
            })
            this.deleteListToList(this.modelName + '.dalGroupDBList', list, 'db_catalog')
        } else {
            this.deleteItemToList(this.modelName + '.dalGroupDBList', {'db_catalog': item}, 'db_catalog')
        }
    }

    onSelectCallback = (object, item, option, _this, isBatch = false) => {
        let dbname
        let groups = this.getValueToJson(this.modelName + '.tree')
        const link = this.modelName + '.dalGroupDBList'
        if (isBatch) {
            const list = []
            object.forEach(item => {
                dbname = item.toLowerCase()
                list.push({
                    'db_catalog': item,
                    'dbname': dbname,
                    'addToGroup': false,
                    'dal_group_id': groups[0].id
                })
            })
            this.addListToList(link, list, 'db_catalog')
            this.onChangeCallback(object)
        } else {
            dbname = object.toLowerCase()
            this.addItemToList(link, {
                'db_catalog': object,
                'dbname': dbname,
                'addToGroup': false,
                'dal_group_id': groups[0].id
            }, 'db_catalog')
            this.onChangeCallback(object)

        }
    }

    cleanSubmitDbs() {
        this.setValueByReducers(this.modelName + '.dalGroupDBList', Immutable.fromJS([]))
    }

    submitDbs() {
        let groupDbs = this.getValueToJson(this.modelName + '.dalGroupDBList')
        let dbconectInfo = this.getValueToJson(this.dbconectInfoLink)
        if (this.tabsSelected == 2) {
            dbconectInfo = this.getValueToJson(this.modelName + '.dbconectSuggestion')
        }
        const linkinfo = DataUtil.ObjUtils.some(dbconectInfo, ['db_type', 'db_address', 'db_port', 'db_user', 'db_password'])
        DataUtil.ObjUtils.extendObjToArr(groupDbs, linkinfo)
        if (_.isEmpty(groupDbs)) {
            this.showErrorsNotification('请添加物理库，再提交!!! ')
            return
        }
        const _groupDbs = DataUtil.ObjUtils.getAllRepeats(groupDbs, 'dbname')
        if (!_.isEmpty(_groupDbs)) {
            this.showErrorsNotification('请检查有重复项，再提交!!! ')
            return
        }
        this.props.addDbs(groupDbs, this, (_this, data) => {
            if (data.code == 200) {
                this.setState({
                    submitTitle: '已提交',
                    submitDisabled: true,
                    submitLoading: false
                }, () => {
                    this.props.visibleCallback(false)
                    this.props.reload()
                    this.cleanSubmitDbs()
                })
            } else {
                this.showErrorsNotification('添加失败!!! ' + data.msg)
                this.setState({
                    submitLoading: false
                }, () => {
                    this.addLable(data.msg)
                })
            }
        })
    }

    onChangeCallback = () => {
        setTimeout(() => {
            const groupDbs = this.getValueToJson(this.modelName + '.dalGroupDBList')
            const _groupDbs = DataUtil.ObjUtils.getAllRepeats(groupDbs, 'dbname')
            let renames = []
            if (_groupDbs.length > 0) {
                renames = DataUtil.ObjUtils.filter(_groupDbs, 'dbname')
            }
            const arr = DataUtil.ObjUtils.filter(groupDbs, 'dbname')
            const _arr = DataUtil.ObjUtils.getRepeat(arr)
            if (_arr.length > 0) {
                this.addLable(_arr)
            }
            FrwkUtil.fetch.fetchPost('db/isExist', arr, this, data => {
                renames = renames.concat(data.msg)
                renames = DataUtil.ObjUtils.unique(renames)
                if (data.code == 200) {
                    this.addLable(renames)
                } else {
                    renames = _.difference(renames, _arr)
                    this.addLable(renames, false)
                }
            })
        }, 500)
    }

    addLable = (msg, type = true) => {
        const link = this.modelName + '.dalGroupDBList'
        let arr
        if (_.isArray(msg)) {
            arr = msg
        } else {
            arr = msg.split(',')
        }
        let groupDbs = this.getValueToJson(link)
        groupDbs.forEach(item => {
            if (type) {
                if (arr.includes(item.dbname)) {
                    item.showExist = true
                } else {
                    item.showExist = false
                }
            } else {
                if (arr.includes(item.dbname)) {
                    item.showExist = false
                }
            }
        })
        this.setValueToImmutable(link, groupDbs)
    }

    addDblist = current => {
        this.setState({
            current: current,
            submitTitle: '提交中，请稍后...',
            submitLoading: true
        }, () => {
            try {
                setTimeout(() => {
                    this.submitDbs()
                }, 2000)
            } catch (e) {
                this.setState({
                    submitLoading: false
                })
            }
        })
    }

    tabsOnChangeCallback = e => {
        this.tabsSelected = e
    }

    testConnect = current => {
        const dbconectInfo = this.getValueToJson(this.dbconectInfoLink)
        dbconectInfo && this.props.connectionTestNew(dbconectInfo, this, (_this, data) => {
            if (data.code == 200) {
                _this.showSuccessMsg('链接成功')
                _this.setState({current})
            } else {
                _this.showSuccessMsg('链接失败！请检查信息重试！')
                window.console.error(data.msg)
            }
        })
    }

    onSetValueByReducersCallback = val => {
        const dbconectInfo = this.getValueToJson(this.dbconectInfoLink)
        let flag = false
        const arr = [dbconectInfo.db_type, dbconectInfo.db_address, dbconectInfo.db_port, dbconectInfo.db_user, dbconectInfo.db_password]
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

    ceateDblinkInfo = suggestionItem => {
        return <Alert
            message={<p style={{
                color: 'green',
                fontSize: '17px'
            }}>数据库类型:{DasUtil.getDbNameByType(suggestionItem.db_type)}</p>}
            description={
                <div>
                    <Row type='flex' justify='center' className='alert-col'>
                        <Col span={4}>数据库地址:</Col>
                        <Col span={18}>{suggestionItem.db_address}</Col>
                    </Row>
                    <Row type='flex' justify='center' className='alert-col'>
                        <Col span={4}>端口:</Col>
                        <Col span={18}>{suggestionItem.db_port}</Col>
                    </Row>
                    <Row type='flex' justify='center' className='alert-col'>
                        <Col span={4}>用户名:</Col>
                        <Col span={18}>{suggestionItem.db_user}</Col>
                    </Row>
                    <Row type='flex' justify='center' className='alert-col'>
                        <Col span={4}>db_address:</Col>
                        <Col span={18}>{suggestionItem.db_address}</Col>
                    </Row>
                    <Row type='flex' justify='center' className='alert-col'>
                        <Col span={4}>catalog:</Col>
                        <Col span={18}>{suggestionItem.db_catalog}</Col>
                    </Row>
                    <Row type='flex' justify='center' className='alert-col'>
                        <Col span={4}>备注:</Col>
                        <Col span={18}>suggestionItem.comment}</Col>
                    </Row>
                </div>}
            type='success'
            showIcon
        />
    }

    render() {
        const rowHeight = '300px'
        const TabPane = Tabs.TabPane
        const Step = Steps.Step
        const Option = Select.Option
        const {suggestionItem, current, nextBtndisable, dbinfoShow, submitTitle, submitLoading} = this.state
        const {databasemodel, setValueByReducers} = this.props
        const db_cataloss = this.getValueByReducers(this.modelName + '.db_catalogs')
        const db_catalog_trees = this.getValueToJson(this.modelName + '.db_catalog_trees')
        const _props = {databasemodel, setValueByReducers, getValueByReducers: this.getValueByReducers}
        const children = []
        db_cataloss && !DataUtil.is.String(db_cataloss) && db_cataloss.forEach((item) => {
            children.push(<Option key={item}>{item}</Option>)
        })
        return (
            <div>
                <Row style={{height: '64px'}}>
                    <Col sm={3}/>
                    <Col sm={18}>
                        <Steps current={current}>
                            {this.steps.map(item => <Step key={item.title} title={item.title}/>)}
                        </Steps>
                    </Col>
                    <Col sm={3}/>
                </Row>
                <Row style={{display: current === 0 ? 'block' : 'none'}}>
                    <Tabs defaultActiveKey='1' onChange={::this.tabsOnChangeCallback}>
                        <TabPane tab={<span><Icon type='plus-circle-o'/>使用新的连接</span>} key='1'>
                            <EditorDBInfo {..._props}
                                          onCheckSetState={(nextBtndisable) => {
                                              this.setState({nextBtndisable})
                                          }}/>
                        </TabPane>
                        <TabPane tab={<span><Icon type='link'/>使用已有连接</span>} key='2'>
                            <Row style={{height: rowHeight}}>
                                <Col>数据库名称:</Col>
                                <Col sm={24}>
                                    <Row>
                                        <DropDownSuggestion url={'/db/dbs'} {...this.props} keyword='name'
                                                            style={{
                                                                divWidth: '100%',
                                                                inputWidth: '94%',
                                                                showWidth: '100%'
                                                            }}
                                                            format={{
                                                                leng: 400,
                                                                title: {
                                                                    'dbname': ' - ',
                                                                    'db_catalog': ' - ',
                                                                    'comment': ''
                                                                }
                                                            }}
                                                            valueLink={this.modelName + '.dbconectSuggestion'}
                                                            defaultVal={null}
                                                            selectedCallback={::this.selectedCallback}
                                                            placeholder='请输入数据库名称'/>
                                    </Row>
                                </Col>
                                <Col sm={24} style={{display: dbinfoShow ? 'block' : 'none'}}>
                                    {this.ceateDblinkInfo(suggestionItem)}
                                </Col>
                            </Row>
                        </TabPane>
                    </Tabs>
                </Row>
                <Spin spinning={submitLoading} tip={submitTitle} size='large'>
                    <Row style={{display: current > 0 ? 'block' : 'none', height: rowHeight}}>
                        <Col sm={24}>
                            选择物理库：
                        </Col>
                        <Col sm={24} style={{display: 'none'}}>
                            <Select mode='multiple'
                                    style={{width: '100%'}}
                                    tokenSeparators={[',']}
                                    onDeselect={::this.onDeselectCallback}
                                    onSelect={::this.onSelectCallback}>
                                {children}
                            </Select>
                        </Col>
                        <Col sm={24}>
                            <TreeSelectPlus {..._props}
                                            cleanSelect={::this.cleanSubmitDbs}
                                            onDeselect={::this.onDeselectCallback}
                                            onSelect={::this.onSelectCallback}
                                            treeData={[db_catalog_trees]}/>
                        </Col>
                        <Col sm={24}>
                            <DatabaseTab {..._props} deleteItem={::this.onDeselectCallback}
                                         onChangeCallback={::this.onChangeCallback}/>
                        </Col>
                    </Row>
                    <div className='steps-action'>
                        {
                            current < this.steps.length - 1
                            &&
                            <Button type='primary' size='large' disabled={nextBtndisable}
                                    onClick={::this.next}>下一步</Button>
                        }
                        {
                            current === this.steps.length - 1
                            &&
                            <Button type='primary' onClick={() => {
                                this.addDblist(current)
                            }}>提交</Button>
                        }
                        {
                            /* current > 0
                             &&
                             <Button style={{marginLeft: 8}} onClick={::this.prev}>上一步</Button>*/
                        }
                    </div>
                </Spin>
            </div>
        )
    }
}