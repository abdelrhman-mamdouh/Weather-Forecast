package com.example.weatherguide.favoriteScreen

import com.example.weatherguide.model.FavoriteLocation

interface OnClickListener {
    fun onClickRemove(favoriteLocation: FavoriteLocation)
    fun onClickLocationFavorite(favoriteLocation: FavoriteLocation)

}