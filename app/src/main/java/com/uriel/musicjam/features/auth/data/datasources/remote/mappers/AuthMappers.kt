package com.uriel.musicjam.features.auth.data.datasources.remote.mappers

import com.uriel.musicjam.features.auth.data.datasources.remote.dtos.AuthResponseDto
import com.uriel.musicjam.features.auth.data.datasources.remote.dtos.UserDto
import com.uriel.musicjam.features.auth.domain.entities.AuthToken
import com.uriel.musicjam.features.auth.domain.entities.User

fun AuthResponseDto.toDomain(): AuthToken {
    return AuthToken(token = this.token)
}

fun UserDto.toDomain(): User {
    return User(
        id = this.id,
        username = this.username,
        email = this.email,
        photo = this.photo
    )
}