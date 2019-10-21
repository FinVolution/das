import React from 'react'

export ServerGroupManage from '../pages/server/ServerGroupManage'
export GroupManage from '../pages/group/GroupManage'
export MemberManage from '../pages/member/MemberManage'
export DatabaseManage from '../pages/databaseManage/DatabaseManage'
export DataBaseGroupManage from '../pages/db/groupManage/DataBaseGroupManage'
export DataBaseSetManage from '../pages/db/logicDBManage/DataBaseSetManage'
export ProjectManage from '../pages/project/ProjectManage'
export ProjectListManage from '../pages/project/ProjectListManage'
export AppGroupManage from '../pages/appGroup/AppGroupManage'
export UserManage from '../pages/usemanage/UserManage'
export CodeManage from '../pages/codeManage/CodeManage'
export PublicStrategyManage from '../pages/publicStrategy/PublicStrategyManage'
export DataBaseSetSyncManage from '../pages/sync/dbset/DataBaseSetSyncManage'
export DataSearchManage from '../pages/dataSearch/DataSearchManage'
export DataSearchMain from '../pages/dataSearch/DataSearchMain'

import ServerGroupManage from '../pages/server/ServerGroupManage'
import GroupManage from '../pages/group/GroupManage'
import MemberManage from '../pages/member/MemberManage'
import DatabaseManage from '../pages/databaseManage/DatabaseManage'
import DataBaseGroupManage from '../pages/db/groupManage/DataBaseGroupManage'
import DataBaseSetManage from '../pages/db/logicDBManage/DataBaseSetManage'
import ProjectManage from '../pages/project/ProjectManage'
import ProjectListManage from '../pages/project/ProjectListManage'
import AppGroupManage from '../pages/appGroup/AppGroupManage'
import UserManage from '../pages/usemanage/UserManage'
import CodeManage from '../pages/codeManage/CodeManage'
import PublicStrategyManage from '../pages/publicStrategy/PublicStrategyManage'
import GroupSyncManage from '../pages/sync/GroupSyncManage'
import ProjecSynctManage from '../pages/sync/ProjecSynctManage'
import DataBaseSyncManage from '../pages/sync/DataBaseSyncManage'
import DataBaseSetSyncManage from '../pages/sync/dbset/DataBaseSetSyncManage'
import TransManage from '../pages/trans/TransManage'
import DataSearchMain from '../pages/dataSearch/DataSearchMain'

export const pageMages = {
    serverManage: <ServerGroupManage/>,
    groupManage: <GroupManage/>,
    memberManage: <MemberManage/>,
    databaseManage: <DatabaseManage/>,
    dataBaseGroupManage: <DataBaseGroupManage/>,
    dataBaseSetManage: <DataBaseSetManage/>,
    projectManage: <ProjectManage/>,
    projectListManage: <ProjectListManage/>,
    appGroupManage: <AppGroupManage/>,
    userManage: <UserManage/>,
    codeManage: <CodeManage/>,
    publicStrategyManage: <PublicStrategyManage/>,
    groupSyncManage: <GroupSyncManage/>,
    projectSyncManage: <ProjecSynctManage/>,
    dataBaseSyncManage: <DataBaseSyncManage/>,
    dataBaseSetSyncManage: <DataBaseSetSyncManage/>,
    transManage: <TransManage/>,
    dataSearchMain: <DataSearchMain/>
}