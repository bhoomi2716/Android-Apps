package com.dev.meditation.model

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

class ModelMusicPlayerViewPager(var songTitle : String, var songTime : String, var song : Int) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(songTitle)
        parcel.writeString(songTime)
        parcel.writeInt(song)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ModelMusicPlayerViewPager> {
        override fun createFromParcel(parcel: Parcel): ModelMusicPlayerViewPager {
            return ModelMusicPlayerViewPager(parcel)
        }

        override fun newArray(size: Int): Array<ModelMusicPlayerViewPager?> {
            return arrayOfNulls(size)
        }
    }
}