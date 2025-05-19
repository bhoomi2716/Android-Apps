package com.dev.meditation.model

import android.os.Parcel
import android.os.Parcelable
import com.example.meditation.R
import io.realm.RealmObject

open class ModelMyFragment() : RealmObject(), Parcelable{
    var id : Int = 0
    var songTitle : String = "No Song Selected"
    var songDuration : String = "00:00"
    var songLyrics: Int = R.raw.focus_attention_music
    var songType: String = ""  // "meditate" or "sleep"

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        songTitle = parcel.readString().toString()
        songDuration = parcel.readString().toString()
        songLyrics = parcel.readInt()
        songType = parcel.readString().toString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(songTitle)
        parcel.writeString(songDuration)
        parcel.writeInt(songLyrics)
        parcel.writeString(songType)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ModelMyFragment> {
        override fun createFromParcel(parcel: Parcel): ModelMyFragment {
            return ModelMyFragment(parcel)
        }

        override fun newArray(size: Int): Array<ModelMyFragment?> {
            return arrayOfNulls(size)
        }
    }
}