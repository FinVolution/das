import React from 'react'
import Component from '../../utils/base/ComponentAlert'
import {Inputlabel, InputPlus, RadioPlus} from '../../utils/index'
import {Row} from 'antd'
import {databaseTypes} from '../../../../model/base/BaseModel'
import {UserEnv} from '../../utils/util/Index'
import './EditorDBInfo.less'

export default class EditorDBInfo extends Component {

    static defaultProps = {
        modelName: 'DatabaseModel',
        item: 'dbconectInfo',
        onCheckSetState: () => {
        }
    }

    constructor(props, context) {
        super(props, context)
        this.modelName = this.props.modelName
        this.dbconectInfoLink = this.modelName + '.' + this.props.item
    }

    onSetValueByReducersCallback = val => {
        const dbconectInfo = this.getValueByReducers(this.dbconectInfoLink).toJS()
        let flag = false
        const arr = [dbconectInfo.db_type, dbconectInfo.db_address, dbconectInfo.db_port, dbconectInfo.db_user, dbconectInfo.db_password, dbconectInfo.dbname, dbconectInfo.db_catalog]
        if (arr.every(f => {
            return String(f).length > 0
        })) {
            this.props.onCheckSetState && this.props.onCheckSetState(false)
        } else {
            flag = true
            this.props.onCheckSetState && this.props.onCheckSetState(flag)
        }
        if (!val && !flag) {
            this.props.onCheckSetState && this.props.onCheckSetState(true)
        }
    }

    render() {
        const rowHeight = '570px'
        const {databasemodel, editerType, setValueByReducers} = this.props
        const dbconectInfo = this.getValueToJson(this.dbconectInfoLink)
        const _props = {setValueByReducers, databasemodel}
        return (
            <div className='addDatabaseList'>
                <Row style={{height: rowHeight}}>
                    <Inputlabel type={1} star={true} title='数据库类型' style={{paddingTop: '0px'}}>
                        <div className='addDatabaseList-radio'>
                            <RadioPlus {..._props}
                                       items={databaseTypes} valueLink={this.dbconectInfoLink + '.db_type'}
                                       disabled={editerType == 1} selectedId={dbconectInfo.db_type}/>
                        </div>
                    </Inputlabel>
                    <Inputlabel type={1} star={true} title='数据库标识符' display={editerType == 1}>
                        <InputPlus {..._props}
                                   validRules={{isDbName: true, maxLength: UserEnv.getCons().dataBaseNameMaxLength}}
                                   valueLink={this.dbconectInfoLink + '.dbname'}
                                   onSetValueByReducersCallback={::this.onSetValueByReducersCallback}
                                   defaultValue={dbconectInfo.dbname}/>
                    </Inputlabel>
                    <Inputlabel type={1} star={true} title='物理库名称' display={editerType == 1}>
                        <InputPlus {..._props}
                                   validRules={{maxLength: 50}} valueLink={this.dbconectInfoLink + '.db_catalog'}
                                   onSetValueByReducersCallback={::this.onSetValueByReducersCallback}
                                   defaultValue={dbconectInfo.db_catalog}/>
                    </Inputlabel>
                    <Inputlabel type={1} star={true} title='数据库地址'>
                        <InputPlus {..._props}
                                   validRules={{maxLength: 50}} valueLink={this.dbconectInfoLink + '.db_address'}
                                   onSetValueByReducersCallback={::this.onSetValueByReducersCallback}
                                   defaultValue={dbconectInfo.db_address}/>
                    </Inputlabel>
                    <Inputlabel type={1} star={true} title='数据库端口'>
                        <InputPlus {..._props}
                                   validRules={{isInt: true, maxLength: 10}}
                                   valueLink={this.dbconectInfoLink + '.db_port'}
                                   onSetValueByReducersCallback={::this.onSetValueByReducersCallback}
                                   defaultValue={dbconectInfo.db_port}/>
                    </Inputlabel>
                    <Inputlabel type={1} star={true} title='数据库用户'>
                        <InputPlus {..._props}
                                   validRules={{maxLength: 40}}
                                   valueLink={this.dbconectInfoLink + '.db_user'}
                                   onSetValueByReducersCallback={::this.onSetValueByReducersCallback}
                                   defaultValue={dbconectInfo.db_user}/>
                    </Inputlabel>
                    <Inputlabel type={1} star={true} title='用户名密码'>
                        <InputPlus {..._props}
                                   type='password'
                                   valueLink={this.dbconectInfoLink + '.db_password'}
                                   onSetValueByReducersCallback={::this.onSetValueByReducersCallback}
                                   defaultValue={dbconectInfo.db_password}/>
                    </Inputlabel>
                    <Inputlabel type={1} title='备注'>
                        <InputPlus {..._props}
                                   validRules={{maxLength: 200}}
                                   valueLink={this.dbconectInfoLink + '.comment'}
                                   defaultValue={dbconectInfo.comment}/>
                    </Inputlabel>
                </Row>
            </div>
        )
    }
}