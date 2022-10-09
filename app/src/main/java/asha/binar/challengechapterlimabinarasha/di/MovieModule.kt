package asha.binar.challengechapterlimabinarasha.di

import android.content.Context
import androidx.room.Room
import asha.binar.challengechapterlimabinarasha.data.datastore.DataStoreManager
import asha.binar.challengechapterlimabinarasha.data.network.ApiService
import asha.binar.challengechapterlimabinarasha.data.room.FavoriteDao
import asha.binar.challengechapterlimabinarasha.data.room.FavoriteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MovieModule {
    private const val BASE_URL = "https://api.themoviedb.org/"
    private const val BASE_USER_URL = "https://6254434619bc53e2347b936c.mockapi.io/"

    private val logging : HttpLoggingInterceptor
        get() {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            return httpLoggingInterceptor.apply {
                httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            }
        }
    private val client = OkHttpClient.Builder().addInterceptor(logging).build()

    @Provides
    @Singleton
    @Named("User")
    fun providesRetrofitUser(): ApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_USER_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    @Named("Movie")
    fun providesInstanceMovie(): ApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAppDatabase (@ApplicationContext appContext: Context): FavoriteDatabase {
        return Room.databaseBuilder(
            appContext,
            FavoriteDatabase::class.java,
            "Favorite.db"
        ).build()
    }

    @Provides
    @Singleton
    fun providesInstanceRoom(favoriteDatabase: FavoriteDatabase): FavoriteDao = favoriteDatabase.favoriteDao()

    @Provides
    @Singleton
    fun providesPref(@ApplicationContext appContext: Context) : DataStoreManager =  DataStoreManager(appContext)
}