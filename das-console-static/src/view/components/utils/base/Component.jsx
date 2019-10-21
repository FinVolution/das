/**
 * Created by liang.wang on 18/6/29.
 */
import React, {Component} from 'react'
import _ from 'underscore'
import {DataUtil, FrwkUtil} from '../util/Index'
import Immutable from 'immutable'

export default class BaseComponent extends Component {

    constructor(props, context) {
        super(props, context)
    }

    getValueToJson() {
        let rs
        switch (arguments.length) {
            case 0:
                rs = this.getValueByReducers()
                break
            case 1:
                rs = this.getValueByReducers(arguments[0])
                break
            case 2:
                rs = this.getValueByReducers(arguments[0], arguments[1])
                break
        }

        if (_.isObject(rs)) {
            return rs.toJS()
        }
        return rs
    }

    setValueToImmutable() {
        switch (arguments.length) {
            case 1:
                this.setValueByReducers(Immutable.fromJS(arguments[0]))
                break
            case 2:
                this.setValueByReducers(arguments[0], Immutable.fromJS(arguments[1]))
                break
        }
    }


    getValueByReducers() {
        switch (arguments.length) {
            case 0:
                !this.props.valueLink && window.console.error('BaseComponent.getValueByReducers, valueLink or arguments[0] miss', this._reactInternalInstance && this._reactInternalInstance && this._reactInternalInstance.getName(), this.props)
                return FrwkUtil.store.getValueByReducers(this.props, this.props.valueLink)
            case 1:
                if (arguments[0].valueLink && _.isObject(arguments[0])) {
                    return FrwkUtil.store.getValueByReducers(arguments[0], arguments[0].valueLink)
                } else if (_.isString(arguments[0])) {
                    return FrwkUtil.store.getValueByReducers(this.props, arguments[0])
                }
                window.console.error('BaseComponent.getValueByReducers, arguments[0]', arguments[0])
                break
            case 2:
                return FrwkUtil.store.getValueByReducers(arguments[0], arguments[1])
        }
    }

    setValueByReducers() {
        switch (arguments.length) {
            case 1:
                !this.props.valueLink && window.console.error('BaseComponent.setValueByReducers, valueLink miss', this._reactInternalInstance.getName(), this.props)
                this.props.setValueByReducers(this.props.valueLink, arguments[0])
                break
            case 2:
                this.props.setValueByReducers(arguments[0], arguments[1])
                break
            case 3:
                this.props.setValueByReducers(arguments[0], arguments[1], arguments[2])
                break
            case 4:
                this.props.setValueByReducers(arguments[0], arguments[1], arguments[2], arguments[3])
                break
            case 5:
                this.props.setValueByReducers(arguments[0], arguments[1], arguments[2], arguments[3], arguments[4])
                break
        }
    }

    addItemToList = (link, item, key = '') => {
        let itemList = this.getValueToJson(link)
        if (!DataUtil.ObjUtils.includes(itemList, item, key)) {
            itemList.push(item)
        }
        this.setValueByReducers(link, Immutable.fromJS(itemList))
    }

    addListToList = (link, list, key = '') => {
        let itemList = this.getValueToJson(link)
        list.forEach(item => {
            if (!DataUtil.ObjUtils.includes(itemList, item, key)) {
                itemList.push(item)
            }
        })
        this.setValueByReducers(link, Immutable.fromJS(itemList))
    }

    updateItemToList = (link, item, key) => {
        let itemList = this.getValueToJson(link)
        itemList = DataUtil.updateTable(itemList, item, key)
        this.setValueByReducers(link, Immutable.fromJS(itemList))
    }

    deleteItemToList = (link, item, key) => {
        let itemList = this.getValueToJson(link)
        itemList = DataUtil.deteleTable(itemList, item, key)
        this.setValueByReducers(link, Immutable.fromJS(itemList))
    }

    deleteListToList = (link, list, key) => {
        let itemList = this.getValueToJson(link)
        for (var i in list) {
            itemList = DataUtil.deteleTable(itemList, list[i], key)
        }
        this.setValueByReducers(link, Immutable.fromJS(itemList))
    }

    replaceValueToItem = (valueLink, key, value) => {
        let item = this.getValueToJson(valueLink)
        item[key] = value
        this.setValueByReducers(valueLink, Immutable.fromJS(item))
    }

    replaceValuesToItem = (valueLink, items) => {
        let item = this.getValueToJson(valueLink)
        _.extend(item, items)
        this.setValueByReducers(valueLink, Immutable.fromJS(item))
    }

    componentWillUnmount() {
        this.setState = () => {
            return
        }
    }

    render() {
        return (
            <h1>重写父类render()方法</h1>
        )
    }
}