package com.deadrudolph.home.di.dependencies

import com.deadrudolph.home.di.component.HomeComponentInternal
import com.deadrudolph.home_domain.di.component.HomeDomainComponentHolder
import com.deadrudolph.home_domain.domain.usecase.users.GetAllUsersUseCase

internal class DependenciesImpl : HomeComponentInternal.Dependencies {

    override val getAllUsersUseCase: GetAllUsersUseCase
        get() = HomeDomainComponentHolder.get().getAllUsersUseCase()
}