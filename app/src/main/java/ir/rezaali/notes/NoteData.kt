package ir.rezaali.notes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object NoteData {
    private var _NoteModel :MutableLiveData<NoteModel> = MutableLiveData()
    var noteModel: LiveData<NoteModel> = _NoteModel
    fun saveNoteModel(model: NoteModel){
        _NoteModel.postValue(model)
    }
}