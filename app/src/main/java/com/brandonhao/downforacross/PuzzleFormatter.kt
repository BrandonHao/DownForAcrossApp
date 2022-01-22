package com.brandonhao.downforacross

import android.util.Log
import org.akop.ararat.core.Crossword
import org.akop.ararat.core.buildWord
import org.akop.ararat.io.CrosswordFormatter
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.ArrayList


class PuzzleFormatter : CrosswordFormatter {

    private var encoding = DEFAULT_ENCODING

    override fun setEncoding(encoding: String) {
        this.encoding = encoding
    }

    private fun populatePuzzleInfo(builder: Crossword.Builder, infoJson: JSONObject){
        builder.title = infoJson.getString(INFO_TITLE_KEY)
        builder.description = infoJson.getString(INFO_DESC_KEY)
        builder.author = infoJson.getString(INFO_AUTHOR_KEY)
    }

    private fun populateGridDimensions(builder: Crossword.Builder, gridJson: JSONArray){
        builder.height = gridJson.length()
        builder.width = gridJson.getJSONArray(0).length()
    }

    private fun getStringList(stringJson: JSONArray):ArrayList<String>{
        val stringList = ArrayList<String>()

        for(i in 0 until stringJson.length()){
            try{
                stringList.add(stringJson.getString(i))
            } catch (e: JSONException){
                Log.e("PZZL FORMAT", e.toString())
            }
        }

        return stringList
    }

    private fun getIntList(intList: JSONArray):IntArray{
        val arr = IntArray(intList.length())

        for(i in arr.indices){
            arr[i] = intList.getInt(i)
        }

        return arr
    }

    private fun getCellGrid(gridJson: JSONArray, circleArray: IntArray): Array<Array<Cell?>> {
        val jsonRowArray = Array(gridJson.length()){ i -> gridJson.getJSONArray(i) }
        val gridHeight = jsonRowArray.size
        val gridWidth = gridJson.getJSONArray(0).length()

        val cellGrid = Array(gridHeight) { arrayOfNulls<Cell?>(gridWidth) }

        for(i in 0 until gridHeight){
            for(j in 0 until gridWidth){
                if(jsonRowArray[i][j].toString() != "."){
                    cellGrid[i][j] = Cell(chars = jsonRowArray[i][j].toString())
                }
            }
        }

        for(i in circleArray){
            cellGrid[i / gridHeight][i % gridHeight]!!.attrs = Crossword.Cell.ATTR_CIRCLED
        }

        return cellGrid
    }

    @Throws(IOException::class)
    override fun read(builder: Crossword.Builder, inputStream: InputStream) {
        val inputString = inputStream.bufferedReader().use { it.readText() }

        val contentsJson = JSONObject(inputString).getJSONObject(CONTENT_KEY)
        populatePuzzleInfo(builder, contentsJson.getJSONObject(INFO_KEY))
        populateGridDimensions(builder, contentsJson.getJSONArray(GRID_KEY))

        val hintsAcross = getStringList(contentsJson.getJSONObject(CLUES_KEY).getJSONArray(
            CLUES_ACROSS_KEY))
        val hintsDown = getStringList(contentsJson.getJSONObject(CLUES_KEY).getJSONArray(
            CLUES_DOWN_KEY))
        val shadeArray = getIntList(contentsJson.getJSONArray(SHADES_KEY))
        val circleArray = getIntList(contentsJson.getJSONArray(CIRCLES_KEY))
        val cellMap: Array<Array<Cell?>> = getCellGrid(contentsJson.getJSONArray(GRID_KEY), circleArray)

        // Complete word information given the 2D map
        mapOutWords(builder, hintsAcross, hintsDown, cellMap)
    }

    private fun mapOutWords(cb: Crossword.Builder,
                            hintsAcross: List<String>,
                            hintsDown: List<String>,
                            cellMap: Array<Array<Cell?>>) {
        var acrossIndex = 0
        var downIndex = 0
        var number = 0
        var actualHeight = 0

        (0..cellMap.lastIndex).forEach { i ->
            var allEmpty = true
            (0..cellMap[i].lastIndex).forEach inner@ { j ->
                if (cellMap[i][j] == null) return@inner

                allEmpty = false
                var incremented = false
                if ((j == 0 || j > 0 && cellMap[i][j - 1] == null)
                    && j < cellMap[i].lastIndex
                    && cellMap[i][j + 1] != null) {
                    // Start of a new Across word
                    number++
                    incremented = true

                    cb.words += buildWord {
                        direction = Crossword.Word.DIR_ACROSS
                        hint = hintsAcross[acrossIndex++]
                        this.number = number
                        startRow = i
                        startColumn = j

                        // Copy contents to a temp buffer
                        for (k in j..cellMap[i].lastIndex) {
                            val cell = cellMap[i][k] ?: break
                            addCell(cell.chars, cell.attrs)
                        }
                    }
                }

                if (i == 0 || i > 0 && cellMap[i - 1][j] == null
                    && i < cellMap.lastIndex
                    && cellMap[i + 1][j] != null) {
                    // Start of a new Down word
                    if (!incremented) number++

                    cb.words += buildWord {
                        direction = Crossword.Word.DIR_DOWN
                        hint = hintsDown[downIndex++]
                        this.number = number
                        startRow = i
                        startColumn = j

                        for (k in i..cellMap.lastIndex) {
                            val cell = cellMap[k][j] ?: break
                            addCell(cell.chars, cell.attrs)
                        }
                    }
                }
            }

            if (!allEmpty) actualHeight++
        }

        cb.setHeight(actualHeight)
    }

    private class Cell(var chars: String = "",
                       var attrs: Int = 0)

    companion object {
        private const val DEFAULT_ENCODING = "UTF-8"

        private const val CONTENT_KEY = "content"
        private const val GRID_KEY = "grid"
        private const val INFO_KEY = "info"
        private const val CLUES_KEY = "clues"
        private const val INFO_TYPE_KEY = "info"
        private const val INFO_AUTHOR_KEY = "author"
        private const val INFO_TITLE_KEY = "title"
        private const val INFO_DESC_KEY = "description"
        private const val CLUES_DOWN_KEY = "down"
        private const val CLUES_ACROSS_KEY = "across"
        private const val SHADES_KEY = "shades"
        private const val CIRCLES_KEY = "circles"
    }
}