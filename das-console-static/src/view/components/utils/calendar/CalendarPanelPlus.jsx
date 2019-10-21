/**
 * Created by liang.wang on 18/6/29.
 */
import React from 'react'
import Component from '../base/Component'
import {CalendarPanel, Input} from 'eagle-ui'
import {findDOMNode} from 'react-dom'

export default class CalendarPanelPlus extends Component {
    static defaultProps = {
        disabled: false,
        viewOnly: false,
        valueLink: '',
        placeholder: '',
        defaultDate: '',
        getValueCallback: function () {
        }
    }

    constructor(props, context) {
        super(props, context)
        this.state = {
            disabled: this.props.disabled,
            viewOnly: this.props.viewOnly,
            defaultDate: this.props.defaultDate
        }
    }

    componentWillReceiveProps(props) {
        if (props.disabled != this.state.disabled) {
            this.setState({
                disabled: props.disabled
            })
        }
        if (props.viewOnly != this.state.viewOnly) {
            this.setState({
                viewOnly: props.viewOnly
            })
        }
    }

    setDisabled() {
        this.input = arguments[0]
        if (this.input) {
            const input = findDOMNode(this.input).querySelector('input')
            input.disabled = true
        }
    }

    setReadOnly(ref, is) {
        this.input = ref
        if (this.input) {
            const input = findDOMNode(this.input).querySelector('input')
            input.readOnly = is
        }
    }

    getPlaceholder() {
        if (this.props.defaultDate) {
            return this.props.defaultDate
        }
        return this.getValueByReducers(this.props, this.props.valueLink)
    }

    setTime(time) {
        this.setState({
            defaultDate: time
        })
        this.setValueByReducers(time)
        this.props.getValueCallback && this.props.getValueCallback(this, time)
    }

    render() {
        const _this = this
        if (this.state.viewOnly) {
            return (
                <div className="inputPlus">
                    <Input type="text" value={this.getValueByReducers(this.props, this.props.valueLink)}
                           ref={(ref) => {
                               _this.setDisabled(ref)
                           }}/>
                </div>
            )
        }
        if (this.state.disabled) {
            return (
                <Input disabled={true} type="text" value=''
                       placeholder={this.getPlaceholder()}
                       ref={(ref) => {
                           _this.setDisabled(ref)
                       }}
                       icon="calendar"/>
            )
        } else {
            return (
                <CalendarPanel startDate={this.props.startDate} defaultDate={this.state.defaultDate}
                               ref={(ref) => {
                                   _this.setReadOnly(ref, true)
                               }}
                               getValueCallback={::this.setTime}>
                    <Input placeholder={this.props.placeholder} icon="calendar"/>
                </CalendarPanel>
            )
        }
    }
}
