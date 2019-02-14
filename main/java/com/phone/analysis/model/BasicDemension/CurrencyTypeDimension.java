package com.phone.analysis.model.BasicDemension;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

/**
 * @description 货币类型的基础维度类
 * @author: 赵燕钦
 * @create: 2018-12-11 14:45:16
 **/
public class CurrencyTypeDimension extends TopLevelDimension {
    private int id;
    private String currency_name;


    @Override
    public String toString() {
        return "CurrencyTypeDimension{" +
                "id=" + id +
                ", currency_name='" + currency_name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrencyTypeDimension that = (CurrencyTypeDimension) o;
        return id == that.id &&
                Objects.equals(currency_name, that.currency_name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, currency_name);
    }

    public CurrencyTypeDimension() {
    }

    public CurrencyTypeDimension(String currency_name) {
        this.currency_name = currency_name;
    }

    public CurrencyTypeDimension(int id, String currency_name) {
        this.id = id;
        this.currency_name = currency_name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCurrency_name() {
        return currency_name;
    }

    public void setCurrency_name(String currency_name) {
        this.currency_name = currency_name;
    }

    @Override
    public int compareTo(TopLevelDimension o) {
        if (this == o){
            return 0;
        }
        CurrencyTypeDimension other = (CurrencyTypeDimension)o;
        int tmp = this.id - other.id;
        if (tmp ==0){
            return tmp;
        }
        return  this.currency_name.compareTo(other.currency_name);
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(id);
        dataOutput.writeUTF(currency_name);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        this.id = dataInput.readInt();
        this.currency_name = dataInput.readUTF();
    }
}
