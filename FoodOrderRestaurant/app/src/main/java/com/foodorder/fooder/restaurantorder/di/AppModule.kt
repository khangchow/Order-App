package com.foodorder.fooder.restaurantorder.di

import android.content.Context
import androidx.room.Room
import com.foodorder.fooder.restaurantorder.db.FoodDao
import com.foodorder.fooder.restaurantorder.db.OrderDao
import com.foodorder.fooder.restaurantorder.db.OrderFoodDao
import com.foodorder.fooder.restaurantorder.db.TableDao
import com.foodorder.fooder.restaurantorder.db.root.OrderFoodDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideAppContext(@ApplicationContext context: Context): Context = context
    
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): OrderFoodDatabase {
        return Room.databaseBuilder(context, OrderFoodDatabase::class.java, "OrderFoodDatabase")
            .allowMainThreadQueries()
            .fallbackToDestructiveMigrationOnDowngrade()
            .fallbackToDestructiveMigration()
            .build()
    }
    
    @Singleton
    @Provides
    fun provideFoodDao(autoReplyDatabase: OrderFoodDatabase): FoodDao {
        return autoReplyDatabase.foodDao()
    }
    
    @Singleton
    @Provides
    fun provideTableDao(autoReplyDatabase: OrderFoodDatabase): TableDao {
        return autoReplyDatabase.tableDao()
    }

    @Singleton
    @Provides
    fun provideOrderDao(autoReplyDatabase: OrderFoodDatabase): OrderDao {
        return autoReplyDatabase.orderDao()
    }

    @Singleton
    @Provides
    fun provideOrderFoodDao(autoReplyDatabase: OrderFoodDatabase): OrderFoodDao {
        return autoReplyDatabase.orderFoodDao()
    }
}