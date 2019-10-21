import React, {Component /*,PropTypes*/} from 'react'
import {Table, Column, HeaderCell, Cell} from 'rsuite-table'
import 'rsuite-table/lib/less/index.less'
import {DataUtil} from '../util/Index'

export default class RsuiteTable extends Component {

    static defaultProps = {
        loading: false,
        dataList: []
    }

    constructor(props) {
        super(props)
        this.state = {
            loading: props.loading,
            dataList: props.dataList,
            columns: DataUtil.ObjUtils.getKeys(props.dataList)
        }
    }

    componentWillReceiveProps(nextProps) {
        const {dataList, loading} = nextProps
        if (!DataUtil.ObjUtils.isEqual(this.state.dataList, dataList)) {
            this.setState({dataList, columns: DataUtil.ObjUtils.getKeys(dataList)})
        }
        if (this.state.loading != loading) {
            this.setState({loading})
        }
    }

    initData = () => {
        const dataList = [
            {id: 1, name: 'a', email: 'a@email.com', avartar: '...'},
            {id: 2, name: 'b', email: 'b@email.com', avartar: '...'},
            {id: 3, name: 'c', email: 'c@email.com', avartar: '...'}
        ]
        const columns = ['id', 'name', 'email', 'avartar']
        this.setState({dataList, columns})
    }

    render() {
        const {dataList, columns, loading} = this.state
        return (
            <div className='rsuiteTable'>
                {/*  <Button onClick={::this.initData}>click</Button>*/}
                <Table data={dataList} virtualized height={600} rowHeight={25} loading={loading}>
                    {
                        columns && columns.map((ele, index) => {
                            return <Column width={100} colSpan={20} sort fixed resizable key={index}>
                                <HeaderCell>{ele}</HeaderCell>
                                <Cell dataKey={ele}/>
                            </Column>
                        })
                    }
                </Table>
            </div>
        )
    }
}
