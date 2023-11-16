package com.deadrudolph.commondatabase.type_converter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.deadrudolph.commondatabase.model.ChordBlockEntity
import com.deadrudolph.commondatabase.model.ChordEntity
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

@ProvidedTypeConverter
class GeneralTypeConverter(
    private val moshi: Moshi
) {

    // region Chord
    @TypeConverter
    fun toChord(value: String?): ChordEntity? {
        return value?.let {
            moshi.adapter(ChordEntity::class.java).fromJson(value)
        }
    }

    @TypeConverter
    fun fromChord(value: ChordEntity?): String? {
        return value?.let {
            moshi.adapter(ChordEntity::class.java).toJson(value)
        }
    }
    //endregion

    // region Chord
    @TypeConverter
    fun toListOfChords(value: String?): List<ChordEntity>? {
        return value?.let {
            val type = Types.newParameterizedType(List::class.java, ChordEntity::class.java)
            val adapter = moshi.adapter<List<ChordEntity>>(type)
            adapter.fromJson(value)
        }
    }

    @TypeConverter
    fun fromListOfChords(value: List<ChordEntity>?): String? {
        return value?.let {
            val type = Types.newParameterizedType(List::class.java, ChordEntity::class.java)
            val adapter = moshi.adapter<List<ChordEntity>>(type)
            adapter.toJson(value)
        }
    }
    //endregion

    @TypeConverter
    fun toChordBlockEntity(value: String?): List<ChordBlockEntity>? {
        return value?.let {
            val type = Types.newParameterizedType(
                List::class.java, ChordBlockEntity::class.java
            )
            val adapter = moshi.adapter<List<ChordBlockEntity>>(type)
            adapter.fromJson(value)
        }
    }

    @TypeConverter
    fun fromChordBlockEntity(value: List<ChordBlockEntity>?): String? {
        return value?.let {
            val type = Types.newParameterizedType(
                List::class.java,
                ChordBlockEntity::class.java
            )
            val adapter = moshi.adapter<List<ChordBlockEntity>>(type)
            adapter.toJson(value)
        }
    }

    // region Chord
    @TypeConverter
    fun toListOfInts(value: String?): List<Int>? {
        return value?.let {
            val type = Types.newParameterizedType(List::class.java, Int::class.javaObjectType)
            val adapter = moshi.adapter<List<Int>>(type)
            adapter.fromJson(value)
        }
    }

    @TypeConverter
    fun fromListOfInts(value: List<Int>?): String? {
        return value?.let {
            val type = Types.newParameterizedType(List::class.java, Int::class.javaObjectType)
            val adapter = moshi.adapter<List<Int>>(type)
            adapter.toJson(value)
        }
    }
    //endregion
}
