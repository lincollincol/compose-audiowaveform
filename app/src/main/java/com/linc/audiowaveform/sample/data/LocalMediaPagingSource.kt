package com.linc.audiowaveform.sample.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.linc.audiowaveform.sample.model.LocalAudio
import javax.inject.Inject

class LocalMediaPagingSource @Inject constructor(
    private val localMediaDataSource: LocalMediaDataSource
) : PagingSource<Int, LocalAudio>() {

    override fun getRefreshKey(state: PagingState<Int, LocalAudio>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LocalAudio> {
        return try {
            val currentPage = params.key ?: 1
            val data = localMediaDataSource.loadAudioFiles(currentPage, params.loadSize)
            val nextPage = if(data.size < params.loadSize) null else currentPage + 1
            LoadResult.Page(
                data = data.filterNotNull(),
                prevKey = null,
                nextKey = nextPage
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

}