package net.johnnyconsole.cfcplayers.objects

import java.io.Serializable

class Player (val cfcId: Int, last: String, first: String, val expiry: String,
              city: String, province: String, val fideID: Int, val regular: Int,
              val quick: Int, val updated: String): Serializable {
    val name: String
    val cityProvince: String

    init {
        name = if(last.isEmpty() || last == ".") {
            first
        }
        else {
            "$last, $first"
        }

        cityProvince = if(city.isEmpty() && province.isEmpty()) {
            "Unknown"
        }
        else if(province.isEmpty()) {
            city
        }
        else {
            "$city, $province"
        }
    }

}