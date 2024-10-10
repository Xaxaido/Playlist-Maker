package com.practicum.playlistmaker.search.data.network

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.practicum.playlistmaker.common.resources.TracksSearchState
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import com.practicum.playlistmaker.search.domain.model.Track

class TracksPagingSource(
    private val repository: TracksRepository,
    private val term: String,
) : PagingSource<Int, Track>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Track> {
        val page = params.key ?: 0
        return when (val result = repository.searchTracks(term, page)) {
            is TracksSearchState.Success -> {
                val nextKey = if (result.tracks.isEmpty()) null else page + 1
                LoadResult.Page(
                    data = result.tracks,
                    prevKey = if (page == 0) null else page - 1,
                    nextKey = nextKey
                )
            }
            is TracksSearchState.Error -> {
                LoadResult.Error(Exception("Error fetching tracks: ${result.error}"))
            }
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Track>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
