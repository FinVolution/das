import {Model} from 'ea-react-dm-v14'

@Model('PageModel')
export default class PageModel {
    static searchData = {
        dataTime: null
    }

    static pages = []

    static pageShow = {
        codeManage: true,
        pointsView: false,
        accountList: false,
        transfer: false,
        groupManage: false,
        userManage: false,
        memberManage: false,
        databaseManage: false,
        dataBaseGroupManage: false,
        logicDataBaseManage: false,
        projectManage: false,
        projectListManage: false,
        serverManage: false,
        appGroupManage: false,
        publicStrategyManage: false,
        groupSyncManage: false,
        projectSyncManage: false,
        dataBaseSyncManage: false,
        dataBaseSetSyncManage: false
    }

    static chartsdata = [
        [
            {url: '/m/rv', title: '实时监控', lengendData: ['认证成功', '详单生成', '芝麻认证通过数']},
            {url: '/m/rv3', title: '成标数据', theme: 'dark', lengendData: ['认证', '详单']}
        ],
        [
            {url: '/m/rv2', title: '金额汇总', lengendData: ['实际1234556']},
            {url: '/m/rv', title: 'APP发标', theme: 'dark', lengendData: ['实际']}
        ],
        [
            {url: '/m/rv', title: 'PC发标', lengendData: ['实际值']},
            {url: '/m/rv', title: '城市贷', theme: 'dark', lengendData: ['实际数']}
        ],
        [
            {url: '/m/rv', title: 'PC贷款', lengendData: ['实际值']},
            {url: '/m/rv', title: 'APP代款', theme: 'dark', lengendData: ['实际数']}
        ],
        [
            {url: '/m/rv', title: '闪电进入', lengendData: ['实际值']},
            {url: '/m/rv', title: '基本资料', theme: 'dark', lengendData: ['实际数']}
        ],
        [
            {url: '/m/rv', title: '认证', lengendData: ['实际值']},
            {url: '/m/rv', title: 'INS通过', theme: 'dark', lengendData: ['实际数']}
        ],
        [
            {url: '/m/rv', title: '邮件营销', lengendData: ['实际值']},
            {url: '/m/rv', title: '城市贷营销', theme: 'dark', lengendData: ['实际数']}
        ]
    ]

    static chartsdataApp = [
        [
            {url: '/m/rv', title: '闪电进入', lengendData: ['认证成功', '详单生成', '芝麻认证通过数']},
            {url: '/m/rv3', title: '基本资料', theme: 'dark', lengendData: ['认证', '详单']}
        ],
        [
            {url: '/m/rv2', title: '认证', lengendData: ['实际']},
            {url: '/m/rv', title: 'INS通过', theme: 'dark', lengendData: ['实际']}
        ],
        [
            {url: '/m/rv', title: '邮件营销', lengendData: ['实际值']},
            {url: '/m/rv', title: '用户确认数', theme: 'dark', lengendData: ['实际数']}
        ]
    ]

    static realTime = null
}