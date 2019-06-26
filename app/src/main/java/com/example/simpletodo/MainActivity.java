package com.example.simpletodo;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> alItems;
    ArrayAdapter<String> itemsAdapter;
    ListView lvItems;
    int editPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the ListView by id
        lvItems = (ListView) findViewById(R.id.lvItems);
        // Read items into ArrayList
        readItems();
        // Set up the adapter between underlying ArrayList and the ListView
        itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, alItems);
        lvItems.setAdapter(itemsAdapter);
        setupListViewListener();
    }

    public void onAddItem(View v) {
        // Get the input from the EditText
        EditText etItemInput = (EditText) findViewById(R.id.etItemInput);
        String itemText = etItemInput.getText().toString();
        if(editPosition == -1) {
            // Add the input to the list
            itemsAdapter.add(itemText);
            // Clear the input field
            Toast.makeText(getApplicationContext(), "Item added!", Toast.LENGTH_SHORT).show();
        }
        else {
            // Update the items at editPosition
            alItems.remove(editPosition);
            alItems.add(editPosition, itemText);

            // Notify the adapter
            itemsAdapter.notifyDataSetChanged();

            // Reset changed elements
            editPosition = -1;
            Button btnAddItem = (Button) findViewById(R.id.btnAddItem);
            btnAddItem.setText("Do It!");
        }

        // Write to data
        etItemInput.setText("");
        writeItems();
    }

    // Sets up the listener for removing items from the list
    private void setupListViewListener() {
        // Set the ListView's itemLongClickListener
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // Remove the given item
                alItems.remove(position);
                // Update the adapter
                itemsAdapter.notifyDataSetChanged();
                writeItems();
                return true;
            }
        });

        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Save the position being edited
                editPosition = position;

                // Update the button
                Button btnAddItem = (Button) findViewById(R.id.btnAddItem);
                btnAddItem.setText("Edit!");
            }
        });
    }

    // Returns the file in which the data is stored
    private File getDataFile() {
        return new File(getFilesDir(), "todo.txt");
    }

    // Reads items from the file systems
    private void readItems() {
        try {
            // Populate the array with persistent items
            alItems = new ArrayList<String>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e) {
            // Print the error to the console
            e.printStackTrace();
            // Initialize an empty array
            alItems = new ArrayList<>();
        }
    }

    // Write items to the filesystem
    private void writeItems() {
        try {
            // Save the item list as a line-delimited text file
            FileUtils.writeLines(getDataFile(), alItems);
        } catch (IOException e) {
            // Print the error to the console
            e.printStackTrace();
        }
    }
}
