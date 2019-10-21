import React from 'react'
import Component from '../../../utils/base/Component'
import {Input, AutoComplete} from 'antd' //Select
import './SearchBox.less'
import {View} from 'ea-react-dm-v14'
import {PageControl, UserControl} from '../../../../../controller/Index'
import _ from 'underscore'
import Immutable from 'immutable'
import {storageCode} from '../../../../../model/base/BaseModel'
import {DataUtil} from '../../../utils/util/Index'

@View([UserControl, PageControl])
export default class SearchBox extends Component {

    static defaultProps = {
        searchType: 0 //0 search 1 AutoComplete
    }

    constructor(props, context) {
        super(props, context)
        this.value = null
        this.excludeKeys = ['DAS-CONSOLE']
        this.state = {
            dataSource: ['同步', '逻辑', '物理库'],
            type: 0
        }
        let _this = this
        window.setTimeout(() => {
            const chartsdata = _this.getValueByReducers('PageModel.chartsdata')
            DataUtil.setLocalStorageData(storageCode.chartsdata, chartsdata.toJS())
            _this.setState({dataSource: ::_this.initDataSource(0)})
        }, 3000)
    }

    initDataSource(type) {
        const _this = this
        const chartsdata = DataUtil.getLocalStorageData(storageCode.chartsdata)
        const menus = DataUtil.getLocalStorageData(storageCode.loadUserMenustorageCode).msg
        let dataSource = []

        const initMenus = (dataSource) => {
            _.each(menus, function (v) {
                if (!_this.excludeKeys.includes(v.header)) {
                    dataSource.push(v.header)
                }
                _.each(v.items, function (item) {
                    dataSource.push(item.title)
                    _.each(item.children, function (child) {
                        dataSource.push(child.title)
                    })
                })

            })
            return dataSource
        }

        const initChartsdata = (dataSource) => {
            _.each(chartsdata, function (v) {
                _.each(v, function (item) {
                    dataSource.push(item.title)
                    dataSource = _.union(dataSource, item.lengendData)
                })
            })
            return dataSource
        }

        if (type == 0) {
            dataSource = initMenus(dataSource)
            this.filterChartsdata('')
        } else if (type == 1) {
            dataSource = initMenus(dataSource)
            dataSource = initChartsdata(dataSource)
        }

        dataSource = _.uniq(dataSource)
        DataUtil.setLocalStorageData(storageCode.dataSource, dataSource)
        return dataSource
    }

    selectOnChange(type) {
        if (type == this.state.type) {
            return
        }
        this.setState({
            type: type,
            dataSource: this.initDataSource(type)
        }, () => {
            if (this.value) {
                this.autoCompleteOnChange(this.value)
            }
        })
    }

    autoCompleteOnChange(value) {
        value = DataUtil.StringUtils.trim(value)
        this.value = value
        switch (this.state.type) {
            case 0:
                this.filterMenus(value)
                break
            case 1:
                this.filterALL(value)
                break
        }

        const dataSource = DataUtil.getLocalStorageData(storageCode.dataSource)
        let rs = []
        _.each(dataSource, (v) => {
            if (v.indexOf(value) > -1) {
                rs.push(v)
            }
        })
        this.setState({dataSource: rs})
    }

    filterALL(value) {
        this.filterMenus(value)
        this.filterChartsdata(value)
    }

    filterChartsdata(value) {
        const initChartsdata = function (_this) {
            const chartsdata = DataUtil.getLocalStorageData(storageCode.chartsdata)
            if (chartsdata) {
                _this.props.updateChartsdata('PageModel.chartsdata', Immutable.fromJS(chartsdata))
            }
        }

        const filterlengendData = function (value, arr) {
            for (let i in arr) {
                if (arr[i].indexOf(value) > -1) {
                    return true
                }
            }
            return false
        }

        if (!value) {
            initChartsdata(this)
            return
        }

        let chartsdata = DataUtil.getLocalStorageData(storageCode.chartsdata)
        if (!chartsdata) {
            chartsdata = this.getValueByReducers('PageModel.chartsdata').toJS()
        }

        let _chartsdata = []
        let items = []
        _.each(chartsdata, function (v) {
            _.each(v, function (item) {
                if (item.title.indexOf(value) > -1 || filterlengendData(value, item.lengendData)) {
                    items.push(item)
                }
            })
        })
        if (items.length <= 2) {
            _chartsdata.push(items)
        } else if (items.length > 2) {
            let _items = []
            _.each(items, function (v, i) {
                _items.push(v)
                if ((i + 1) % 2 == 0) {
                    _chartsdata.push(_items)
                    _items = []
                }
                if ((i + 1) % 2 != 0 && i == items.length - 1) {
                    _chartsdata.push(_items)
                }
            })
        }
        if (value.length > 0) {
            this.props.updateChartsdata('PageModel.chartsdata', Immutable.fromJS(_chartsdata))
        } else {
            initChartsdata(this)
        }
    }

    filterMenus(value) {
        const initMenus = function (_this) {
            const menus = DataUtil.getLocalStorageData(storageCode.loadUserMenustorageCode)
            if (menus.msg) {
                _this.props.updateUserMenus('UserModel.menus', Immutable.fromJS(menus.msg))
            }
        }
        if (!value) {
            initMenus(this)
            return
        }

        let menus = DataUtil.getLocalStorageData(storageCode.loadUserMenustorageCode).msg
        if (!menus) {
            menus = this.getValueByReducers('UserModel.menus').toJS()
        }
        let _menus = []
        _.each(menus, function (v) {
            if (v.header.indexOf(value) > -1) {
                _menus.push(v)
            } else {
                let items = []
                _.each(v.items, function (item) {
                    if (item.title.indexOf(value) > -1) {
                        items.push(item)
                    } else {
                        let children = []
                        _.each(item.children, function (child) {
                            if (child.title.indexOf(value) > -1) {
                                children.push(child)
                            }
                        })
                        if (children.length > 0) {
                            item.children = children
                            items.push(item)
                        }
                    }
                })
                if (items.length > 0) {
                    v.items = items
                    _menus.push(v)
                }
            }
        })
        if (value.length > 0) {
            this.props.updateUserMenus('UserModel.menus', Immutable.fromJS(_menus))
        } else {
            initMenus(this)
        }
    }

    render() {
        //const Option = Select.Option
        const InputGroup = Input.Group
        const Search = Input.Search
        const {searchType} = this.props
        if (searchType === 0) {
            return <div className='search-input-search'>
                <Search placeholder='查询....'
                        style={{width: '180px', paddingLeft: '14px'}}
                        onChange={e => this.autoCompleteOnChange(e.target.value)}/>
            </div>
        }
        return (
            <div className='search-box'>
                <InputGroup compact>
                    {/*<Select defaultValue='导航' onChange={::this.selectOnChange}>
                        <Option value={0}>导航</Option>
                        <Option value={1}>全站</Option>
                    </Select>*/}
                    <AutoComplete dataSource={this.state.dataSource}
                                  style={{width: 200}}
                                  onChange={::this.autoCompleteOnChange}
                                  placeholder='查询....'/>
                </InputGroup>
            </div>
        )
    }
}

