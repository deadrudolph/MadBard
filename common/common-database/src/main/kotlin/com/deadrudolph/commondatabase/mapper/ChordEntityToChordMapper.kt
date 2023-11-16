package com.deadrudolph.commondatabase.mapper

import com.deadrudolph.common_domain.model.ChordType
import com.deadrudolph.commondatabase.model.ChordTypeEntity

class ChordEntityToChordMapper {
    operator fun invoke(chordTypeEntity: ChordTypeEntity): ChordType {
        return chordTypeEntity.run {
            ChordType(
                marker = marker,
                scheme = scheme,
                chordGroup = chordGroup,
                regexCondition = regexCondition
            )
        }
    }
}
