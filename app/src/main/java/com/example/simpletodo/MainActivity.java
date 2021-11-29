package com.example.simpletodo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    List<String> items;
    Button buttonAdd;
    EditText editText;
    RecyclerView rvItems;
    ItemsAdapter itemsAdapter;

    public static final String KEY_ITEM_TEXT = "item_text";
    public static final String KEY_ITEM_POSITION = "item_position";
    public static final Integer EDIT_TEXT_CODE = 20;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonAdd = findViewById(R.id.buttonAdd);
        editText = findViewById(R.id.edText);
        rvItems = findViewById(R.id.rvItems);

        editText.setText("");
        loadItems();

        ItemsAdapter.OnLongClickListener onLongClickListener;
           onLongClickListener = new ItemsAdapter.OnLongClickListener() {
            @Override
            public void onItemLongClicked(int position) {
            //Delete the item from the model
                items.remove(position);
                //notify the adapter
                itemsAdapter.notifyItemRemoved(position);
                Toast.makeText(getApplicationContext(),"Item was removed", Toast.LENGTH_SHORT).show();
                saveItems();
            }
        };

        ItemsAdapter.OnClickListener onClickListener = new ItemsAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int position) {
                Log.e("MainActivity", "Single click at position " + position);
                //new activity
                Intent i = new Intent(MainActivity.this, EditActivity.class);
                //pass data being edited
                i.putExtra(KEY_ITEM_TEXT,items.get(position));
                i.putExtra(KEY_ITEM_POSITION, position);

                //display activity
                startActivityForResult(i,EDIT_TEXT_CODE);
            }
        };
        itemsAdapter= new ItemsAdapter(items,onLongClickListener,onClickListener);
        rvItems.setAdapter(itemsAdapter);
        rvItems.setLayoutManager(new LinearLayoutManager(this));

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String todoItem = editText.getText().toString();
                //Add item to the model
                //Notify adapter that an item is inserted//
                items.add(todoItem);
                itemsAdapter.notifyItemInserted(items.size()-1);
                editText.setText("");
                Toast.makeText(getApplicationContext(), "Item was Added", Toast.LENGTH_SHORT).show();
                saveItems();
            }
        });
    }
    //handle the result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == EDIT_TEXT_CODE){
            String itemText = data.getStringExtra(KEY_ITEM_TEXT);
            int position = data.getExtras().getInt(KEY_ITEM_POSITION);

            items.set(position,itemText);
            itemsAdapter.notifyItemChanged(position);
            saveItems();
            Toast.makeText(getApplicationContext(), "Item updated Succesfully",Toast.LENGTH_SHORT).show();

        }else{
            Log.w("MainActivity", "Unknown call to onActivityResult");
        }
    }

    private File getDataFile(){
        return new File(getFilesDir(),"data.txt");

    }

   // This func will load items by reading every line of the data file
    private void loadItems(){
        try {
            items = new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e) {
            Log.e("MainActivity", "Error reading items", e);
            items = new ArrayList<>();
        }
    }
    private void saveItems(){
        try {
            FileUtils.writeLines(getDataFile(),items);
        } catch (IOException e) {
Log.e("MainActivity", "Error writing items",e);        }
    }
}