package training.tselexx.toodyu.model

data class ModelCatEvent(
        var catEvent_cat : Int,
        var catEvent_id : Int,
        var catEvent_year : Int,
        var catEvent_month : Int,
        var catEvent_day : Int,
        var catEvent_hour : Int,
        var catEvent_minute : Int,
        var catEvent_message : String,
        var catEvent_stamp : Long
)