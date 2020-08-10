package com.yjkj.chainup.bean

data class VersionData(val build: Int, val version: String = "", val force: Int,
                       val title: String = "", val content: String = "", val downloadUrl: String = "")