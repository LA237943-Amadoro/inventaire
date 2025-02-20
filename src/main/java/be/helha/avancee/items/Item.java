package be.helha.avancee.items;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;

public class Item {
    @JsonProperty("_id")
    protected ObjectId id;
    protected String name;
    protected String type;

    public Item(String name, String type) {
        this.name = name;
        this.type = type ;
        this.id = new ObjectId();
    }
    public Item(){
        this.id = new ObjectId();
    }

    public static class ObjectIdWrapper {
        @JsonProperty("$oid")
        private String oid;

        public String getOid() { return oid; }
        public void setOid(String oid) { this.oid = oid; }

        @Override
        public String toString() {
            return oid;
        }
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Item2{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
