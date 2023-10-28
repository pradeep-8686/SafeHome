package com.example.safehome.polls

data class PollResponseModel(
    var raisedBy : String,
    var createdDate : String,
    var pollImage : Int? = null,
    var pollTitle : String,
    var options : ArrayList<Options>,
    var noOfVotes : Int,
    var flatNo : String
) {
    class Options (
        var optionsTitle : String,
        var optionsResult : String
    )
}