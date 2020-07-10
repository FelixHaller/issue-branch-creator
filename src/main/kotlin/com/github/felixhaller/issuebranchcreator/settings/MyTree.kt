package com.github.felixhaller.issuebranchcreator.settings

class MyTree: com.intellij.openapi.actionSystem.DataProvider{
    override fun getData(dataId: String): Any? {
        if (dataId == "Vcs.Log.Branches") {
            return "branch-1234"
        }
        return null
    }

}