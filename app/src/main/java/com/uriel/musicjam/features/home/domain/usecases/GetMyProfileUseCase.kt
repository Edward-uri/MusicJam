package com.uriel.musicjam.features.home.domain.usecases

import com.uriel.musicjam.core.network.Result
import com.uriel.musicjam.features.home.domain.entities.UserProfile
import com.uriel.musicjam.features.home.domain.repositories.HomeRepository
import javax.inject.Inject

class GetMyProfileUseCase @Inject constructor(
    private val repository: HomeRepository
) {
    suspend operator fun invoke(): Result<UserProfile> =
        repository.getMyProfile()
}
