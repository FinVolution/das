/**
 * Created by liang.wang on 18/9/14.
 */
import React from 'react'
import Component from '../../utils/base/ComponentAlert'
import {Input, Modal, Tree, Alert, Tooltip} from 'antd'
import {DataUtil, FrwkUtil, UserEnv} from '../../utils/util/Index'
import './TreePanle.less'
import _ from 'underscore'


const TreeNode = Tree.TreeNode
const Search = Input.Search

export default class TreePanle extends Component {

    static defaultProps = {
        searchShow: true,
        rootShow: false, //显示根节点
        rootKey: 0,
        defaultExpandedKeys: ['team001'],
        showLine: false,
        treeUrl: 'group/tree',
        searchUrl: 'project/group',
        format: {
            tree: {title: 'group_name', key: 'id', tooltip: 'group_comment', isLeaf: false},
            leaf: {title: 'name', key: 'id', tooltip: 'app_scene', isLeaf: true}
        },
        remindItem: {menuName: '请联系管理员给当前用户添加组', menuUrl: ''},
        onSelect: () => {
        },
        onExpand: () => {
        },
        getDefaultSelected: () => {
        }
    }

    constructor(props, context) {
        super(props, context)
        this.isOneLvel = props.format.tree.isLeaf
        this.dataList = []
        this.selected = []
        this.flag = true
        this.state = {
            visible: false,
            treeData: [],
            expandedKeys: [],
            searchValue: '',
            autoExpandParent: true,
            selectedKeys: []
        }
        this.loadTree(0)
    }

    transfor = (data, format) => {
        let arr = []
        data.forEach((item) => {
            arr.push({
                title: item[format.title].toString(),
                key: item[format.title].toString(),
                id: item[format.key],
                tooltip: item[format.tooltip],
                isLeaf: format.isLeaf
            })
        })
        return arr
    }

    loadTree = (appid, func) => {
        const {treeUrl, format} = this.props
        FrwkUtil.fetch.fetchGet(treeUrl, {appid}, this, rs => {
                if (rs.code === 200) {
                    const data = this.transfor(rs.msg, format.tree)
                    if (!_.isEmpty(data)) {
                        const selectedKeys = [...data[0].key]
                        this.selected.push(data[0])
                        let state = {expandedKeys: selectedKeys, treeData: [...data]}
                        if (this.isOneLvel) {
                            state = {selectedKeys, expandedKeys: selectedKeys, treeData: [...data]}
                        } else {
                            this.loadLeaf(data[0].id, data[0])
                        }
                        this.setState(state, () => {
                            if (this.isOneLvel && this.flag) {
                                this.flag = false
                                this.props.getDefaultSelected(this.selected)
                            }
                            if (!appid || appid === 0) {
                                this.props.getDefaultSelected(this.selected)
                            }
                            this.generateList(data)
                        })
                        func && func(data)
                    } else {
                        this.setState({visible: true})
                    }
                } else {
                    this.showErrorsNotification(rs.msg)
                }
            }
        )
    }

    loadData = treeNode => {
        return new Promise((resolve) => {
            if (treeNode.props.children) {
                resolve()
                return
            }
            this.loadLeaf(treeNode.props.dataRef.id, treeNode)
            resolve()
        })
    }

    loadLeaf = (id, treeNode) => {
        let {selectedKeys} = this.state
        const {searchUrl, format} = this.props
        const url = searchUrl + '?groupId=' + id
        FrwkUtil.fetch.fetchGet(url, null, this, rs => {
                if (rs.code === 200 && rs.msg.length > 0) {
                    const data = this.transfor(rs.msg, format.leaf)
                    if (!this.isOneLvel && treeNode.props && treeNode.props.dataRef) {
                        treeNode.props.dataRef.children = data
                    }
                    if (treeNode.isLeaf == false) {
                        treeNode.children = data
                    }
                    let state = {treeData: [...this.state.treeData]}
                    if (!this.isOneLvel) {
                        selectedKeys = []
                        selectedKeys.push(data[0].key)
                        state = {selectedKeys, treeData: [...this.state.treeData]}
                    }
                    this.setState(state, () => {
                        if (this.isOneLvel == false && this.flag) {
                            this.flag = false
                            this.selected.push(data[0])
                            this.props.getDefaultSelected(this.selected)
                        }
                        this.generateList(data)
                    })
                } else {
                    this.showSuccessMsg('未找到项目！！')
                }
            }
        )
    }

    //暂停用
    renderTreeNodes = (data) => {
        return data.map((item) => {
            if (item.children) {
                return (
                    <TreeNode title={item.title} key={item.key} dataRef={item}>
                        {this.renderTreeNodes(item.children)}
                    </TreeNode>
                )
            }
            return <TreeNode {...item} dataRef={item}/>
        })
    }

    onSelect = (selectedKeys, info) => {
        this.setState({selectedKeys})
        this.props.onSelect && this.props.onSelect(selectedKeys, info, null, this)
    }

    /***********************************************************************
     * 模糊查询
     ***********************************************************************/
    createTreeNodes = data => data.map(item => {
        const {searchValue} = this.state
        const index = item.key.indexOf(searchValue)
        const beforeStr = item.key.substr(0, index)
        const afterStr = item.key.substr(index + searchValue.length)
        const title = index > -1 ? (
                <span>{beforeStr}<span style={{color: '#f50'}}>{searchValue}</span>{afterStr}</span>) :
            <span>{item.key}</span>
        if (item.children) {
            if (item.tooltip) {
                return <TreeNode key={item.key} title={<Tooltip placement='right' title={item.tooltip}>
                    <span>{title}</span>
                </Tooltip>} isLeaf={item.isLeaf} dataRef={item}>
                    {this.createTreeNodes(item.children)}
                </TreeNode>
            }
            return (
                <TreeNode key={item.key} title={title} dataRef={item}>
                    {this.createTreeNodes(item.children)}
                </TreeNode>
            )
        }
        if (item.tooltip) {
            return <TreeNode key={item.key} title={<Tooltip placement='right' title={item.tooltip}>
                <span>{title}</span>
            </Tooltip>} isLeaf={item.isLeaf} dataRef={item}/>
        }
        return <TreeNode key={item.key} title={title} isLeaf={item.isLeaf} dataRef={item}/>
    })

    generateList = data => {
        for (let i = 0; i < data.length; i++) {
            const node = data[i]
            const key = node.key
            this.dataList.push({key, title: key})
            if (node.children) {
                this.generateList(node.children, node.key)
            }
        }
    }

    onExpand = (expandedKeys, node) => {
        this.setState({
            expandedKeys,
            autoExpandParent: false
        }, () => {
            this.props.onExpand(node, expandedKeys)
        })
    }

    getParentKey = (key, tree) => {
        let parentKey
        for (let i = 0; i < tree.length; i++) {
            const node = tree[i]
            if (node.children) {
                if (node.children.some(item => item.key === key)) {
                    parentKey = node.key
                } else if (this.getParentKey(key, node.children)) {
                    parentKey = this.getParentKey(key, node.children)
                }
            }
        }
        return parentKey
    }

    onChange = e => {
        const value = DataUtil.StringUtils.trim(e.target.value)
        let expandedKeys = this.dataList.map((item) => {
            if (item.key.indexOf(value) > -1) {
                return this.getParentKey(item.key, this.state.treeData)
            }
            return null
        }).filter((item, i, self) => item && self.indexOf(item) === i)
        if (expandedKeys.length === 0 && this.dataList.some(item => item.key.indexOf(value) > -1)) {
            expandedKeys = [this.props.rootKey]
        }
        this.setState({
            expandedKeys,
            searchValue: value,
            autoExpandParent: true
        })
        this.searchTreeByAppid(value)
    }

    searchTreeByAppid = appid => {
        if (!DataUtil.StringUtils.isEmpty(appid)) {
            FrwkUtil.fetch.fetchGet('/apiext/appidExist', {appid}, this, data => {
                if (data.code === 200) {
                    this.loadTree(appid, data => {
                        if (data.length === 1) {
                            const info = {node: {props: {dataRef: data[0]}}}
                            this.props.onSelect && this.props.onSelect([], info, appid, this)
                        }
                    })
                } else {
                    this.selected = []
                    this.setState({treeData: []}, () => {
                        this.props.onSelect && this.props.onSelect([], null, -1, this)
                    })
                }
            })
        } else if (DataUtil.StringUtils.isEmpty(appid)) {
            this.selected = []
            this.loadTree(0)
        }
    }

    createModel = () => {
        const {visible} = this.state
        const {remindItem} = this.props
        return <Modal title='当前用户未添加组'
                      width={900}
                      height={700}
                      visible={visible}
                      footer={null}>
            {
                remindItem.menuUrl ? <Alert message={remindItem.menuName}
                                            description={<a
                                                href={remindItem.menuUrl}
                                                target='_blank'>请查看用户手册</a>}
                                            type='info'
                                            showIcon
                /> : <Alert message={remindItem.menuName} type='info' showIcon/>
            }
        </Modal>
    }

    render() {
        const {treeData, expandedKeys, autoExpandParent, selectedKeys, searchValue} = this.state
        const {rootShow, searchShow} = this.props
        const {visible} = this.state
        if(UserEnv.isAdmin() === undefined){
            //window.location.reload()
        }
        if (visible && !UserEnv.isAdmin()) {
            return (<div>{this.createModel()}</div>)
        }
        return (
            <div className='treePanle' style={{resize: 'horizontal'}}>
                {searchShow &&
                <Search size='large' style={{marginBottom: 8}} placeholder='Search' value={searchValue}
                        onChange={::this.onChange}/>}
                <Tree showLine={this.props.showLine}
                      selectedKeys={selectedKeys}
                      expandedKeys={expandedKeys}
                      loadData={::this.loadData}
                      onSelect={::this.onSelect}
                      onExpand={::this.onExpand}
                      autoExpandParent={autoExpandParent}>
                    {
                        rootShow ? <TreeNode title='ALL TEAM' key={this.props.rootKey}>
                            {::this.createTreeNodes(treeData)}
                        </TreeNode> : ::this.createTreeNodes(treeData)
                    }
                </Tree>
            </div>)
    }
}
