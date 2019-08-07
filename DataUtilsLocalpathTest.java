package com.moonpi.swiftnotes;

import org.json.JSONArray;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doThrow;


// Exception tests mocking localPath variable
@PowerMockIgnore({ "org.mockito.*", "org.robolectric.*" })
@PrepareForTest({DataUtils.class, MainActivity.class})
public class DataUtilsLocalpathTest {
    DataUtils note;

    @Rule
    public PowerMockRule rule = new PowerMockRule();
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        note = new DataUtils();
    }

    @Test
    @PrepareForTest(MainActivity.class)
    public void SaveDataException() throws Exception {
        final File mockFile = Mockito.mock(File.class);
        final FileWriter mockFileWriter = Mockito.mock(FileWriter.class);
        final BufferedWriter mockBufferedWriter = Mockito.mock(BufferedWriter.class);
        PowerMockito.whenNew(FileWriter.class).withAnyArguments().thenReturn(mockFileWriter);
        PowerMockito.whenNew(BufferedWriter.class).withAnyArguments().thenReturn(mockBufferedWriter);
        // mock MainActivity
        PowerMockito.mockStatic(MainActivity.class);
        // mock localpath
        PowerMockito.when(MainActivity.getLocalPath()).thenReturn(mockFile);

        //stub file.exists && file.createNewFile to return false
        Mockito.when(mockFile.exists()).thenReturn(false);
        Mockito.when(mockFile.createNewFile()).thenReturn(false);
        assertFalse(note.saveData(mockFile, new JSONArray()));

        // stub file.exists && file.createNewFile to trigger Exception
        doThrow(IOException.class).when(mockFile).createNewFile();
        assertFalse(note.saveData(mockFile, new JSONArray()));
    }

    @Test
    public void retrieveSaveData() throws Exception{
        // mocking MainActivity and localpath
        final File mockFile = Mockito.mock(File.class);
        PowerMockito.mockStatic(MainActivity.class);
        PowerMockito.when(MainActivity.getLocalPath()).thenReturn(mockFile);
        // stub file.exists to trigger exception
        Mockito.when(mockFile.exists()).thenReturn(false);
        assertNull(note.retrieveData(mockFile));
    }
}