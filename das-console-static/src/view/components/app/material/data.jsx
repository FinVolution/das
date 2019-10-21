import React from 'react'
import Assessment from 'material-ui/svg-icons/action/assessment'
import GridOn from 'material-ui/svg-icons/image/grid-on'
import PermIdentity from 'material-ui/svg-icons/action/perm-identity'
import Web from 'material-ui/svg-icons/av/web'

const data = {
    menus: [
        {text: 'DashBoard', icon: <Assessment/>, link: '/dashboard'},
        {text: 'Form Api', icon: <Web/>, link: '/form'},
        {text: 'Table Api', icon: <GridOn/>, link: '/table'},
        {text: 'Login Api', icon: <PermIdentity/>, link: '/login'}
    ]
}

export default data
