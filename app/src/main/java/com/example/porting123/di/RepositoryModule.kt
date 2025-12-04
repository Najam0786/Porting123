package com.example.porting123.di

import com.example.porting123.data.repository.TaskRepository
import com.example.porting123.data.repository.TaskRepositoryImpl
import com.example.porting123.domain.service.ValidationService
import com.example.porting123.domain.service.ValidationServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTaskRepository(taskRepositoryImpl: TaskRepositoryImpl): TaskRepository

    @Binds
    @Singleton
    abstract fun bindValidationService(validationServiceImpl: ValidationServiceImpl): ValidationService
}
