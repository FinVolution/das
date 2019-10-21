import React, {PropTypes} from 'react'
//import {Link} from 'react-router'
import Component from '../utils/base/ComponentAlert'
import AppBar from 'material-ui/AppBar'
import IconButton from 'material-ui/IconButton'
import IconMenu from 'material-ui/IconMenu'
import MenuItem from 'material-ui/MenuItem'
import MoreVertIcon from 'material-ui/svg-icons/navigation/more-vert'
import Menu from 'material-ui/svg-icons/navigation/menu'
import ViewModule from 'material-ui/svg-icons/action/view-module'
import {white} from 'material-ui/styles/colors'
import FrwkUtil from '../utils/util/FrwkUtil'
import Alert from 'react-s-alert'
//import SearchBox from './material/components/SearchBox'
//import {DataUtil} from '../utils/util/Index'
//import {actionType} from '../../../constants/action-type'
//import Immutable from 'immutable'

export default class Header extends Component {

    constructor(props, context) {
        super(props, context)
        this.initMenuItems()
        this.state = {
            items: []
        }
    }

    initMenuItems = () => {
        FrwkUtil.fetch.fetchGet('/api/items', null, this, data => {
            if (data.code === 200) {
                this.setState({items: data.msg})
            } else {
                this.showErrorsNotification(data.msg)
            }
        })
    }

    loginOut() {
        window.location.href = '/logout'
    }

    createItems = () => {
        const {items} = this.state
        return items && items.map((i, n) => {
            return <MenuItem key={n} primaryText={i.menuName} onClick={() => {
                window.open(i.menuUrl, '_blank')
            }}/>
        })
    }

    render() {
        const {styles, handleChangeRequestNavDrawer} = this.props
        const style = {
            appBar: {
                position: 'fixed',
                top: 0,
                overflow: 'hidden',
                maxHeight: 57
            },
            menuButton: {
                marginLeft: 10
            },
            iconsRightContainer: {
                marginLeft: 20
            }
        }

        return (
            <div>
                <Alert stack={true} timeout={4000}/>
                <AppBar style={{...styles, ...style.appBar}}
                    /*title={window.DASENV.isAdmin && <SearchBox/>}*/
                        iconElementLeft={
                            <IconButton style={style.menuButton} onClick={handleChangeRequestNavDrawer}>
                                <Menu color={white}/>
                            </IconButton>
                        }
                        iconElementRight={
                            <div style={style.iconsRightContainer}>
                                <IconMenu color={white}
                                          iconButtonElement={<IconButton><ViewModule color={white}/></IconButton>}
                                          targetOrigin={{horizontal: 'right', vertical: 'top'}}
                                          anchorOrigin={{horizontal: 'right', vertical: 'top'}}>
                                    {this.createItems()}
                                </IconMenu>
                                <IconMenu color={white}
                                          iconButtonElement={<IconButton><MoreVertIcon color={white}/></IconButton>}
                                          targetOrigin={{horizontal: 'right', vertical: 'top'}}
                                          anchorOrigin={{horizontal: 'right', vertical: 'top'}}>
                                    <MenuItem primaryText='退出' onClick={::this.loginOut}/>
                                </IconMenu>
                            </div>
                        }
                />
            </div>
        )
    }
}

Header.propTypes = {
    styles: PropTypes.object,
    handleChangeRequestNavDrawer: PropTypes.func
}


