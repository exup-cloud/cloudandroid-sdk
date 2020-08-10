package com.yjkj.chainup.bean

data class HomePageData(val cmsAdvertList: MutableList<Banner> = mutableListOf(), val country_code: String = "",
                        val newsList: MutableList<News> = mutableListOf(), val noticeInfo: Notice,
                        val imgPath: String = "", val lang: String = "", val symbolList: MutableList<HomeSymbol> = mutableListOf()) {
    data class Banner(val id: Int, val title: String = "", val imageUrl: String = "",
                      val httpUrl: String = "", val sort: Int, val lang: String = "")

    data class News(val id: Int, val title: String = "", val indexImgUrl: String = "",
                    val author: String = "", val source: String = "", val topic: String = "",
                    val ctime: String = "", val mtime: String = "", val lang: String = "",
                    val content: String = "", var httpUrl: String = "")

    data class Notice(val id: Int, val title: String = "", val stime: String = "",
                      val ctime: String = "", val mtime: String = "", val lang: String = "",
                      val content: String = "", val httpUrl: String = "")

    data class HomeSymbol(val name: String = "", val key: String = "")
}