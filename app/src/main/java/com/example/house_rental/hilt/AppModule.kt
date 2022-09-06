package com.example.house_rental.hilt

import com.example.house_rental.data.api.ApiHelper
import com.example.house_rental.data.api.ApiService
import com.example.house_rental.data.api.RetrofitBuilder
import com.example.house_rental.data.repo.MainRepository
import com.example.house_rental.databinding.FragmentSignupBinding
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providesRepository(apiHelper: ApiHelper): MainRepository {
        return MainRepository(apiHelper)
    }

    @Singleton
    @Provides
    fun providesApiHelper(apiService: ApiService): ApiHelper {
        return ApiHelper(apiService)
    }

    @Singleton
    @Provides
    fun providesApiService(): ApiService {
        return RetrofitBuilder.apiService
    }

}