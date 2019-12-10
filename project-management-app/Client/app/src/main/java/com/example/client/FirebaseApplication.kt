package com.example.client



import android.app.Application
import com.example.client.data.firebase.FirebaseSource
import com.example.client.data.repositories.UserRepository
import com.example.client.ui.auth.AuthViewModelFactory
import com.google.firebase.database.FirebaseDatabase
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

class FirebaseApplication : Application(), KodeinAware{

    override val kodein = Kodein.lazy {
        import(androidXModule(this@FirebaseApplication))

        bind() from singleton { FirebaseSource() }
        bind() from singleton { UserRepository(instance()) }
        bind() from provider { AuthViewModelFactory(instance()) }

        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

    }
}