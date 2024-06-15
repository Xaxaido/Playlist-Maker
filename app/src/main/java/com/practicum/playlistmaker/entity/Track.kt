package com.practicum.playlistmaker.entity

import android.os.Parcel
import android.os.Parcelable

data class Track(
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: String,
    val artworkUrl100: String?,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
    val previewUrl: String?,
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(trackId)
        parcel.writeString(trackName)
        parcel.writeString(artistName)
        parcel.writeString(trackTimeMillis)
        parcel.writeString(artworkUrl100)
        parcel.writeString(collectionName)
        parcel.writeString(releaseDate)
        parcel.writeString(primaryGenreName)
        parcel.writeString(country)
        parcel.writeString(previewUrl)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<Track> {

        override fun createFromParcel(parcel: Parcel) = Track(parcel)

        override fun newArray(size: Int): Array<Track?> {
            return arrayOfNulls(size)
        }
    }
}