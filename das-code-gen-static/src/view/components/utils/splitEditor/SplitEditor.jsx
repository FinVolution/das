import React, {Component /*,PropTypes*/} from 'react'
import {split as SplitEditor} from 'react-ace'
import './SplitEditor.less'

import 'brace/mode/mysql'
import 'brace/snippets/mysql'
import 'brace/ext/language_tools'
import 'brace/theme/monokai'

export default class SplitEditorPuls extends Component {


    static defaultProps = {
        onChangeCallback: () => {
        }
    }

    constructor(props) {
        super(props)
        this.state = {
            value: []
        }
    }

    onSelectionChange(newValue, event) {
        window.console.log('select-change', newValue)
        window.console.log('select-change-event', event)
    }

    onCursorChange(newValue, event) {
        window.console.log('cursor-change', newValue)
        window.console.log('cursor-change-event', event)
    }

    onChange(value) {
        this.setState({value})
        this.props.onChangeCallback(value)
    }

    render() {
        const {value} = this.state
        return (
            <div className='splitEditorDiv'>
                <SplitEditor mode='mysql'
                             theme='monokai'
                             width='100%' height='150px'
                             className='SplitEditor'
                             splits={1}
                             commands={['aaaa', 'bbbb']}
                             orientation='beside'
                             value={value}
                             annotations={[{row: 0, column: 2, type: 'error', text: 'Some error.'}]}
                             markers={[{
                                 startRow: 0,
                                 startCol: 2,
                                 endRow: 1,
                                 endCol: 20,
                                 className: 'error-marker',
                                 type: 'background'
                             }]}
                             onChange={::this.onChange}
                             enableLiveAutocompletion={true}
                             enableBasicAutocompletion={true}
                             enableSnippets={true}
                             name='UNIQUE_ID_OF_DIV'
                             editorProps={{$blockScrolling: true}}/>
            </div>
        )
    }
}
