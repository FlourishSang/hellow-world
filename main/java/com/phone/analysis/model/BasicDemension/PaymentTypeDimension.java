package com.phone.analysis.model.BasicDemension;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

/**
 * @description 支付方式基础维度类
 * @author: 赵燕钦
 * @create: 2018-12-11 14:38:27
 **/
public class PaymentTypeDimension extends TopLevelDimension {
    private int id;
    private String payment_type;

    @Override
    public String toString() {
        return "PaymentTypeDimension{" +
                "id=" + id +
                ", payment_type='" + payment_type + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentTypeDimension that = (PaymentTypeDimension) o;
        return id == that.id &&
                Objects.equals(payment_type, that.payment_type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, payment_type);
    }


    public PaymentTypeDimension() {
    }

    public PaymentTypeDimension(String payment_type) {
        this.payment_type = payment_type;
    }

    public PaymentTypeDimension(int id, String payment_type) {
        this.id = id;
        this.payment_type = payment_type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPayment_type() {
        return payment_type;
    }

    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }

    @Override
    public int compareTo(TopLevelDimension o) {
        if (this ==o){
            return 0;
        }
        PaymentTypeDimension other = (PaymentTypeDimension)o;
        int tmp = this.id - other.id;
        if (tmp==0){
            return tmp;
        }
        return this.payment_type.compareTo(other.payment_type);
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(id);
        dataOutput.writeUTF(payment_type);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        this.id = dataInput.readInt();
        this.payment_type = dataInput.readUTF();
    }
}
