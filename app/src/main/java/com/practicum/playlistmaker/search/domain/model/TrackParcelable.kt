package com.practicum.playlistmaker.search.domain.model

import android.os.Parcel
import android.os.Parcelable

data class TrackParcelable(
    val id: Long,
    val trackName: String,
    val artistName: String,
    val duration: String,
    val albumCover: String?,
    val albumName: String?,
    val releaseDate: String?,
    val genre: String?,
    val country: String?,
    val previewUrl: String,
    val artistViewUrl: String,
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()  ?: "",
        parcel.readString() ?: "",
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(trackName)
        parcel.writeString(artistName)
        parcel.writeString(duration)
        parcel.writeString(albumCover)
        parcel.writeString(albumName)
        parcel.writeString(releaseDate)
        parcel.writeString(genre)
        parcel.writeString(country)
        parcel.writeString(previewUrl)
        parcel.writeString(artistViewUrl)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<TrackParcelable> {
        override fun createFromParcel(parcel: Parcel): TrackParcelable {
            return TrackParcelable(parcel)
        }

        override fun newArray(size: Int): Array<TrackParcelable?> {
            return arrayOfNulls(size)
        }
    }
}