/**
 * Created by liang.wang on 16/10/29.
 */
import React, {PropTypes} from 'react'
import Component from '../base/Component'
import {RadioGroup, Input} from 'eagle-ui'
import {FrwkUtil, DataUtil} from '../util/Index'
import './RadioPlus.less'
import _ from 'underscore'
import classNames from 'classnames'

export default class RadioPlusOld extends Component {
    static propTypes = {
        /**
         * 是否只读
         */
        viewOnly: PropTypes.bool,
        /**
         * 是否disabled
         */
        disabled: PropTypes.bool,
        /**
         * value链接
         */
        valueLink: PropTypes.string.isRequired,

        /**
         * 初始化数据，可以更新
         */
        list: PropTypes.oneOfType([
            PropTypes.array,
            PropTypes.object
        ])
    }
    /**
     * @type {{viewOnly: boolean, disabled: boolean, param: {id: string, name: string}, valueLink: string, defaultId: null, defaultName: null, getValueCallback: RadioPlusOld.defaultProps.getValueCallback}}
     * 优先级 viewOnly ---> disabled
     * defaultChecked 优先级 defaultId > defaultName
     * valueLink <==> id
     */
    static defaultProps = {
        viewOnly: false,
        disabled: false,
        list: null,
        param: {id: 'id', name: 'name'},
        valueLink: '',
        defaultId: null,
        defaultName: null,
        getValueCallback: function () {
        }
    }

    constructor(props, context) {
        super(props, context)
        this.state = {
            defaultId: FrwkUtil.ComponentUtils.getDefaultId(this),
            disabled: props.disabled,
            viewOnly: props.viewOnly,
            list: FrwkUtil.ComponentUtils.transform(props)
        }
    }

    componentWillReceiveProps(nextProps) {
        let _state = {}
        nextProps.viewOnly != this.props.viewOnly && _.extend(_state, {viewOnly: nextProps.viewOnly})
        nextProps.disabled != this.props.disabled && _.extend(_state, {disabled: nextProps.disabled})
        nextProps.defaultId != this.props.defaultId && _.extend(_state, {defaultId: nextProps.defaultId})
        if (!DataUtil.ObjUtils.isEqual(nextProps.list, this.props.list)) {
            const list = FrwkUtil.ComponentUtils.transform(nextProps)
            _.extend(_state, {list: list})
        }
        this.setState(_state)
    }

    getValueCallback(e) {
        if (this.state.disabled || this.state.viewOnly) {
            this.setState({
                defaultId: this.state.defaultId
            })
            return
        }
        this.setState({
            defaultId: e
        })
        if (this.props.valueLink) {
            this.setValueByReducers(e)
        }
        this.props.getValueCallback && this.props.getValueCallback(e)
    }

    createRadios() {
        const list = this.state.list
        let radios = []
        if (DataUtil.is.Object(list)) {
            _.each(list, function (name, id) {
                radios.push(<Input type="radio" label={name} value={id} key={id}/>)
            })
        }
        return radios
    }

    render() {
        const _this = this
        const className = classNames('radioPlus', {
            'radioPlus-disabled': this.state.viewOnly,
            'cursor-auto': (this.state.viewOnly || this.state.disabled)
        })
        return <RadioGroup className={className} defaultChecked={this.state.defaultId}
                           getValueCallback={(e) => {
                               _this.getValueCallback(e)
                           }}>
            {this.createRadios()}
        </RadioGroup>
    }
}