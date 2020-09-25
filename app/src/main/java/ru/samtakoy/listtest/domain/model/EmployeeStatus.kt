package ru.samtakoy.listtest.domain.model

enum class EmployeeStatus(val id: Int, val color: Int) {

    unknown(0, 0x666666),
    favorite(1, 0x00ff00),
    usual(2, 0x0000ff),
    expert(3, 0xff0000);

    companion object{

        fun getRandomly(): EmployeeStatus {
            val rndIndex = values().indices.random()
            return values().get( rndIndex )
        }

        fun getById(id: Int): EmployeeStatus?{

            if(_table == null){
                initTable();
            }
            return _table!![id]
        }

        private inline fun initTable(){
            if(_table == null) {
                _table = HashMap()
                for (e in values()) {
                    _table!![e.id] = e
                }
            }
        }

        private var _table: HashMap<Int, EmployeeStatus>? = null
    }

}