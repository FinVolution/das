/**
 * Created by liang.wang on 17/9/7.
 */
import React, {Component} from 'react'
import './TbodyRow.less'
import {Button} from 'antd'

export default class TradeRow extends Component {

    constructor(props, context) {
        super(props, context)
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

    render() {
        const _this = this
        const list = this.props.list
        const columnInfo = this.props.columnInfo
        let rowNo = 1
        let trList = []
        {
            list && list.map((ele) => {
                let rowColor = rowNo % 2 == 0 ? 'row-color-odd' : 'row-color-eve'
                rowNo++
                trList.push(<tr className={rowColor} >
                    {
                        columnInfo && columnInfo.get('column').map((item, i) => {
                            if (item.get('button')) {
                                return <td style={{
                                    width: item.get('width') + '%',
                                    paddingTop: '0px',
                                    paddingBottom: '0px'
                                }}
                                           className={item.get('classNameColumn')}
                                           key={i}>{_this.createButton(item, ele)}
                                </td>
                            }

                            item = item.toJS()
                            return <td style={{width: item.width + '%'}} className={item.classNameColumn}
                                       key={i}>{item.key ? ele.get(item.key) ? ele.get(item.key) : '-' : '-'}
                            </td>
                        })
                    }
                </tr>)
            })
        }

        return (trList)
    }
}