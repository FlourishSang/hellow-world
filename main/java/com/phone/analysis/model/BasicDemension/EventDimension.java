package com.phone.analysis.model.BasicDemension;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @description 事件维度类
 * @author: 赵燕钦
 * @create: 2018-12-08 17:38:44
 **/
public class EventDimension extends TopLevelDimension {

    private int id;
    private  String category;
    private String action;

    public EventDimension() {
    }

    public EventDimension(int id, String category, String action) {
        this.id = id;
        this.category = category;
        this.action = action;
    }

    public EventDimension(String category, String action) {
        this.category = category;
        this.action = action;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public int compareTo(TopLevelDimension o) {

        if (this == o){
            return 0;
        }
        EventDimension other = (EventDimension)o;
        int tmp = this.id- other.id;
        if (tmp ==0){
            return tmp;
        }

        tmp = this.category.compareTo(other.category);
        if (tmp == 0){
            return tmp;
        }
        return this.action.compareTo(other.action);
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {

        dataOutput.writeInt(id);
        dataOutput.writeUTF(category);
        dataOutput.writeUTF(action);


    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        this.id = dataInput.readInt();
        this.category = dataInput.readUTF();
        this.action = dataInput.readUTF();

    }
}
