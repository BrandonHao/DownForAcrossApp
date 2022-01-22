package com.brandonhao.downforacross;

import org.junit.*;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Scanner;

public class PuzzleListTest {
    private String testJsonString;
    public PuzzleList testPuzzleList;

    @Before
    public void setUp(){
        File file = file = new File(getClass().getResource("/json").getPath());

        try
        {
            InputStream in = new FileInputStream(file);
            Scanner scanner = new Scanner(in);
            testJsonString = scanner.useDelimiter("\\A").next();
        } catch (Exception e) {}
    }

    @Test
    public void testParsePuzzleListJson(){
        testPuzzleList = new PuzzleList();
        testPuzzleList.addPuzzles(testJsonString);
        assertEquals(testPuzzleList.getPuzzleCount(), 3);
        assertEquals(testPuzzleList.getPuzzle(0).pid, 18981);
    }
}
