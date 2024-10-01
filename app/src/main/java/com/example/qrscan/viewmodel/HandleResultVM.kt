package com.example.qrscan.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.qrscan.base.BaseViewModel
import com.example.qrscan.data.model.MultipleScanModel
import com.example.qrscan.data.model.ResultScanModel
import com.example.qrscan.data.repository.QRResultRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HandleResultVM(private val repository: QRResultRepository) : BaseViewModel() {
    private val _listHistory = MutableLiveData<List<ResultScanModel>>()
    val listHistory: LiveData<List<ResultScanModel>> = _listHistory

    val listFavorite = MutableLiveData<ArrayList<ResultScanModel>>()


    val listScannedHis = MutableLiveData<ArrayList<ResultScanModel>>()

    val listCreateHis = MutableLiveData<ArrayList<ResultScanModel>>()

    var isDeleteActiveFavorite = MutableLiveData<Boolean>()
    var isDeleteActiveScanned = MutableLiveData<Boolean>()
    var isDeleteActiveCreated = MutableLiveData<Boolean>()

    init {
        viewModelScope.launch {
            repository.listFavorite.collect {
                listFavorite.postValue(it as ArrayList<ResultScanModel>?)
            }
        }
        viewModelScope.launch {
            repository.listCreatedHistory.collect {
                listCreateHis.postValue(it as ArrayList<ResultScanModel>?)
            }
        }
        viewModelScope.launch {
            repository.listScannedHistory.collect {
                listScannedHis.postValue(it as ArrayList<ResultScanModel>?)
            }
        }
        viewModelScope.launch {
            repository.listHistory.collect {
                _listHistory.postValue(it)
            }
        }
    }

    fun insertResultScan(resultScanModel: ResultScanModel) = viewModelScope.launch {
        repository.insertItem(resultScanModel)
    }
    fun updateResultScan(id: Int, favorite: Boolean) = viewModelScope.launch {
        repository.updateFavorite(id, favorite)
    }

//favorite
    fun setDeleteAllItemFavorite(active: Boolean) {
        listFavorite.value = listFavorite.value?.map {
            it.copy(isCheck = active)
        } as ArrayList<ResultScanModel>?
    }

    fun deleteItemFavoriteActive(active: Boolean) {
        listFavorite.value = listFavorite.value?.map {
            it.copy(isDelete = active)
        } as ArrayList<ResultScanModel>?
    }

    fun setCheckItemFavoriteDelete(id: Int, selected: Boolean) {
        listFavorite.value = listFavorite.value?.map {
            if (it.id == id) {
                it.copy(isCheck = selected)
            } else {
                it
            }
        } as ArrayList<ResultScanModel>?
    }

    fun setIsDeleteFavoriteAll(active: Boolean) {
        isDeleteActiveFavorite.value = active
    }


    fun deleteFavorite(update:Boolean,favorite: Boolean, isCheck: Boolean) = viewModelScope.launch {
        repository.deleteFavorite(update,favorite, isCheck)
    }

    fun updateIsDeleteItemFavorite(isDelete: Boolean,favorite: Boolean) = viewModelScope.launch {
        repository.updateIsDeleteItemFavorite(isDelete,favorite)
    }

    fun updateIsCheckItemFavorite(isCheck: Boolean,favorite: Boolean) = viewModelScope.launch {
        repository.updateIsCheckItemFavorite(isCheck,favorite)
    }

    fun deleteFavoriteAll(update: Boolean,favorite: Boolean) = viewModelScope.launch {
        repository.deleteFavoriteAll(update,favorite)
    }

    fun updateCheckItemFavorite(id: Int, isCheck: Boolean, favorite: Boolean) =
        viewModelScope.launch {
            repository.updateCheckItemFavorite(id, isCheck, favorite)
        }


    //created
    fun setDeleteAllItemCreated(active: Boolean) {
        listCreateHis.value = listCreateHis.value?.map {
            it.copy(isCheck = active)
        } as ArrayList<ResultScanModel>?
    }

    fun deleteItemCreatedActive(active: Boolean) {
        listCreateHis.value = listCreateHis.value?.map {
            it.copy(isDelete = active)
        } as ArrayList<ResultScanModel>?
    }

    fun setCheckItemCreatedDelete(id: Int, selected: Boolean) {
        listCreateHis.value = listCreateHis.value?.map {
            if (it.id == id) {
                it.copy(isCheck = selected)
            } else {
                it
            }
        } as ArrayList<ResultScanModel>?
    }

    fun setIsDeleteCreatedAll(active: Boolean) {
        isDeleteActiveCreated.value = active
    }
    fun deleteCreated(created: Boolean, isCheck: Boolean) = viewModelScope.launch {
        repository.deleteCreated(created, isCheck)
    }

    fun updateIsDeleteItemCreated(isDelete: Boolean,created: Boolean) = viewModelScope.launch {
        repository.updateIsDeleteItemCreated(isDelete,created)
    }

    fun updateIsCheckItemCreated(isCheck: Boolean,created: Boolean) = viewModelScope.launch {
        repository.updateIsCheckItemCreated(isCheck,created)
    }

    fun deleteCreatedAll(created: Boolean) = viewModelScope.launch {
        repository.deleteCreatedAll(created)
    }

    fun updateCheckItemCreated(id: Int, isCheck: Boolean, created: Boolean) =
        viewModelScope.launch {
            repository.updateCheckItemCreated(id, isCheck, created)
        }


    //scanned
    fun setDeleteAllItemScanned(active: Boolean) {
        listScannedHis.value = listScannedHis.value?.map {
            it.copy(isCheck = active)
        } as ArrayList<ResultScanModel>?
    }

    fun deleteItemScannedActive(active: Boolean) {
        listScannedHis.value = listScannedHis.value?.map {
            it.copy(isDelete = active)
        } as ArrayList<ResultScanModel>?
    }

    fun setCheckItemScannedDelete(id: Int, selected: Boolean) {
        listScannedHis.value = listScannedHis.value?.map {
            if (it.id == id) {
                it.copy(isCheck = selected)
            } else {
                it
            }
        } as ArrayList<ResultScanModel>?
    }

    fun setIsDeleteScannedAll(active: Boolean) {
        isDeleteActiveScanned.value = active
    }
    fun deleteScanned(scanned: Boolean, isCheck: Boolean) = viewModelScope.launch {
        repository.deleteScanned(scanned, isCheck)
    }

    fun updateIsDeleteItemScanned(isDelete: Boolean,scanned: Boolean) = viewModelScope.launch {
        repository.updateIsDeleteItemScanned(isDelete,scanned)
    }

    fun updateIsCheckItemScanned(isCheck: Boolean,scanned: Boolean) = viewModelScope.launch {
        repository.updateIsCheckItemScanned(isCheck,scanned)
    }

    fun deleteScannedAll(scanned: Boolean) = viewModelScope.launch {
        repository.deleteScannedAll(scanned)
    }

    fun updateCheckItemScanned(id: Int, isCheck: Boolean, scanned: Boolean) =
        viewModelScope.launch {
            repository.updateCheckItemScanned(id, isCheck, scanned)
        }

    fun clearVMCreated(){
        setIsDeleteCreatedAll(false)
        updateIsDeleteItemCreated(isDelete = false, created = true)
        deleteItemCreatedActive(false)
        setDeleteAllItemCreated(false)
    }
    fun clearVMScanned(){
        setIsDeleteScannedAll(false)
        updateIsDeleteItemScanned(isDelete = false, scanned = true)
        deleteItemScannedActive(false)
        setDeleteAllItemScanned(false)
    }

    fun clearVMFavorite(){
        updateIsDeleteItemFavorite(isDelete = false, favorite = true)
        setIsDeleteFavoriteAll(false)
        setDeleteAllItemFavorite(false)
        deleteItemFavoriteActive(false)
        updateIsCheckItemFavorite(isCheck = false, favorite = true)
    }



}

class HandleResultFactory(private val repository: QRResultRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(HandleResultVM::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HandleResultVM(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}