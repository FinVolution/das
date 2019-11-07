import React, {PropTypes} from 'react'
import Common from './Common'
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider'
import Header from './Header'
import LeftDrawer from './LeftDrawer'
import {LogRegControl} from '../../../controller/Index'
//import /* withWidth,*/ {LARGE, SMALL} from 'material-ui/utils/withWidth'
import ThemeDefault from './theme-default'
import Data from './material/data'
import './material/styles.less'
import Alert from 'react-s-alert'
import {View} from 'ea-react-dm-v14'
import {PageControl} from '../../../controller/Index'
import QueueAnim from 'rc-queue-anim'
import {UserEnv} from '../utils/util/Index'
import {pageMages, CodeManage, ProjectListManage} from './Index'
import _ from 'underscore'
import Init from '../pages/init/Init'

@View([PageControl, LogRegControl])
export default class App extends Common {

    static propTypes = {
        children: PropTypes.element,
        width: PropTypes.number
    }

    constructor(props, context) {
        super(props, context)
        const screenW = window.innerWidth
        this.pages = window.DASENV && window.DASENV.isAdmin ?
            [<li key={Date.now()}><ProjectListManage/></li>] : [<li key={Date.now()}><CodeManage/></li>]
        this.state = {
            checked: false,
            displayName: '',
            navDrawerOpen: screenW < 776 ? false : true,
            width: window.innerWidth,
            show: true,
            pages: this.pages
        }
        this.initeChekck()
        this.chaekLogin()
    }

    chaekLogin() {
        window.chaekLoginTimer = window.setTimeout(() => {
            UserEnv.refresh(data => {
                this.setState({displayName: data.msg.user.userRealName})
                if (data.msg.dasLogin) {
                    window.location.href = '/#/login'
                }
            })
            window.clearTimeout(window.chaekLoginTimer)
        }, 1000)
    }

    /** @Override **/
    initeChekckCallBack = data => {
        if (data.code === 200) {
            this.setState({checked: true}, () => {
                this.loginCkeck()
            })
        } else {
            this.setState({checked: false}, () => {
            })
        }
    }

    componentWillReceiveProps(nextProps) {
        if (this.state.width !== nextProps.width) {
            this.setState({})
        }
    }

    handleChangeRequestNavDrawer() {
        this.setState({
            navDrawerOpen: !this.state.navDrawerOpen
        })
    }

    componentDidMount() {
        window.addEventListener('resize', this.handleResize)
    }

    componentWillUnmount() {
        window.removeEventListener('resize', this.handleResize)
    }

    handleResize = () => {
        const screenWidth = window.innerWidth
        if (screenWidth < 776) {
            this.setState({
                navDrawerOpen: false,
                width: screenWidth
            })
        } else {
            this.setState({
                navDrawerOpen: true,
                width: screenWidth
            })
        }
    }

    onChangePage = page => {
        let {pages} = this.state
        while (pages.length > 0) {
            pages.shift()
        }
        this.setState(pages, () => {
            setTimeout(() => {
                if (_.isEmpty(pages)) {
                    pages.push(<li key={Date.now()}>{pageMages[page]}</li>)
                }
                this.setState(pages)
            }, 600)
        })
        //tems.push(<li key={Date.now()}>{item.value}</li>)
    }

    render() {
        let {navDrawerOpen, displayName, show, pages, checked} = this.state
        const paddingLeftDrawerOpen = 212
        const styles = {
            header: {
                paddingLeft: navDrawerOpen ? paddingLeftDrawerOpen : 0
            },
            container: {
                margin: '72px 20px 20px 15px',
                paddingLeft: navDrawerOpen && this.state.width > 776 ? paddingLeftDrawerOpen : 0
            }
        }
        displayName = displayName ? displayName : '客官'
        if (checked) {
            return (
                <div style={{minWidth: '1200px'}}>
                    <Alert stack={true} timeout={4000}/>
                    <MuiThemeProvider muiTheme={ThemeDefault}>
                        <div>
                            <Header styles={styles.header} key='header'
                                    handleChangeRequestNavDrawer={this.handleChangeRequestNavDrawer.bind(this)}/>
                            <LeftDrawer navDrawerOpen={navDrawerOpen} menus={Data.menus}
                                        onChangePage={::this.onChangePage}
                                        username={'你好！' + displayName}/>
                            <QueueAnim component='ul' type={['bottom', 'right']} duration={600} style={styles.container}>
                                {show ? pages : null}
                            </QueueAnim>
                        </div>
                    </MuiThemeProvider>
                </div>
            )
        } else {
            return <Init/>
        }
    }
}