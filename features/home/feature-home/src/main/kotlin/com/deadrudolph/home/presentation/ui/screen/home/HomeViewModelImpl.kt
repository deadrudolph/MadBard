package com.deadrudolph.home.presentation.ui.screen.home

import androidx.lifecycle.viewModelScope
import com.deadrudolph.home_domain.domain.model.response.User
import com.deadrudolph.home_domain.domain.usecase.users.GetAllUsersUseCase
import com.puls.stateutil.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class HomeViewModelImpl @Inject constructor(
    private val getAllUsersUseCase: GetAllUsersUseCase,
) : HomeViewModel() {

    override val usersFlow =
        MutableStateFlow<Result<List<User>>>(Result.Loading(false))

    override fun fetchContent() {
        viewModelScope.launch {
            usersFlow.value = Result.Loading(true)
            usersFlow.value = getAllUsersUseCase()
        }
    }
}
