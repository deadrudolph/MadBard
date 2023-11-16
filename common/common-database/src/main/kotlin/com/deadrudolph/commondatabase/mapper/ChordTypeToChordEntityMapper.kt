package com.deadrudolph.commondatabase.mapper

import com.deadrudolph.common_domain.model.ChordType
import com.deadrudolph.commondatabase.model.ChordTypeEntity

class ChordTypeToChordEntityMapper {
    operator fun invoke(chordType: ChordType): ChordTypeEntity {
        return chordType.run {
            ChordTypeEntity(
                marker = marker,
                scheme = scheme,
                chordGroup = chordGroup,
                regexCondition = regexCondition
            )
        }
    }
}
