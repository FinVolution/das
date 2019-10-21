/**
 * Created by liang.wang on 18/9/14.
 */
import React from 'react'
import Component from '../../../utils/base/ComponentAlert'
import {Alert, Modal, Tree} from 'antd'
import {FrwkUtil} from '../../../utils/util/Index'
import _ from 'underscore'

export default class FileTree extends Component {

    static defaultProps = {
        tabIndex: 0,
        projectId: 0,
        defaultSelectedKeys: [],
        createCode: () => {
        },
        onSelectCallback: () => {
        },
        changeActiveKey: function () {
        }
    }

    constructor(props, context) {
        super(props, context)
        this.loadCdeContentFlag = true
        this.visible = false
        this.state = {
            isNeedCreateCode: false,    //0：需要
            visible: false,
            confirmLoading: false,
            tabIndex: 0,
            projectId: props.projectId,
            defaultSelectedKeys: [],
            name: 'entitis',//entitis，tables
            entitis: [],
            tables: []
        }
    }

    componentWillReceiveProps(nextProps) {
        const {projectId, tabIndex} = nextProps
        if (tabIndex == 1) {
            if (this.state.projectId != projectId || this.state.tabIndex != tabIndex) {
                this.setState({projectId, tabIndex}, () => {
                    this.onExpand()
                })
            }
        } else {
            if (this.state.projectId != projectId || this.state.tabIndex != tabIndex) {
                this.setState({projectId, tabIndex})
            }
        }
    }

    loadFils = name => {
        let {projectId} = this.state
        const data = {projectId, name: name}
        FrwkUtil.fetch.fetchGet('/code/files', data, this, (data, _this) => {
                if (data.code === 200) {
                    if (name === 'Entity') {
                        _this.setState({'entitis': data.msg}, () => {
                            if (this.loadCdeContentFlag && !_.isEmpty(data.msg)) {
                                const defaultSelectedKeys = [...data.msg[0].data]
                                this.loadCdeContentFlag = false
                                this.setState({defaultSelectedKeys}, () => {
                                    this.loadCodeContent({projectId: projectId, name: data.msg[0].data})
                                })
                            }
                        })
                    } else {
                        _this.setState({'tables': data.msg}, () => {
                            if (this.loadCdeContentFlag && !_.isEmpty(data.msg)) {
                                const defaultSelectedKeys = [...data.msg[0].data]
                                this.loadCdeContentFlag = false
                                this.setState({defaultSelectedKeys}, () => {
                                    this.loadCodeContent({projectId: projectId, name: data.msg[0].data})
                                })
                            }
                        })
                    }
                    /*if (_.isEmpty(data.msg)) {
                        this.visible = true
                    } else {
                        this.visible = false
                    }*/
                } else if (data.code === 500) {
                    _this.showErrorsNotification(data.msg)
                }
            }
        )

    }

    onExpand() {
        if (this.state.projectId === 0) {
            this.showSuccessMsg('请选择项目！！')
            return
        }
        const {projectId} = this.state
        FrwkUtil.fetch.fetchGet('/code/count', {projectId}, this, data => {
                if (data.code === 200) {
                    if (data.msg == true) {
                        this.loadCdeContentFlag = true
                        this.loadFils('Entity')
                        this.loadFils('Table')
                        this.setState({visible: false, isNeedCreateCode: true})
                    } else {
                        this.setState({visible: true, isNeedCreateCode: false, defaultSelectedKeys: []}, () => {
                            this.props.onSelectCallback('')
                        })
                    }
                }
            }
        )

        /*        setTimeout(() => {
                    this.setState({visible: this.visible})
                }, 1000)*/
    }

    renderTreeNodes = (data) => {
        const TreeNode = Tree.TreeNode
        return data.map((item) => <TreeNode title={item.text} key={item.data} dataRef={item}/>)
    }

    onSelect = (selectedKeys, info) => {
        const data = {projectId: this.state.projectId, name: info.node.props.dataRef.data}
        this.loadCodeContent(data)
    }

    loadCodeContent = data => {
        FrwkUtil.fetch.fetchGet('/code/content', data, this, (data, _this) => {
                if (data.code === 200) {
                    _this.props.onSelectCallback(data.msg)
                } else if (data.code === 500) {
                    _this.showErrorsNotification(data.msg)
                }
            }
        )
    }

    handleOk = () => {
        const {isNeedCreateCode} = this.state
        if (isNeedCreateCode) {
            this.setState({confirmLoading: true}, () => {
                this.props.createCode()
                setTimeout(() => {
                    this.setState({visible: false, confirmLoading: false})
                }, 1000)
            })
        } else {
            this.props.changeActiveKey('2')
            this.setState({visible: false, confirmLoading: false})
        }
    }

    handleCancel = () => {
        const {isNeedCreateCode} = this.state
        this.loadCdeContentFlag = true
        if (isNeedCreateCode) {
            this.setState({visible: false, confirmLoading: false})
        } else {
            this.setState({visible: false, confirmLoading: false})
        }
    }

    createModel = () => {
        const {visible, confirmLoading} = this.state
        return <Modal title='当前项目未生成代码'
                      width={900}
                      height={700}
                      visible={visible}
                      confirmLoading={confirmLoading}
                      onOk={::this.handleOk}
                      onCancel={::this.handleCancel}>
            <Alert message='请确认并生成代码或新建实体类或查询实体'
                   type='info'
                   showIcon/>
        </Modal>
    }

    render() {
        const {entitis, tables, defaultSelectedKeys, visible} = this.state
        const TreeNode = Tree.TreeNode
        if (visible) {
            return (<div>{this.createModel()}</div>)
        }
        if (_.isEmpty(defaultSelectedKeys)) {
            return <div/>
        }
        return (
            <div style={{resize: 'horizontal'}}>
                {::this.createModel()}
                <Tree defaultExpandAll={true}
                      defaultSelectedKeys={defaultSelectedKeys}
                      onSelect={::this.onSelect}>
                    <TreeNode title='Entity' key='Entity'>
                        {::this.renderTreeNodes(entitis)}
                    </TreeNode>
                    <TreeNode title='Table' key='Table'>
                        {::this.renderTreeNodes(tables)}
                    </TreeNode>
                </Tree>
            </div>)
    }
}
