/**
 * Created by liang.wang on 18/10/29.
 */
import React from 'react'
import Component from '../base/Component'
import {DatePicker} from 'antd'
import moment from 'moment'

export default class DatePickerPuls extends Component {

    static defaultProps = {
        valueLink: '',
        value: null,
        dateFormat: 'YYYY-MM-DD',
        onChangeCallback: () => {
        }
    }

    constructor(props, context) {
        super(props, context)
        const {value} = props
        this.state = {value}
    }

    componentWillReceiveProps(nextProps) {
        const {value} = nextProps
        if (value != this.state.value) {
            this.setState({value})
        }
    }

    onChange(date, dateString) {
        this.setValueByReducers(dateString)
    }

    render() {
        const {dateFormat} = this.props
        let {value} = this.state
        if(!value || value.length < 10){
            value = moment().format(dateFormat)
        }
        return <DatePicker onChange={::this.onChange} value={moment(value, dateFormat)}/>
    }
}