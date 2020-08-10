package com.yjkj.chainup.contract.data.bean

class TabInfo{
    var name: String
    var index = 0
    var extras: String? = null

    constructor(name: String, index: Int) {
        this.name = name
        this.index = index
    }

    constructor(name: String, extras: String?) {
        this.name = name
        this.extras = extras
    }

}