package net.rhubarbdev.android.timetablegenerator.data

import androidx.lifecycle.*
import kotlinx.coroutines.*

class ItemViewModel(private val repository: ItemRepository) : ViewModel() {

    val allItems: LiveData<List<Item>> = repository.allItems.asLiveData()
    fun insert(item: Item) = viewModelScope.launch {
        repository.insert(item)
    }
    fun delete(id : Int) = viewModelScope.launch {
        repository.delete(id)
    }
}

class ItemViewModelFactory(private val repository: ItemRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ItemViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ItemViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
