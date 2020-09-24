package ru.samtakoy.listtest.domain.model

enum class EmployeeStatus(id: Int, val color: Int) {

    unknown(0, 0x666666),
    favorite(1, 0x00ff00),
    usual(2, 0x0000ff),
    expert(3, 0xff0000);


    var id: Int = id
        private set(value){
            if(_table == null){ _table = HashMap() }
            _table?.put(value, this)
            field = value
        }

    companion object{

        fun getRandomly(): EmployeeStatus {
            val rndIndex = values().indices.random()
            return values().get( rndIndex )
        }

        fun getById(id: Int): EmployeeStatus?= _table!![id]

        private var _table: HashMap<Int, EmployeeStatus>? = null
    }

}