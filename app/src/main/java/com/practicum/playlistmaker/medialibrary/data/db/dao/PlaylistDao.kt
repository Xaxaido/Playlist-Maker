package com.practicum.playlistmaker.medialibrary.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.practicum.playlistmaker.medialibrary.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.medialibrary.data.db.entity.PlaylistTrackEntity
import com.practicum.playlistmaker.medialibrary.data.db.entity.PlaylistTracksEntity
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import kotlinx.coroutines.flow.Flow

@Dao
abstract class PlaylistDao {

    @Insert(entity = PlaylistEntity::class, onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun add(playlist: PlaylistEntity)

    /**/
    @Transaction
    open suspend fun remove(playlistId: Int) {
        removePlaylistTracks(playlistId)
        removePlaylist(playlistId)
        clearTrack()
    }

    @Query("DELETE FROM playlists WHERE id = :playlistId")
    abstract suspend fun removePlaylist(playlistId: Int)

    @Query("DELETE FROM playlist_tracks WHERE playlistId = :playlistId")
    abstract suspend fun removePlaylistTracks(playlistId: Int)
    /**/

    @Transaction
    open suspend fun addTrack(
        playlist: PlaylistEntity,
        playlistTracks: PlaylistTracksEntity,
        playlistTrack: PlaylistTrackEntity,
    ) {
        addTrackToPlaylist(playlistTracks)
        updatePlaylist(playlist.id!!, playlist.tracksCount + 1)
        addPlaylistTrack(playlistTrack)
    }

    @Insert(entity = PlaylistTracksEntity::class)
    abstract suspend fun addTrackToPlaylist(track: PlaylistTracksEntity)

    @Query("UPDATE playlists SET tracksCount = :updatedCount WHERE id = :playlistId")
    abstract suspend fun updatePlaylist(playlistId: Int, updatedCount: Int)

    @Insert(entity = PlaylistTrackEntity::class, onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun addPlaylistTrack(track: PlaylistTrackEntity)
    /**/

    @Transaction
    open suspend fun removeTrack(playlist: PlaylistEntity, trackId: Long) {
        removeTrackFromPlaylist(playlist.id!!, trackId)
        updatePlaylist(playlist.id, playlist.tracksCount - 1)
        clearTrack()
    }

    @Query("DELETE FROM playlist_tracks WHERE playlistId = :playlistId AND trackId = :trackId")
    abstract suspend fun removeTrackFromPlaylist(playlistId: Int, trackId: Long)
    /**/

    @Query("SELECT EXISTS (SELECT 1 FROM playlist_tracks WHERE playlistId = :playlistId AND trackId = :trackId)")
    abstract suspend fun isTrackInPlaylist(playlistId: Int, trackId: Long): Boolean

    @Query("DELETE FROM playlists_tracks WHERE id NOT IN (SELECT DISTINCT(trackId) FROM playlist_tracks)")
    abstract suspend fun clearTrack()

    @Query("SELECT * FROM playlists WHERE id = :playlistId")
    abstract fun getPlaylist(playlistId: Int): Flow<Playlist>

    @Query("SELECT * FROM playlists ORDER BY tracksCount DESC")
    abstract fun getAllPlaylists(): Flow<List<Playlist>>

    @Query("SELECT playlists_tracks.* FROM playlists_tracks " +
            "INNER JOIN playlist_tracks ON playlists_tracks.id = playlist_tracks.trackId " +
            "WHERE playlist_tracks.playlistId = :playlistId")
    abstract suspend fun getAllPlaylistTracks(playlistId: Int): List<PlaylistTrackEntity>
}