package com.moonpi.swiftnotes;

import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.skyscreamer.jsonassert.JSONAssert;
import org.junit.Before;
import org.junit.Test;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doThrow;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import android.os.Environment;
import org.robolectric.shadows.ShadowEnvironment;
import org.junit.rules.ExpectedException;
import org.junit.Rule;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@PowerMockIgnore({ "org.mockito.*", "org.robolectric.*", "android.*"})
@PrepareForTest(DataUtils.class)
@Config(constants = BuildConfig.class)
public class DataUtilsTest {
    DataUtils note;

    @Rule
    public PowerMockRule rule = new PowerMockRule();
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        note = new DataUtils();
    }

    @Test
    public void saveData() throws Exception {
        File toFile = new File("notes.json");
        JSONArray test0 = null;
        JSONObject testObj = new JSONObject();
        try {
            testObj.put("title", "hi")
                    .put("body", " ")
                    .put("colour"," ")
                    .put("favoured",true)
                    .put("fontSize", 12)
                    .put("hideBody",true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONArray test1 = new JSONArray()
                .put(testObj);
        //if path is not null and notes is not null, return true
        assertTrue("If the file path is not null and the json array is not empty, " +
                "it will be true",note.saveData(toFile, test1));
        //if path is not null, but notes is null, return false
        assertFalse("If the file path is not null but the json array is null, " +
                "it will be false",note.saveData(toFile, test0));
        //if the file is null, but note is not null, return false
        assertFalse("If the file path is null but the note is not null, " +
                "it will be false",note.saveData(null, test1));
        //if the file is null, but note is null, return false
        assertFalse("If the file path and the json array are null, it will be false",
                note.saveData(null, null));
        //if the file does not exist, return false
        assertFalse("If the file does not exist, it will be false",
                note.saveData(null, null));
        //if file is backup
        MainActivity activity = Robolectric.setupActivity(MainActivity.class);
        File backupfile = activity.getBackupPath();
        //if isExternalStorageReadable() && isExternalStorageWritable() is false
        assertFalse(note.saveData(backupfile ,test1));
        //if isExternalStorageReadable() && isExternalStorageWritable() is true
        ShadowEnvironment.setExternalStorageState(Environment.MEDIA_MOUNTED);
        assertFalse(note.saveData(backupfile, test1));
        //problem: it has bug: add "toFile.getParentFile().mkdirs();" in DataUtils.java (line 87)
        //if file is local
        File localfile = activity.getLocalPath();
        assertTrue(note.saveData(localfile ,test1));

        // Exceptions coverage
        // stub any buffer.flush() to trigger exception
        BufferedWriter mockBuffer = Mockito.mock(BufferedWriter.class);
        PowerMockito.whenNew(BufferedWriter.class).withAnyArguments().thenReturn(mockBuffer);
        doThrow(new IOException("IO Failure")).when(mockBuffer).flush();
        exceptionRule.expect(IOException.class);
        note.saveData(toFile, test1);

        // stub any buffer.write() to trigger exception
        doThrow(new IOException("IO Failure")).when(mockBuffer).write(Mockito.any(String.class));
        exceptionRule.expect(JSONException.class);
        assertFalse(note.saveData(toFile, test1));

        // Stub JSONObject.put to trigger Exception
        JSONObject mockJsonObject = Mockito.mock(JSONObject.class);
        PowerMockito.whenNew(JSONObject.class).withNoArguments().thenReturn(mockJsonObject); //create new json object = inside function 63
        doThrow(new JSONException("Failure")).when(mockJsonObject).put(Mockito.any(String.class), Mockito.any(JSONArray.class)); //for line 68
        assertFalse(note.saveData(toFile, new JSONArray()));
    }

    @Test
    public void retrieveData() throws Exception{
        File fromFile = new File("notes.json");
        JSONObject testObj = new JSONObject();
        try {
            testObj.put("title", "hi")
                    .put("body", " ")
                    .put("colour"," ")
                    .put("favoured",true)
                    .put("fontSize", 12)
                    .put("hideBody",true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONArray testRetrieve = new JSONArray()
                .put(testObj);
        //if the file is not empty, return the JSON array from file
        try{
            JSONAssert.assertEquals(testRetrieve, note.retrieveData(fromFile), true);
        } catch (Exception e){
            e.printStackTrace();
        }
        //if the file is  null, return null
        //assertEquals(null, note.retrieveData(null));
        //if the file is  empty, return null
        //assertEquals(null, note.retrieveData(new File("")));

        //if file is backup
        MainActivity activity = Robolectric.setupActivity(MainActivity.class);
        File backupfile = activity.getBackupPath();
        //if isExternalStorageReadable() && isExternalStorageWritable() is false
        assertNull(note.retrieveData(backupfile));
        //if isExternalStorageReadable() && isExternalStorageWritable() is true
        ShadowEnvironment.setExternalStorageState(Environment.MEDIA_MOUNTED);
        assertNull(note.retrieveData(backupfile));
        //if file is local
        File localfile = activity.getLocalPath();
        try{
            JSONAssert.assertNotEquals(testRetrieve, note.retrieveData(localfile), true);
        } catch (Exception e){
            e.printStackTrace();
        }

        // Exception Coverage
        // stub BufferedReader.close() => IOException
        BufferedReader mockBuffer = Mockito.mock(BufferedReader.class);
        PowerMockito.whenNew(BufferedReader.class).withAnyArguments().thenReturn(mockBuffer);
        doThrow(new IOException("IO Failure")).when(mockBuffer).close();
        exceptionRule.expect(JSONException.class);
        note.retrieveData(backupfile);

        // Stub JSONObject.getJSONArray => JSONException
        JSONObject mockJsonObject = Mockito.mock(JSONObject.class);
        PowerMockito.whenNew(JSONObject.class).withAnyArguments().thenReturn(mockJsonObject);
        doThrow(new JSONException("Failure")).when(mockJsonObject).getJSONArray(Mockito.any(String.class));
        exceptionRule.expect(JSONException.class);
        note.retrieveData(fromFile);

        // Stub JSONObject.put => JSONException
        JSONArray mockJsonArray = Mockito.mock(JSONArray.class);
        PowerMockito.whenNew(JSONArray.class).withNoArguments().thenReturn(mockJsonArray);
        doThrow(new JSONException("Failure")).when(mockJsonArray).put(Mockito.any(String.class));
        exceptionRule.expect(JSONException.class);
        note.retrieveData(fromFile);
    }

    @Test
    public void deleteNotes() throws Exception {
        ArrayList<Integer> position = new ArrayList<>();
        position.add(0);
        JSONObject testObj1 = new JSONObject();
        try {
            testObj1.put("title", "hi")
                    .put("body", " ")
                    .put("colour"," ")
                    .put("favoured",true)
                    .put("fontSize", 12)
                    .put("hideBody",true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject testObj2 = new JSONObject();
        try {
            testObj1.put("title", "world")
                    .put("body", " ")
                    .put("colour"," ")
                    .put("favoured",true)
                    .put("fontSize", 12)
                    .put("hideBody",true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONArray test1 = new JSONArray()
                .put(testObj1)
                .put(testObj2);
        JSONArray test2 = new JSONArray();
        JSONArray res = new JSONArray().put(testObj2);
        JSONArray res2 = new JSONArray();
        //if input JSON array is not empty, and the arraylist is not mull, return new array
        try{
            JSONAssert.assertEquals(res, note.deleteNotes(test1,position), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //if input JSON array is empty, return empty JSON array
        try{
            JSONAssert.assertEquals(res2, note.deleteNotes(test2,position), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //if input JSON array is empty, return original JSON array
        ArrayList<Integer> position2 = new ArrayList<>();
        try{
            JSONAssert.assertEquals(test1, note.deleteNotes(test1,position2), true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Stub JSONObject.put => Exception
        JSONArray mockJsonArray = Mockito.mock(JSONArray.class);
        PowerMockito.whenNew(JSONArray.class).withNoArguments().thenReturn(mockJsonArray);
        doThrow(new JSONException("Failure")).when(mockJsonArray).put(Mockito.any(String.class));
        exceptionRule.expect(JSONException.class);
        note.deleteNotes(test1,position);
    }
}