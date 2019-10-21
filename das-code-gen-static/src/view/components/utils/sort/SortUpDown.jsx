import React, {PropTypes} from 'react'
import Component from '../base/Component'
import {DataUtil} from '../util/Index'
import './TextArea.less'
import classNames from 'classnames'

export default class TextArea extends Component {
    static propTypes = {
        /**
         * 是否只读
         */
        sort: PropTypes.string.isRequired,
        /**
         * 是否disabled
         */
        sortType: PropTypes.bool
    }
    /**
     *
     * @type {{sort: null, sortType: boolean, onChangeCallback: TextArea.defaultProps.onChangeCallback}}
     */
    static defaultProps = {
        sort: null,
        sortType: false,
        onChangeCallback: function () {
        }
    }

    constructor(props, context) {
        super(props, context)
        this.state = {
            sort: props.sort,
            sortType: props.sortType
        }
    }

    componentWillReceiveProps(nextProps) {
        if (nextProps.disabled != this.props.disabled || nextProps.viewOnly != this.props.viewOnly) {
            this.setState({
                disabled: nextProps.disabled,
                viewOnly: nextProps.viewOnly
            })
        }
    }

    onChangeHandler(evt) {
        let val = DataUtil.StringUtils.trim(evt.target.value) || ''
        if (val.length > this.props.maxLength) {
            val = val.substr(0, this.props.maxLength)
        }
        if (this.state.content != val) {
            this.setState({
                content: val
            })
            this.setValueByReducers(val)
        }
        this.props.onChangeCallback && this.props.onChangeCallback.call(evt, val, this)
    }

    render() {
        const {maxLength, cols, rows, placeholder, className} = this.props
        const remain = maxLength - this.state.content.length
        const remainColor = this.state.content.length ? '' : 'default'
        const _className = classNames((className || ''), 'q-text-ctn')
        if (this.state.viewOnly) {
            const height = this.props.rows * 14
            return (
                <div className="textArea">
                    <span style={{height: height + 'px'}}>{this.state.content}</span>
                </div>
            )
        } else {
            return (
                <div className="q-text-wrap">
                <textarea disabled={this.state.disabled}
                          ref="description"
                          className={_className}
                          onChange={this.onChangeHandler.bind(this)}
                          rows={rows}
                          cols={cols}
                          placeholder={placeholder}
                          value={this.state.content}/>
                    <p className="q-text-remain">
                        <span className={'num ' + remainColor}><i>{remain}</i>/{maxLength}</span>
                    </p>
                </div>
            )
        }
    }
}