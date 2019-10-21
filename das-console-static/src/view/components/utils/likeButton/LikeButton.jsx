/**
 * Created by liang.wang on 18/10/29.
 */
import React, { Component} from 'react'
import {Button} from 'eagle-ui'
import './LikeButton.less'

export default class LikeButton extends Component {

    static defaultProps = {
        likeButton_otitle: '获取验证码',
        likeButton_title: '重获验证码',
        count: 60,
        className: 'likeButton',
        onClickCallback: function () {
        }
    }

    constructor(props, context) {
        super(props, context)
        this.state = {
            title: this.props.likeButton_otitle,
            count: this.props.count,
            liked: true
        }
    }

    handleClick() {
        const _this = this
        if (this.state.liked) {
            _this.timeHander = window.setInterval(function () {
                let count = _this.state.count
                if (_this.state.liked) {
                    _this.state.liked = false
                }
                count -= 1
                if (count < 1) {
                    _this.timeHander && window.clearInterval(_this.timeHander)
                    //console.log('liked ', _this.state.liked)
                    _this.setState({
                        count: _this.props.count,
                        title: _this.props.likeButton_title,
                        liked: true
                    })
                } else {
                    _this.setState({
                        count: count
                    })
                }
            }, 1000)
        }
    }

    closeLikeButton() {
        this.timeHander && window.clearInterval(this.timeHander)
        this.setState({
            count: this.props.count,
            title: this.props.likeButton_otitle,
            liked: true
        })
    }

    onClick(){
        if(this.state.liked){
            this.props.onClickCallback && this.props.onClickCallback(this)
        }
    }

    render() {
        let text = this.state.liked ? this.state.title : this.state.count + '秒后可重发'
        return (
            <div className={this.props.className}>
                <Button radius egSize="sm" block onClick={()=>{::this.onClick()}}
                        disabled={!this.state.liked}>{text}</Button>
            </div>)
    }
}