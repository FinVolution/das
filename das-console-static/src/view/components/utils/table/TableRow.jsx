/**
 * Created by liang.wang on 17/9/7.
 */
import React, {Component} from 'react'
import './TableRow.less'
import {Button, Tooltip, Popover} from 'antd'
import QueueAnim from 'rc-queue-anim'
import FrwkUtil from '../util/FrwkUtil'

export default class TradeRow extends Component {

    constructor(props, context) {
        super(props, context)
        this.state = {
            content: '',
            setnames:''
        }
    }

    createButton(ele, row) {
        return <div className='button-editor'>
            {
                ele.get('button').map((item, i) => {
                    item = item.toJS()
                    return <span style={{paddingRight: '8px'}} key={i}>
                        <Button size='small' type={item.type}
                                onClick={item.onClick.bind(this, row)}>{item.title}</Button>
                    </span>
                })
            }
        </div>
    }

    getDbInfo = app_id => {
        FrwkUtil.fetch.fetchGet('/db/getdbnames', {appid: app_id}, this, data => {
            if (data.code === 200) {
                this.setState({content: data.msg})
            }
        })
    }

    getdbsetnames = projectId => {
        FrwkUtil.fetch.fetchGet('/groupdbset/getdbsetnames', {projectId}, this, data => {
            if (data.code === 200) {
                this.setState({setnames: data.msg})
            }
        })
    }

    createTd = (item, ele, index) => {
        const {content, setnames} = this.state
        if (item.type === 'sequence') {
            return index
        }
        if (item.link) {
            return <Tooltip placement='top' title='点击查看项目配置检查结果'>
                <a href={window.location.origin + '/#/' + item.link.url + ele.get(item.key)}
                   target='_blank'>{ele.get(item.key)}</a>
            </Tooltip>
        }
        if (item.key === 'name') {
            const _content = FrwkUtil.createContent(content)
            return <Popover placement='right' title='关联物理库' content={_content} trigger='click'>
                <a onClick={() => this.getDbInfo(ele.toJS().app_id)}>{ele.get(item.key)}</a>
            </Popover>
        }
        if (item.key === 'comment') {
            const _content = FrwkUtil.createContent(setnames)
            return <Popover placement='top' title='关联逻辑库' content={_content} trigger='click'>
                <a onClick={() => this.getdbsetnames(ele.toJS().id)}>{ele.get(item.key)}</a>
            </Popover>
        }
        if (item.key) {
            if (ele.get(item.key)) {
                return ele.get(item.key)
            }
        }
        return '-'
    }

    render() {
        const _this = this
        const list = this.props.list
        const {columnInfo, search} = this.props
        let index = (search.page - 1) * search.pageSize
        let rowNo = 1
        return (
            <div className='tradeRow'>
                <div className='divTable paleTable'>
                    <QueueAnim className='divTableBody' duration={300} component='div' interval={10}
                               type={['scaleX', 'right']} delay={10}>
                        {
                            list && list.map(ele => {
                                    rowNo++
                                    index += 1
                                    return <div key={rowNo} className='divTableRow'>
                                        {
                                            columnInfo && columnInfo.get('column').map((item, i) => {
                                                if (item.get('button')) {
                                                    return <div style={{
                                                        width: item.get('width') + '%',
                                                        paddingTop: '0px',
                                                        paddingBottom: '0px',
                                                        fontSize: '11px'
                                                    }} className={item.get('classNameColumn')}
                                                                key={i}>{_this.createButton(item, ele)}
                                                    </div>
                                                }
                                                item = item.toJS()
                                                return <div className='divTableCell' key={i}
                                                            style={{width: item.width + '%', fontSize: '11px'}}>
                                                    {::this.createTd(item, ele, index)}
                                                </div>
                                            })
                                        }
                                    </div>
                                }
                            )
                        }
                    </QueueAnim>
                </div>
            </div>
        )
    }
}