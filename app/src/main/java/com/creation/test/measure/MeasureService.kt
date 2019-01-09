package com.creation.test.measure

import android.app.Application

class MeasureService private constructor(private val impl: IMeasureService): IMeasureService by impl {
    companion object {
        private var instance: MeasureService = MeasureService(MeasureServiceImpl())

        fun registor(impl: IMeasureService) {
            instance = MeasureService(impl)
        }

        fun instance(): MeasureService {
            return instance
        }
    }
}