package com.example.qrscan.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.qrscan.base.BaseViewModel
import com.example.qrscan.data.model.MultipleScanModel
import com.example.qrscan.data.repository.MultipleScanRepository
import kotlinx.coroutines.launch

class HandleMultipleScanVM(private val repository: MultipleScanRepository) : BaseViewModel() {
    var listResult = MutableLiveData<List<MultipleScanModel>>()
    var statusDelete = MutableLiveData<Boolean>()

    init {
        viewModelScope.launch {
            repository.listMultipleScan.collect {
                listResult.postValue(it as ArrayList<MultipleScanModel>?)
            }
        }
    }

    fun setStatusDelete(status: Boolean) {
        statusDelete.value = status
    }

    fun removeCheckedItems() {
        listResult.value = listResult.value?.filterNot { it.isCheck }
    }

    fun setListChoiceAllDelete(choice: Boolean) {
        listResult.value = listResult.value?.map {
            it.copy(isCheck = choice)
        } as ArrayList<MultipleScanModel>?
    }

    fun setListActiveDelete(choice: Boolean) {
        listResult.value = listResult.value?.map {
            it.copy(isDelete = choice)
        } as ArrayList<MultipleScanModel>?

    }

    fun setCheckItemDelete(id: Int, selected: Boolean) {
        listResult.value = listResult.value?.map {
            if (it.id == id) {
                it.copy(isCheck = selected)
            } else {
                it
            }
        } as ArrayList<MultipleScanModel>?
    }

    fun getQuantityScan(): Int {
        return listResult.value?.size ?: 0
    }

    fun setListResult(setData: ArrayList<MultipleScanModel>) {
        listResult.value = setData
    }

    fun insertItem(multipleScanModel: MultipleScanModel) = viewModelScope.launch {
        repository.insertItem(multipleScanModel)
    }

    fun updateIsDeleteItem(isDelete: Boolean) = viewModelScope.launch {
        repository.updateIsDeleteItem(isDelete)
    }

    fun updateIsCheckItem(isCheck: Boolean) = viewModelScope.launch {
        repository.updateIsCheckItem(isCheck)
    }

    fun updateCheckItem(id:Int,isCheck: Boolean) = viewModelScope.launch {
        repository.updateCheckItem(id,isCheck)
    }

    fun deleteItem(isCheck: Boolean) = viewModelScope.launch {
        repository.deleteItem(isCheck)
    }

    fun deleteAll() = viewModelScope.launch {
        repository.deleteAllItem()
    }
}

class HandleMultipleSCanFactory(private val repository: MultipleScanRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(HandleMultipleScanVM::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HandleMultipleScanVM(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}