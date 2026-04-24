package com.rksrtx76.hearyou.di

import com.rksrtx76.hearyou.data.repository.ConversationRepositoryImpl
import com.rksrtx76.hearyou.domain.repository.ConversationRepository
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
    abstract fun bindConversationRepository(
        conversationRepositoryImpl : ConversationRepositoryImpl
    ) : ConversationRepository
}