import React, {PropTypes} from 'react'
import Paper from 'material-ui/Paper'
import Divider from 'material-ui/Divider'
import globalStyles from './styles'
import {Row, Col, Tooltip} from 'antd'
import {UserEnv} from '../utils/util/Index'
import ContentAdd from 'material-ui/svg-icons/content/add'
import ContentSort from 'material-ui/svg-icons/content/sort'
import FloatingActionButton from 'material-ui/FloatingActionButton'
import Component from '../utils/base/Component'
import QueueAnim from 'rc-queue-anim'
import _ from 'underscore'

export default class PageBase extends Component {
    static defaultProps = {
        zDepth: 1,
        style: null,
        addButtonShow: true,
        showDivider: true,
        checkButtonShow: false,
        title: PropTypes.string,
        navigation: PropTypes.string,
        children: PropTypes.element,
        clickBack: function () {
        },
        clickCheckBack: function () {
        }
    }

    constructor(props, context) {
        super(props, context)
        this.state = {
            addButtonShow: props.addButtonShow,
            checkButtonShow: props.checkButtonShow
        }
    }

    componentWillReceiveProps(nextProps) {
        const {addButtonShow, checkButtonShow} = nextProps
        if (this.state.addButtonShow != addButtonShow || this.state.checkButtonShow != checkButtonShow) {
            this.setState({addButtonShow, checkButtonShow})
        }
    }

    clickBack(e) {
        this.props.clickBack && this.props.clickBack(e, this)
    }

    clickCheckBack(e) {
        this.props.clickCheckBack && this.props.clickCheckBack(e, this)
    }

    render() {
        const {title, navigation, zDepth, style, showDivider} = this.props
        const {addButtonShow, checkButtonShow} = this.state
        return (
            <div>
                {navigation && <span style={globalStyles.navigation}>{navigation}</span>}
                <Paper style={style == null ? globalStyles.paper : style} zDepth={zDepth}>
                    {!_.isEmpty(title) && <Row>
                        <QueueAnim type={['bottom', 'right']} delay={600}>
                            <Col sm={6} key='a'>
                                <h3 style={globalStyles.title}>{title}</h3>
                            </Col>
                            <Col sm={12}/>
                            <Col sm={6} key='b' style={{textAlign: 'right', paddingRight: '30px'}}>
                                <div style={{display:checkButtonShow?'inline':'none', paddingRight: '10px'}}>
                                    <Tooltip placement='top' title={'查看组逻辑库' + UserEnv.getConfigCenterName() + '数据的正确性'}>
                                        <FloatingActionButton mini={true} zDepth={1}
                                                              onClick={(e) => this.clickCheckBack(e)}>
                                            <ContentSort/>
                                        </FloatingActionButton>
                                    </Tooltip>
                                </div>
                                <div style={{display:addButtonShow?'inline':'none'}}>
                                    <FloatingActionButton mini={true} zDepth={1} onClick={(e) => this.clickBack(e)}>
                                        <ContentAdd/>
                                    </FloatingActionButton>
                                </div>
                            </Col>
                        </QueueAnim>
                    </Row>}
                    <QueueAnim type={['bottom', 'right']} delay={100}>
                        {showDivider && <Divider key='a'/>}
                    </QueueAnim>
                    <div style={{clear: 'both'}}/>
                    <Row>
                        {this.props.children}
                    </Row>
                </Paper>
            </div>
        )
    }
}


