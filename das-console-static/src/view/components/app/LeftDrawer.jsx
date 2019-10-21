import React, {PropTypes} from 'react'
import Component from '../utils/base/ComponentAlert'
import Drawer from 'material-ui/Drawer'
import {spacing, typography} from 'material-ui/styles'
import {white, blue600} from 'material-ui/styles/colors'
import {Sidebar} from 'react-adminlte-dash'
import './LeftDrawer.less'
import {View} from 'ea-react-dm-v14'
import {PageControl, UserControl} from '../../../controller/Index'
import QueueAnim from 'rc-queue-anim'
//import Immutable from 'immutable'
import {DataUtil} from '../utils/util/Index'
import {storageCode} from '../../../model/base/BaseModel'
import {actionType} from '../../../constants/action-type'
import SearchBox from './material/components/SearchBox'
//import {pages} from './Index'

@View([UserControl, PageControl])
export default class LeftDrawer extends Component {

    static propTypes = {
        navDrawerOpen: PropTypes.bool,
        menus: PropTypes.array,
        username: PropTypes.string
    }

    static defaultProps = {
        onChangePage: () => {
        }
    }

    constructor(props, context) {
        super(props, context)
        let loginfo = DataUtil.getLocalStorageData(actionType.LOGIN_ACCOUNT)
        loginfo = {accountId: 'accountId', email: 'email'}
        if (!loginfo.email) {
            window.location.href = window.location.origin + '#/login'
        }
        this.props.loadUserMenus({accountId: loginfo.email}, this, (_this, data) => {
            DataUtil.setLocalStorageData(storageCode.loadUserMenustorageCode, data)
        })

        this.state = {
            menus: [],
            open: false,
            leftDrawer:null
        }
    }

    componentWillReceiveProps(nextProps) {
        this.setState({open: nextProps.navDrawerOpen})
    }

    changePage(page) {
        let pageShow = this.getValueToJson('PageModel.pageShow')

        this.props.onChangePage && this.props.onChangePage(page)

        const initPage = (page) => {
            for (const key in pageShow) {
                if (key == page) {
                    pageShow[key] = true

                } else {
                    pageShow[key] = false
                }
            }
        }

        initPage(page)


        //this.setValueByReducers('PageModel.pageShow', Immutable.fromJS(pageShow))
    }

    createSidebarChildren_Back() {
        return this.props.usermodel.get('menus').map((item, i) => {
            item = item.toJS()
            return <Sidebar.Menu header={item.header} icon={{className: item.className ? item.className : ''}} key={i}>
                {
                    item.items.map((a, j) => {
                        return <Sidebar.Menu.Item
                            icon={{className: a.className ? a.className : ''}}
                            title={a.title}
                            key={j}
                            style={{color: 'darkturquoise'}}>
                            {
                                a.children.map((b, n) => {
                                    return <Sidebar.Menu.Item icon={{className: a.className ? b.className : ''}}
                                                              title={b.title} key={n}
                                                              onClick={() => this.changePage(b.href)}/>
                                })
                            }
                        </Sidebar.Menu.Item>
                    })
                }
            </Sidebar.Menu>
        })
    }

    createSidebarChildren() {
        let menus = this.props.usermodel.get('menus')
        if (DataUtil.is.String(menus)) {
            this.showErrorsNotification(menus)
            return
        }
        menus = menus.toJS()
        const sidebarMenus = []
        const items = []
        menus.forEach(item => {
            const _items = []
            item.items.forEach((a, j) => {
                _items.push(<Sidebar.Menu.Item icon={{className: a.className ? a.className : ''}} title={a.title}
                                               key={j} style={{color: 'darkturquoise'}}>
                    {
                        a.children.map((b, n) => {
                            return <Sidebar.Menu.Item icon={{className: a.className ? b.className : ''}}
                                                      title={b.title} key={n}
                                                      onClick={() => this.changePage(b.href)}/>
                        })
                    }
                </Sidebar.Menu.Item>)
            })
            items.push(<QueueAnim type={['left', 'right']} delay={600}>{_items}</QueueAnim>)
        })

        menus.forEach((item, i) => {
            sidebarMenus.push(<Sidebar.Menu header={item.header}
                                            icon={{className: item.className ? item.className : ''}} key={i}>
                {
                    items[i]
                }
            </Sidebar.Menu>)
        })


        return sidebarMenus
    }

    /** test **/
    ceateSisss = () => {
        const items = <QueueAnim type={['bottom', 'right']} delay={600}>
            <Sidebar.Menu.Item icon={{className: 'fa-pie-chart'}} title="Charts" key={1}>
                <Sidebar.Menu.Item title="ChartJS"/>
                <Sidebar.Menu.Item title="Morris"/>
                <Sidebar.Menu.Item title="Flot"/>
                <Sidebar.Menu.Item title="Inline Charts"/>
            </Sidebar.Menu.Item>
            <Sidebar.Menu.Item icon={{className: 'fa-laptop'}} title="UI Elements" key={2}>
                <Sidebar.Menu.Item title="General"/>
                <Sidebar.Menu.Item title="Icons"/>
                <Sidebar.Menu.Item title="Buttons"/>
                <Sidebar.Menu.Item title="Sliders"/>
                <Sidebar.Menu.Item title="Timeline"/>
                <Sidebar.Menu.Item title="Modals"/>
            </Sidebar.Menu.Item>
            <Sidebar.Menu.Item icon={{className: 'fa-edit'}} title="Forms" key={3}>
                <Sidebar.Menu.Item title="General Elements"/>
                <Sidebar.Menu.Item title="Advanced Elements"/>
                <Sidebar.Menu.Item title="Editors"/>
            </Sidebar.Menu.Item>
        </QueueAnim>
        const sidebar = <Sidebar.Menu header="实时监控" key="3">{items}</Sidebar.Menu>

        return sidebar
    }

    onRequestChange(leftDrawer) {
       return leftDrawer
    }

    handleToggle() {
        this.setState({open: !this.state.open})
    }

    render() {
        const _this = this
        const styles = {
            logo: {
                cursor: 'pointer',
                fontSize: 22,
                color: typography.textFullWhite,
                lineHeight: `${spacing.desktopKeylineIncrement}px`,
                fontWeight: typography.fontWeightLight,
                backgroundColor: blue600,
                paddingLeft: 10,
                height: 56,
            },
            menuItem: {
                color: white,
                fontSize: 14
            },
            avatar: {
                div: {
                    padding: '15px 0 20px 15px',
                    height: 45
                },
                icon: {
                    float: 'left',
                    display: 'block',
                    marginRight: 15,
                    boxShadow: '0px 0px 0px 8px rgba(0,0,0,0.2)'
                },
                span: {
                    paddingTop: 12,
                    display: 'block',
                    color: 'white',
                    fontWeight: 300,
                    textShadow: '1px 1px #444'
                }
            }
        }
        return (
            <div className='leftDrawer'>
                <Drawer docked={true}
                        width={200}
                        ref={e => _this.Drawer = e}
                        open={this.state.open}
                        disableSwipeToOpen={true}
                        onRequestChange={::this.onRequestChange(this)}
                        containerStyle={{overflow: 'hidden'}}>
                    <QueueAnim>
                        <div key='k01' style={styles.logo}>DAS-CONSOLE</div>
                        <div key='k02' style={styles.avatar.div}>
                            <span style={styles.avatar.span}>{this.props.username}</span>
                        </div>
                        <div key='k03' style={{height: '10px'}}/>
                        <div key='k04' className='scrollbar'>
                            {<SearchBox/>}
                            {this.createSidebarChildren()}
                        </div>
                    </QueueAnim>
                </Drawer>
            </div>
        )
    }
}
