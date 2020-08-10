package com.yjkj.chainup.bean

import android.os.Parcel
import android.os.Parcelable
import java.util.ArrayList

data class SymbolData(
        val btc: MutableList<Symbol> = mutableListOf(),
        val eth: MutableList<Symbol> = mutableListOf(),
        val usdt: MutableList<Symbol> = mutableListOf()) {


    class Symbol(var name: String ?= "",
                 var key: String ?= "",
                 var dept: ArrayList<String> = ArrayList(),
                 var pricePrecision: Int,
                 var volumePrecision: Int,
                 var isShow: Int = 1) : Parcelable {

        constructor(parcel: Parcel) : this(
                parcel.readString(),
                parcel.readString(),
                parcel.createStringArrayList() as ArrayList<String>,
                parcel.readInt(),
                parcel.readInt(),
                parcel.readInt())

        fun saveSymbol(symbol: Symbol) {
            name = symbol.name
            key = symbol.key
            dept = symbol.dept
            pricePrecision = symbol.pricePrecision
            volumePrecision = symbol.volumePrecision
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(name)
            parcel.writeString(key)
            parcel.writeStringList(dept)
            parcel.writeInt(pricePrecision)
            parcel.writeInt(volumePrecision)
            parcel.writeInt(isShow)
        }

        override fun describeContents(): Int {
            return 0
        }

        override fun toString(): String {
            return "Symbol(name='$name', key='$key', dept=$dept, pricePrecision=$pricePrecision, volumePrecision=$volumePrecision, isShow=$isShow)"
        }

        companion object CREATOR : Parcelable.Creator<Symbol> {
            override fun createFromParcel(parcel: Parcel): Symbol {
                return Symbol(parcel)
            }

            override fun newArray(size: Int): Array<Symbol?> {
                return arrayOfNulls(size)
            }
        }

    }

    override fun toString(): String {
        return "SymbolData(btc=$btc, eth=$eth, usdt=$usdt)"
    }

}
