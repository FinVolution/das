/**
 * Created by liang.wang on 18/6/20.
 */
import React from 'react'
import Component from '../../utils/base/ComponentAlert'
import ReactHighcharts from 'react-highcharts'
import {actionType} from '../../../../constants/action-type'
import {DataUtil} from '../../utils/util/Index'
import TradUtil from '../../../../view/components/trade/common/common'
import './TradeHistoryChart.less'
import {fetch} from 'ea-react-dm-v14'
import {rtools} from '../../../pages/Index'
import {Spin} from 'antd'

export default class TradeHistoryChart extends Component {
    static defaultProps = {
        ele: {},
        value: '123'
    }

    constructor(props, context) {
        super(props, context)
        this.state = {
            loading: false,
            categories: [],
            series: [
                {
                    name: '买入',
                    data: []
                },
                {
                    name: '卖出',
                    data: []
                }
            ]
        }
    }

    loadData() {
        const _this = this
        const type = this.props.ele.type
        const code = this.props.ele.code
        const time = this.props.ele.time
        if (type && code && time) {
            this.setState({
                loading:true
            }, ()=>{
                rtools.constructor.addLoadingBar({run: () => {}, end: () => {}})
                const url = actionType.BASE_URL + '/trade/stock/buysel'
                fetch(url + '?time=' + time + '&code=' + code + '&type=' + type, {
                    method: 'GET',
                    timeout: 60000
                }).then((data) => {
                    if (data && data.code == 200) {
                        _this.initData(data.msg)
                    }
                    _this.setState({
                        loading:false
                    })
                }, (error) => {
                    _this.setState({
                        loading:false
                    }, ()=>{
                        _this.showMsg(_this.props.AlertType.error, url + ' error!!')
                    })
                    window.console.error('loadData : ' + url + ' error!!', error)
                })
            })
        }
    }

    initData(list) {
        let categories = []
        let series = [
            {
                name: '买入|次',
                data: []
            },
            {
                name: '卖出|次',
                data: []
            }
        ]
        if (list.length > 0) {
            for (let i in list) {
                const item = list[i]
                categories.push(DataUtil.Date.formatTime(item.time))
                series[0].data.push(item.buy)
                series[1].data.push(item.sel)
            }
        }
        this.setState({
            categories: categories,
            series: series,
            title: list[0].name + '(' + TradUtil.getType(this.props.ele.type) + ')'
        })
    }

    render() {
        const config = {
            column: {
                pointPadding: 0.2,
                borderWidth: 0,
                pointWidth: 30
            },
            chart: {
                type: 'column'
            },
            title: {
                text: this.state.title
            },
            xAxis: {
                categories: this.state.categories
            },
            credits: {
                enabled: false
            },
            series: this.state.series
        }
        const url = TradUtil.getStockUrl(this.props.ele)
        return (
            <div className="msg" style={{position: 'relative'}}>
                <a href={url} onMouseOver={::this.loadData} target="_blank">{this.props.value}</a>
                <div className="msg-body">
                    <Spin className="msg-spin-body" ize="large" spinning={this.state.loading}>
                        <ReactHighcharts config={config} style={{'min-width': '1200px'}}/>
                    </Spin>
                </div>
            </div>
        )
    }
}