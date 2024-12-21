package com.example.metroapp
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
// addint transation stations
class MainActivity : AppCompatActivity() {
    lateinit var startStation: Spinner
    lateinit var endStation: Spinner
    lateinit var calculate: Button
    lateinit var swap: Button
    lateinit var number: TextView
    lateinit var time: TextView
    lateinit var directionstation: TextView
    lateinit var price: TextView
    lateinit var otherprice: TextView
    lateinit var route: TextView
    lateinit var comstation: TextView



    val line1 = listOf(
        "helwan", "ain helwan", "helwan university", "wadi hof",
        "hadayek helwan", "elmasraa", "tura el-esmant", "kozzika",
        "tora el-balad", "sakanat el-maadi", "maadi", "hadayek el-maadi",
        "dar el-salam", "el-zahraa", "mar girgis", "el-malek el-saleh",
        "al-sayeda zeinab", "saad zaghloul", "sadat", "nasser", "arabi",
        "al-shohada", "ghamra", "damardash", "manshiyet el-sadr", "kobry el-qobba",
        "hammamat el-qobba", "saray el-qobba", "hadid el-zeitoun", "helmeyet el-zeitoun",
        "rabi'ya", "ain shams", "ezbet el-nakhl", "el-marg", "new el-marg"
    )
    val line2 = listOf(
        "shubra al khaimah", "koliet el-zeraa", "mezallat", "khalafawy",
        "st. teresa", "rod el-farag", "masarra", "al-shohada", "attaba",
        "mohamed naguib", "sadat", "operal", "dokki", "el bohoth",
        "cairo university", "faisal", "giza", "omm el masryeen", "sakiat mekky",
        "al monib"
    )

    val line3 = listOf(
        "adly mansour", "haykestep", "omar ibn el-khattab", "qobaa", "hesham barakat", "el-nozha", "el-shams club",
        "alf maskan", "heliopolis square", "al-ahram", "haroun", "stadium", "fair zone", "abbassiya", "abdou pasha",
        "el-geish", "bab el-shaaria", "attaba", "nasser", "maspero", "safaa hijazy", "kit kat", "sudan", "imbaba",
        "el-bohy", "el-qawmia", "ring road", "rawd al-farag corridor"
    )

    val line4 = listOf(
        "adly mansour", "haykestep", "omar ibn el-khattab", "qobaa", "hesham barakat", "el-nozha", "el-shams club",
        "alf maskan", "heliopolis square", "al-ahram", "haroun", "stadium", "fair zone", "abbassiya", "abdou pasha",
        "el-geish", "bab el-shaaria", "attaba", "nasser", "maspero", "safaa hijazy", "kit kat", "tawfiqiya", "wadi el nil",
        "gamet el dowel", "boulak el dakrour", "cairo university"
    )

    val commonStations = listOf("sadat", "al-shohada", "attaba", "nasser", "cairo university", "kit kat")

   // val allStations = mutableListOf("Please select station") + line1 + line2 + line3 + line4
    val allStations = mutableListOf("Please select station") + (line1 + line2 + line3 + line4).distinct().sorted()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startStation = findViewById(R.id.startStation)
        endStation = findViewById(R.id.endStation)
        calculate = findViewById(R.id.calculate)
        swap = findViewById(R.id.swap)
        number = findViewById(R.id.number)
        time = findViewById(R.id.time)
        directionstation = findViewById(R.id.directionstation)
        price = findViewById(R.id.price)
        otherprice = findViewById(R.id.otherprice)
        route = findViewById(R.id.route)
        comstation=findViewById(R.id.comstation)



        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, allStations)
        startStation.adapter = adapter
        endStation.adapter = adapter

    }

    fun calculate(view: View) {
        if (startStation.selectedItemPosition == 0 || endStation.selectedItemPosition == 0) {
            Toast.makeText(this, "Please select both start and end stations", Toast.LENGTH_SHORT).show()
            return
        }
        if (startStation.selectedItemPosition == endStation.selectedItemPosition ) {
            Toast.makeText(this, "you have to choose defferent stations", Toast.LENGTH_SHORT).show()
            return
        }
       //get the name of sattion from spinner
        val startStationName = startStation.selectedItem.toString()
        val endStationName = endStation.selectedItem.toString()

        val lines = listOf(line1, line2, line3, line4)
        var shortestRoute: List<String>? = null
        var direction: String = ""

        //same line
        for (line in lines) {
            if (startStationName in line && endStationName in line) {
                val startIndex = line.indexOf(startStationName)
                val endIndex = line.indexOf(endStationName)
                shortestRoute = if (startIndex < endIndex) line.slice(startIndex..endIndex)
                else line.slice(endIndex..startIndex).reversed()

                direction = if (endIndex > startIndex) line.last() else line.first()
                break
            }
        }
        // defferent lines
            for (line in lines) {
                if (startStationName !in line) continue
                val startIndex = line.indexOf(startStationName)

                for (commonStation in commonStations) {
                    if (commonStation !in line) continue
                    val transferIndex = line.indexOf(commonStation)
                    val startToTransferStation = if (startIndex < transferIndex) line.slice(startIndex..transferIndex)
                    else line.slice(transferIndex..startIndex).reversed()

                    for (tline in lines) {
                        if (commonStation in tline && endStationName in tline) {
                            val transferIndex2 = tline.indexOf(commonStation)
                            val endIndex = tline.indexOf(endStationName)
                            val transferToEndStation = if (transferIndex2 < endIndex) tline.slice(transferIndex2..endIndex)
                            else tline.slice(endIndex..transferIndex2).reversed()

                            val fullRoute = if (startToTransferStation.last() == transferToEndStation.first()) {
                                startToTransferStation + transferToEndStation.subList(1, transferToEndStation.size)
                            } else {
                                startToTransferStation + transferToEndStation
                            }
                            if (shortestRoute == null || fullRoute.size <= shortestRoute.size) {
                                shortestRoute = fullRoute
                                direction = if (endIndex > transferIndex2) tline.last() else tline.first()
                            }
                        }
                    }
                }
            }

        //calc the price
        if (shortestRoute != null) {
            val numberOfStations = shortestRoute.size
            val priceForNormalPeople = when {
                numberOfStations <= 9 -> 8
                numberOfStations <= 16 -> 10
                numberOfStations <= 23 -> 15
                else -> 20
            }
            val priceForOlderPeople = priceForNormalPeople / 2
            var commonR: MutableList<String>? = null

            if (commonR != null) {
                commonR.clear()
            } else {
                commonR = mutableListOf()
            }
            for (station in shortestRoute) {
                if (station in commonStations && station != "kit kat") {
                    commonR.add(station)
                }
            }




            //print the output
            number.text ="Number of stations : ${numberOfStations.toString()} Stations "
            time.text = "Estimated time : ${numberOfStations * 2} minutes "
            directionstation.text = "Direction : ${ direction } "
            price.text = "Ticket price : $priceForNormalPeople EGP "
            otherprice.text = "price for elderly: $priceForOlderPeople EGP, for special needs:5 EGP "
            route.text = "Shortes Route : ${shortestRoute.joinToString(" -> ")} "
            if (!commonR.isNullOrEmpty()) {
                comstation.text = "Transaction station : ${commonR.joinToString(" , ")}"
            } else {
                comstation.text = "Transaction station : No transaction stations found"
            }



            Toast.makeText(this, "Route calculated successfully", Toast.LENGTH_SHORT).show()
        }
    }
// swap between two spinner
    fun swap(view: View) {
        //get the name of sattion
        val startStationName = startStation.selectedItem.toString()
        val endStationName = endStation.selectedItem.toString()
        //git the position of station
        val startPosition = startStation.selectedItemPosition
        val endPosition = endStation.selectedItemPosition
       //swap between two spinner
        startStation.setSelection(endPosition)
        endStation.setSelection(startPosition)

        calculate(view)
    }

}
