package com.deadrudolph.tuner.model.settings

import androidx.annotation.StringRes
import com.deadrudolph.tuner.R
import com.deadrudolph.tuner.view.components.SelectOption

enum class NotationOption(
    @StringRes override val labelRes: Int
) : SelectOption<NotationOption> {
    A_B_C(R.string.notation_a_b_c),
    DO_RE_MI(R.string.notation_do_re_mi);
}
