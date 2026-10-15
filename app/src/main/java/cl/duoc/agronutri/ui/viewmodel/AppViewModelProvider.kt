package cl.duoc.agronutri.ui.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cl.duoc.agronutri.AgroNutriApp

fun CreationExtras.app(): AgroNutriApp =
    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AgroNutriApp

/** Fábrica única de ViewModels (inyección manual desde AppContainer). */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            MainViewModel(app().container.repository, app().container.networkMonitor)
        }
        initializer {
            PrediosViewModel(app().container.repository)
        }
        initializer {
            RegistroViewModel(createSavedStateHandle(), app(), app().container.repository)
        }
        initializer {
            HistorialViewModel(app().container.repository)
        }
        initializer {
            RecomendacionViewModel(createSavedStateHandle(), app().container.repository)
        }
    }
}
