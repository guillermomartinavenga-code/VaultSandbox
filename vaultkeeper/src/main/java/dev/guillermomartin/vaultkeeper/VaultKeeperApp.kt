package dev.guillermomartin.vaultkeeper

import android.app.Application
import android.content.Context
import dev.guillermomartin.vaultkeeper.data.local.DemoDataSeeder
import dev.guillermomartin.vaultkeeper.di.dataModule
import dev.guillermomartin.vaultkeeper.di.domainModule
import dev.guillermomartin.vaultkeeper.di.presentationModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class VaultKeeperApp : Application() {

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        // Koin starts here, not in onCreate(): VaultContentProvider#onCreate() runs before
        // Application#onCreate(), but after attachBaseContext — if Koin started in onCreate(),
        // the provider wouldn't have the DI graph available yet.
        startKoin {
            androidContext(this@VaultKeeperApp)
            modules(dataModule, domainModule, presentationModule)
        }
    }

    override fun onCreate() {
        super.onCreate()
        appScope.launch {
            get<DemoDataSeeder>().ensureSeeded()
        }
    }
}
