/**
 * Created by liang.wang on 18/9/14.
 */
import React from 'react'
import Component from '../../../utils/base/ComponentAlert'
import {View} from 'ea-react-dm-v14'
import {GenerateCodeControl} from '../../../../../controller/Index'
import FileTree from './FileTree'
import {Row, Col, Button, Spin} from 'antd'
import {CodeEditor} from '../../../utils/index'
import FrwkUtil from '../../../utils/util/FrwkUtil'
import QueueAnim from 'rc-queue-anim'

@View(GenerateCodeControl)
export default class GenerateCodeManage extends Component {

    static defaultProps = {
        tabIndex: '1',
        groupId: 0,
        projectId: 0,
        changeActiveKey: function () {
        }
    }

    constructor(props, context) {
        super(props, context)
        this.modelName = 'CodeModel'
        this.searchInfo = this.modelName + '.searchInfo'
        this.objName = this.modelName + '.dbinfo'
        this.groupId = props.groupId
        this.state = {
            tabIndex: props.tabIndex,
            projectId: props.projectId,
            submitDisabled: false,
            submitLoading: false,
            content: ''
        }
        setTimeout(() => {
            this.initData()
        }, 1500)
    }

    initData() {
        this.fileTreeOnExpand()
        //this.createCode(this.props.tabIndex)
    }

    componentWillReceiveProps(nextProps) {
        const {groupId, projectId, tabIndex} = nextProps
        this.groupId = groupId
        if (this.state.projectId != nextProps.projectId || this.state.tabIndex != tabIndex) {
            this.setState({projectId, tabIndex})
        }
    }

    generateCode = () => {
        FrwkUtil.fetch.fetchPost('/code/generate', {projectId: this.state.projectId}, this, (data, _this) => {
                if (data.code === 200) {
                    _this.setState({
                        submitTitle: '代码生成中....',
                        submitLoading: false
                    }, () => {
                        setTimeout(() => {
                            this.fileTreeOnExpand()
                        }, 1500)
                    })
                    _this.showSuccessMsg('生成代码完成!!!')
                } else if (data.code === 500) {
                    _this.showErrorsNotification('生成代码失败!!! ' + data.msg)
                    _this.setState({
                        submitLoading: false
                    })
                }
            }
        )
    }

    generate = () => {
        this.setState({
            submitTitle: '代码生成中，请稍后...',
            submitLoading: true
        }, () => {
            try {
                setTimeout(() => {
                    this.generateCode()
                }, 2000)
            } catch (e) {
                this.setState({
                    submitLoading: false
                })
            }
        })
    }

    onClickButton = e => {
        const type = e.target.value
        this.createCode(type)
    }

    createCode = type => {
        if (this.state.projectId === 0) {
            //this.showSuccessMsg('请选择项目！')
            return
        } else {
            if (type == '1') {
                this.generate()
            } else if (type == '2') {
                window.location.href = '/code/download?projectId=' + this.state.projectId
            } else if (type == '3') {
                FrwkUtil.fetch.fetchGet('/code/clearFiles', {projectId: this.state.projectId}, this, data => {
                    if (data.code == 200) {
                        this.fileTreeOnExpand()
                    }
                })
            }
        }
    }

    onSelectCallback = content => {
        if (this.state.content != content) {
            this.setState({content})
        }
    }

    fileTreeOnExpand = () => {
        if (this.fileTree) {
            this.fileTree.onExpand()
        }
    }

    render() {
        const {submitTitle, submitLoading, projectId, content, tabIndex} = this.state
        return (<div>
                <Spin spinning={submitLoading} tip={submitTitle} size='large'>
                    <Row>
                        <QueueAnim type={['right', 'right']} delay={600}>
                            <Col sm={5} key='1'>
                                <QueueAnim type={['bottom', 'right']} delay={300}>
                                    <Row key='a'>
                                        <Col span={24}>
                                            <Row>
                                                <QueueAnim type={['bottom', 'right']} delay={100}>
                                                    <Col span={8} key='a'>
                                                        <Button type='primary' value='1'
                                                                onClick={::this.onClickButton}>生成代码</Button>
                                                    </Col>
                                                    <Col span={8} key='b'>
                                                        <Button type='primary' value='2' ghost
                                                                onClick={::this.onClickButton}>下载代码</Button>
                                                    </Col>
                                                    <Col span={8} key='c'>
                                                        <Button type='danger' value='3' ghost
                                                                onClick={::this.onClickButton}>清空代码</Button>
                                                    </Col>
                                                </QueueAnim>
                                            </Row>
                                        </Col>
                                    </Row>
                                    <Row key='b'>
                                        <Col span={24}>
                                            <FileTree projectId={projectId} tabIndex={tabIndex}
                                                      createCode={::this.generate} ref={e => this.fileTree = e}
                                                      changeActiveKey={this.props.changeActiveKey}
                                                      onSelectCallback={::this.onSelectCallback}/>
                                        </Col>
                                    </Row>
                                </QueueAnim>
                            </Col>
                            <Col sm={19} key='2'>
                                <CodeEditor style={{width: '100%', height: '1000px'}} mode='java' theme='monokai'
                                            value={content}/>
                            </Col>
                        </QueueAnim>
                    </Row>
                </Spin>
            </div>
        )
    }
}
