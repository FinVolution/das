/**
 * Created by liang.wang on 19/6/14.
 */
import React from 'react'
import ManagePanle from '../base/ManagePanle'
import {View} from 'ea-react-dm-v14'
import {DataSearchControl} from '../../../../controller/Index'
import TablePanle from '../base/TablePanle'

@View(DataSearchControl)
export default class DataSearchManage extends ManagePanle {

    static propTypes = {
        tabIndex: 0
    }

    constructor(props, context) {
        super(props, context)
        this.modelName = 'DataSearchModel'
        this.objName = this.modelName + '.dataSearch'
        this.searchInfo = this.modelName + '.searchInfo'
        this.loadList = this.props.loadList
        this.state = {
            limit: 100,
            tabIndex: 0,
            loading: false
        }
    }

    componentWillReceiveProps(nextProps) {
        const {tabIndex} = nextProps
        if (this.state.tabIndex != tabIndex) {
            this.setState({tabIndex}, () => {
                if (tabIndex == 2) {
                    this.loadLogList()
                }
            })
        }
    }

    loadLogList() {
        this.loadList(this.getValueToJson(this.searchInfo), this, null, this.loadListFiler)
    }

    render() {
        const {datasearchmodel, setValueByReducers} = this.props
        return (
            <TablePanle title='' navigation=''
                        type={2}
                        lineTop={0}
                        modelName={this.modelName}
                        datasearchmodel={datasearchmodel}
                        setValueByReducers={setValueByReducers}
                        loadList={::this.loadList}/>
        )
    }
}
