package com.example.porting123.domain.service

import javax.inject.Inject

class ValidationServiceImpl @Inject constructor() : ValidationService {
    override fun validateTaskTitle(title: String): Boolean {
        return title.isNotBlank()
    }
}
