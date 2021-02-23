package org.eugene.controller;

import java.util.List;

import org.eugene.ui.Table;

import javafx.stage.Stage;

public class TableRenderer {
    private Stage stage;
    private Table table;

    public void init(){
        table.initTable();
    }

    public void setTable(Table table){
        this.table = table;
    }

    public void refresh(List<String> showingList, List<String> propertyList, int rowNumber, int columnNumber, List<List<String>> data){
        table.refresh(showingList, propertyList, rowNumber, columnNumber, data);
    }

}
