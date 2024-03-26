package com.example.weatherguide.favoriteScreen

import com.example.weatherguide.model.FavoriteLocation

interface OnClickListener<T> {
    fun onClickRemove(item: T)
    fun onClickLocationFavorite(item: T)
}