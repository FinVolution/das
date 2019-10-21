import React from 'react'
import Component from '../base/Component'
import AceEditor from 'react-ace'
import CopyToClipboard from 'react-copy-to-clipboard'
import {Button, Icon, Tooltip} from 'antd'
import './CodeEditor.less'
import 'brace/mode/sql'
import 'brace/mode/java'
import 'brace/mode/json'
import 'brace/theme/monokai'

export default class CodeEditor extends Component {

    static defaultProps = {
        valueLink: '',
        value: '',
        mode: 'java',
        theme: 'monokai',
        contStyle: {height: '1000px', width: '100%'},
        style: {width: '100%', height: '300px'},
        etOptions: {
            enableBasicAutocompletion: true,
            enableLiveAutocompletion: true,
            enableSnippets: false,
            showLineNumbers: true,
            tabSize: 2
        },
        onChangeCallback: function () {
        }
    }

    constructor(props, context) {
        super(props, context)
        this.state = {
            value: props.value
        }
    }

    componentWillReceiveProps(nextProps) {
        const {value} = nextProps
        if (value != this.state.value) {
            this.setState({value})
        }
    }

    onChange = e => {
        const {valueLink, onChangeCallback, setValueByReducers} = this.props
        onChangeCallback(e)
        valueLink && setValueByReducers && setValueByReducers(valueLink, e)
    }

    render() {
        const {mode, theme, style, etOptions, contStyle} = this.props
        const {value} = this.state
        return <div className='codeEditor' style={contStyle}>
            <Tooltip placement='top' title='点击复制'>
                <CopyToClipboard className='copyToClipboard' text={this.state.value}>
                    <Button type='primary' size='large'>
                        <Icon type='copy'/>
                    </Button>
                </CopyToClipboard>
            </Tooltip>
            <AceEditor className='aceEditorstyle' mode={mode} theme={theme} name='aceEditor' style={style} fontSize={12}
                       showPrintMargin={true} showGutter={true} highlightActiveLine={true} onChange={::this.onChange}
                       value={value} etOptions={etOptions}/>
        </div>
    }
}