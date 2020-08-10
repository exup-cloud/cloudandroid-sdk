package com.yjkj.chainup.bean

import android.text.TextUtils
import com.github.promeg.pinyinhelper.Pinyin
import java.util.*

class CountryInfo(val enName: String = "", val cnName: String = "", val dialingCode: String = "",
                  val numberCode: String = "", val showName: String = "") : BaseStickyBean
        , Comparator<CountryInfo> {

    //    //按照首字母升序排列
    override fun compare(o1: CountryInfo?, o2: CountryInfo?): Int =
            o1!!.getStickItem().compareTo(o2!!.getStickItem())

    //
    override fun getStickItem(): String {
        val language = Locale.getDefault().language
        if (language.contains("zh")) {
            return if (TextUtils.isEmpty(cnName)) {
                "-"
            } else {
                Pinyin.toPinyin(cnName, "").substring(0, 1).toUpperCase()
            }

        } else {
            return if (TextUtils.isEmpty(enName)) {
                "-"
            } else {
                enName.substring(0, 1).toUpperCase()
            }
        }


    }

    override fun toString(): String {
        return "CountryInfo(enName='$enName', cnName='$cnName', dialingCode='$dialingCode', numberCode='$numberCode', showName='$showName')"
    }


}

