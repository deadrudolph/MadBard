package com.deadrudolph.home_domain.data.repository.users

import com.deadrudolph.home_domain.domain.model.response.User
import com.puls.stateutil.Result

internal interface UsersRepository {

    suspend fun getUsers(): Result<List<User>>
}