package com.dev.meditation.model

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

class ModelBottomRecyclerViewSleepFragment(
    var toolbarImg: Int = 0,
    var img: Int = 0,
    var name : String,
    var time : String,
    var song : Int)
    : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(toolbarImg)
        parcel.writeInt(img)
        parcel.writeString(name)
        parcel.writeString(time)
        parcel.writeInt(song)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ModelBottomRecyclerViewSleepFragment> {
        override fun createFromParcel(parcel: Parcel): ModelBottomRecyclerViewSleepFragment {
            return ModelBottomRecyclerViewSleepFragment(parcel)
        }

        override fun newArray(size: Int): Array<ModelBottomRecyclerViewSleepFragment?> {
            return arrayOfNulls(size)
        }
    }
}