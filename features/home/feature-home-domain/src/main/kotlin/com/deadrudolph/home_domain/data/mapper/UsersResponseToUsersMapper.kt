package com.deadrudolph.home_domain.data.mapper

import com.deadrudolph.home_domain.data.model.response.UserResponse
import com.deadrudolph.home_domain.domain.model.response.User
import javax.inject.Inject

internal class UsersResponseToUsersMapper @Inject constructor() :
    (List<UserResponse>) -> List<User> {

    override operator fun invoke(usersResponse: List<UserResponse>) =
        usersResponse.map { user ->
            User(
                id = user.id,
                fullName = user.fullName,
                firstName = user.firstName,
                lastName = user.lastName,
                gender = user.gender,
                birthday = user.birthday,
                age = user.age,
                avatar = user.avatar,
                address = user.address,
                zip = user.zip,
                state = user.state,
                phone = user.phone,
                email = user.email,
                twitter = user.twitter,
                ssn = user.ssn,
            )
        }
}