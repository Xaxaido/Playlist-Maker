    package com.practicum.playlistmaker.search.data.impl

    import com.practicum.playlistmaker.common.resources.TracksSearchState
    import com.practicum.playlistmaker.common.utils.DtoConverter.toTracksList
    import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
    import com.practicum.playlistmaker.search.data.dto.TracksSearchResponse
    import com.practicum.playlistmaker.common.utils.Util
    import com.practicum.playlistmaker.search.data.dto.RetrofitSearchRequest
    import com.practicum.playlistmaker.search.domain.api.TracksRepository
    import kotlinx.coroutines.flow.Flow
    import kotlinx.coroutines.flow.flow

    class TracksRepositoryImpl(
        private val networkClient: RetrofitNetworkClient
    ) : TracksRepository {

        override fun searchTracks(term: String, page: Int): Flow<TracksSearchState> = flow {
            val response = networkClient.doRequest(RetrofitSearchRequest(term, page))

            when (response.resultCode) {
                Util.INTERNAL_SERVER_ERROR -> {
                    emit(TracksSearchState.Error(Util.INTERNAL_SERVER_ERROR, term))
                }
                Util.HTTP_OK -> {
                    val result = (response as TracksSearchResponse).results

                    if (result.isEmpty()) {
                        emit(TracksSearchState.Success(emptyList(), term))
                    } else {
                        val tracks = result
                            .filter { !it.previewUrl.isNullOrEmpty() }
                            .toTracksList()

                        emit(TracksSearchState.Success(tracks, term))
                    }
                }
                else -> {
                    emit(TracksSearchState.Error(response.resultCode, term))
                }
            }
        }
    }