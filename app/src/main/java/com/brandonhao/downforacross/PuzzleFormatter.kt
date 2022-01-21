package com.brandonhao.downforacross

import org.akop.ararat.core.Crossword
import org.akop.ararat.io.CrosswordFormatter

import java.io.InputStream
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.lang.StringBuilder
import java.nio.charset.Charset


class PuzzleFormatter : CrosswordFormatter {

    private var encoding = "UTF-8"
    private val rowStringList = ArrayList<String>()
    private val rowWordList = ArrayList<String>()
    private val downWordList = ArrayList<String>()
    private val downClueList = ArrayList<String>()

    override fun setEncoding(encoding: String){
        this.encoding = encoding
    }

    @Throws(IOException::class)
    override fun read(builder: Crossword.Builder, inputStream: InputStream) {
        val inputString = inputStream.bufferedReader(Charset.forName(encoding)).use { it.readText() }
        val jsonObj = JSONObject(inputString)
        val jsonContents = jsonObj.getJSONObject("content")
        getWordList(jsonContents.getJSONArray("grid"))

        builder.width = rowStringList[0].length
        builder.height = rowStringList.size

        val jsonInfo = jsonContents.getJSONObject("info")
        builder.author = jsonInfo.getString("author")
        builder.title = jsonInfo.getString("title")
        builder.description = jsonInfo.getString("description")

    }

    private fun getWordList(grid : JSONArray){
        for(i in 0 until grid.length()){
            //Save the row as a string
            rowStringList.add(getRowString(grid.getJSONArray(i)))
            //Get the words in the row
            rowWordList.addAll(getWords(rowStringList[i]))
        }
        getDownWords()
    }

    private fun getRowString(rowData: JSONArray): String{
        val string = StringBuilder()
        for(i in 0 until rowData.length()){
            string.append(rowData.getString(i))
        }
        return string.toString()
    }

    private fun getWords(rowString : String): ArrayList<String>{
        val words = ArrayList<String>()
        var startIdx = 0
        while(startIdx != rowString.length){
            var currIdx = startIdx
            //If this index does not have a char, continue
            if(rowString[startIdx] == '.'){
                startIdx++
                continue
            }

            //Find the end of the word, either we find a non-char or the end of the row
            while(currIdx != rowString.length && rowString[currIdx] != '.'){
                currIdx++
            }

            //If the word had a length of one, it wasn't a word so ignore it
            if(currIdx - startIdx != 1){
                words.add(rowString.substring(startIdx, currIdx))
            }
            //Move the start index up and continue
            startIdx = currIdx
        }
        return words
    }

    private fun getDownWords(){
        val stringBuilders = ArrayList<DownStringStruct>()
        val downStringData = ArrayList<String>(downClueList.size)
        for(i in downStringData.indices){
            stringBuilders.add(DownStringStruct(StringBuilder(), 0))
        }
        var wordCnt = 0
        for(rowIdx in 0 until rowStringList.size){
            for(colIdx in 0 until rowStringList[rowIdx].length){
                if(rowStringList[rowIdx][colIdx] == '.') {
                    //If there is currently a word, in this column, we found the end of the word,
                    //set the corresponding entry in the word list and clear the stringbuilder
                    if(stringBuilders[colIdx].data.isNotEmpty()){
                        downStringData[stringBuilders[colIdx].wordIdx] = stringBuilders[colIdx].toString()
                        stringBuilders[colIdx].data.clear()
                    }
                    continue
                }
                //If the stringbuilder is empty, then this is the start of a word
                if(stringBuilders[colIdx].data.isEmpty()){
                    //If the next char is the end of the word, the length will be one and this word
                    //won't be valid so skip it
                    if(rowIdx == rowStringList.size - 1 || rowStringList[rowIdx + 1][colIdx] == '.'){
                        continue
                    }
                    //If this is the start of a valid word, increment the word count
                    stringBuilders[colIdx].wordIdx = wordCnt
                    wordCnt++
                    //Add an entry to the word list
                    downWordList.add("")
                }
                stringBuilders[colIdx].data.append(rowStringList[rowIdx][colIdx])
            }
        }
        //If any stringbuilders had a word when the loop ended, set the corresponding word in the
        //word list
        for(str in stringBuilders){
            downWordList[str.wordIdx] = str.data.toString()
        }
    }

    private data class DownStringStruct(
        val data: StringBuilder,
        var wordIdx: Int
    )
}