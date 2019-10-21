import React, {Component /*,PropTypes*/} from 'react'
import {Redirect, Router, Route} from 'react-router'
import {page} from 'ea-react-dm-v14'
import History from 'history/lib/createHashHistory'
import app from '../components/app/App'
import api from '../components/pages/api/Api'
import Login from '../components/logReg/Login'
import Register from '../components/logReg/Register'
import '../styles/antd.less'

class AppRouter extends Component {

    constructor(props) {
        super(props)
        // Opt-out of persistent state, not recommended.

        this.history = new History({
            queryKey: false
        })
    }

    /**
     * 页面路由总览，children为外接做入口，外接入口即为AppRouter
     */
    render() {
        return (
            <div>
                <Router history={this.history}>
                    <Route path="/login" component={Login}/>
                    <Route path="/register" component={Register}/>
                    <Route path="/app" component={app}/>
                    <Route path="/api" component={api}/>
                    <Redirect from="/" to="/app"/>
                </Router>
            </div>
        )
    }
}

export const rtools = page(AppRouter)