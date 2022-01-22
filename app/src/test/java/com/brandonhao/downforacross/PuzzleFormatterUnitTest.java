package com.brandonhao.downforacross;

import org.junit.*;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Path;

import org.akop.ararat.core.Crossword;

public class PuzzleFormatterUnitTest {
    InputStream in;

    @Before
    public void setUp(){
        File file = new File(getClass().getResource("/single.json").getPath());

        try
        {
            in = new FileInputStream(file);
        } catch (Exception ignored) {}
    }

    @Test
    public void testPuzzleFormatter(){
        PuzzleFormatter p = new com.brandonhao.downforacross.PuzzleFormatter();
        Crossword.Builder builder = new Crossword.Builder();
        try {
            p.read(builder, in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals(builder.getAuthor(), "By Brendan Emmett Quigley/Edited by Mike Shenk");
        assertEquals(builder.getHeight(), 15);
        assertEquals(builder.getWords().get(1).getHint(), "\"Gotta jet\"");
        assertEquals(builder.getWords().get(2).getNumber(), 2);
        assertEquals(builder.getWords().get(3).getDirection(), 1);
    }
}
