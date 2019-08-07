package com.moonpi.swiftnotes;

import android.app.Activity;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import static org.junit.Assert.*;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import android.view.LayoutInflater;


@RunWith(MockitoJUnitRunner.class)
public class NoteAdapterTest {
    // Estimated value near to MAX_VALUE without crashing the data preparation function getJSONArray
    // On the testing environment, if Out of memory, please decrease this value.
    int ESTIMATED_MAX = 10000;
    @Test
    public void getItemId() {
        Context context = Mockito.mock(Context.class);
        NoteAdapter adapter = new NoteAdapter(context, null);
        assertEquals(0, adapter.getItemId(0));
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void getCount() {
        Context context = Mockito.mock(Context.class);
        // null JSONArray - invalid
        // output 0 expected from behavior of NoteAdapter.getCount()
        NoteAdapter adapter = new NoteAdapter(context, null);
        assertEquals(0, adapter.getCount());

        // size array: 0  (empty JSONArray)
        adapter = new NoteAdapter(context, getJSONArray(0));
        assertEquals(0, adapter.getCount());

        // size array: 1
        adapter = new NoteAdapter(context, getJSONArray(1));
        assertEquals(1, adapter.getCount());

        // size array: ESTIMATED_MAX
        adapter = new NoteAdapter(context, getJSONArray(ESTIMATED_MAX));
        assertEquals(ESTIMATED_MAX, adapter.getCount());

        // test size array: MAX_VALUE
        // NOTE: Currently crushes the preparation of data to test
        // size array: Integer.MAX_VALUE;
//        adapter = new NoteAdapter(context, getJSONArray(Integer.MAX_VALUE));
//        assertEquals(Integer.MAX_VALUE, adapter.getCount());
    }


    @Test
    public void getItem() {
        Context context = Mockito.mock(Context.class);
        /*** Valid Cases ***/

        // size array: 1, position: 0
        int position = 0;
        int sizeArray = 1;
        JSONArray jsonArray1 = getJSONArray(sizeArray);
        NoteAdapter adapter = new NoteAdapter(context, jsonArray1);
        try {
            JSONAssert.assertEquals((JSONObject)jsonArray1.get(position), adapter.getItem(position), false);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // size array: MAX_VALUE, position: MAX_VALUE
        // position = Integer.MAX_VALUE;
        // sizeArray = Integer.MAX_VALUE;
        // ESTIMATED_MAX is an estimated MAX_VALUE supported for the testing environment
        position = ESTIMATED_MAX - 1;
        sizeArray = ESTIMATED_MAX;
        JSONArray jsonArrayMax = getJSONArray(sizeArray);
        adapter = new NoteAdapter(context, jsonArrayMax);
        try {
            JSONAssert.assertEquals((JSONObject)jsonArrayMax.get(position), adapter.getItem(position), false);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // size: 2, position: 1
        position = 1;
        sizeArray = 2;
        JSONArray jsonArray2 = getJSONArray(sizeArray);
        adapter = new NoteAdapter(context, jsonArray2);
        try {
            JSONAssert.assertEquals((JSONObject)jsonArray2.get(position), adapter.getItem(position), false);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        /*** Invalid Cases ***/
        // A. Invalid JSONArray size

        // size: null
        position = 0;
        adapter = new NoteAdapter(context, null);
        assertNull(adapter.getItem(position));

        // size: 0 array
        position = 0;
        JSONArray jsonArray0 = new JSONArray();
        adapter = new NoteAdapter(context, jsonArray0);
        try {
            JSONAssert.assertEquals((JSONObject)jsonArray0.get(position), adapter.getItem(position), false);
        } catch (JSONException e) {
            System.out.println("Exception expected");
        }

        // B. Invalid position values

        // size: 1 valid, position: MIN_VALUE invalid
        position = Integer.MIN_VALUE;
        adapter = new NoteAdapter(context, jsonArray1);
        try {
            JSONAssert.assertEquals((JSONObject)jsonArray1.get(position), adapter.getItem(position), false);
        } catch (JSONException e) {
            System.out.println("Exception expected");
        }

        // Size: 1 valid, position: negative invalid
        position = -1;
        adapter = new NoteAdapter(context, jsonArray1);
        try {
            JSONAssert.assertEquals((JSONObject)jsonArray1.get(position), adapter.getItem(position), false);
        } catch (JSONException e) {
            System.out.println("Exception expected");
        }


        // Edge case: position greater than size
        position = 10;
        sizeArray = 8;
        JSONArray jsonArrayN = getJSONArray(sizeArray);
        adapter = new NoteAdapter(context, jsonArrayN);
        try {
            JSONAssert.assertEquals((JSONObject)jsonArrayN.get(sizeArray), adapter.getItem(position), false);
        } catch (JSONException e) {
            System.out.println("Exception expected");
        }

    }

    // Returns a JSONArray of JSONObject notes of size totalNotes
    public JSONArray getJSONArray(int totalNotes){
        JSONArray jsonArray = new JSONArray();
        for(int i=0; i<totalNotes; i++) {
            JSONObject testObj = new JSONObject();
            try {
                testObj.put("title", "Title " + i)
                        .put("body", "Test" + i )
                        .put("colour", " ")
                        .put("favoured", true)
                        .put("fontSize", 12)
                        .put("hideBody", true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArray.put(testObj);
        }
        return jsonArray;
    }
}